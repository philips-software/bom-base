/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.core.meta.MetaStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MetaRegistryTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "Version";
    private static final PackageUrl PURL = new PackageUrl("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final Field FIELD = Field.TITLE;
    private static final String VALUE = "Value";
    private static final int SCORE = 50;

    final MetaStore store = mock(MetaStore.class);
    final MetaRegistry registry = new MetaRegistry(store, new QueuedTaskRunner(store));
    final Package pkg = new Package(PURL);

    @BeforeEach
    void beforeEach() {
        when(store.findPackage(PURL)).thenReturn(Optional.of(pkg));
        when(store.createPackage(any()))
                .thenAnswer((p) -> new Package(p.getArgument(0)));
    }

    @Test
    void editsPackageFields() {
        registry.edit(PURL, editor -> editor.update(FIELD, SCORE, VALUE));

        assertThat(pkg.getAttributeFor(FIELD).orElseThrow().getValue()).contains(VALUE);
    }

    @Nested
    class Listeners {
        final MetaRegistry.PackageListener listener = mock(MetaRegistry.PackageListener.class);

        @BeforeEach
        void beforeEach() {
            registry.addListener(listener);
        }

        @Test
        void notifiesListeners_createPackage() {
            registry.edit(PURL, pkg -> pkg.get(FIELD));

            verify(listener).onUpdated(PURL, Set.of(), Map.of());
        }

        @Test
        void notifiesListeners_modifiedFields() {
            registry.edit(PURL, pkg -> pkg.update(FIELD, SCORE, VALUE));

            verify(listener).onUpdated(PURL, Set.of(FIELD), Map.of(FIELD, VALUE));
        }

        @Test
        void executesListenerGeneratedTasks() {
            final var task = mock(Consumer.class);
            //noinspection unchecked
            when(listener.onUpdated(any(), any(), any())).thenReturn(Optional.of(task));

            registry.edit(PURL, editor -> editor.update(FIELD, SCORE, VALUE));

            //noinspection unchecked
            verify(task).accept(any(PackageAttributeEditor.class));
        }
    }
}
