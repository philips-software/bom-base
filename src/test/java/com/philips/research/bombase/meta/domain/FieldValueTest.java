/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.Origin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FieldValueTest {
    private static final String VALUE = "Value";
    private static final String TEXT = "Text";
    private static final Origin ORIGIN = Origin.CLEARLY_DEFINED;
    private static final Origin OTHER_ORIGIN = Origin.API;

    final FieldValue field = new FieldValue(Field.TITLE);

    @Test
    void createsInstance() {
        assertThat(field.getValue()).isEmpty();
        assertThat(field.getOrigin()).isEmpty();
        assertThat(field.getContesting()).isEmpty();
        assertThat(field.getError()).isEmpty();
        assertThat(field.getTimestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(field.getTimestamp()).isAfter(Instant.now().minusSeconds(5));
    }

    @Nested
    class NoPriorValue {
        @Test
        void setsValue() {
            final var previous = field.getTimestamp();
            field.setValue(Origin.CLEARLY_DEFINED, VALUE);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getOrigin()).contains(ORIGIN);
            assertThat(field.getTimestamp()).isAfter(previous);
        }

        @Test
        void ignoresContesting() {
            field.contest(TEXT);

            assertThat(field.getValue()).isEmpty();
            assertThat(field.getOrigin()).isEmpty();
            assertThat(field.getContesting()).isEmpty();
        }

        @Test
        void recordsError() {
            field.error(TEXT);

            assertThat(field.getValue()).isEmpty();
            assertThat(field.getOrigin()).isEmpty();
            assertThat(field.getError()).contains(TEXT);
        }
    }

    @Nested
    class HasValue {
        @BeforeEach
        void beforeEach() {
            field.setValue(ORIGIN, VALUE);
        }

        @Test
        void contestsValue() {
            field.contest(TEXT);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getOrigin()).contains(ORIGIN);
            assertThat(field.getContesting()).contains(TEXT);
        }

        @Test
        void indicatesError() {
            field.error(TEXT);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getOrigin()).contains(ORIGIN);
            assertThat(field.getError()).contains(TEXT);
        }

        @Test
        void overwritesContest() {
            field.contest("Other");
            field.contest(TEXT);

            assertThat(field.getContesting()).contains(TEXT);
        }

        @Test
        void overridesValue() {
            final var correction = "Correction";

            field.override(OTHER_ORIGIN, correction);

            assertThat(field.getValue()).contains(correction);
            assertThat(field.getOrigin()).contains(OTHER_ORIGIN);
        }
    }

    @Nested
    class HasError {
        @BeforeEach
        void beforeEach() {
            field.error(TEXT);
        }

        @Test
        void ignoresContest() {
            field.contest("Other");

            assertThat(field.getError()).contains(TEXT);
            assertThat(field.getContesting()).isEmpty();
        }

        @Test
        void clearsErrorBySettingValue() {
            field.error(TEXT);
            field.setValue(ORIGIN, VALUE);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getOrigin()).contains(ORIGIN);
            assertThat(field.getError()).isEmpty();
        }
    }

    @Nested
    class Overridden {
        @BeforeEach
        void BeforeEach() {
            field.override(ORIGIN, VALUE);
        }

        @Test
        void ignoresSetValue() {
            field.setValue(OTHER_ORIGIN, "Ignored");

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getOrigin()).contains(ORIGIN);
        }

        @Test
        void ignoresContesting() {
            field.contest(TEXT);

            assertThat(field.getContesting()).isEmpty();
        }

        @Test
        void ignoresError() {
            field.error(TEXT);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getOrigin()).contains(ORIGIN);
            assertThat(field.getError()).isEmpty();
        }

        @Test
        void overrides() {
            final var correction = "Correction";

            field.override(OTHER_ORIGIN, correction);

            assertThat(field.getValue()).contains(correction);
            assertThat(field.getOrigin()).contains(OTHER_ORIGIN);
        }

        @Test
        void clearsOverride() {
            field.override(OTHER_ORIGIN, null);
            field.error(TEXT);

            assertThat(field.getValue()).isEmpty();
            assertThat(field.getOrigin()).isEmpty();
            assertThat(field.getError()).contains(TEXT);
        }
    }
}
