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

    private final Package pkg = new Package(PURL);
    private final PackageModifier modifier = new PackageModifier(pkg);

    @Test
    void tracksModifiedFields() {
        modifier.get(Field.SOURCE_LOCATION);
        modifier.update(Field.DOWNLOAD_LOCATION, null);
        modifier.update(Field.TITLE, VALUE);
        modifier.set(Field.DESCRIPTION, VALUE);

        assertThat(modifier.getModifiedFields()).containsExactlyInAnyOrder(Field.TITLE, Field.DESCRIPTION);
    }

    @Nested
    class ExistingField {
        private final Attribute attr = pkg.add(new Attribute(FIELD));

        @Test
        void getsFieldValue() {
            modifier.set(FIELD, VALUE);

            assertThat(modifier.get(FIELD)).contains(VALUE);
        }

        @Test
        void setsFieldValue() {
            modifier.set(FIELD, VALUE);

            assertThat(attr.getValue()).contains(VALUE);
        }

        @Test
        void updatesField() {
            modifier.update(FIELD, VALUE);

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
            modifier.set(FIELD, VALUE);

            assertThat(pkg.getAttributeFor(FIELD).orElseThrow().getValue()).contains(VALUE);
        }
    }

    @Nested
    class ExistingValue {
        final Attribute attribute = pkg.add(new Attribute(FIELD));

        @BeforeEach
        void beforeEach() {
            modifier.update(FIELD, VALUE);
        }

        @Test
        void ignoresNullValue() {
            modifier.update(FIELD, null);

            assertThat(attribute.getValue()).contains(VALUE);
        }

        @Test
        void overridesField() {
            modifier.set(FIELD, OTHER_VALUE);

            assertThat(attribute.getValue()).contains(OTHER_VALUE);
        }
    }
}
