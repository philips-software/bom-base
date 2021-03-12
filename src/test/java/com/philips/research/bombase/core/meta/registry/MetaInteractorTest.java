/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.meta.MetaInteractor;
import com.philips.research.bombase.core.meta.MetaStore;
import com.philips.research.bombase.core.meta.registry.MetaRegistry.PackageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MetaInteractorTest {
    private static final PackageURL PURL = toPurl("pkg:Type/Group/Name@Version");
    private static final String TITLE = "Title";

    final MetaStore store = mock(MetaStore.class);
    final MetaRegistry registry = new MetaRegistry(store, mock(QueuedTaskRunner.class));
    final MetaService interactor = new MetaInteractor(registry, store);
    final Package pkg = new Package(PURL);
    final PackageAttributeEditor editor = new PackageAttributeEditor(pkg);
    final PackageListener listener = mock(PackageListener.class);

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    void getsLatestScans() {
        when(store.latestScans(anyInt())).thenReturn(List.of(pkg));

        final var latest = interactor.latestScans();

        assertThat(latest).contains(PURL);
    }

    @Nested
    class ReadingPackageAttributes {
        @BeforeEach
        void beforeEach() {
            when(store.findPackage(PURL)).thenReturn(Optional.of(pkg));
        }

        @Test
        void queriesPackageDetails() {
            editor.update(Field.TITLE, 100, TITLE);

            final var values = interactor.getAttributes(PURL);

            assertThat(values).hasSize(1).containsEntry("title", TITLE);
        }

        @Test
        void queriesOnlyPackageAttributesHoldingValues() {
            pkg.add(new Attribute(Field.SHA1));

            assertThat(interactor.getAttributes(PURL)).isEmpty();
        }

        @Test
        void createsPackage_queryUnknownPackage() {
            when(store.findPackage(PURL)).thenReturn(Optional.empty());
            when(store.createPackage(PURL)).thenReturn(pkg);

            assertThat(interactor.getAttributes(PURL)).isEmpty();
        }
    }

    @Nested
    class UpdatingPackageValues {
        @BeforeEach
        void beforeEach() {
            registry.addListener(listener);
            when(store.findPackage(PURL)).thenReturn(Optional.of(pkg));
        }

        @Test
        void updatesAttributeValue() {
            editor.update(Field.TITLE, 99, "Removed");

            interactor.setAttributes(PURL, Map.of("title", TITLE));

            assertThat(editor.get(Field.TITLE)).contains(TITLE);
            verify(listener).onUpdated(PURL, Set.of(Field.TITLE), Map.of(Field.TITLE, TITLE));
        }

        @Test
        void ignoresNullValue() {
            final Map<String, Object> nullValue = new HashMap<>();
            //noinspection ConstantConditions
            nullValue.put("sha1", null);
            interactor.setAttributes(PURL, nullValue);

            assertThat(pkg.getAttributes().findAny()).isEmpty();
            verifyNoInteractions(listener);
        }
    }
}
