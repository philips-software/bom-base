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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.philips.research.bombase.core.clearlydefined.ClearlyDefinedException;
import pl.tlinkowski.annotation.basic.NullOr;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class ClearlyDefinedClient {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE)
            .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

    private final ClearlyDefinedAPI rest;

    public ClearlyDefinedClient() {
        this(URI.create("https://api.clearlydefined.io"));
    }

    ClearlyDefinedClient(URI uri) {
        final var retrofit = new Retrofit.Builder()
                .baseUrl(uri.toASCIIString())
                .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                .build();
        rest = retrofit.create(ClearlyDefinedAPI.class);
    }

    public Optional<PackageDefinition> getPackageDefinition(String type, String provider, @NullOr String namespace, String name, String revision) {
        final var ns = (namespace == null || namespace.isEmpty()) ? "-" : namespace;
        return query(rest.getDefinition(type, provider, ns, name, revision))
                .map(def -> (PackageDefinition) def)
                .filter(PackageDefinition::isValid);
    }

    private <T> Optional<T> query(Call<? extends T> query) {
        try {
            final var response = query.execute();
            if (!response.isSuccessful()) {
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
