/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PyPiHarvesterTest {
    private static final String TYPE = "type";
    private static final String NAMESPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final PackageURL PURL = toPurl(String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION));
    private static final String DESCRIPTION = "Description";
    private static final URI HOMEPAGE = URI.create("https://example.com/home");
    private static final String DECLARED_LICENSE = "Declared";
    private static final URI SOURCE_LOCATION = URI.create("git+https://github.com/source");
    private static final int META_SCORE = 80;

    private final PyPiClient client = mock(PyPiClient.class);
    private final PyPiHarvester listener = new PyPiHarvester(client);

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
        private final ReleaseDefinition response = mock(ReleaseDefinition.class);

        @BeforeEach
        void beforeEach() {
            when(client.getRelease(PURL)).thenReturn(Optional.of(response));
        }

        @Test
        void harvestsMetadata() {
            when(response.getSummary()).thenReturn(Optional.of(DESCRIPTION));
            when(response.getHomepage()).thenReturn(Optional.of(HOMEPAGE));
            when(response.getLicense()).thenReturn(Optional.of(DECLARED_LICENSE));
            when(response.getSourceUrl()).thenReturn(Optional.of(SOURCE_LOCATION));

            task.accept(pkg);

            verify(pkg).update(Field.DESCRIPTION, META_SCORE, DESCRIPTION);
            verify(pkg).update(Field.HOME_PAGE, META_SCORE, HOMEPAGE);
            verify(pkg).update(Field.DECLARED_LICENSE, META_SCORE, DECLARED_LICENSE);
            verify(pkg).update(Field.SOURCE_LOCATION, META_SCORE, SOURCE_LOCATION);
        }
    }
}
