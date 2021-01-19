package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public class FieldValue {
    private static final Map<Field, ValueConverter> convert = new HashMap<>();

    public static void register(Field field, ValueConverter converter) {
        convert.put(field, converter);
    }

    private enum State {VALUE, CONTESTED, OVERRIDDEN, ERROR}

    private final Field field;
    private State state = State.VALUE;
    private String value;
    private String parameter;
    private Instant timestamp = Instant.now();

    public FieldValue(Field field) {
       this.field = field;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    void setValue(String value) {
        if (state == State.OVERRIDDEN) {
            return;
        }
        state = State.VALUE;
        this.value = value;
        timestamp = Instant.now();
    }

    Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    void override(String value) {
        state = (value != null) ? State.OVERRIDDEN : State.VALUE;
        this.value = value;
        timestamp = Instant.now();
    }

    void contest(String value) {
        if (this.value == null || (state != State.VALUE && state != State.CONTESTED)) {
            return;
        }
        state = State.CONTESTED;
        parameter = value;
    }

    Optional<String> getContesting() {
        return (state == State.CONTESTED) ? Optional.ofNullable(parameter) : Optional.empty();
    }

    void error(String message) {
        if (state == FieldValue.State.OVERRIDDEN) {
            return;
        }
        state = State.ERROR;
        parameter = message;
    }

    Optional<String> getError() {
        return (state == State.ERROR) ? Optional.ofNullable(parameter) : Optional.empty();
    }
}

