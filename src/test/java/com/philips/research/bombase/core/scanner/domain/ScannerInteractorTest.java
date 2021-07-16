/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import com.philips.research.bombase.core.downloader.DownloadException;
import com.philips.research.bombase.core.downloader.DownloadService;
import com.philips.research.bombase.core.scanner.ScannerException;
import com.philips.research.bombase.core.scanner.ScannerService;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ScannerInteractorTest {
    private static final Path PATH = Path.of("directory", "path");
    private static final URI LICENSE_URL = URI.create("http://example.com/license");
    private static final String LICENSE = "License";

    private final DownloadService downloader = mock(DownloadService.class);
    private final ScanCodeScanner scanner = mock(ScanCodeScanner.class);
    private final ScannerService interactor = new ScannerInteractor(downloader, scanner);

    @Test
    void scansDirectory() {
        //TODO This seems not much of a test
        when(scanner.scan(PATH)).thenReturn(mock(ScannerService.ScanResult.class));

        final var result = interactor.scan(PATH);

        assertThat(result).isNotNull();
    }

    @Test
    void scansFromUrl() {
        setupDownloader(LICENSE_URL, PATH);
        when(scanner.scan(PATH)).thenAnswer((x) -> scanResult(LICENSE));

        final var licenses = interactor.scanLicenses(LICENSE_URL);

        assertThat(licenses).containsExactly(LICENSE);
    }

    @Test
    void throws_downloadFails() {
        when(downloader.download(eq(LICENSE_URL), any())).thenThrow(new DownloadException("Test"));

        assertThatThrownBy(() -> interactor.scanLicenses(LICENSE_URL))
                .isInstanceOf(DownloadException.class)
                .hasMessageContaining("Test");
    }

    @Test
    void throws_scanFails() {
        setupDownloader(LICENSE_URL, PATH);
        when(scanner.scan(PATH)).thenThrow(new ScannerException("Test"));

        assertThatThrownBy(() -> interactor.scanLicenses(LICENSE_URL))
                .isInstanceOf(ScannerException.class)
                .hasMessageContaining("Test");
    }

    /**
     * Matches the download URL and provides a path to the processor.
     */
    private void setupDownloader(URI url, Path path) {
        doAnswer(a -> { // Pass download path to provided consumer
            final Function<Path, List<String>> consumer = a.getArgument(1);
            return consumer.apply(path);
        }).when(downloader).download(eq(url), any());
    }

    /**
     * Configures scanner to report the indicated licenses.
     */
    private ScannerService.ScanResult scanResult(String... licenses) {
        final var list = Arrays.stream(licenses)
                .map(lic -> {
                    final var result = mock(ScannerService.LicenseResult.class);
                    when(result.getExpression()).thenReturn(lic);
                    return result;
                })
                .collect(Collectors.toList());
        final var scanResult = mock(ScannerService.ScanResult.class);
        when(scanResult.getLicenses()).thenReturn(list);
        return scanResult;
    }
}
