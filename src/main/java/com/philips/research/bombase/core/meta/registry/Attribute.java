/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Current timestamped value for a field with an indication of its reliability and validation of the type.
 * <ul>
 *   <li>A <strong>contested</strong> value indicates an alternate source does not agree with the current value,
 *   making it unreliable.</li>
 *   <li>An <strong>error</strong> indicates a problem occurred while trying to obtain the value
 *   from a source.</li>
 * </ul>
 */
public class Attribute {
    private final Field field;
    private State state = State.VALUE;
    private @NullOr Object value;
    private @NullOr String argument;
    private Instant timestamp = Instant.now();

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
        return Optional.ofNullable((T) value);
    }

    void setValue(Object value) {
        field.validate(value);
        if (state == State.OVERRIDDEN) {
            return;
        }
        state = State.VALUE;
        this.value = value;
        timestamp = Instant.now();
    }

    void override(@NullOr Object value) {
        field.validate(value);
        state = (value != null) ? State.OVERRIDDEN : State.VALUE;
        this.value = value;
        timestamp = Instant.now();
    }

    void contest(String alternative) {
        if (this.value == null || (state != State.VALUE && state != State.CONTESTED)) {
            return;
        }
        state = State.CONTESTED;
        argument = alternative;
    }

    Optional<String> getContesting() {
        return (state == State.CONTESTED) ? Optional.ofNullable(argument) : Optional.empty();
    }

    void error(String message) {
        if (state == Attribute.State.OVERRIDDEN) {
            return;
        }
        state = State.ERROR;
        argument = message;
    }

    Optional<String> getError() {
        return (state == State.ERROR) ? Optional.ofNullable(argument) : Optional.empty();
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

    private enum State {VALUE, CONTESTED, OVERRIDDEN, ERROR}
}

