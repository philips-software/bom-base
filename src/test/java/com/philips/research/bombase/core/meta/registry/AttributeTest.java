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
    private static final Trust HIGHER_TRUST = Trust.values()[TRUST.ordinal() + 1];
    private static final Trust LOWER_TRUST = Trust.values()[TRUST.ordinal() - 1];

    private final Attribute<Object> field = new Attribute<>(FIELD);

    @Test
    void createsInstance() {
        assertThat(field.getField()).isEqualTo(FIELD);
        assertThat(field.getScore()).isEqualTo(Trust.NONE.getScore());
        assertThat(field.getValue()).isEmpty();
        assertThat(field.getAltScore()).isEqualTo(Trust.NONE.getScore());
        assertThat(field.getAltValue()).isEmpty();
    }

    @Test
    void setsInitialValue() {
        final var modified = field.setValue(TRUST, VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void overridesValue_equalTrust() {
        field.setValue(TRUST, VALUE);

        final var modified = field.setValue(TRUST, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getScore()).isEqualTo(TRUST.getScore());
        assertThat(field.getAltValue()).contains(VALUE);
        assertThat(field.getAltScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void increasesTrust() {
        field.setValue(TRUST, VALUE);

        final var modified = field.setValue(HIGHER_TRUST, VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getScore()).isEqualTo(HIGHER_TRUST.getScore());
        assertThat(field.getAltValue()).isEmpty();
    }

    @Test
    void overridesValue_higherTrust() {
        field.setValue(TRUST, VALUE);

        final var modified = field.setValue(HIGHER_TRUST, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getScore()).isEqualTo(HIGHER_TRUST.getScore());
        assertThat(field.getAltValue()).contains(VALUE);
        assertThat(field.getAltScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void overridesTruth() {
        field.setValue(Trust.TRUTH, VALUE);

        final var modified = field.setValue(Trust.TRUTH, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getAltValue()).isEmpty();
    }

    @Test
    void confirmsTruth() {
        field.setValue(Trust.TRUTH, VALUE);

        final var modified = field.setValue(Trust.TRUTH, VALUE);

        assertThat(modified).isTrue();
    }

    @Test
    void contestsValue_lowerScore() {
        field.setValue(TRUST, VALUE);

        final var modified = field.setValue(LOWER_TRUST, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getScore()).isEqualTo(TRUST.getScore());
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
        assertThat(field.getAltScore()).isEqualTo(LOWER_TRUST.getScore());
    }

    @Test
    void overridesValueAndContestingValue() {
        field.setValue(LOWER_TRUST, "Replaced");
        field.setValue(TRUST, VALUE);

        final var modified = field.setValue(HIGHER_TRUST, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getScore()).isEqualTo(HIGHER_TRUST.getScore());
        assertThat(field.getAltValue()).contains(VALUE);
        assertThat(field.getAltScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void overridesContestingValue() {
        field.setValue(HIGHER_TRUST, VALUE);
        field.setValue(LOWER_TRUST, "Replaced");

        final var modified = field.setValue(TRUST, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getScore()).isEqualTo(HIGHER_TRUST.getScore());
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
        assertThat(field.getAltScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void ignoresNonContestingValue() {
        field.setValue(HIGHER_TRUST, VALUE);
        field.setValue(TRUST, OTHER_VALUE);

        final var modified = field.setValue(LOWER_TRUST, "Ignored");

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getScore()).isEqualTo(HIGHER_TRUST.getScore());
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
        assertThat(field.getAltScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void promotesContestingValue() {
        field.setValue(LOWER_TRUST, VALUE);
        field.setValue(TRUST, OTHER_VALUE);

        final var modified = field.setValue(HIGHER_TRUST, VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getScore()).isEqualTo(HIGHER_TRUST.getScore());
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
        assertThat(field.getAltScore()).isEqualTo(TRUST.getScore());
    }

    @Test
    void increasesTrustOfContestingValue() {
        field.setValue(Trust.TRUTH, OTHER_VALUE);
        field.setValue(LOWER_TRUST, VALUE);

        final var modified = field.setValue(HIGHER_TRUST, VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getScore()).isEqualTo(Trust.TRUTH.getScore());
        assertThat(field.getAltValue()).contains(VALUE);
        assertThat(field.getAltScore()).isEqualTo(HIGHER_TRUST.getScore());
    }

    @Test
    void ignoresEqualContestingValue() {
        field.setValue(HIGHER_TRUST, VALUE);
        field.setValue(LOWER_TRUST, OTHER_VALUE);

        final var modified = field.setValue(LOWER_TRUST, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getAltValue()).contains(OTHER_VALUE);
        assertThat(field.getAltScore()).isEqualTo(LOWER_TRUST.getScore());
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
