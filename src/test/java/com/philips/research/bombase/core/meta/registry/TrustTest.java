/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrustTest {
    @Test
    void mapsToPercentageScore() {
        for (var trust : Trust.values()) {
            assertThat(trust.getScore()).isBetween(1, 100);
        }
    }

    @Test
    void mapsToIncreasingScores() {
        var previous = 0;
        for (var trust : Trust.values()) {
            assertThat(trust.getScore()).isGreaterThan(previous);
            previous = trust.getScore();
        }
    }
}
