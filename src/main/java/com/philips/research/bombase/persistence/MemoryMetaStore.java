/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.persistence;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.MetaStore;
import com.philips.research.bombase.core.meta.registry.Attribute;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Package;
import org.springframework.stereotype.Repository;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MemoryMetaStore implements MetaStore {
    private final Map<PackageURL, Package> packages = new HashMap<>();

    @Override
    public Package createPackage(PackageURL purl) {
        return packages.computeIfAbsent(purl, (key) -> new Package(purl));
    }

    @Override
    public Optional<Package> findPackage(PackageURL purl) {
        return Optional.ofNullable(packages.get(purl));
    }

    @Override
    public List<Package> latestScans(int limit) {
        final var scans = new ArrayList<>(packages.values());
        scans.sort(Comparator.comparing(Package::getLastUpdated));
        Collections.reverse(scans);
        return scans.subList(0, Math.min(limit, scans.size()));
    }

    @Override
    public List<Package> findPackageVersions(PackageURL purl) {
        return packages.values().stream()
                .filter(p -> p.getPurl().equals(purl))
                .collect(Collectors.toList());
    }

    @Override
    //TODO Is this actually necessary (already)?
    public Attribute createField(Package pkg, Field field) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Package> findPackages(String type, String namespace, String name, String version) {
        return packages.values().stream()
                .filter(pkg -> {
                    final var purl = pkg.getPurl();
                    return notNull(purl.getType()).contains(type)
                            && notNull(purl.getNamespace()).contains(namespace)
                            && notNull(purl.getName()).contains(name)
                            && notNull(purl.getVersion()).contains(version);
                })
                .sorted((l, r) -> r.getLastUpdated().compareTo(l.getLastUpdated()))
                .limit(100)
                .collect(Collectors.toList());
    }

    private String notNull(@NullOr String string) {
        return string != null ? string : "";
    }
}
