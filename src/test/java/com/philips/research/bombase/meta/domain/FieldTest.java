/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class FieldTest {
    private static final String STRING = "String";

    @Nested
    class Validation {
        @Test
        void validatesCompatibleType() {
            final var value = Field.TITLE.validate(STRING);

            assertThat(value).isSameAs(STRING);
        }

        @Test
        void throws_validatingIfNoTypeAssigned() {
            assertThatThrownBy(() -> Field.TYPE.validate(STRING))
                    .isInstanceOf(MetaException.class)
                    .hasMessageContaining("not hold any value");
        }

        @Test
        void throws_incompatibleType() {
            assertThatThrownBy(() -> Field.TITLE.validate(666))
                    .isInstanceOf(MetaException.class)
                    .hasMessageContaining("not hold a value of type");
        }
    }
}
