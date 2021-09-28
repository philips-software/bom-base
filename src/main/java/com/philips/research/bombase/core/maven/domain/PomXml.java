/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven.domain;

import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Trust;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Maven package management repository metadata.
 *
 * @see <a href="https://search.maven.org/classic/#api">POM format</a>
 */
class PomXml implements PackageMetadata {
    @NullOr String name;
    @NullOr String description;
    @NullOr String url;
    @NullOr ReferenceXml organization;
    @NullOr List<ReferenceXml> licenses;
    @NullOr ReferenceXml scm;

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
        if (organization == null || organization.name == null) {
            return Optional.empty();
        }
        return Optional.of(List.of(organization.name));
    }

    @Override
    public Optional<URI> getHomepage() {
        try {
            //noinspection ConstantConditions
            return Optional.ofNullable(URI.create(url));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getDeclaredLicense() {
        if (licenses == null) {
            return Optional.empty();
        }
        final var license = licenses.stream()
                .map(this::licenseOf)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));
        return Optional.ofNullable(!license.isEmpty() ? license : null);
    }

    private @NullOr String licenseOf(ReferenceXml xml) {
        return (xml.url != null) ? xml.url : xml.name;
    }

    @Override
    public Optional<String> getSourceLocation() {
        if (scm == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(scm.url);
    }

    @Override
    public Optional<URI> getDownloadLocation() {
        //TODO Obtain from repo
        return Optional.empty();
    }

    @Override
    public Optional<String> getSha1() {
        //TODO Obtain from repo?
        return Optional.empty();
    }

    static class ReferenceXml {
        @NullOr String name;
        @NullOr String url;
    }
}

