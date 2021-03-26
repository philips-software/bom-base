/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ScanCodeScannerTest {
    private static final Path SAMPLE_DIRECTORY = Path.of("src", "test", "resources", "scanner", "sample");

    private final ScanCodeScanner scanner = new ScanCodeScanner();

    @Test
    void scansSampleDirectory() {
        final var result = scanner.scan(SAMPLE_DIRECTORY);

        final var licenses = result.getLicenses();
        assertThat(licenses).hasSize(3);
    }
}
