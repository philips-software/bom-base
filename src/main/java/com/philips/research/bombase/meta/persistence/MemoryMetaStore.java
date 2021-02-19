/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.persistence;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.Field;
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
    private final Map<PackageUrl, Package> packages = new HashMap<>();

    @Override
    public Package createPackage(PackageUrl purl) {
        return packages.computeIfAbsent(purl, (key) -> new Package(purl));
    }

    @Override
    public Optional<Package> findPackage(PackageUrl purl) {
        return Optional.ofNullable(packages.get(purl));
    }

    @Override
    public List<Package> findPackageVersions(PackageUrl purl) {
        return packages.values().stream()
                //FIXME Could use a comparator in PackageUrl
                .filter(p -> p.getPurl().getType().equals(purl.getType()) &&
                        p.getPurl().getNamespace().equals(purl.getNamespace()) &&
                        p.getPurl().getName().equals(purl.getName()))
                .collect(Collectors.toList());
    }

    @Override
    //TODO Is this actually necessary (already)?
    public FieldValue createField(Package pkg, Field field) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
