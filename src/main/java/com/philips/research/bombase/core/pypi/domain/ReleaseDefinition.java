/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import java.net.URI;
import java.util.Optional;

interface ReleaseDefinition {
    Optional<String> getSummary();

    Optional<URI> getHomepage();

    Optional<String> getLicense();

    Optional<URI> getSourceUrl();
}
