/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.maven.domain;

import com.philips.research.bombase.maven.MavenService;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MavenInteractorTest {
    private static final String MAVEN = "maven";
    private static final String GROUP = "Group";
    private static final String NAME = "Name";
    private static final String VERSION = "Version";
    private static final URI PACKAGE = URI.create(String.format("pkg:%s/%s/%s@%s", MAVEN, GROUP, NAME, VERSION));

    private final MetaService meta = mock(MetaService.class);
    private final MavenService interactor = spy(new MavenInteractor(meta));

    @Test
    void updatesPackageMetadata() {
        interactor.updatePackage(PACKAGE, GROUP, NAME, VERSION);
    }

    @Nested
    class TriggerFromMetaUpdates {
        private MetaService.PackageListener listener;

        @BeforeEach
        void beforeEach() {
            final var captor = ArgumentCaptor.forClass(MetaService.PackageListener.class);
            doNothing().when(meta).addListener(captor.capture());
            interactor.init();
            listener = captor.getValue();
        }

        @Test
        void registersForMetadataUpdates() {
            assertThat(listener).isNotNull();
        }

        @Test
        void updatesMetadata_typeUpdated() {
            when(meta.value(PACKAGE)).thenReturn(Map.of(Field.TYPE, MAVEN, Field.NAME, GROUP + '/' + NAME, Field.VERSION, VERSION));

            listener.onUpdated(PACKAGE, Set.of(Field.TYPE), Map.of(Field.TYPE, MAVEN))
                    .ifPresent(Runnable::run);

            verify(interactor).updatePackage(PACKAGE, GROUP, NAME, VERSION);
        }
    }
}
