/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Package;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.meta.registry.Trust;
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

class AbstractRepoHarvesterTest {
    private static final PackageURL PURL = toPurl("pkg:type/namespace/name@version");
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final URI HOMEPAGE = URI.create("https://example.com/home");
    private static final String SOURCE_LOCATION = "git+https://github.com/source";
    private static final List<String> ATTRIBUTION = List.of("Attribution");
    private static final String DECLARED_LICENSE = "Declared";
    private static final String DETECTED_LICENSE = "Detected";
    private static final URI DOWNLOAD_LOCATION = URI.create("https://example.com/download");
    private static final String SHA1 = "Sha1";
    private static final String SHA256 = "Sha256";
    private static final Trust TRUST = Trust.PROBABLY;

    private final AbstractRepoHarvester.Client client = mock(AbstractRepoHarvester.Client.class);
    private final AbstractRepoHarvester harvester = new TestHarvester(client);

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    static class TestHarvester extends AbstractRepoHarvester {
        TestHarvester(Client client) {
            super(client);
        }

        @Override
        protected boolean isSupportedType(String type) {
            return type.equals(PURL.getType());
        }
    }

    @Nested
    class TaskCreation {
        @Test
        void returnsTask_noMetadataAvailable() {
            final var task = harvester.onUpdated(PURL, Set.of(), Map.of());

            assertThat(task).isNotEmpty();
        }

        @Test
        void returnsNothing_unsupportedPurlType() {
            final var task = harvester.onUpdated(toPurl("pkg:generic/name@version"), Set.of(), Map.of());

            assertThat(task).isEmpty();
        }

        @Test
        void returnsNothing_anyFieldModified() {
            final var task = harvester.onUpdated(PURL, Set.of(Field.TITLE), Map.of());

            assertThat(task).isEmpty();
        }
    }

    @Nested
    class MetadataTaskCreated {
        private final PackageAttributeEditor editor = spy(new PackageAttributeEditor(new Package(PURL)));
        private final Consumer<PackageAttributeEditor> task = harvester.onUpdated(PURL, Set.of(), Map.of()).orElseThrow();
        private final PackageMetadata response = mock(PackageMetadata.class);

        @BeforeEach
        void beforeEach() {
            when(client.read(PURL)).thenReturn(Optional.of(response));
        }

        @Test
        void harvestsMetadata() {
            when(response.trust(any())).thenReturn(TRUST);
            when(response.getSourceLocation()).thenReturn(Optional.of(SOURCE_LOCATION));
            when(response.getTitle()).thenReturn(Optional.of(TITLE));
            when(response.getDescription()).thenReturn(Optional.of(DESCRIPTION));
            when(response.getDownloadLocation()).thenReturn(Optional.of(DOWNLOAD_LOCATION));
            when(response.getHomepage()).thenReturn(Optional.of(HOMEPAGE));
            when(response.getAuthors()).thenReturn(Optional.of(ATTRIBUTION));
            when(response.getDetectedLicenses()).thenReturn(Optional.of(List.of(DETECTED_LICENSE)));
            when(response.getDeclaredLicense()).thenReturn(Optional.of(DECLARED_LICENSE));
            when(response.getSha1()).thenReturn(Optional.of(SHA1));
            when(response.getSha256()).thenReturn(Optional.of(SHA256));

            task.accept(editor);

            verify(editor).update(Field.TITLE, TRUST, TITLE);
            verify(editor).update(Field.DESCRIPTION, TRUST, DESCRIPTION);
            verify(editor).update(Field.SOURCE_LOCATION, TRUST, SOURCE_LOCATION);
            verify(editor).update(Field.DOWNLOAD_LOCATION, TRUST, DOWNLOAD_LOCATION);
            verify(editor).update(Field.HOME_PAGE, TRUST, HOMEPAGE);
            verify(editor).update(Field.ATTRIBUTION, TRUST, ATTRIBUTION);
            verify(editor).update(Field.DECLARED_LICENSE, TRUST, DECLARED_LICENSE);
            verify(editor).update(Field.DETECTED_LICENSES, TRUST, List.of(DETECTED_LICENSE));
            verify(editor).update(Field.SHA1, TRUST, SHA1);
            verify(editor).update(Field.SHA256, TRUST, SHA256);
        }

        @Test
        void skipsFieldsThatWereNotHarvested() {
            task.accept(editor);

            verifyNoInteractions(editor);
        }
    }
}
