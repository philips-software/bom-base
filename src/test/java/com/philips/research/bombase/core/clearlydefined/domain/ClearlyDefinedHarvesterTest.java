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

class ClearlyDefinedHarvesterTest {
    private static final String TYPE = "maven";
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
    private static final int MAX_SCORE = 70;
    private static final int META_SCORE = 73;
    private static final int META_TOTAL_SCORE = Math.round((META_SCORE * MAX_SCORE) / 100f);
    private static final int LICENSE_SCORE = 42;
    private static final int LICENSE_TOTAL_SCORE = Math.round((LICENSE_SCORE * MAX_SCORE) / 100f);

    private final ClearlyDefinedClient client = mock(ClearlyDefinedClient.class);
    private final ClearlyDefinedHarvester listener = new ClearlyDefinedHarvester(client);

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
        void returnsNothing_unsupportedPurlType() {
            final var task = listener.onUpdated(toPurl("pkg:generic/name@version"), Set.of(), Map.of());

            assertThat(task).isEmpty();
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
            when(client.getPackageDefinition(PURL)).thenReturn(Optional.of(response));
        }

        @Test
        void harvestsMetadata() {
            when(response.getDescribedScore()).thenReturn(META_SCORE);
            when(response.getLicensedScore()).thenReturn(LICENSE_SCORE);
            when(response.getSourceLocation()).thenReturn(Optional.of(SOURCE_LOCATION));
            when(response.getTitle()).thenReturn(Optional.of(NAME));
            when(response.getDownloadLocation()).thenReturn(Optional.of(DOWNLOAD_LOCATION));
            when(response.getHomepage()).thenReturn(Optional.of(HOMEPAGE));
            when(response.getAuthors()).thenReturn(Optional.of(ATTRIBUTION));
            when(response.getDetectedLicenses()).thenReturn(Optional.of(List.of(DETECTED_LICENSE)));
            when(response.getDeclaredLicense()).thenReturn(Optional.of(DECLARED_LICENSE));
            when(response.getSha1()).thenReturn(Optional.of(SHA1));
            when(response.getSha256()).thenReturn(Optional.of(SHA256));

            task.accept(pkg);

            verify(pkg).update(Field.TITLE, META_TOTAL_SCORE, NAME);
            verify(pkg).update(Field.SOURCE_LOCATION, META_TOTAL_SCORE, SOURCE_LOCATION);
            verify(pkg).update(Field.DOWNLOAD_LOCATION, META_TOTAL_SCORE, DOWNLOAD_LOCATION);
            verify(pkg).update(Field.HOME_PAGE, META_TOTAL_SCORE, HOMEPAGE);
            verify(pkg).update(Field.ATTRIBUTION, META_TOTAL_SCORE, ATTRIBUTION);
            verify(pkg).update(Field.DECLARED_LICENSE, META_TOTAL_SCORE, DECLARED_LICENSE);
            verify(pkg).update(Field.DETECTED_LICENSES, LICENSE_TOTAL_SCORE, List.of(DETECTED_LICENSE));
            verify(pkg).update(Field.SHA1, META_TOTAL_SCORE, SHA1);
            verify(pkg).update(Field.SHA256, META_TOTAL_SCORE, SHA256);
        }
    }
}
