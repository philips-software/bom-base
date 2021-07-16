/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.MetaStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MetaRegistryTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "Version";
    private static final PackageURL PURL = toPurl("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final Field FIELD = Field.TITLE;
    private static final String VALUE = "Value";
    private static final Trust TRUST = Trust.PROBABLY;

    final MetaStore store = mock(MetaStore.class);
    final MetaRegistry registry = new MetaRegistry(store, new QueuedTaskRunner(store));
    final Package pkg = new Package(PURL);

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @BeforeEach
    void beforeEach() {
        when(store.findPackage(PURL)).thenReturn(Optional.of(pkg));
        when(store.createPackage(any()))
                .thenAnswer((p) -> new Package(p.getArgument(0)));
    }

    @Test
    void readsPackageFieldValues() {
        registry.edit(PURL, pkg -> pkg.update(FIELD, TRUST, VALUE));

        final var values = registry.getAttributeValues(PURL).orElseThrow();

        assertThat(values.get(FIELD).getScore()).isEqualTo(TRUST.getScore());
        assertThat(values.get(FIELD).getValue()).isEqualTo(Optional.of(VALUE));
    }

    @Test
    void editsPackageFields() {
        registry.edit(PURL, editor -> editor.update(FIELD, TRUST, VALUE));

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
            when(store.findPackage(PURL)).thenReturn(Optional.empty());
            registry.edit(PURL, pkg -> pkg.get(FIELD));

            verify(listener).onUpdated(PURL, Set.of(), Map.of());
        }

        @Test
        void notifiesListeners_modifiedFields() {
            registry.edit(PURL, pkg -> pkg.update(FIELD, TRUST, VALUE));

            verify(listener).onUpdated(PURL, Set.of(FIELD), Map.of(FIELD, VALUE));
        }

        @Test
        void noNotifications_noModifications() {
            registry.edit(PURL, pkg -> { /* Nothing */ });

            verifyNoInteractions(listener);
        }

        @Test
        void executesListenerGeneratedTasks() {
            final var task = mock(Consumer.class);
            //noinspection unchecked
            when(listener.onUpdated(any(), any(), any())).thenReturn(Optional.of(task));

            registry.edit(PURL, editor -> editor.update(FIELD, TRUST, VALUE));

            //noinspection unchecked
            verify(task).accept(any(PackageAttributeEditor.class));
        }

        @Test
        void cascadesEditsToListenersUntilNoMoreEditsAreMade() {
            final var counter = new AtomicInteger(1);
            final var task = mock(Consumer.class);
            //noinspection unchecked
            when(listener.onUpdated(any(), any(), any())).thenReturn(Optional.of(task));
            //noinspection unchecked
            doAnswer(param -> {
                final PackageAttributeEditor editor = param.getArgument(0);
                final var offset = counter.incrementAndGet();
                if (offset < Trust.values().length) {
                    editor.update(FIELD, Trust.values()[offset], VALUE + offset);
                }
                return null;
            }).when(task).accept(any());

            registry.edit(PURL, editor -> editor.update(FIELD, TRUST, VALUE));

            //noinspection unchecked
            verify(task, times(Trust.values().length - 1)).accept(any());
        }
    }
}
