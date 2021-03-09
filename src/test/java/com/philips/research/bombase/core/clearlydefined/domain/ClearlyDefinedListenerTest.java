/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.PackageModifier;
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
    private static final PackageUrl PURL = new PackageUrl(String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION));
    private static final URI HOMEPAGE = URI.create("https://example.com/home");
    private static final URI DOWNLOAD_LOCATION = URI.create("https://example.com/download");
    private static final URI SOURCE_LOCATION = URI.create("git+https://github.com/source");
    private static final List<String> ATTRIBUTION = List.of("Attribution");
    private static final String DECLARED_LICENSE = "Declared";
    private static final String DETECTED_LICENSE = "Detected";
    private static final String SHA1 = "Sha1";
    private static final String SHA256 = "Sha256";

    private final ClearlyDefinedClient client = mock(ClearlyDefinedClient.class);
    private final ClearlyDefinedListener listener = new ClearlyDefinedListener(client);

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
        private final PackageModifier modifier = mock(PackageModifier.class);
        private final Consumer<PackageModifier> task = listener.onUpdated(PURL, Set.of(), Map.of()).orElseThrow();
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
            when(response.getAuthors()).thenReturn(ATTRIBUTION);
            when(response.getDetectedLicenses()).thenReturn(List.of(DETECTED_LICENSE));
            when(response.getDeclaredLicense()).thenReturn(Optional.of(DECLARED_LICENSE));

            task.accept(modifier);

            verify(modifier).update(Field.SOURCE_LOCATION, SOURCE_LOCATION);
            verify(modifier).update(Field.DOWNLOAD_LOCATION, DOWNLOAD_LOCATION);
            verify(modifier).update(Field.HOME_PAGE, HOMEPAGE);
            //TODO Temporarily disabled until lists are properly handled by fields
//            verify(modifier).update(Field.ATTRIBUTION, ATTRIBUTION);
            verify(modifier).update(Field.DETECTED_LICENSE, DETECTED_LICENSE);
            verify(modifier).update(Field.DECLARED_LICENSE, DECLARED_LICENSE);
        }

        //TODO Concatenation of licenses
    }
}
