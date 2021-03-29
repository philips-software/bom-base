/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DetectionTest {
    private static final String LICENSE = "License";
    private static final int SCORE = 73;
    private static final File FILE = new File("path/to/file.txt");
    private static final int START_LINE = 10;
    private static final int END_LINE = 20;


    @Test
    void createsInstance() {
        final var detection = new Detection(LICENSE, SCORE, FILE, START_LINE, END_LINE);

        assertThat(detection.getExpression()).isEqualTo(LICENSE);
        assertThat(detection.getFile()).isEqualTo(FILE);
        assertThat(detection.getScore()).isEqualTo(SCORE);
        assertThat(detection.getStartLine()).isEqualTo(START_LINE);
        assertThat(detection.getEndLine()).isEqualTo(END_LINE);
        assertThat(detection.getConfirmations()).isEqualTo(1);
        assertThat(detection.isIgnored()).isFalse();
    }

    @Test
    void ignoresSuspiciousPaths() {
        final var detection = new Detection(LICENSE, SCORE, new File("path/ToTests/blah.txt"), 1, 10);

        assertThat(detection.isIgnored()).isTrue();
    }

    @Test
    void countsLowerAndEqualScoringEvidence() {
        final var detection = new Detection(LICENSE, SCORE, FILE, START_LINE, END_LINE)
                .merge(new Detection(LICENSE, SCORE, new File("other.txt"), 666, 666))
                .merge(new Detection(LICENSE, SCORE - 1, new File("other.txt"), 666, 666));

        assertThat(detection.getScore()).isEqualTo(SCORE);
        assertThat(detection.getFile()).isEqualTo(FILE);
        assertThat(detection.getConfirmations()).isEqualTo(3);
    }

    @Test
    void prefersLongerEvidenceBlocks() {
        final var detection = new Detection(LICENSE, SCORE, new File("other.txt"), 1, 10)
                .merge(new Detection(LICENSE, SCORE, FILE, 11, 21));

        assertThat(detection.getFile()).isEqualTo(FILE);
        assertThat(detection.getStartLine()).isEqualTo(11);
        assertThat(detection.getEndLine()).isEqualTo(21);
        assertThat(detection.getConfirmations()).isEqualTo(2);
    }

    @Test
    void recoversFromSuspiciousPaths() {
        final var detection = new Detection(LICENSE, 100, new File("path/Sample/blah.txt"), 1, 10)
                .merge(new Detection(LICENSE, 1, new File("path/reliable/blah.txt"), 1, 10));

        assertThat(detection.isIgnored()).isFalse();
    }

    @Test
    void throws_mergeWithDifferentExpression() {
        final var detection = new Detection(LICENSE, SCORE, FILE, START_LINE, END_LINE);
        final var incompatible = new Detection("Other", SCORE, FILE, START_LINE, END_LINE);

        assertThatThrownBy(() -> detection.merge(incompatible))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("compatible");
    }

    @Test
    void marksFalsePositive() {
        //TODO Is this even useful?
        final var detection = new Detection(LICENSE, SCORE, FILE, START_LINE, END_LINE);

        detection.setIgnored(true);

        assertThat(detection.isIgnored()).isTrue();
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Detection.class)
                .withOnlyTheseFields("expression")
                .verify();
    }
}
