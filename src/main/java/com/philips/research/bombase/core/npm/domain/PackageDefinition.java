/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm.domain;

import java.net.URI;
import java.util.Optional;

interface PackageDefinition {
    Optional<String> getName();

    Optional<String> getDescription();

    Optional<String> getAuthor();

    Optional<URI> getHomepage();

    Optional<String> getLicense();

    Optional<URI> getSourceUrl();

    Optional<URI> getDownloadUrl();

    Optional<String> getSha();
}
