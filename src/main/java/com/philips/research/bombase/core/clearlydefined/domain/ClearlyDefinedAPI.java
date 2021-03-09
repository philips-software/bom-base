/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import pl.tlinkowski.annotation.basic.NullOr;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public interface ClearlyDefinedAPI {
    @GET("definitions/{type}/{provider}/{namespace}/{name}/{revision}")
    Call<ResponseJson> getDefinition(@Path("type") String type, @Path("provider") String provider, @Path("namespace") String namespace,
                                     @Path("name") String name, @Path("revision") String revision);

    @SuppressWarnings("NotNullFieldNotInitialized")
    class ResponseJson implements PackageDefinition {
        DescribedJson described;
        LicensedJson licensed;

        @Override
        public Optional<URI> getDownloadLocation() {
            return described.getDownloadLocation();
        }

        @Override
        public Optional<URI> getSourceLocation() {
            return described.getSourceLocation();
        }

        @Override
        public Optional<URI> getHomepage() {
            return described.getHomepage();
        }

        @Override
        public List<String> getAuthors() {
            return licensed.getAttribution();
        }

        @Override
        public Optional<String> getDeclaredLicense() {
            return licensed.getDeclaredLicense();
        }

        @Override
        public List<String> getDetectedLicenses() {
            return licensed.getDetectedLicenses();
        }

        @Override
        public Optional<String> getSha1() {
            return described.getSha1();
        }

        @Override
        public Optional<String> getSha256() {
            return described.getSha256();
        }
    }

    class DescribedJson {
        @NullOr SourceLocationJson sourceLocation;
        List<UrlJson> urls = new ArrayList<>();
        List<HashJson> hashes = new ArrayList<>();
        @NullOr URI projectWebsite;

        Optional<URI> getDownloadLocation() {
            return urls.stream()
                    .filter(json -> json.download != null)
                    .findFirst()
                    .map(json -> json.download);
        }

        Optional<URI> getSourceLocation() {
            return Optional.ofNullable((sourceLocation != null && sourceLocation.url != null) ? sourceLocation.url : null);
        }

        Optional<URI> getHomepage() {
            return Optional.ofNullable(projectWebsite);
        }

        Optional<String> getSha1() {
            return firstSha(sha -> sha.sha1);
        }

        Optional<String> getSha256() {
            return firstSha(sha -> sha.sha256);
        }

        private Optional<String> firstSha(Function<HashJson, @NullOr String> which) {
            return hashes.stream()
                    .map(which)
                    .filter(Objects::nonNull)
                    .findFirst();
        }
    }

    class SourceLocationJson {
        @NullOr URI url;
    }

    class UrlJson {
        @NullOr URI download;
    }

    class HashJson {
        @NullOr String sha1;
        @NullOr String sha256;
    }

    class LicensedJson {
        @NullOr String declared;
        List<FacetsJson> facets = new ArrayList<>();

        Optional<String> getDeclaredLicense() {
            if ("NOASSERTION".equals(declared)) {
                return Optional.empty();
            }
            return Optional.ofNullable(declared);
        }

        List<String> getDetectedLicenses() {
            //noinspection ConstantConditions
            return listFromFirstCoreFacet(f -> f.core.getExpressions());
        }

        List<String> getAttribution() {
            //noinspection ConstantConditions
            return listFromFirstCoreFacet(f -> f.core.getAttribution());
        }

        private List<String> listFromFirstCoreFacet(Function<@NullOr FacetsJson, List<String>> accessor) {
            return facets.stream()
                    .filter(f -> f.core != null)
                    .findFirst()
                    .map(accessor)
                    .orElse(List.of());
        }
    }

    class FacetsJson {
        @NullOr FacetJson core;
    }

    class FacetJson {
        @NullOr AttributionJson attribution;
        @NullOr DiscoveredJson discovered;

        List<String> getAttribution() {
            return (attribution != null) ? attribution.parties : List.of();
        }

        List<String> getExpressions() {
            return (discovered != null) ? discovered.expressions : List.of();
        }
    }

    class AttributionJson {
        List<String> parties = new ArrayList<>();
    }

    class DiscoveredJson {
        List<String> expressions = new ArrayList<>();
    }
}
