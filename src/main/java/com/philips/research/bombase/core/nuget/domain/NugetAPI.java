/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.nuget.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.meta.registry.Field;
import pl.tlinkowski.annotation.basic.NullOr;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface NugetAPI {
    // TODO Is this a proper value?
    int NUGET_SCORE = 80;

    @GET("registration5-semver1/{project}/{version}.json")
    Call<CatalogResponseJson> getCatalogEntry(@Path("project") String project,
                                              @Path("version") String version);
    @GET("{catalogEntryUrl}")
    Call<ResponseJson> getDefinition(@Path("catalogEntryUrl") String catalogEntryUrl);

    @SuppressWarnings("NotNullFieldNotInitialized")
    class CatalogResponseJson {
        @JsonProperty("@id")
        public String id;
        @JsonProperty("@type")
        public List<String> type;
        @JsonProperty("catalogEntry")
        public String catalogEntry;
        public boolean listed;
        public Date published;
        public String registration;

    }
    @SuppressWarnings("NotNullFieldNotInitialized")
    class ResponseJson implements PackageMetadata {
        @JsonProperty("title")
        @NullOr String name;
        @NullOr String description;
        @JsonProperty("projectUrl")
        @NullOr URI homepage;
        @JsonProperty("licenseExpression")
        @NullOr String license;
        @JsonProperty("authors")
        @NullOr String author;
        @NullOr JsonNode repository;
        @JsonProperty("packageHash")
        @NullOr String packageHash;

        @Override
        public int score(Field field) {
            return NUGET_SCORE;
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
            return Optional.of(Stream.of(author.split(","))
                    .map(String::stripLeading)
                    .collect(Collectors.toList()));
        }

        @Override
        public Optional<URI> getHomepage() {
            return Optional.ofNullable(homepage);
        }

        @Override
        public Optional<String> getDeclaredLicense() {
            return Optional.ofNullable(license);
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
            return Optional.ofNullable(null);
        }

        @Override
        // TODO: This actually is a SHA256
        public Optional<String> getSha1() {
            return Optional.ofNullable(packageHash);
        }
    }

}
