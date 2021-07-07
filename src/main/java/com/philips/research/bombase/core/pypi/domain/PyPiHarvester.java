/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class PyPiHarvester implements MetaRegistry.PackageListener {
    private static final Logger LOG = LoggerFactory.getLogger(PyPiHarvester.class);
    private static final int PYPI_SCORE = 80;

    private final PyPiClient client;

    @Autowired
    public PyPiHarvester() {
        this(new PyPiClient());
    }

    PyPiHarvester(PyPiClient client) {
        this.client = client;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, ?> values) {
        if (!purl.getType().equals("pypi") || !updated.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pkg -> harvest(purl, pkg));
    }

    private void harvest(PackageURL purl, PackageAttributeEditor pkg) {
        client.getRelease(purl).ifPresentOrElse(release -> {
            storeField(pkg, Field.TITLE, release.getName());
            storeField(pkg, Field.DESCRIPTION, release.getSummary());
            storeField(pkg, Field.HOME_PAGE, release.getHomepage());
            storeField(pkg, Field.DECLARED_LICENSE, release.getLicense());
            storeField(pkg, Field.SOURCE_LOCATION, release.getSourceUrl());
        }, () -> LOG.info("No metadata for {}", purl));
    }

    private <T> void storeField(PackageAttributeEditor pkg, Field field, Optional<T> value) {
        value.ifPresent(v -> pkg.update(field, PYPI_SCORE, v));
    }
}
