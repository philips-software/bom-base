/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;

/**
 * Current value for a field.
 */
public class Attribute {
    private static final int TRUTH = 100;

    private final Field field;
    private int score;
    private @NullOr Object value;
    private int altScore;
    private @NullOr Object altValue;

    Attribute(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    <T> Optional<T> getValue() {
        return Optional.ofNullable((T) value);
    }

    boolean setValue(int score, @NullOr Object value) {
        if (value == null || score <= 0 || (this.score == TRUTH && score < TRUTH)) {
            return false;
        }

        field.validate(value);
        if (score >= TRUTH) {
            updateTruth(value);
            return true;
        } else if (score > this.score) {
            return updateValue(score, value);
        } else {
            updateAltValue(score, value);
            return false;
        }
    }

    private void updateTruth(Object value) {
        this.score = TRUTH;
        this.value = value;
        this.altScore = 0;
        this.altValue = null;
    }

    private void updateAltValue(int score, Object value) {
        if (score > this.altScore) {
            this.altScore = score;
            this.altValue = value;
        }
    }

    private boolean updateValue(int score, Object value) {
        if (!value.equals(this.value)) {
            replaceValue(score, value);
            return true;
        } else {
            this.score = score;
            return false;
        }
    }

    private void replaceValue(int score, Object value) {
        this.altScore = this.score;
        this.altValue = this.value;
        this.value = value;
        this.score = score;
    }

    public Optional<Object> getContested() {
        return Optional.ofNullable((altScore > 0) ? altValue : null);
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

