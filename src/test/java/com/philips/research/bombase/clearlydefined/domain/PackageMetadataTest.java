/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class PackageMetadataTest {
    private static final URI LOCATION = URI.create("http://example.com/location");

    final PackageMetadata meta = new PackageMetadata();

    @Test
    void createsInstance() {
        assertThat(meta.getSourceLocation()).isEmpty();
    }

    @Test
    void updatesSourceLocation() {
        meta.setSourceLocation(LOCATION);

        assertThat(meta.getSourceLocation()).contains(LOCATION);
    }
}
