/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackageTest {
    private static final PackageURL PURL = toPurl("pkg:type/name@version");
    private static final Field FIELD = Field.TITLE;

    private final Package pkg = new Package(PURL);
    private final Instant now = Instant.now();

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    void createsInstance() {
        assertThat(pkg.getPurl()).isEqualTo(PURL);
        assertThat(pkg.getLastUpdated()).isBetween(now.minusMillis(1000), now);
        assertThat(pkg.getAttributes()).isEmpty();
        assertThat(pkg.getAttributeFor(FIELD)).isEmpty();
    }

    @Test
    void addsAttribute() {
        final var attribute = new Attribute(FIELD);

        pkg.add(attribute);

        assertThat(pkg.getAttributes()).contains(attribute);
        assertThat(pkg.getAttributeFor(FIELD)).contains(attribute);
    }

    @Test
    void throws_addDuplicateAttribute() {
        pkg.add(new Attribute(FIELD));

        assertThatThrownBy(() -> pkg.add(new Attribute(FIELD)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }
}
