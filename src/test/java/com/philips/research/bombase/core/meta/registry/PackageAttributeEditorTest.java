/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PackageAttributeEditorTest {
    private static final PackageURL PURL = toPurl("pkg:type/ns/name@version");
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final Trust TRUST = Trust.LIKELY;

    private final Package pkg = new Package(PURL);
    private final PackageAttributeEditor editor = new PackageAttributeEditor(pkg);

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    void indicatesPackageUrl() {
        assertThat(editor.getPurl()).isEqualTo(PURL);
    }

    @Test
    void tracksFieldValues() {
        final var attr = new Attribute(Field.TITLE);
        attr.setValue(TRUST, TITLE);
        pkg.add(attr);

        editor.update(Field.DESCRIPTION, TRUST, DESCRIPTION);

        assertThat(editor.get(Field.TITLE)).contains(TITLE);
        assertThat(editor.get(Field.DESCRIPTION)).contains(DESCRIPTION);
    }

    @Test
    void tracksModifiedFields() {
        editor.update(Field.DOWNLOAD_LOCATION, TRUST, null);
        editor.update(Field.TITLE, TRUST, TITLE);

        assertThat(editor.getModifiedFields()).containsExactly(Field.TITLE);
        assertThat(editor.isModified()).isTrue();
    }

    @Test
    void createsNewAttribute() {
        editor.update(Field.TITLE, TRUST, TITLE);

        assertThat(pkg.getAttributeFor(Field.TITLE).orElseThrow().getValue()).contains(TITLE);
        assertThat(editor.isModified()).isTrue();
    }

    @Test
    void snapshotsValues() {
        pkg.add(new Attribute<>(Field.SHA1));
        editor.update(Field.TITLE, TRUST, TITLE);
        final var snapshot = editor.getValues();

        editor.update(Field.TITLE, Trust.values()[TRUST.ordinal() + 1], "Changed");
        editor.update(Field.DESCRIPTION, TRUST, "Created");

        assertThat(snapshot).isEqualTo(Map.of(Field.TITLE, TITLE));
    }

    @Test
    void indicatesTrustForField() {
        pkg.add(new Attribute<>(Field.TITLE));
        editor.update(Field.TITLE, TRUST, TITLE);

        assertThat(editor.trust(Field.TITLE)).isEqualTo(TRUST);
    }

    @Test
    void minimalTrustForFieldWithoutValue() {
        assertThat(editor.trust(Field.TITLE)).isEqualTo(Trust.NONE);
    }
}
