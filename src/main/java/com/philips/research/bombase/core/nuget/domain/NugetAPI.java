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

    @SuppressWarnings("NotNullFieldNotInitialized")
    class License {
        String type;
        String text;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class Repository {
        @NullOr String type;
        @NullOr String url;
        @NullOr String commit;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class Group {
        String targetFramework;
        List<Dependency> dependency;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class Dependency {
        String id;
        String version;
        String exclude;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class Dependencies {
        List<Group> group;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class Metadata {
        String id;
        String version;
        String title;
        String authors;
        boolean requireLicenseAcceptance;
        @NullOr License license;
        @NullOr String licenseUrl;
        String icon;
        @NullOr String projectUrl;
        String description;
        @NullOr String copyright;
        @NullOr String tags;
        @NullOr Repository repository;
        Dependencies dependencies;
        String minClientVersion;
        String text;
    }

    class XmlNugetSpecPackage {
        Metadata metadata;
        String xmlns;
        String text;

        Optional<String> getRepositoryURL() {
            if (metadata.repository == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(metadata.repository.url);
        }
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class CatalogResponseJson {
        @JsonProperty("@id")
        @NullOr String id;
        @JsonProperty("@type")
        @NullOr List<String> type;
        @JsonProperty("catalogEntry")
        @NullOr String catalogEntry;
        @JsonProperty("packageContent")
        @NullOr String packageContent;
        @NullOr boolean listed;
        @NullOr Date published;
        @NullOr String registration;

        // Extracts the catalog entry from the Catalog Response.
        Optional<String> extractCatalogEntryPath(NugetAPI.CatalogResponseJson cat, URI baseURI) {
            return Optional.ofNullable(cat.catalogEntry).map(temp -> {
                final var splitUrl = temp.split(baseURI.toASCIIString());
                if (splitUrl.length == 2) {
                    return splitUrl[1];
                }
                return null;
            });
        }
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class ResponseJson implements PackageMetadata {
        @NullOr String downloadLocation;
        @JsonProperty("title")
        String name;
        String description;
        @JsonProperty("projectUrl")
        @NullOr URI homepage;
        @JsonProperty("licenseExpression")
        @NullOr String licenseExpression;
        @JsonProperty("licenseUrl")
        @NullOr String licenseUrl;
        @JsonProperty("authors")
        String author;
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
            final var expression = (licenseUrl != null) ? licenseUrl : licenseExpression;
            return Optional.ofNullable(expression);
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
            return Optional.of(URI.create(downloadLocation));
        }

        @Override
        public Optional<String> getSha512() {
            return Optional.ofNullable(packageHash);
        }
    }
}
