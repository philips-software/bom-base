/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Package;
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
    private static final String NAMESPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final PackageURL PURL = toPurl(String.format("pkg:pypi/%s/%s@%s", NAMESPACE, NAME, VERSION));
    private static final String DESCRIPTION = "Description";
    private static final URI HOMEPAGE = URI.create("https://example.com/home");
    private static final String DECLARED_LICENSE = "Declared";
    private static final String SOURCE_LOCATION = "git+https://github.com/source";
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

        @Test
        void returnsNothing_notPythonPurl() {
            final var task = listener.onUpdated(toPurl("pkg:npm/name@1.0"), Set.of(), Map.of());

            assertThat(task).isEmpty();
        }
    }

    @Nested
    class MetadataTaskCreated {
        private final PackageAttributeEditor editor = spy(new PackageAttributeEditor(new Package(PURL)));
        private final Consumer<PackageAttributeEditor> task = listener.onUpdated(PURL, Set.of(), Map.of()).orElseThrow();
        private final ReleaseDefinition response = mock(ReleaseDefinition.class);

        @BeforeEach
        void beforeEach() {
            when(client.getRelease(PURL)).thenReturn(Optional.of(response));
        }

        @Test
        void harvestsMetadata() {
            when(response.getName()).thenReturn(Optional.of(NAME));
            when(response.getSummary()).thenReturn(Optional.of(DESCRIPTION));
            when(response.getHomepage()).thenReturn(Optional.of(HOMEPAGE));
            when(response.getLicense()).thenReturn(Optional.of(DECLARED_LICENSE));
            when(response.getSourceUrl()).thenReturn(Optional.of(SOURCE_LOCATION));

            task.accept(editor);

            verify(editor).update(Field.TITLE, META_SCORE, NAME);
            verify(editor).update(Field.DESCRIPTION, META_SCORE, DESCRIPTION);
            verify(editor).update(Field.HOME_PAGE, META_SCORE, HOMEPAGE);
            verify(editor).update(Field.DECLARED_LICENSE, META_SCORE, DECLARED_LICENSE);
            verify(editor).update(Field.SOURCE_LOCATION, META_SCORE, SOURCE_LOCATION);
        }
    }
}
