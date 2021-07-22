/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Trust;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface PackageMetadata {
    Trust trust(Field field);

    Optional<String> getTitle();

    Optional<String> getDescription();

    Optional<List<String>> getAuthors();

    Optional<URI> getHomepage();

    Optional<String> getSourceLocation();

    default Optional<String> getDeclaredLicense() {
        return Optional.empty();
    }

    default Optional<List<String>> getDetectedLicenses() {
        return Optional.empty();
    }

    Optional<URI> getDownloadLocation();

    default Optional<String> getSha1() {
        return Optional.empty();
    }

    default Optional<String> getSha256() {
        return Optional.empty();
    }

    default Optional<String> getSha512() {
        return Optional.empty();
    }

}
