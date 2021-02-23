/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.*;
import com.philips.research.bombase.meta.MetaService.PackageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    private static final PackageUrl PURL = new PackageUrl("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final Field FIELD = Field.TITLE;
    private static final Field OTHER_FIELD = Field.DESCRIPTION;
    private static final String VALUE = "Value";
    private static final String OTHER_VALUE = "Other value";
    private static final Origin MY_ORIGIN = Origin.API;
    private static final Origin OTHER_ORIGIN = Origin.CLEARLY_DEFINED;

    final MetaStore store = mock(MetaStore.class);
    final MetaService interactor = new MetaInteractor(store, new QueuedTaskRunner());
    final Package pkg = new Package(PURL);

    @BeforeEach
    void beforeEach() {
        when(store.findPackage(PURL)).thenReturn(Optional.of(pkg));
        when(store.createPackage(any()))
                .thenAnswer((p) -> new Package(p.getArgument(0)));
    }

    @Test
    void throws_getValuesForUnknownPackage() {
        assertThatThrownBy(() -> interactor.valuesOf(new PackageUrl("type/unknown@version")))
                .isInstanceOf(UnknownPackageException.class);
    }

    @Test
    void updateStoresValue() {
        interactor.update(MY_ORIGIN, PURL, Map.of(FIELD, VALUE));

        assertThat(interactor.valuesOf(PURL)).containsEntry(FIELD, VALUE);
    }

    @Nested
    class Listeners {
        final PackageListener listener = mock(PackageListener.class);

        @BeforeEach
        void beforeEach() {
            interactor.addListener(OTHER_ORIGIN, listener);
        }

        @Test
        void notifiesListeners_createPackage() {
            when(store.findPackage(any())).thenReturn(Optional.empty());
            final var triggered = new AtomicBoolean(false);
            when(listener.onUpdated(PURL, Set.of(), Map.of())).thenReturn(Optional.of(() -> triggered.set(true)));

            interactor.update(MY_ORIGIN, PURL, Map.of());

            assertThat(triggered.get()).isTrue();
        }

        @Test
        void notifiesListenersOnce_updateFields() {
            final var triggered = new AtomicInteger(0);
            pkg.setValue(MY_ORIGIN, OTHER_FIELD, OTHER_VALUE);
            //noinspection unchecked
            final ArgumentCaptor<Map<Field, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(listener.onUpdated(eq(PURL), eq(Set.of(FIELD, OTHER_FIELD)), captor.capture()))
                    .thenReturn(Optional.of(triggered::incrementAndGet));

            interactor.update(MY_ORIGIN, PURL, Map.of(FIELD, VALUE, OTHER_FIELD, OTHER_VALUE));

            assertThat(captor.getValue()).containsEntry(FIELD, VALUE);
            assertThat(triggered.get()).isEqualTo(1);
        }

        @Test
        void ignoresOriginatingListener_updateFields() {
            interactor.update(OTHER_ORIGIN, PURL, Map.of(FIELD, VALUE));

            verify(listener, never()).onUpdated(any(), any(), any());
        }

        @Test
        void executesListenerGeneratedTasks() {
            final var task = mock(Runnable.class);
            when(listener.onUpdated(any(), any(), any())).thenReturn(Optional.of(task));

            interactor.update(MY_ORIGIN, PURL, Map.of(FIELD, VALUE));

            verify(task).run();
        }
    }
}
