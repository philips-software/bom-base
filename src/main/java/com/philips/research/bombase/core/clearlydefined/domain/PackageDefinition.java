/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import java.net.URI;
import java.util.List;
import java.util.Optional;

interface PackageDefinition {
    boolean isValid();

    int getDescribedScore();

    int getLicensedScore();

    Optional<String> getTitle();

    Optional<URI> getDownloadLocation();

    Optional<String> getSourceLocation();

    Optional<URI> getHomepage();

    Optional<List<String>> getAuthors();

    Optional<String> getDeclaredLicense();

    Optional<List<String>> getDetectedLicenses();

    Optional<String> getSha1();

    Optional<String> getSha256();
}
