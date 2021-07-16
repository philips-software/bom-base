/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

public enum Trust {
    NONE(0),
    MAYBE(10),
    PROBABLY(60),
    LIKELY(70),
    CERTAIN(80),
    TRUTH(100);

    private final int score;

    Trust(int score) {
        this.score = score;
    }

    static Trust of(int score) {
        for (var trust : Trust.values()) {
            if (trust.getScore() >= score) {
                return trust;
            }
        }
        return Trust.TRUTH;
    }

    int getScore() {
        return score;
    }
}
