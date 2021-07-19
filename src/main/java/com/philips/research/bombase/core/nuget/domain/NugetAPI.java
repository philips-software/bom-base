/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.nuget.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Trust;
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

/**
 * NuGet package management repository metadata.
 *
 * @see <a href="https://api.nuget.org/v3/index.json">For more information.</a>
 * Combination of JSON and XML is used in the NuGet API.
 * More information can also be found <a href="https://docs.microsoft.com/en-us/nuget/api/overview">here</a>
 */
public interface NugetAPI {
    @GET("registration5-semver1/{project}/{version}.json")
    Call<CatalogResponseJson> getCatalogEntry(@Path("project") String project,
                                              @Path("version") String version);

    @GET("{catalogEntryUrl}")
    Call<ResponseJson> getDefinition(@Path("catalogEntryUrl") String catalogEntryUrl);

    @GET("{nugetSpecUrl}")
    Call<XmlNugetSpecPackage> getNugetSpec(@Path("nugetSpecUrl") String nugetSpecUrl);

    class License {
        public String type;
        public String text;
    }

    class Repository {
        public String type;
        public String url;
        public String commit;
    }

    class Group {
        public String targetFramework;
        public List<Dependency> dependency;
    }

    class Dependency {
        public String id;
        public String version;
        public String exclude;
    }

    class Dependencies {
        public List<Group> group;
    }

    class Metadata {
        public String id;
        public String version;
        public String title;
        public String authors;
        public boolean requireLicenseAcceptance;
        public License license;
        public String licenseUrl;
        public String icon;
        public String projectUrl;
        public String description;
        public String copyright;
        public String tags;
        public Repository repository;
        public Dependencies dependencies;
        public String minClientVersion;
        public String text;
    }

    class XmlNugetSpecPackage {
        public Metadata metadata;
        public String xmlns;
        public String text;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class CatalogResponseJson {
        @JsonProperty("@id")
        @NullOr
        public String id;
        @JsonProperty("@type")
        @NullOr
        public List<String> type;
        @JsonProperty("catalogEntry")
        @NullOr
        public String catalogEntry;
        @JsonProperty("packageContent")
        @NullOr
        public String packageContent;
        @NullOr
        public boolean listed;
        @NullOr
        public Date published;
        @NullOr
        public String registration;

    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class ResponseJson implements PackageMetadata {
        @NullOr String downloadLocation;

        @JsonProperty("title")
        @NullOr String name;
        @NullOr String description;
        @JsonProperty("projectUrl")
        @NullOr URI homepage;
        @JsonProperty("licenseExpression")
        @NullOr String licenseExpression;
        @JsonProperty("licenseUrl")
        @NullOr String licenseUrl;
        @JsonProperty("authors")
        @NullOr String author;
        @NullOr String sourceUrl;
        @JsonProperty("packageHash")
        @NullOr String packageHash;

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
            if (licenseUrl != null) {
                return Optional.ofNullable(licenseUrl);
            }
            return Optional.ofNullable(licenseExpression);
        }

        @Override
        public Optional<String> getSourceLocation() {
            if (sourceUrl == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(sourceUrl);
        }

        @Override
        public Optional<URI> getDownloadLocation() {
            if (downloadLocation == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(URI.create(downloadLocation));
        }

        @Override
        public Optional<String> getSha512() {
            return Optional.ofNullable(packageHash);
        }
    }

}
