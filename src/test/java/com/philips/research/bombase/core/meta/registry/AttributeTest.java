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
    private static final String OTHER_VALUE = "Other";
    private static final Field FIELD = Field.TITLE;
    private static final int SCORE = 50;
    private static final int HIGHER_SCORE = 75;
    private static final int LOWER_SCORE = 25;

    private final Instant start = Instant.now();
    private final Attribute field = new Attribute(FIELD);

    @Test
    void createsInstance() {
        assertThat(field.getField()).isEqualTo(FIELD);
        assertThat(field.getValue()).isEmpty();
        assertThat(field.getContested()).isEmpty();
    }

    @Test
    void setsValue() {
        final var modified = field.setValue(SCORE, VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getContested()).isEmpty();
    }

    @Test
    void overridesValue_higherScore() {
        field.setValue(SCORE, VALUE);
        final var modified = field.setValue(HIGHER_SCORE, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getContested()).contains(VALUE);
    }

    @Test
    void ignoresIdenticalValue() {
        field.setValue(SCORE, VALUE);
        final var modified = field.setValue(HIGHER_SCORE, VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getContested()).isEmpty();
    }

    @Test
    void ignoresZeroScore() {
        final var modified = field.setValue(0, VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).isEmpty();
    }

    @Test
    void clipsScoreToZero() {
        final var modified = field.setValue(-1, VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).isEmpty();
    }

    @Test
    void contestsValue_lowerScore() {
        field.setValue(SCORE, VALUE);
        final var modified = field.setValue(LOWER_SCORE, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getContested()).contains(OTHER_VALUE);
    }

    @Test
    void overridesValueAndContestingValue() {
        field.setValue(LOWER_SCORE, "Replaced");
        field.setValue(SCORE, VALUE);
        final var modified = field.setValue(HIGHER_SCORE, OTHER_VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(OTHER_VALUE);
        assertThat(field.getContested()).contains(VALUE);
    }

    @Test
    void overridesContestingValue() {
        field.setValue(HIGHER_SCORE, VALUE);
        field.setValue(LOWER_SCORE, "Replaced");
        final var modified = field.setValue(SCORE, OTHER_VALUE);

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getContested()).contains(OTHER_VALUE);
    }

    @Test
    void keepsAbsoluteTruth() {
        field.setValue(SCORE, "Removed");
        field.setValue(100, VALUE);
        final var modified = field.setValue(LOWER_SCORE, "Ignored");

        assertThat(modified).isFalse();
        assertThat(field.getValue()).contains(VALUE);
        assertThat(field.getContested()).isEmpty();
    }

    @Test
    void clipsScoreAtAbsoluteTruth() {
        field.setValue(101, "Removed");
        final var modified = field.setValue(100, VALUE);

        assertThat(modified).isTrue();
        assertThat(field.getValue()).contains(VALUE);
    }

    @Test
    void throws_ValueOfWrongType() {
        assertThatThrownBy(() -> field.setValue(SCORE, 666))
                .isInstanceOf(MetaException.class)
                .hasMessageContaining("cannot hold");
    }

    @Test
    void ignoresNullValue() {
        field.setValue(SCORE, VALUE);
        final var modified = field.setValue(HIGHER_SCORE, null);

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
