/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.meta.MetaInteractor;

import static org.mockito.Mockito.mock;

class MetaInteractorTest {
    private static final String TYPE = "Type";
    private static final String NAME = "Group/Name";
    private static final String VERSION = "Version";
    private static final PackageURL PURL = toPurl("pkg:" + TYPE + "/" + NAME + "@" + VERSION);
    private static final Field FIELD = Field.TITLE;
    private static final Field OTHER_FIELD = Field.DESCRIPTION;
    private static final String VALUE = "Value";
    private static final String OTHER_VALUE = "Other value";

    final MetaRegistry registry = mock(MetaRegistry.class);
    final MetaService interactor = new MetaInteractor(registry);
    final Package pkg = new Package(PURL);

    static PackageURL toPurl(String uri) {
        try {
            return new PackageURL(uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    //TODO Add tests ... :-)
}
