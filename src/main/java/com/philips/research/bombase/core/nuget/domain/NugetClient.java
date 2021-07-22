/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.nuget.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.nuget.NugetException;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Optional;

@Component
public class NugetClient {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private static final ObjectMapper XML_MAPPER = new XmlMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE);
    private final NugetAPI restJson;
    private final NugetAPI restXml;
    private final URI baseURI;

    NugetClient() {
        this(URI.create("https://api.nuget.org/v3/"));
    }

    NugetClient(URI uri) {
        final var retrofitJson = new Retrofit.Builder()
                .baseUrl(uri.toASCIIString())
                .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                .build();
        final var retrofitXML = new Retrofit.Builder()
                .baseUrl(uri.toASCIIString())
                .addConverterFactory(JacksonConverterFactory.create(XML_MAPPER))
                .build();
        restJson = retrofitJson.create(NugetAPI.class);
        restXml = retrofitXML.create(NugetAPI.class);
        baseURI = uri;
    }

    Optional<PackageMetadata> getPackageMetadata(PackageURL purl) {
        // Some NuGet repositories requires a lower case package name.
        final var lowerCasedPurlName = purl.getName().toLowerCase(Locale.ROOT);
        final var catalogResponse =
                query(restJson.getCatalogEntry(lowerCasedPurlName, purl.getVersion()));
        return catalogResponse.flatMap(cat -> cat.extractCatalogEntryPath(cat, baseURI))
                .flatMap(path -> query(restXml.getNugetSpec(
                        String.format("flatcontainer/%s/%s/%s.nuspec",
                                lowerCasedPurlName,
                                purl.getVersion(),
                                lowerCasedPurlName)))
                        .flatMap(nugetSpecResponse -> query(restJson.getDefinition(path))
                                .map(result -> {
                                    result.downloadLocation = catalogResponse.get().packageContent;
                                    nugetSpecResponse.getRepositoryURL().ifPresent(url -> result.sourceUrl = url);
                                    return result;
                                })));
    }

    private <T> Optional<T> query(Call<? extends T> query) {
        try {
            final var response = query.execute();
            if (response.code() == 404) {
                return Optional.empty();
            }
            if (!response.isSuccessful()) {
                throw new NugetException("Nuget server responded with status " +
                        response.code());
            }
            return Optional.ofNullable(response.body());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON/XML formatting error", e);
        } catch (IOException e) {
            throw new NugetException("Nuget is not reachable");
        }
    }
}
