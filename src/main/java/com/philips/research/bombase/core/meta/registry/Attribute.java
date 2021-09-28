/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;

/**
 * Current metadata value for a field.
 */
public class Attribute<T> implements AttributeValue<T> {
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

    @Override
    public Optional<T> getAltValue() {
        return Optional.ofNullable((altScore > 0) ? altValue : null);
    }

    @Override
    public int getAltScore() {
        return altScore;
    }

    /**
     * Potentially updates the current and alternative value.
     *
     * @param trust indication of how certain the caller is of the value
     * @param value the new value
     * @return true if the main value was updated (which is independent from score updates or alt value changes)
     */
    boolean setValue(Trust trust, @NullOr T value) {
        int score = trust.getScore();
        if (value == null) {
            return false;
        }

        field.validate(value);
        if (trust == Trust.TRUTH) {
            return updateTruth(value);
        } else if (score >= this.score) {
            return updateValue(score, value);
        } else {
            updateAltValue(score, value);
            return false;
        }
    }

    private boolean updateTruth(T value) {
        this.value = value;
        this.score = Trust.TRUTH.getScore();
        this.altValue = null;
        this.altScore = Trust.NONE.getScore();
        return true;
    }

    private boolean updateValue(int score, T value) {
        if (Objects.equals(this.value, value)) {
            this.score = score;
            return false;
        }
        this.altScore = this.score;
        this.altValue = this.value;
        this.score = score;
        this.value = value;
        return true;
    }

    private void updateAltValue(int score, T value) {
        if (score < this.altScore) {
            return;
        }
        this.altScore = score;
        this.altValue = value;
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

