/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.philips.research.bombase.PackageUrl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackageTest {
    private static final PackageUrl PURL = new PackageUrl("pkg:type/name@version");
    private static final Field FIELD = Field.TITLE;

    final Package pkg = new Package(PURL);

    @Test
    void createsInstance() {
        assertThat(pkg.getPurl()).isEqualTo(PURL);
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
