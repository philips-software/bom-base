/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class PackageAttributeEditorTest {
    private static final PackageURL PURL = toPurl("pkg:type/ns/name@version");
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final int SCORE = 50;

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
    void tracksFieldValues() {
        final var attr = new Attribute(Field.TITLE);
        attr.setValue(SCORE, TITLE);
        pkg.add(attr);

        editor.update(Field.DESCRIPTION, SCORE, DESCRIPTION);

        assertThat(editor.get(Field.TITLE)).contains(TITLE);
        assertThat(editor.get(Field.DESCRIPTION)).contains(DESCRIPTION);
    }

    @Test
    void tracksModifiedFields() {
        editor.update(Field.DOWNLOAD_LOCATION, SCORE, null);
        editor.update(Field.SOURCE_LOCATION, 0, URI.create("http://example.com/source"));
        editor.update(Field.TITLE, SCORE, TITLE);

        assertThat(editor.getModifiedFields()).containsExactly(Field.TITLE);
    }

    @Test
    void createsNewAttribute() {
        editor.update(Field.TITLE, SCORE, TITLE);

        assertThat(pkg.getAttributeFor(Field.TITLE).orElseThrow().getValue()).contains(TITLE);
    }
}
