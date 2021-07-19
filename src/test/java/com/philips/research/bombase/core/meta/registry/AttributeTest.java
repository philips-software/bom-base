/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.philips.research.bombase.core.meta.MetaException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeTest {
    private static final String VALUE = "Value";
    private static final String OTHER_VALUE = "Other";
    private static final Field FIELD = Field.TITLE;
    private static final Trust TRUST = Trust.PROBABLY;
    private static final Trust HIGHER_TRUST = Trust.CERTAIN;
    private static final Trust LOWER_TRUST = Trust.MAYBE;

    private final Attribute<Object> field = new Attribute<>(FIELD);

    @Test
    void createsInstance() {
        assertThat(field.getField()).isEqualTo(FIELD);
        assertThat(field.getValue()).isEmpty();
        assertThat(field.getAltValue()).isEmpty();
    }

    @Test
    void setsValue() {
        final var modified = field.setValue(TRUST, VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getAltValue()).isEmpty();
    }

    @Test
    void overridesValue_equalTrust() {
        field.setValue(TRUST, VALUE);
        final var modified = field.setValue(TRUST, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getAltValue()).contains(VALUE);
    }

    @Test
    void overridesValue_higherTrust() {
        field.setValue(TRUST, VALUE);
        final var modified = field.setValue(HIGHER_TRUST, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getAltValue()).contains(VALUE);
    }

    @Test
    void ignoresIdenticalValue() {
        field.setValue(TRUST, VALUE);
        final var modified = field.setValue(HIGHER_TRUST, VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getAltValue()).isEmpty();
    }

    @Test
    void contestsValue_lowerScore() {
        field.setValue(TRUST, VALUE);
        final var modified = field.setValue(LOWER_TRUST, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
    }

    @Test
    void overridesValueAndContestingValue() {
        field.setValue(LOWER_TRUST, "Replaced");
        field.setValue(TRUST, VALUE);
        final var modified = field.setValue(HIGHER_TRUST, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getAltValue()).contains(VALUE);
    }

    @Test
    void overridesContestingValue() {
        field.setValue(HIGHER_TRUST, VALUE);
        field.setValue(LOWER_TRUST, "Replaced");
        final var modified = field.setValue(TRUST, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
    }

    @Test
    void promotesContestingValue() {
        field.setValue(LOWER_TRUST, VALUE);
        field.setValue(TRUST, OTHER_VALUE);
        final var modified = field.setValue(HIGHER_TRUST, VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
    }

    @Test
    void throws_ValueOfWrongType() {
        assertThatThrownBy(() -> field.setValue(TRUST, 666))
                .isInstanceOf(MetaException.class)
                .hasMessageContaining("cannot hold");
    }

    @Test
    void ignoresNullValue() {
        field.setValue(TRUST, VALUE);
        final var modified = field.setValue(HIGHER_TRUST, null);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Attribute.class)
                .withOnlyTheseFields("field")
                .withNonnullFields("field")
                .verify();
    }
}
