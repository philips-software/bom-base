/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import java.net.URI;
import java.util.Optional;

public class PackageMetadata {
    private URI sourceLocation;

    public Optional<URI> getSourceLocation() {
        return Optional.ofNullable(sourceLocation);
    }

    public PackageMetadata setSourceLocation(URI location) {
        this.sourceLocation = location;
        return this;
    }
}
