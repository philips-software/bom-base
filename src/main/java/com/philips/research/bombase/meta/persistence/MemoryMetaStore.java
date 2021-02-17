/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.persistence;

import com.philips.research.bombase.meta.MetaStore;
import com.philips.research.bombase.meta.domain.FieldValue;
import com.philips.research.bombase.meta.domain.Package;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MemoryMetaStore implements MetaStore {
    private final Map<String, Package> packages = new HashMap<>();

    @Override
    public Package createPackage(String type, String name, String version) {
        return packages.putIfAbsent(purl(type, name, version), new Package(type, name, version));
    }

    @Override
    public Optional<Package> findPackage(String type, String name, String version) {
        return Optional.ofNullable(packages.get(purl(type, name, version)));
    }

    private String purl(String type, String name, String version) {
        return type + ':' + name + "-" + version;
    }

    @Override
    public List<Package> findPackages(String type, String name) {
        return packages.values().stream()
                .filter(p -> p.getType().equals(type) && p.getName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    //TODO Is this actually necessary (now)?
    public FieldValue<?> createField(Package pkg, String field) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
