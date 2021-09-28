/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.license_cleaner.LicenseCleanerStore;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Package;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.meta.registry.Trust;
import com.philips.research.bombase.core.scanner.ScannerException;
import com.philips.research.bombase.core.scanner.ScannerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LicenseCleanerTest {
    private static final String LICENSE_URL = "https://example.com/license";
    private static final PackageURL PURL = purlOf("pkg:generic/name@version");

    private final LicenseCleanerStore store = mock(LicenseCleanerStore.class);
    private final ScannerService scanner = mock(ScannerService.class);
    private final LicenseCleaner cleaner = new LicenseCleaner(store, scanner);

    private static PackageURL purlOf(String purl) {
        try {
            return new PackageURL(purl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Package URL");
        }
    }

    @Test
    void createsTaskOnDeclaredLicenseUpdateWithURL() {
        final var task = cleaner.onUpdated(PURL, Set.of(Field.DECLARED_LICENSE), Map.of(Field.DECLARED_LICENSE, LICENSE_URL));

        assertThat(task).isNotEmpty();
    }

    @Test
    void noTask_noLicenseUpdate() {
        final var allOtherFields = Arrays.stream(Field.values())
                .filter(field -> field != Field.DECLARED_LICENSE)
                .collect(Collectors.toSet());

        final var task = cleaner.onUpdated(PURL, allOtherFields, Map.of(Field.DECLARED_LICENSE, LICENSE_URL));

        assertThat(task).isEmpty();
    }

    @Test
    void noTask_noUrlInLicenseUpdate() {
        final var task = cleaner.onUpdated(PURL, Set.of(Field.DECLARED_LICENSE), Map.of(Field.DECLARED_LICENSE, "No URL here"));

        assertThat(task).isEmpty();
    }

    @Nested
    class LicenseValueUpdate {
        private static final String LICENSE = "License";
        private static final String PREFIX = "Prefix ";
        private static final String POSTFIX = " Postfix";
        private final Trust TRUST = Trust.PROBABLY;

        private final Package pkg = new Package(PURL);
        private final PackageAttributeEditor editor = spy(new PackageAttributeEditor(pkg));
        private final Consumer<PackageAttributeEditor> task = cleaner
                .onUpdated(PURL, Set.of(Field.DECLARED_LICENSE), Map.of(Field.DECLARED_LICENSE, LICENSE_URL)).orElseThrow();

        @Test
        void replacesUrlByScanningLicenses() {
            editor.update(Field.DECLARED_LICENSE, TRUST, PREFIX + LICENSE_URL + POSTFIX);
            when(scanner.scanLicenses(URI.create(LICENSE_URL))).thenReturn(List.of(LICENSE));

            task.accept(editor);

            verify(editor).update(Field.DECLARED_LICENSE, TRUST, PREFIX + LICENSE + POSTFIX);
        }

        @Test
        void replacesUrlByCuratedLicense() {
            editor.update(Field.DECLARED_LICENSE, TRUST, LICENSE_URL);
            when(store.findCuration(LICENSE_URL)).thenReturn(Optional.of(LICENSE));

            task.accept(editor);

            verify(editor).update(Field.DECLARED_LICENSE, TRUST, LICENSE);
        }

        @Test
        void replacesMultipleUrls() {
            editor.update(Field.DECLARED_LICENSE, TRUST, LICENSE_URL + " AND " + LICENSE_URL);
            when(scanner.scanLicenses(URI.create(LICENSE_URL))).thenReturn(List.of(LICENSE));

            task.accept(editor);

            verify(editor).update(Field.DECLARED_LICENSE, TRUST, LICENSE + " AND " + LICENSE);
        }

        @Test
        void ignoresFailedLicenseScans() {
            editor.update(Field.DECLARED_LICENSE, TRUST, LICENSE_URL);
            when(scanner.scanLicenses(URI.create(LICENSE_URL))).thenThrow(new ScannerException("Test"));

            task.accept(editor);

            verify(editor, times(2)).update(Field.DECLARED_LICENSE, TRUST, LICENSE_URL);
        }

        @Test
        void ignoresScansWithoutLicenseResult() {
            editor.update(Field.DECLARED_LICENSE, TRUST, LICENSE_URL);
            when(scanner.scanLicenses(URI.create(LICENSE_URL))).thenReturn(List.of());

            task.accept(editor);

            verify(editor, times(2)).update(Field.DECLARED_LICENSE, TRUST, LICENSE_URL);
        }
    }
}
