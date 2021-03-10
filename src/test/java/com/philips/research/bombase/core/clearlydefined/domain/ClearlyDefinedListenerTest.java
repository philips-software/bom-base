/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import org.junit.jupiter.api.BeforeEach;
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

class ClearlyDefinedListenerTest {
    private static final String TYPE = "type";
    private static final String NAMESPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final PackageURL PURL = toPurl(String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION));
    private static final URI HOMEPAGE = URI.create("https://example.com/home");
    private static final URI DOWNLOAD_LOCATION = URI.create("https://example.com/download");
    private static final URI SOURCE_LOCATION = URI.create("git+https://github.com/source");
    private static final List<String> ATTRIBUTION = List.of("Attribution");
    private static final String DECLARED_LICENSE = "Declared";
    private static final String DETECTED_LICENSE = "Detected";
    private static final String SHA1 = "Sha1";
    private static final String SHA256 = "Sha256";
    private static final int SCORE = 70;

    private final ClearlyDefinedClient client = mock(ClearlyDefinedClient.class);
    private final ClearlyDefinedListener listener = new ClearlyDefinedListener(client);

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nested
    class ListenerNotification {
        @Test
        void returnsTask_noMetadataAvailable() {
            final var task = listener.onUpdated(PURL, Set.of(), Map.of());

            assertThat(task).isNotEmpty();
        }

        @Test
        void returnsNothing_anyFieldModified() {
            final var task = listener.onUpdated(PURL, Set.of(Field.TITLE), Map.of());

            assertThat(task).isEmpty();
        }
    }

    @Nested
    class MetadataTaskCreated {
        private final PackageAttributeEditor pkg = mock(PackageAttributeEditor.class);
        private final Consumer<PackageAttributeEditor> task = listener.onUpdated(PURL, Set.of(), Map.of()).orElseThrow();
        private final PackageDefinition response = mock(PackageDefinition.class);

        @BeforeEach
        void beforeEach() {
            when(client.getPackageDefinition(TYPE, TYPE, NAMESPACE, NAME, VERSION)).thenReturn(Optional.of(response));
        }

        @Test
        void harvestsMetadata() {
            when(response.getSourceLocation()).thenReturn(Optional.of(SOURCE_LOCATION));
            when(response.getDownloadLocation()).thenReturn(Optional.of(DOWNLOAD_LOCATION));
            when(response.getHomepage()).thenReturn(Optional.of(HOMEPAGE));
            when(response.getAuthors()).thenReturn(Optional.of(ATTRIBUTION));
            when(response.getDetectedLicenses()).thenReturn(List.of(DETECTED_LICENSE));
            when(response.getDeclaredLicense()).thenReturn(Optional.of(DECLARED_LICENSE));
            when(response.getSha1()).thenReturn(Optional.of(SHA1));
            when(response.getSha256()).thenReturn(Optional.of(SHA256));

            task.accept(pkg);

            verify(pkg).update(Field.SOURCE_LOCATION, SCORE, SOURCE_LOCATION);
            verify(pkg).update(Field.DOWNLOAD_LOCATION, SCORE, DOWNLOAD_LOCATION);
            verify(pkg).update(Field.HOME_PAGE, SCORE, HOMEPAGE);
            verify(pkg).update(Field.ATTRIBUTION, SCORE, ATTRIBUTION);
            verify(pkg).update(Field.DETECTED_LICENSE, SCORE, DETECTED_LICENSE);
            verify(pkg).update(Field.DECLARED_LICENSE, SCORE, DECLARED_LICENSE);
            verify(pkg).update(Field.SHA1, SCORE, SHA1);
            verify(pkg).update(Field.SHA256, SCORE, SHA256);
        }

        @Test
        void concatenatesDetectedLicenses() {
            when(response.getDetectedLicenses()).thenReturn(List.of(DECLARED_LICENSE, DETECTED_LICENSE));

            task.accept(pkg);

            verify(pkg).update(Field.DETECTED_LICENSE, SCORE, String.format("%s AND %s", DECLARED_LICENSE, DETECTED_LICENSE));
        }
    }
}
