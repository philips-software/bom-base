/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.Origin;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PackageTest {
    private static final PackageUrl PURL = new PackageUrl("pkg:type/name@version");
    private static final Field FIELD = Field.TITLE;
    private static final String VALUE = "Value";
    private static final Origin ORIGIN = Origin.API;

    final Package pkg = new Package(PURL);

    @Test
    void createsInstance() {
        assertThat(pkg.getPurl()).isEqualTo(PURL);
        assertThat(pkg.getValues()).isEmpty();
    }

    @Test
    void unknownFieldsAreEmpty() {
        assertThat(pkg.getValue(FIELD)).isEmpty();
    }

    @Test
    void storesValueInNewField() {
        pkg.setValue(ORIGIN, FIELD, VALUE);

        assertThat(pkg.getValue(FIELD)).contains(VALUE);
        assertThat(pkg.getValues()).containsEntry(FIELD, VALUE);
    }

    @Test
    void updatesExistingField() {
        pkg.setValue(ORIGIN, FIELD, "Old value");
        pkg.setValue(ORIGIN , FIELD, VALUE);

        assertThat(pkg.getValue(FIELD)).contains(VALUE);
        assertThat(pkg.getValues()).containsEntry(FIELD, VALUE);
    }

    @Test
    void updatesMultipleFields() {
        pkg.setValues(ORIGIN, Map.of(FIELD, VALUE));

        assertThat(pkg.getValue(FIELD)).contains(VALUE);
    }
}
