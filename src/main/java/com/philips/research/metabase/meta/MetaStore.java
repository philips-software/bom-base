package com.philips.research.metabase.meta;

import com.philips.research.metabase.meta.domain.FieldValue;
import com.philips.research.metabase.meta.domain.Package;

import java.util.List;
import java.util.Optional;

public interface MetaStore {
    Package createPackage(String type, String name, String version);

    Optional<Package> findPackage(String type, String name, String version);

    List<Package> findPackages(String type, String name);

    FieldValue createField(Package pkg, String field);

    Optional<FieldValue> findField(Package pkg, String field);

    List<FieldValue> findFields(Package pkg);
}
