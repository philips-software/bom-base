package com.philips.research.metabase.activity.domain;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PackageTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "1.2.3";
    private static final String FIELD = "Field";
    private static final String VALUE = "Value";

    final Package pkg = new Package(TYPE, NAME, VERSION);

    @Test
    void createsInstance() {
        assertThat(pkg.getType()).isEqualTo(TYPE);
        assertThat(pkg.getName()).isEqualTo(NAME);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
        assertThat(pkg.getFields()).isEmpty();
    }

    @Test
    void getsNewFieldByName() {
        final var field = pkg.getField(FIELD);

        assertThat(field).isInstanceOf(FieldValue.class);
    }

    @Test
    void getsAllFields() {
        final var field = pkg.getField(FIELD);

        final var fields = pkg.getFields();

        assertThat(fields).containsEntry(FIELD, field);
    }

    @Test
    void throws_modifyingFieldsDirectly() {
       assertThatThrownBy(()->pkg.getFields().put(FIELD, new FieldValue()))
               .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getsExistingFieldByName() {
        final var pkg = new Package(TYPE, NAME, VERSION);
        final var created = pkg.getField(FIELD);
        created.setValue(VALUE);

        final var field = pkg.getField(FIELD);

        assertThat(field.getValue()).contains(VALUE);
    }

    @Test
    void createsInstanceFromPackageUri() {
        final var purl = URI.create(String.format("pkg:%s/%s@%s", TYPE, NAME, VERSION));
        final var pkg = Package.from(purl);

        assertThat(pkg.getType()).isEqualTo(TYPE);
        assertThat(pkg.getName()).isEqualTo(NAME);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }
}
