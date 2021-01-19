package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.Field;
import com.philips.research.metabase.activity.MetaService;
import com.philips.research.metabase.activity.MetaStore;
import com.philips.research.metabase.activity.UnknownPackageException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaInteractor implements MetaService {
    private final MetaStore store;

    public MetaInteractor(MetaStore store) {
        this.store = store;
    }

    @Override
    public void addTypeListener(Field type, FieldListener listener) {

    }

    @Override
    public void addFieldListener(Field field, FieldListener listener) {

    }

    @Override
    public <T> void storeFieldValue(URI pkg, Field field, T value) {
//        final var stringValue = valueConverterFor(field).toString(value);
//        final Package p = getOrCreatePackage(pkg);
//        p.getField(field).setValue(stringValue);
    }

    @Override
    public void contestField(URI pkg, Field field, Object value) {

    }

    @Override
    public void overrideField(URI pkg, Field field, Object value) {

    }

    @Override
    public void clearField(URI pkg, Field field) {

    }

    @Override
    public Map<String, Object> value(URI pkg) {
        validPackage(pkg)
                .getFields().entrySet().stream()
                .collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
        return null;
    }

    @Override
    public List<URI> contested(Field field, int limit) {
        return null;
    }

    private Package getOrCreatePackage(URI pkg) {
        final var temp = Package.from(pkg);
        return store.findPackage(temp.getType(), temp.getName(), temp.getVersion())
                .orElseGet(() -> store.createPackage(temp.getType(), temp.getName(), temp.getVersion()));
    }

    private Package validPackage(URI pkg) {
        final var temp = Package.from(pkg);
        return store.findPackage(temp.getType(), temp.getName(), temp.getVersion())
                .orElseThrow(() -> new UnknownPackageException(pkg));
    }

}
