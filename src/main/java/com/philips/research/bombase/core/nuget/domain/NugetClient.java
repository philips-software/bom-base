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
    private URI baseURI;
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private final NugetAPI rest;

    NugetClient() {
        this(URI.create("https://api.nuget.org/v3/"));
    }

    NugetClient(URI uri) {
        final var retrofit = new Retrofit.Builder()
                .baseUrl(uri.toASCIIString())
                .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                .build();
        rest = retrofit.create(NugetAPI.class);
        baseURI = uri;
    }

    Optional<PackageMetadata> getPackageMetadata(PackageURL purl) {
        Optional<NugetAPI.CatalogResponseJson> optionalCatalogResponseJson = query(rest.getCatalogEntry(
                purl.getName().toLowerCase(Locale.ROOT), purl.getVersion()));
        if (optionalCatalogResponseJson.isEmpty() || optionalCatalogResponseJson.get().catalogEntry == null) {
            return Optional.empty();
        } else {
            Optional<String> catalogEntryUrl = Optional.ofNullable(optionalCatalogResponseJson.get()
                    .catalogEntry.split(baseURI.toASCIIString())[1]);

            if (catalogEntryUrl.isPresent()) {
                return query(rest.getDefinition(catalogEntryUrl.get()));
            } else {
                return Optional.empty();
            }
        }
    }

    private <T> Optional<T> query(Call<? extends T> query) {
        try {
            final var response = query.execute();
            if (response.code() == 404) {
                return Optional.empty();
            }
            if (!response.isSuccessful()) {
                throw new NugetException("Nuget server responded with status " + response.code());
            }
            return Optional.ofNullable(response.body());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON formatting error", e);
        } catch (IOException e) {
            throw new NugetException("Nuget is not reachable");
        }
    }
}
