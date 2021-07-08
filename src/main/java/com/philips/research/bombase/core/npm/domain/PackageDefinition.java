/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm.domain;

import java.net.URI;
import java.util.List;
import java.util.Optional;

interface PackageDefinition {
    Optional<String> getName();

    Optional<String> getDescription();

    Optional<List<String>> getAuthors();

    Optional<URI> getHomepage();

    Optional<String> getLicense();

    Optional<String> getSourceUrl();

    Optional<URI> getDownloadUrl();

    Optional<String> getSha();
}
