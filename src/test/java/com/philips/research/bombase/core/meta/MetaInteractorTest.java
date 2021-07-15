/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.UnknownPackageException;
import com.philips.research.bombase.core.meta.registry.*;
import com.philips.research.bombase.core.meta.registry.Package;
import com.philips.research.bombase.core.meta.registry.MetaRegistry.PackageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MetaInteractorTest {
    private static final String TYPE = "type";
    private static final String NAMESPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
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
    void createsNewPackage() {
        when(store.createPackage(PURL)).thenReturn(pkg);

        interactor.createPackage(PURL);

        verify(store).createPackage(PURL);
    }

    @Test
    void getsLatestScans() {
        when(store.latestScans(anyInt())).thenReturn(List.of(pkg));

        final var latest = interactor.latestScans();

        assertThat(latest.get(0).purl).isEqualTo(PURL);
    }

    @Test
    void searchesForPackages() {
        when(store.findPackages(TYPE, NAMESPACE, NAME, VERSION)).thenReturn(List.of(pkg));

        final var found = interactor.search(TYPE, NAMESPACE, NAME, VERSION);

        assertThat(found.get(0).purl).isEqualTo(PURL);
    }

    @Nested
    class ReadingPackageAttributes {
        @BeforeEach
        void beforeEach() {
            when(store.findPackage(PURL)).thenReturn(Optional.of(pkg));
        }

        @Test
        void queriesPackageDetails() {
            editor.update(Field.TITLE, Trust.TRUTH, TITLE);

            final var values = interactor.getAttributes(PURL);

            final var attr = values.get(Field.TITLE.name().toLowerCase());
            assert attr.value != null;
            assertThat(attr.value).isEqualTo(TITLE);
        }

        @Test
        void throws_queryUnknownPackage() {
            when(store.findPackage(PURL)).thenReturn(Optional.empty());
            when(store.createPackage(PURL)).thenReturn(pkg);

            assertThatThrownBy(() -> interactor.getAttributes(PURL))
                    .isInstanceOf(UnknownPackageException.class);
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
            editor.update(Field.TITLE, Trust.CERTAIN, "Removed");

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
