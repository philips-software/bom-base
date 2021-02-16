/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import java.time.Instant;
import java.util.Optional;

/**
 * Current timestamped value for a field, with an indication of its reliability.
 * A contested value indicates an alternate source does not agree with the current value, making it unreliable.
 * An error indicates a problem occurred while trying to obtain the value from a source.
 */
public class FieldValue<T> {
    private final Class<T> type;
    private State state = State.VALUE;
    private T value;
    private String argument;
    private Instant timestamp = Instant.now();

    public FieldValue(Class<T> type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    void setValue(T value) {
        if (state == State.OVERRIDDEN) {
            return;
        }
        state = State.VALUE;
        this.value = value;
        timestamp = Instant.now();
    }

    void override(T value) {
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

