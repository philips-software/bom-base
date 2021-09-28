/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.source_scan.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.meta.registry.Trust;
import com.philips.research.bombase.core.scanner.ScannerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SourceLicensesHarvesterTest {
    private static final PackageURL PURL = createPurl("pkg:type/ns/name@version");
    private static final String SOURCE_LOCATION = "https://example.com/sources";

    private final ScannerService scanner = mock(ScannerService.class);
    private final MetaRegistry.PackageListener listener = new SourceLicensesHarvester(scanner);

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
        private static final String LICENSE = "license";

        private final Consumer<PackageAttributeEditor> task = listener.onUpdated(PURL, Set.of(Field.SOURCE_LOCATION), Map.of()).orElseThrow();
        private final PackageAttributeEditor editor = mock(PackageAttributeEditor.class);

        @Test
        void scansLocationForLicenses() {
            when(editor.get(Field.SOURCE_LOCATION)).thenReturn(Optional.of(SOURCE_LOCATION));
            when(scanner.scanLicenses(URI.create(SOURCE_LOCATION))).thenReturn(List.of(LICENSE));

            task.accept(editor);

            verify(editor).update(Field.DETECTED_LICENSES, Trust.PROBABLY, List.of(LICENSE));
        }
    }
}
