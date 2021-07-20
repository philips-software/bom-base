/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PomXmlTest {
    private PomXml.ReferenceXml reference(String name, @NullOr URI url) {
        final var xml = new PomXml.ReferenceXml();
        xml.name = name;
        if (url != null) {
            xml.url = url.toASCIIString();
        }
        return xml;
    }

    @Nested
    class Licenses {
        private static final String LICENSE1 = "License 1";
        private static final String LICENSE2 = "License 2";
        private static final String LICENSE_URL = "https://example.com/license";

        @Test
        void combinesListedLicenses() {
            final var xml = new PomXml();
            xml.licenses = List.of(reference(LICENSE1, null), reference(LICENSE2, null));

            assertThat(xml.getDeclaredLicense()).contains(LICENSE1 + " AND " + LICENSE2);
        }

        @Test
        void prefersUrlOverName() {
            final var xml = new PomXml();
            xml.licenses = List.of(reference(LICENSE1, URI.create(LICENSE_URL)));

            assertThat(xml.getDeclaredLicense()).contains(LICENSE_URL);
        }

        @Test
        void ignoresEmptyLicenses() {
            final var xml = new PomXml();
            xml.licenses = List.of(new PomXml.ReferenceXml());

            assertThat(xml.getDeclaredLicense()).isEmpty();
        }
    }
}
