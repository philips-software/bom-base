package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.MetaService;
import com.philips.research.metabase.activity.MetaStore;
import com.philips.research.metabase.activity.UnknownPackageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetaInteractorTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "Version";
    private static final URI PACKAGE = URI.create("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final String INT_FIELD = "IntegerField";
    private static final String STRING_FIELD = "StringField";
    private static final Integer INT_VALUE = 42;
    private static final String STRING_VALUE = "TextString";

    final MetaStore store = mock(MetaStore.class);
    final MetaService interactor = new MetaInteractor(store);
    final Package pkg = new Package(TYPE, NAME, VERSION);

    @BeforeEach
    void beforeEach() {
//        interactor.registerField(STRING_FIELD, String.class);
//        interactor.registerField(INT_FIELD, Integer.class);
    }

    @Test
    void storesPackageField() {
        when(store.findPackage(TYPE,NAME,VERSION)).thenReturn(Optional.of(pkg));

//        interactor.storeFieldValue(PACKAGE, STRING_FIELD, STRING_VALUE);
        final var value = interactor.value(PACKAGE);

        assertThat(value).containsEntry(STRING_FIELD, STRING_VALUE);
    }

    @Test
    void createsPackageForField() {
        when(store.createPackage(TYPE, NAME, VERSION)).thenReturn(pkg);

//        interactor.storeFieldValue(PACKAGE, INT_FIELD, INT_VALUE);
        final var value = interactor.value(PACKAGE);

        assertThat(value).containsEntry(INT_FIELD, INT_VALUE);
    }

    @Test
    void throws_fieldForUnknownPackage() {
        assertThatThrownBy(() -> interactor.value(PACKAGE))
                .isInstanceOf(UnknownPackageException.class)
                .hasMessageContaining(PACKAGE.toString());
    }

    @Test
    void emptyFieldsAreEmpty() {
//        interactor.storeFieldValue(PACKAGE, INT_FIELD, 0);

        assertThat(interactor.value(PACKAGE)).isEmpty();
    }

}
