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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NpmAPI {
    @GET("{project}/{version}")
    Call<ResponseJson> getDefinition(@Path("project") String project, @Path("version") String version);

    class ResponseJson implements ReleaseDefinition {
        @NullOr String release = null;
        @SuppressWarnings("NotNullFieldNotInitialized")
        InfoJson info;
        Map<String, List<FileJson>> dist = new HashMap<>();

        @Override
        public Optional<String> getName() {
            return Optional.ofNullable(info.name);
        }

        @Override
        public Optional<String> getSummary() {
            // TODO: ugly mapping between Description and Summary
            return Optional.ofNullable(info.description);
        }

        @Override
        public Optional<URI> getHomepage() {
            return Optional.ofNullable(info.homePage);
        }

        @Override
        public Optional<String> getLicense() {
            return Optional.ofNullable(info.license);
        }

        @Override
        public Optional<URI> getSourceUrl() {
            return Optional.ofNullable(info.dist.tarball);
        }
    }

    class InfoJson {
        @NullOr String name;
        @NullOr String description;
        @NullOr URI homePage;
        @NullOr String license;
        @NullOr FileJson dist;
    }

    class FileJson {
        @NullOr URI tarball;
    }
}
