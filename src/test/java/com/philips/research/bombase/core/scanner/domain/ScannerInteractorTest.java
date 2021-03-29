/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import com.philips.research.bombase.core.scanner.ScannerService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScannerInteractorTest {
    private static final Path DIRECTORY = Path.of("directory", "path");

    private final ScanCodeScanner scanner = mock(ScanCodeScanner.class);
    private final ScannerService interactor = new ScannerInteractor(scanner);

    @Test
    void scansDirectory() {
        when(scanner.scan(DIRECTORY)).thenReturn(mock(ScannerService.ScanResult.class));

        final var result = interactor.scan(DIRECTORY);

        assertThat(result).isNotNull();
    }
}
