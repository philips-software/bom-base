/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.meta.Field;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackageTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "1.2.3";
    private static final Field FIELD = Field.TITLE;
    private static final Double VALUE = 1.234;

    final Package pkg = new Package(TYPE, NAME, VERSION);

    @BeforeAll
    static void BeforeAll() {
        Package.register(FIELD, Number.class);
    }

    @Test
    void createsInstance() {
        assertThat(pkg.getType()).isEqualTo(TYPE);
        assertThat(pkg.getName()).isEqualTo(NAME);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void reportsPackageProperties() {
        assertThat(pkg.getValues()).isEqualTo(Map.of(Field.TYPE, TYPE, Field.NAME, NAME, Field.VERSION, VERSION));
    }

    @Test
    void throws_updateOfPackageProperties() {
        assertThatThrownBy(() -> pkg.setValue(Field.TYPE, "Other")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> pkg.setValue(Field.NAME, "Other")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> pkg.setValue(Field.VERSION, "Other")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unknownFieldsAreEmpty() {
        assertThat(pkg.getValue(FIELD)).isEmpty();
    }

    @Test
    void storesValueInNewField() {
        pkg.setValue(FIELD, VALUE);

        assertThat(pkg.getValue(FIELD)).contains(VALUE);
        assertThat(pkg.getValues()).containsEntry(FIELD, VALUE);
    }

    @Test
    void updatesExistingField() {
        pkg.setValue(FIELD, 666.666);
        pkg.setValue(FIELD, VALUE);

        assertThat(pkg.getValue(FIELD)).contains(VALUE);
        assertThat(pkg.getValues()).containsEntry(FIELD, VALUE);
    }

    @Test
    void updatesMultipleFields() {
        pkg.setValues(Map.of(FIELD, VALUE));

        assertThat(pkg.getValue(FIELD)).contains(VALUE);
    }

    @Test
    void throws_setIncompatibleValue() {
        assertThatThrownBy(() -> pkg.setValue(FIELD, "Not a number"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("can not be assigned");
    }

    @Test
    void createsInstanceFromPackageUri() {
        final var purl = URI.create(String.format("pkg:%s/%s@%s", TYPE, NAME, VERSION));
        final var pkg = Package.from(purl);

        assertThat(pkg.getType()).isEqualTo(TYPE);
        assertThat(pkg.getName()).isEqualTo(NAME);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }
}
