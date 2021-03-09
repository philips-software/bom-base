/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Current value for a field.
 */
public class Attribute {
    private final Field field;
    private final Set<Object> values = new HashSet<>();
    private Instant timestamp = Instant.now();
    private boolean confirmed;

    Attribute(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    Instant getTimestamp() {
        return timestamp;
    }

    <T> Optional<T> getValue() {
        //noinspection unchecked
        return values.stream().findFirst().map(v -> (T) v);
    }

    <T> void setValue(@NullOr T value) {
        if (confirmed || value == null) {
            return;
        }
        field.validate(value);
        values.add(value);
        timestamp = Instant.now();
    }

    void override(@NullOr Object value) {
        values.clear();
        confirmed = false;
        setValue(value);
        confirmed = true;
    }

    public Set<Object> getContested() {
        return confirmed ? Set.of() : values;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;
        Attribute attribute = (Attribute) o;
        return field == attribute.field;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public String toString() {
        return String.format("{%s}", field);
    }
}

