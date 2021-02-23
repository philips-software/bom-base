/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MetaInteractor implements MetaService {
    private final MetaStore store;
    private final QueuedTaskRunner runner;
    private final Map<PackageListener, Origin> listeners = new HashMap<>();

    public MetaInteractor(MetaStore store, QueuedTaskRunner runner) {
        this.store = store;
        this.runner = runner;
    }

    @Override
    public void addListener(Origin origin, PackageListener listener) {
        listeners.put(listener, origin);
    }

    @Override
    public void update(Origin origin, PackageUrl purl, Map<Field, Object> values) {
        final var pkg = getOrCreatePackage(purl);
        pkg.setValues(origin, values);
        notifyValueListeners(origin, purl, values.keySet(), pkg.getValues());
    }

    @Override
    public Map<Field, Object> valuesOf(PackageUrl purl) {
        return validPackage(purl).getValues();
    }

    private Package getOrCreatePackage(PackageUrl purl) {
        return store.findPackage(purl)
                .orElseGet(() -> createPackage(purl));
    }

    private Package createPackage(PackageUrl purl) {
        final var pkg = store.createPackage(purl);
        notifyValueListeners(Origin.META, purl, Set.of(), Map.of());
        return pkg;
    }

    private Package validPackage(PackageUrl purl) {
        return store.findPackage(purl)
                .orElseThrow(() -> new UnknownPackageException(purl));
    }

    private void notifyValueListeners(Origin origin, PackageUrl purl, Set<Field> fields, Map<Field, Object> values) {
        listeners.entrySet().stream()
                .filter(e -> e.getValue() != origin)
                .map(Map.Entry::getKey)
                .forEach(l -> l.onUpdated(purl, fields, values).ifPresent(runner::execute));
    }
}
