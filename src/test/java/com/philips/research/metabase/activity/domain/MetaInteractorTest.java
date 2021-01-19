package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.Field;
import com.philips.research.metabase.activity.MetaService;
import com.philips.research.metabase.activity.MetaService.PackageListener;
import com.philips.research.metabase.activity.MetaStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetaInteractorTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "Version";
    private static final URI PACKAGE = URI.create("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final URI OTHER_PACKAGE = URI.create("pkg:other/group/name@version");
    private static final Field FIELD = Field.TITLE;
    private static final Double VALUE = 1.23;

    final MetaStore store = mock(MetaStore.class);
    final MetaService interactor = new MetaInteractor(store);
    final Package pkg = new Package(TYPE, NAME, VERSION);
    final PackageListener listener = mock(PackageListener.class);

    @BeforeAll
    static void beforeAll() {
        Package.register(FIELD, Number.class);
    }

    @BeforeEach
    void beforeEach() {
        when(store.findPackage(TYPE, NAME, VERSION)).thenReturn(Optional.of(pkg));
        when(store.createPackage(any(), any(), any()))
                .thenAnswer((p) -> new Package(p.getArgument(0), p.getArgument(1), p.getArgument(2)));
    }

    @Test
    void updateStoresValue() {
        interactor.update(PACKAGE, Map.of(FIELD, VALUE));

        assertThat(interactor.value(PACKAGE)).isEqualTo(Map.of(FIELD, VALUE));
    }

    @Test
    void updateNotifiesListeners() {
        final var triggered = new AtomicInteger(0);
        when(listener.onUpdated(PACKAGE, FIELD, VALUE)).thenReturn(Optional.of(triggered::incrementAndGet));
        interactor.addListener(listener);

        interactor.update(PACKAGE, Map.of(FIELD, VALUE));
        interactor.update(OTHER_PACKAGE, Map.of(FIELD, VALUE));

        assertThat(triggered.get()).isEqualTo(1);
    }
}
