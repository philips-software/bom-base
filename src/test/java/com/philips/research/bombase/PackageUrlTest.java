/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackageUrlTest {
    private static final String TYPE = "type";
    private static final String NAMESPACE = "Name/space %";
    private static final String NAME = "Name/ %";
    private static final String VERSION = "1%2+3@4";
    private static final String PURL_STRING = "pkg:type/Name%2fspace+%25/Name%2f+%25@1%252%2B3%404";
    private static final String PURL_SHORT_STRING = "pkg:type/Name%2f+%25@1%252%2B3%404";
    private static final PackageUrl PURL = new PackageUrl(TYPE, NAMESPACE, NAME, VERSION);
    private static final PackageUrl SHORT_PURL = new PackageUrl(TYPE, null, NAME, VERSION);

    @Test
    void createsInstanceFromElements() {
        assertThat(PURL.getType()).isEqualTo(TYPE);
        assertThat(PURL.getNamespace()).contains(NAMESPACE);
        assertThat(PURL.getName()).isEqualTo(NAME);
        assertThat(PURL.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void parsesInstanceFromEncodedString() {
        final var purl = new PackageUrl(PURL_STRING);

        assertThat(purl).isEqualTo(PURL);
    }

    @Test
    void parsesInstanceFromStringWithoutNamespace() {
        final var purl = new PackageUrl(PURL_SHORT_STRING);

        assertThat(purl).isEqualTo(SHORT_PURL);
    }

    @Test
    void parsesInstanceIgnoringModifiers() {
        final var purl = new PackageUrl(PURL_STRING + "?modifier");

        assertThat(purl).isEqualTo(PURL);
    }

    @Test
    void parsesInstanceIgnoringSubPath() {
        final var purl = new PackageUrl(PURL_STRING + "#subpath");

        assertThat(purl).isEqualTo(PURL);
    }

    @Test
    void parsesInstanceFromURI() {
        final var purl = new PackageUrl(URI.create(PURL_STRING));

        assertThat(purl).isEqualTo(PURL);
    }

    @Test
    void parsesInstanceWithoutScheme() {
        final var purl = new PackageUrl("type/name@version");

        assertThat(purl.getName()).isEqualTo("name");
    }

    @Test
    void throws_parsePurlWithWrongScheme() {
        assertThatThrownBy(() -> new PackageUrl("wrong:type/name@version"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scheme");
    }

    @Test
    void throws_parsePurlWithoutType() {
        assertThatThrownBy(() -> new PackageUrl("pkg:name@version"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("type");
    }

    @Test
    void throws_remainingNameParts() {
        assertThatThrownBy(() -> new PackageUrl("pkg:type/namespace/name/extra@version"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name parts");
    }

    @Test
    void throws_parsePurlWithoutVersion() {
        assertThatThrownBy(() -> new PackageUrl("pkg:type/name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("version");
    }

    @Test
    void createsEncodedUri() {
        final var uri = PURL.toUri();

        assertThat(new PackageUrl(uri)).isEqualTo(PURL);
    }

    @Test
    void createsUriWithoutNamespace() {
        final var uri = SHORT_PURL.toUri();

        assertThat(new PackageUrl(uri)).isEqualTo(SHORT_PURL);
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(PackageUrl.class)
                .withNonnullFields("type", "name", "version")
                .verify();
    }

    @Test
    void printsUri() {
        assertThat(PURL.toString()).isEqualTo(PURL.toUri().toASCIIString());
    }
}
