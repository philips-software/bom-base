/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.philips.research.bombase.PackageUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackageModifierTest {
    private static final PackageUrl PURL = new PackageUrl("pkg:type/ns/name@version");
    private static final Field FIELD = Field.TITLE;
    private static final String VALUE = "Value";
    private static final String OTHER_VALUE = "Other value";
    private static final int SCORE = 50;

    private final Package pkg = new Package(PURL);
    private final PackageModifier modifier = new PackageModifier(pkg);

    @Test
    void tracksModifiedFields() {
        //TODO Do these tests cover everything?
        modifier.get(Field.SOURCE_LOCATION);
        modifier.update(Field.DOWNLOAD_LOCATION, SCORE, null);
        modifier.update(Field.TITLE, SCORE, VALUE);
        modifier.update(Field.DESCRIPTION, 100, VALUE);

        assertThat(modifier.getModifiedFields()).containsExactlyInAnyOrder(Field.TITLE, Field.DESCRIPTION);
    }

    @Nested
    class ExistingField {
        private final Attribute attr = pkg.add(new Attribute(FIELD));

        @Test
        void providesFieldValue() {
            modifier.update(FIELD, SCORE, VALUE);

            assertThat(modifier.get(FIELD)).contains(VALUE);
        }
    }

    @Nested
    class NewField {
        @Test
        void noValueForUndefinedField() {
            assertThat(modifier.get(Field.ATTRIBUTION)).isEmpty();
        }

        @Test
        void createsNewAttribute() {
            modifier.update(FIELD, SCORE, VALUE);

            assertThat(pkg.getAttributeFor(FIELD).orElseThrow().getValue()).contains(VALUE);
        }
    }

    @Nested
    class ExistingValue {
        final Attribute attribute = pkg.add(new Attribute(FIELD));

        @BeforeEach
        void beforeEach() {
            modifier.update(FIELD, SCORE, VALUE);
        }

        @Test
        void ignoresNullValue() {
            modifier.update(FIELD, SCORE, null);

            assertThat(attribute.getValue()).contains(VALUE);
        }

        @Test
        void overridesField() {
            modifier.update(FIELD, SCORE, OTHER_VALUE);

            assertThat(attribute.getValue()).contains(OTHER_VALUE);
        }
    }
}
