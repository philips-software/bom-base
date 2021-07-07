/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm.domain;

import pl.tlinkowski.annotation.basic.NullOr;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.net.URI;
import java.util.Optional;

public interface NpmAPI {
    @GET("{project}/{version}")
    Call<ResponseJson> getDefinition(@Path("project") String project,
                                     @Path("version") String version);

    @SuppressWarnings("NotNullFieldNotInitialized")
    class ResponseJson implements PackageDefinition {
        @NullOr String name;
        @NullOr String description;
        @NullOr URI homePage;
        @NullOr String license;
        @NullOr PersonJson author;
        @NullOr RepositoryJson repository;
        DistJson dist;

        @Override
        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.ofNullable(description);
        }

        @Override
        public Optional<String> getAuthor() {
            return Optional.ofNullable(author != null ? author.name : null);
        }

        @Override
        public Optional<URI> getHomepage() {
            return Optional.ofNullable(homePage);
        }

        @Override
        public Optional<String> getLicense() {
            return Optional.ofNullable(license);
        }

        @Override
        public Optional<URI> getSourceUrl() {
            return Optional.ofNullable(repository != null ? repository.url : null);
        }

        @Override
        public Optional<URI> getDownloadUrl() {
            return Optional.ofNullable(dist.tarball);
        }

        @Override
        public Optional<String> getSha() {
            return Optional.ofNullable(dist.shasum);
        }
    }

    class RepositoryJson {
        @NullOr URI url;
    }

    class PersonJson {
        @NullOr String name;
    }

    class DistJson {
        @NullOr URI tarball;
        @NullOr String shasum;
    }
}
