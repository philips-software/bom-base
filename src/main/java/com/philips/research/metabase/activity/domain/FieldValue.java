package com.philips.research.metabase.activity.domain;

import java.time.Instant;
import java.util.Optional;

/**
 *
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

