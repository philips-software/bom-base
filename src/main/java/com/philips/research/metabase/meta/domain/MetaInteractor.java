package com.philips.research.metabase.meta.domain;

import com.philips.research.metabase.meta.Field;
import com.philips.research.metabase.meta.MetaService;
import com.philips.research.metabase.meta.MetaStore;
import com.philips.research.metabase.meta.UnknownPackageException;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetaInteractor implements MetaService {
    private final MetaStore store;
    private final Set<PackageListener> listeners = new HashSet<>();

    public MetaInteractor(MetaStore store) {
        this.store = store;
    }

    @Override
    public void addListener(PackageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void update(URI purl, Map<Field, Object> values) {
        final var pkg = getOrCreatePackage(purl);
        pkg.setValues(values);
        notifyValueListeners(purl, values.keySet(), pkg.getValues());
    }

    @Override
    public Map<Field, Object> value(URI purl) {
        return validPackage(purl).getValues();
    }

    private Package getOrCreatePackage(URI purl) {
        final var temp = Package.from(purl);
        return store.findPackage(temp.getType(), temp.getName(), temp.getVersion())
                .orElseGet(() -> createPackage(purl, temp));
    }

    private Package createPackage(URI purl, Package temp) {
        final var pkg = store.createPackage(temp.getType(), temp.getName(), temp.getVersion());
        final var values = pkg.getValues();
        notifyValueListeners(purl, values.keySet(), values);
        return pkg;
    }

    private Package validPackage(URI purl) {
        final var temp = Package.from(purl);
        return store.findPackage(temp.getType(), temp.getName(), temp.getVersion())
                .orElseThrow(() -> new UnknownPackageException(purl));
    }

    private void notifyValueListeners(URI purl, Set<Field> fields, Map<Field, Object> values) {
        listeners.forEach(l -> l.onUpdated(purl, fields, values).ifPresent(Runnable::run));
    }
}
