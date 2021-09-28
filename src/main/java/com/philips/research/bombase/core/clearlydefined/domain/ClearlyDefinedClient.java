/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.clearlydefined.ClearlyDefinedException;
import com.philips.research.bombase.core.meta.PackageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Component
class ClearlyDefinedClient {
    private static final Logger LOG = LoggerFactory.getLogger(ClearlyDefinedClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
    private static final Map<String, String> TYPE_MAPPING = Map.of( // Default is 1:1 mapping from type
            "cocoapods", "pod",
            "cargo", "crate",
            "github", "git");
    private static final Map<String, String> PROVIDER_MAPPING = Map.of( // Default is 1:1 mapping from ClearlyDefined type
            "crate", "cratesio",
            "deb", "debian",
            "git", "github",
            "maven", "mavencentral",
            "npm", "npmjs",
            "gem", "rubygems");

    private final ClearlyDefinedAPI rest;

    ClearlyDefinedClient() {
        this(URI.create("https://api.clearlydefined.io"));
    }

    ClearlyDefinedClient(URI uri) {
        final var retrofit = new Retrofit.Builder()
                .baseUrl(uri.toASCIIString())
                .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                .build();
        rest = retrofit.create(ClearlyDefinedAPI.class);
    }

    Optional<PackageMetadata> getPackageMetadata(PackageURL purl) {
        final var type = TYPE_MAPPING.getOrDefault(purl.getType().toLowerCase(), purl.getType());
        final var provider = PROVIDER_MAPPING.getOrDefault(type.toLowerCase(), type);
        final var namespace = purl.getNamespace();
        final var ns = (namespace == null || namespace.isEmpty()) ? "-" : namespace;
        return query(rest.getDefinition(type, provider, ns, purl.getName(), purl.getVersion()))
                .filter(ClearlyDefinedAPI.ResponseJson::isValid)
                .map(meta -> meta);
    }

    private <T> Optional<T> query(Call<? extends T> query) {
        try {
            final var response = query.execute();
            if (!response.isSuccessful()) {
                LOG.info("Query={}", response.raw().request().url());
                throw new ClearlyDefinedException("ClearlyDefined server responded with status " + response.code());
            }
            return Optional.ofNullable(response.body());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON formatting error", e);
        } catch (IOException e) {
            throw new ClearlyDefinedException("ClearlyDefined is not reachable");
        }
    }
}
