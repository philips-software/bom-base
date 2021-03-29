/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.source_scan.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.downloader.DownloadService;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.scanner.ScannerService;
import com.philips.research.bombase.core.scanner.ScannerService.LicenseResult;
import com.philips.research.bombase.core.scanner.ScannerService.ScanResult;
import com.philips.research.bombase.core.scanner.domain.Detection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SourceScanListenerTest {
    private static final PackageURL PURL = createPurl("pkg:type/ns/name@version");
    private static final URI SOURCE_LOCATION = URI.create("https://example.com/sources");

    private final DownloadService downloader = mock(DownloadService.class);
    private final ScannerService scanner = mock(ScannerService.class);
    private final MetaRegistry.PackageListener listener = new SourceScanListener(downloader, scanner);

    private static PackageURL createPurl(String purl) {
        try {
            return new PackageURL(purl);
        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nested
    class ListenerNotification {
        @Test
        void returnsTask_modifiedSourceLocation() {
            final var task = listener.onUpdated(PURL, Set.of(Field.SOURCE_LOCATION), Map.of(Field.SOURCE_LOCATION, SOURCE_LOCATION));

            assertThat(task).isNotEmpty();
        }

        @Test
        void returnsNothing_sourceLocationUnchanged() {
            final var task = listener.onUpdated(PURL, Set.of(Field.TITLE), Map.of(Field.SOURCE_LOCATION, SOURCE_LOCATION));

            assertThat(task).isEmpty();
        }
    }

    @Nested
    class MetadataTaskCreated {
        private static final String LICENSE1 = "license1";
        private static final String LICENSE2 = "license2";
        private final Path PATH = Path.of("source_path");
        private final Consumer<PackageAttributeEditor> task = listener.onUpdated(PURL, Set.of(Field.SOURCE_LOCATION), Map.of()).orElseThrow();
        private final PackageAttributeEditor pkg = mock(PackageAttributeEditor.class);
        private final ScanResult scanResult = mock(ScanResult.class);

        @BeforeEach
        void beforeEach() {
            when(pkg.get(Field.SOURCE_LOCATION)).thenReturn(Optional.of(SOURCE_LOCATION));
            doAnswer(a -> { // Pass download path to provided consumer
                final Consumer<Path> consumer = a.getArgument(1);
                consumer.accept(PATH);
                return null;
            }).when(downloader).download(eq(SOURCE_LOCATION), any());
            when(scanner.scan(PATH)).thenReturn(scanResult);
        }

        @Test
        void scansDownloadedSources() {
            task.accept(pkg);

            verify(downloader).download(eq(SOURCE_LOCATION), any());
            verify(scanner).scan(PATH);
        }

        @Test
        void updatesDetectedLicenses() {
            LicenseResult license1 = new Detection(LICENSE1, 100, new File("."), 1, 2);
            LicenseResult license2 = new Detection(LICENSE2, 100, new File("."), 1, 2);
            when(scanResult.getLicenses()).thenReturn(List.of(license1, license2));

            task.accept(pkg);

            verify(pkg).update(Field.DETECTED_LICENSE, SourceScanListener.MAX_SCORE, LICENSE1 + '\n' + LICENSE2);
        }

        @Test
        void weightsDetectedLicenses() {
            LicenseResult license1 = mock(LicenseResult.class);
            when(license1.getScore()).thenReturn(50);
            when(license1.getConfirmations()).thenReturn(400);
            LicenseResult license2 = mock(LicenseResult.class);
            when(license2.getScore()).thenReturn(80);
            when(license2.getConfirmations()).thenReturn(200);
            when(scanResult.getLicenses()).thenReturn(List.of(license1, license2));

            task.accept(pkg);

            final var expected = Math.round((50f * 400 + 80f * 200) / ((400 + 200) * 100) * SourceScanListener.MAX_SCORE);
            verify(pkg).update(eq(Field.DETECTED_LICENSE), eq(expected), anyString());
        }
    }
}
