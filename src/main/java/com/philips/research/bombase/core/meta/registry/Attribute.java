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
public class Attribute<T> implements AttributeValue<T> {
    private static final int TRUTH = 100;

    private final Field field;
    private int score;
    private @NullOr T value;
    private int altScore;
    private @NullOr T altValue;

    public Attribute(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public int getScore() {
        return score;
    }

    boolean setValue(int score, @NullOr T value) {
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

    private void updateTruth(@NullOr T value) {
        this.score = TRUTH;
        this.value = value;
        this.altScore = 0;
        this.altValue = null;
    }

    @Override
    public Optional<T> getAltValue() {
        return Optional.ofNullable((altScore > 0) ? altValue : null);
    }

    @Override
    public int getAltScore() {
        return altScore;
    }

    private void updateAltValue(int score, @NullOr T value) {
        if (score > this.altScore) {
            this.altScore = score;
            this.altValue = value;
        }
    }

    private boolean updateValue(int score, @NullOr T value) {
        if (!value.equals(this.value)) {
            replaceValue(score, value);
            return true;
        } else {
            this.score = score;
            return false;
        }
    }

    private void replaceValue(int score, @NullOr T value) {
        this.altScore = this.score;
        this.altValue = this.value;
        this.value = value;
        this.score = score;
    }

    @Override
    public final boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;
        Attribute<?> attribute = (Attribute<?>) o;
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

