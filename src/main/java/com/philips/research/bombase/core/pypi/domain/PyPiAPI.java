/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Trust;
import pl.tlinkowski.annotation.basic.NullOr;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Python package management repository API.
 * See https://warehouse.readthedocs.io/api-reference/json.html#release
 */
public interface PyPiAPI {
    String SOURCE_FILE = "sdist";
    String BINARY_FILE = "bdist_wheel";

    @GET("pypi/{project}/{version}/json")
    Call<ResponseJson> getDefinition(@Path("project") String project, @Path("version") String version);

    class ResponseJson implements PackageMetadata {
        @NullOr String release = null;
        @SuppressWarnings("NotNullFieldNotInitialized")
        InfoJson info;
        List<FileJson> urls = new ArrayList<>();

        @Override
        public Trust trust(Field field) {
            return Trust.LIKELY;
        }

        @Override
        public Optional<String> getTitle() {
            return Optional.ofNullable(info.name);
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.ofNullable(info.summary);
        }

        @Override
        public Optional<List<String>> getAuthors() {
            return info.author != null ? Optional.of(List.of(info.author)) : Optional.empty();
        }

        @Override
        public Optional<URI> getHomepage() {
            return Optional.ofNullable(info.homePage);
        }

        @Override
        public Optional<String> getDeclaredLicense() {
            return Optional.ofNullable(info.license);
        }

        @Override
        public Optional<String> getSourceLocation() {
            return filesFor(SOURCE_FILE)
                    .filter(file -> file.url != null)
                    .map(file -> file.url)
                    .findAny();
        }

        @Override
        public Optional<URI> getDownloadLocation() {
            return filesFor(BINARY_FILE)
                    .filter(file -> file.url != null)
                    .map(file -> URI.create(file.url))
                    .findAny();
        }

        @Override
        public Optional<String> getSha256() {
            return filesFor(BINARY_FILE)
                    .filter(file -> file.digests != null && file.digests.sha256 != null)
                    .map(file -> file.digests.sha256)
                    .findAny();
        }

        private Stream<FileJson> filesFor(String packageType) {
            return urls.stream()
                    .filter(file -> packageType.equals(file.packagetype));
        }
    }

    class InfoJson {
        @NullOr String author;
        @NullOr String name;
        @NullOr String summary;
        @NullOr URI homePage;
        @NullOr String license;
    }

    class FileJson {
        @NullOr String packagetype;
        @NullOr DigestsJson digests;
        @NullOr String url;
    }

    class DigestsJson {
        @NullOr String sha256;
    }
}
