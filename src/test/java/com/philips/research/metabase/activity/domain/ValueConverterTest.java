package com.philips.research.metabase.activity.domain;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ValueConverterTest {
    @Test
    void convertsString() {
        final var text = "Text";
        final var converter = new ValueConverter(String.class);

        assertThat(converter.toString(text)).isSameAs(text);
        assertThat(converter.parse(text)).isSameAs(text);
    }

    @Test
    void convertsInteger() {
        final var value = 42;
        final var converter = new ValueConverter(Integer.class);

        assertThat(converter.toString(value)).isEqualTo(Integer.toString(value));
        assertThat(converter.parse(Integer.toString(value))).isEqualTo(value);
    }

    @Test
    void throws_encodingUnsupportedValueType() {
       final var converter = new ValueConverter(Integer.class) ;

       assertThatThrownBy(()->converter.toString(123.456))
               .isInstanceOf(IllegalArgumentException.class)
               .hasMessageContaining(Integer.class.getSimpleName());
    }

    @Test
    void throws_decodingToUnsupportedValueType() {
        final var converter = new ValueConverter(ApplicationContext.class);

        assertThatThrownBy(()->converter.parse("ignore"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining(ApplicationContext.class.getSimpleName());
    }

    @Test
    void throws_parseToValueFails() {
        final var converter = new ValueConverter(Integer.class);

        assertThatThrownBy(()->converter.parse("1.234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("parse value");
    }
}
