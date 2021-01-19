package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.Field;
import com.philips.research.metabase.activity.MetaService;
import com.philips.research.metabase.activity.MetaStore;
import com.philips.research.metabase.activity.UnknownPackageException;

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
        values.forEach((field, value) -> listeners
                .forEach(l -> l.onUpdated(purl, field, value)
                        .ifPresent(Runnable::run)));
    }

    @Override
    public Map<Field, Object> value(URI purl) {
        return validPackage(purl).getValues();
    }

    private Package getOrCreatePackage(URI purl) {
        final var temp = Package.from(purl);
        return store.findPackage(temp.getType(), temp.getName(), temp.getVersion())
                .orElseGet(() -> store.createPackage(temp.getType(), temp.getName(), temp.getVersion()));
    }

    private Package validPackage(URI purl) {
        final var temp = Package.from(purl);
        return store.findPackage(temp.getType(), temp.getName(), temp.getVersion())
                .orElseThrow(() -> new UnknownPackageException(purl));
    }
}
