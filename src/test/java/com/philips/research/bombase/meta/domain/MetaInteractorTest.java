/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;
import com.philips.research.bombase.meta.MetaService.PackageListener;
import com.philips.research.bombase.meta.MetaStore;
import com.philips.research.bombase.meta.UnknownPackageException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MetaInteractorTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "Version";
    private static final URI PACKAGE = URI.create("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final Field FIELD = Field.TITLE;
    private static final Field OTHER_FIELD = Field.DESCRIPTION;
    private static final Integer VALUE = 42;
    private static final Double OTHER_VALUE = 1.23;

    final MetaStore store = mock(MetaStore.class);
    final MetaService interactor = new MetaInteractor(store, new QueuedTaskRunner());
    final Package pkg = new Package(TYPE, NAME, VERSION);

    @BeforeAll
    static void beforeAll() {
        Package.register(FIELD, Number.class);
        Package.register(OTHER_FIELD, Number.class);
    }

    @BeforeEach
    void beforeEach() {
        when(store.findPackage(TYPE, NAME, VERSION)).thenReturn(Optional.of(pkg));
        when(store.createPackage(any(), any(), any()))
                .thenAnswer((p) -> new Package(p.getArgument(0), p.getArgument(1), p.getArgument(2)));
    }

    @Test
    void throws_getValuesForUnknownPackage() {
        assertThatThrownBy(() -> interactor.value(URI.create("type/unknown@version")))
                .isInstanceOf(UnknownPackageException.class);
    }

    @Test
    void updateStoresValue() {
        interactor.update(PACKAGE, Map.of(FIELD, VALUE));

        assertThat(interactor.value(PACKAGE)).containsEntry(FIELD, VALUE);
    }

    @Nested
    class Listeners {
        final PackageListener listener = mock(PackageListener.class);

        @BeforeEach
        void beforeEach() {
            interactor.addListener(listener);
        }

        @Test
        void notifiesListeners_createPackage() {
            when(store.findPackage(any(), any(), any())).thenReturn(Optional.empty());
            final var triggered = new AtomicBoolean(false);
            final var values = Map.of(Field.TYPE, TYPE, Field.NAME, NAME, Field.VERSION, VERSION);
            when(listener.onUpdated(PACKAGE, values.keySet(), values)).thenReturn(Optional.of(() -> triggered.set(true)));

            interactor.update(PACKAGE, Map.of());

            assertThat(triggered.get()).isTrue();
        }

        @Test
        void notifiesListenersOnce_updateFields() {
            final var triggered = new AtomicInteger(0);
            pkg.setValue(OTHER_FIELD, OTHER_VALUE);
            //noinspection unchecked
            final ArgumentCaptor<Map<Field, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(listener.onUpdated(eq(PACKAGE), eq(Set.of(FIELD, OTHER_FIELD)), captor.capture()))
                    .thenReturn(Optional.of(triggered::incrementAndGet));

            interactor.update(PACKAGE, Map.of(FIELD, VALUE, OTHER_FIELD, OTHER_VALUE));

            assertThat(captor.getValue()).containsEntry(FIELD, VALUE);
            assertThat(triggered.get()).isEqualTo(1);
        }

        @Test
        void executesListenerGeneratedTasks() {
            final var task = mock(Runnable.class);
            when(listener.onUpdated(any(), any(), any())).thenReturn(Optional.of(task));

            interactor.update(PACKAGE, Map.of(FIELD, VALUE));

            verify(task).run();
        }
    }
}
