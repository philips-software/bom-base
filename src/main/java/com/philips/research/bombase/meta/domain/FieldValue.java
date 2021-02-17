/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.meta.Field;
import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Instant;
import java.util.Optional;

/**
 * Current timestamped value for a field, with an indication of its reliability.
 * A contested value indicates an alternate source does not agree with the current value, making it unreliable.
 * An error indicates a problem occurred while trying to obtain the value from a source.
 */
public class FieldValue {
    private final Field field;
    private State state = State.VALUE;
    private @NullOr Object value;
    private @NullOr String argument;
    private Instant timestamp = Instant.now();

    public FieldValue(Field field) {
        this.field = field;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    Optional<Object> getValue() {
        return Optional.ofNullable(value);
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
        if (state == FieldValue.State.OVERRIDDEN) {
            return;
        }
        state = State.ERROR;
        argument = message;
    }

    Optional<String> getError() {
        return (state == State.ERROR) ? Optional.ofNullable(argument) : Optional.empty();
    }

    private enum State {VALUE, CONTESTED, OVERRIDDEN, ERROR}
}

