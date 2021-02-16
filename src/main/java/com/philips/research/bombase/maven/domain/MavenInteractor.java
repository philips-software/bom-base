/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.maven.domain;

import com.philips.research.bombase.maven.MavenService;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;

import java.net.URI;
import java.util.Optional;

public class MavenInteractor implements MavenService {
    private final MetaService meta;

    public MavenInteractor(MetaService meta) {
        this.meta = meta;
    }

    @Override
    public void init() {
        meta.addListener((purl, fields, values) -> {
            if (!fields.contains(Field.TYPE) || !values.get(Field.TYPE).equals("maven")) {
                return Optional.empty();
            }

            return Optional.of(() -> {
                final var value = meta.value(purl);
                final var name = (String) value.get(Field.NAME);
                final var pos = name.indexOf('/');
                final var group = (pos >= 0) ? name.substring(0, pos) : "";
                final var artifact = (pos >= 0) ? name.substring(pos + 1) : name;
                final var version = (String) value.get(Field.VERSION);
                this.updatePackage(purl, group, artifact, version);
            });
        });
    }

    @Override
    public void updatePackage(URI purl, String group, String artifact, String version) {

    }
}
