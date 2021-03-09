/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.philips.research.bombase.core.meta.MetaException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeTest {
    private static final String VALUE = "Value";
    private static final Field FIELD = Field.TITLE;

    private final Instant start = Instant.now();
    private final Attribute field = new Attribute(FIELD);

    @Test
    void createsInstance() {
        assertThat(field.getField()).isEqualTo(FIELD);
        assertThat(field.getValue()).isEmpty();
        assertThat(field.getTimestamp()).isBetween(start, Instant.now());
    }

    @Test
    void setsValue() {
        field.setValue(VALUE);

        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getTimestamp()).isBetween(start, Instant.now());
    }

    @Test
    void throws_ValueOfWrongType() {
        assertThatThrownBy(() -> field.setValue(666))
                .isInstanceOf(MetaException.class)
                .hasMessageContaining("cannot hold");
    }

    @Test
    void ignoresNullValue() {
        field.setValue(VALUE);
        field.setValue(null);

        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getContested()).containsExactly(VALUE);
    }

    @Test
    void ignoresSetValue_overridden() {
        field.setValue("Other value");
        field.override(VALUE);
        final var timestamp = field.getTimestamp();
        field.setValue("Other value");

        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void listsContestingValues() {
        final var otherValue = "Other";
        field.setValue(VALUE);
        field.setValue(VALUE);
        field.setValue(otherValue);

        assertThat(field.getContested()).containsExactlyInAnyOrder(VALUE, otherValue);
    }

    @Test
    void overrideClearsContestingValues() {
        field.setValue("Some");
        field.override(VALUE);
        field.setValue("Other");

        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getContested()).isEmpty();
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Attribute.class)
                .withOnlyTheseFields("field")
                .withNonnullFields("field")
                .verify();
    }
}
