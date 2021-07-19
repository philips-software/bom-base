/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Trust;
import pl.tlinkowski.annotation.basic.NullOr;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface NpmAPI {
    @GET("{project}/{version}")
    Call<ResponseJson> getDefinition(@Path("project") String project,
                                     @Path("version") String version);

    @SuppressWarnings("NotNullFieldNotInitialized")
    class ResponseJson implements PackageMetadata {
        @NullOr String name;
        @NullOr String description;
        @NullOr URI homepage;
        @NullOr JsonNode license;
        @NullOr JsonNode author;
        @NullOr JsonNode repository;
        DistJson dist;

        @Override
        public Trust trust(Field field) {
            return Trust.LIKELY;
        }

        @Override
        public Optional<String> getTitle() {
            return Optional.ofNullable(name);
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.ofNullable(description);
        }

        @Override
        public Optional<List<String>> getAuthors() {
            if (author == null) {
                return Optional.empty();
            }
            final var result = author.findValuesAsText("name");
            return Optional.of(result);
        }

        @Override
        public Optional<URI> getHomepage() {
            return Optional.ofNullable(homepage);
        }

        @Override
        public Optional<String> getDeclaredLicense() {
            return Optional.ofNullable(licenseOf(license));
        }

        @NullOr String licenseOf(@NullOr JsonNode node) {
            if (node == null) {
                return null;
            }
            if (node.isArray()) {
                return StreamSupport.stream(node.spliterator(), false)
                        .map(this::licenseOf)
                        .collect(Collectors.joining(" AND ")); // NPM provides insufficient info
            }
            if (node.isObject()) {
                return node.get("type").textValue();
            }
            return node.textValue();
        }

        @Override
        public Optional<String> getSourceLocation() {
            if (repository == null) {
                return Optional.empty();
            }
            if (repository.isObject()) {
                return Optional.ofNullable(repository.get("url").textValue());
            }
            return Optional.of(repository.textValue());
        }

        @Override
        public Optional<URI> getDownloadLocation() {
            return Optional.ofNullable(dist.tarball);
        }

        @Override
        public Optional<String> getSha1() {
            return Optional.ofNullable(dist.shasum);
        }
    }

    class DistJson {
        @NullOr URI tarball;
        @NullOr String shasum;
    }
}
