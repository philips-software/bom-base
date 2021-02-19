/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;
import com.philips.research.bombase.meta.MetaStore;
import com.philips.research.bombase.meta.UnknownPackageException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class MetaInteractor implements MetaService {
    private final MetaStore store;
    private final QueuedTaskRunner runner;
    private final Set<PackageListener> listeners = new HashSet<>();

    public MetaInteractor(MetaStore store, QueuedTaskRunner runner) {
        this.store = store;
        this.runner = runner;
    }

    @Override
    public void addListener(PackageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void update(PackageUrl purl, Map<Field, Object> values) {
        final var pkg = getOrCreatePackage(purl);
        pkg.setValues(values);
        notifyValueListeners(purl, values.keySet(), pkg.getValues());
    }

    @Override
    public Map<Field, Object> value(PackageUrl purl) {
        return validPackage(purl).getValues();
    }

    private Package getOrCreatePackage(PackageUrl purl) {
        return store.findPackage(purl)
                .orElseGet(() -> createPackage(purl));
    }

    private Package createPackage(PackageUrl purl) {
        final var pkg = store.createPackage(purl);
        notifyValueListeners(purl, Set.of(), Map.of());
        return pkg;
    }

    private Package validPackage(PackageUrl purl) {
        return store.findPackage(purl)
                .orElseThrow(() -> new UnknownPackageException(purl));
    }

    private void notifyValueListeners(PackageUrl purl, Set<Field> fields, Map<Field, Object> values) {
        listeners.forEach(l -> l.onUpdated(purl, fields, values).ifPresent(runner::execute));
    }
}
