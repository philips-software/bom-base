/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScanCodeJsonTest {
    private static final String LICENSE = "License";
    private static final String KEY = "Key";
    private static final int SCORE = 73;
    private static final String FILE = "filename.ext";
    private static final int START = 23;
    private static final int END = 42;

    private static LicenseJson licenseJson(@NullOr String key, @NullOr String spdx, int score, int start, int end) {
        final var json = new LicenseJson();
        json.key = key;
        json.spdx = spdx;
        json.score = score;
        json.startLine = start;
        json.endLine = end;
        return json;
    }

    @Nested
    class ScanResults {
        @Test
        void exposesLicense() {
            final var detection = new Detection(LICENSE, SCORE, new File(FILE), START, END);
            final var result = new ScanCodeJson();
            final var file = mock(FileJson.class);
            when(file.getDetections()).thenReturn(List.of(detection));
            result.files.add(file);

            assertThat(result.getLicenses()).containsExactly(detection);
        }

        @Test
        void combinesLicenseExpressions() {
            final var detection = new Detection(LICENSE, SCORE, new File(FILE), START, END);
            final var scanResult = new ScanCodeJson();
            final var file = mock(FileJson.class);
            when(file.getDetections()).thenReturn(List.of(detection));
            scanResult.files.addAll(List.of(file, file));

            final var licenses = scanResult.getLicenses();

            assertThat(licenses).hasSize(1);
            assertThat(licenses.get(0).getConfirmations()).isEqualTo(2);
        }
    }

    @Nested
    class FileInformation {
        @Test
        void exposesDetectedLicense() {
            final var json = new FileJson();
            json.path = FILE;
            json.expressions.add(KEY);
            json.licenses.add(licenseJson(KEY, LICENSE, SCORE, START, END));

            assertThat(json.getDetections()).hasSize(1);
            final var detection = json.getDetections().get(0);
            assertThat(detection.getExpression()).isEqualTo(LICENSE);
            assertThat(detection.getConfirmations()).isEqualTo(1);
            assertThat(detection.getFile()).isEqualTo(new File(FILE));
            assertThat(detection.getStartLine()).isEqualTo(START);
            assertThat(detection.getEndLine()).isEqualTo(END);
            assertThat(detection.getScore()).isEqualTo(SCORE);
        }

        @Test
        void combinesExpressionElements() {
            final var json = new FileJson();
            json.path = FILE;
            json.expressions.add("a and(b) or C");
            json.licenses.add(licenseJson("a", "A", 100, 10, 20));
            json.licenses.add(licenseJson("b", "B", 75, 5, 30));

            final var detection = json.getDetections().get(0);
            assertThat(detection.getExpression()).isEqualTo("A and(B) or C");
            assertThat(detection.getScore()).isEqualTo(75);
            assertThat(detection.getStartLine()).isEqualTo(5);
            assertThat(detection.getEndLine()).isEqualTo(30);
        }
    }

    @Nested
    class LicenseInformation {
        @Test
        void prefersSpdxIdentifier() {
            final var json = licenseJson(KEY, LICENSE, SCORE, START, END);

            assertThat(json.getIdentifier()).isEqualTo(LICENSE);
        }

        @Test
        void skipsMissingSpdxIdentifier() {
            final var json = licenseJson(KEY, null, SCORE, START, END);

            assertThat(json.getIdentifier()).isEqualTo(KEY);
        }

        @Test
        void buildsDictionary() {
            final var json = licenseJson(KEY, LICENSE, SCORE, START, END);

            final var dict = LicenseJson.buildDictionary(List.of(json));

            assertThat(dict.get(KEY)).isSameAs(json);
        }

        @Test
        void replacesHigherScoreInDictionary() {
            final var json = licenseJson(KEY, LICENSE, 90, START, END);
            final var higher = licenseJson(KEY, LICENSE, 100, START, END);
            final var lower = licenseJson(KEY, LICENSE, 80, START, END);

            final var dict = LicenseJson.buildDictionary(List.of(json, higher, lower));

            assertThat(dict.get(KEY)).isSameAs(higher);
        }

        @Test
        void replacesLongerSectionInDictionary() {
            final var json = licenseJson(KEY, LICENSE, SCORE, 12, 17);
            final var longer = licenseJson(KEY, LICENSE, SCORE, 20, 30);
            final var shorter = licenseJson(KEY, LICENSE, SCORE, 5, 6);

            final var dict = LicenseJson.buildDictionary(List.of(json, longer, shorter));

            assertThat(dict.get(KEY)).isSameAs(longer);
        }

    }
}
