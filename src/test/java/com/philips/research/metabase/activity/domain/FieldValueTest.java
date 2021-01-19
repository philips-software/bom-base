package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.Field;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FieldValueTest {
    private static final String VALUE = "Value";
    private static final String TEXT = "Text";

    final FieldValue field = new FieldValue(Field.TITLE);

    @BeforeAll
    static void beforeAll() {
       FieldValue.register(Field.TITLE, new ValueConverter(String.class)) ;
        FieldValue.register(Field.NUM_FILES, new ValueConverter(Integer.class)) ;
    }

    @Test
    void createsInstance() {
        assertThat(field.getValue()).isEmpty();
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
            field.setValue(VALUE);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getTimestamp()).isAfter(previous);
        }

        @Test
        void ignoresContesting() {
            field.contest(TEXT);

            assertThat(field.getValue()).isEmpty();
            assertThat(field.getContesting()).isEmpty();
        }

        @Test
        void recordsError() {
            field.error(TEXT);

            assertThat(field.getValue()).isEmpty();
            assertThat(field.getError()).contains(TEXT);
        }
    }

    @Nested
    class HasValue {
        @BeforeEach
        void beforeEach() {
            field.setValue(VALUE);
        }

        @Test
        void contestsValue() {
            field.contest(TEXT);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getContesting()).contains(TEXT);
        }

        @Test
        void indicatesError() {
            field.error(TEXT);

            assertThat(field.getValue()).contains(VALUE);
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

            field.override(correction);

            assertThat(field.getValue()).contains(correction);
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
            field.setValue(VALUE);

            assertThat(field.getValue()).contains(VALUE);
            assertThat(field.getError()).isEmpty();
        }
    }

    @Nested
    class Overridden {
        @BeforeEach
        void BeforeEach() {
            field.override(VALUE);
        }

        @Test
        void ignoresSetValue() {
            field.setValue("Other");

            assertThat(field.getValue()).contains(VALUE);
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
            assertThat(field.getError()).isEmpty();
        }

        @Test
        void overrides() {
            final var correction = "Correction";

            field.override(correction);

            assertThat(field.getValue()).contains(correction);
        }

        @Test
        void clearsOverride() {
            field.override(null);
            field.error(TEXT);

            assertThat(field.getValue()).isEmpty();
            assertThat(field.getError()).contains(TEXT);
        }
    }
}
