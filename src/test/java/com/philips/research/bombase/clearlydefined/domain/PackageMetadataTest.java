/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PackageMetadataTest {
    private static final URI VALID_URI = URI.create("http://example.com");
    private static final String VALID_STRING = "Valid";

    final PackageMetadata meta = new PackageMetadata();

    @Test
    void createsInstance() {
        assertThat(meta.getHomePage()).isEmpty();
        assertThat(meta.getSourceLocation()).isEmpty();
        assertThat(meta.getAttribution()).isEmpty();
    }

    @Test
    void updatesHomePage() {
        meta.setHomePage(VALID_URI);

        assertThat(meta.getHomePage()).contains(VALID_URI);
    }

    @Test
    void updatesAttribution() {
        meta.setAttribution(List.of(VALID_STRING));

        assertThat(meta.getAttribution()).isEqualTo(List.of(VALID_STRING));
    }

    @Test
    void updatesDownloadLocation() {
        meta.setDownloadLocation(VALID_URI);

        assertThat(meta.getDownloadLocation()).contains(VALID_URI);
    }

    @Test
    void updatesSourceLocation() {
        meta.setSourceLocation(VALID_URI);

        assertThat(meta.getSourceLocation()).contains(VALID_URI);
    }

    @Test
    void updatesDeclaredLicense() {
        meta.setDeclaredLicense(VALID_STRING);

        assertThat(meta.getDeclaredLicense()).contains(VALID_STRING);
    }

    @Test
    void updatesDetectedLicense() {
        meta.setDetectedLicense(VALID_STRING);

        assertThat(meta.getDetectedLicense()).contains(VALID_STRING);
    }

    @Test
    void updatesSha1() {
        meta.setSha1(VALID_STRING);

        assertThat(meta.getSha1()).contains(VALID_STRING);
    }

    @Test
    void updatesSha256 () {
        meta.setSha256(VALID_STRING);

        assertThat(meta.getSha256()).contains(VALID_STRING);
    }
}
