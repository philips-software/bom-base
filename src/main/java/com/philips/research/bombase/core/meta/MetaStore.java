/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Attribute;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.Package;

import java.util.List;
import java.util.Optional;

/**
 * Persistence API for the metadata domain.
 */
public interface MetaStore {
    /**
     * Creates a new unique package from the provided coordinates.
     *
     * @param purl package id
     * @return persistent package instance
     */
    Package createPackage(PackageURL purl);

    /**
     * Retrieves a package for the provided coordinates if one exists.
     *
     * @param purl package id
     * @return persistent package instance, if one exists
     */
    Optional<Package> findPackage(PackageURL purl);

    /**
     * Lists latest scanned packages in anti-chronological order.
     *
     * @param limit maximum number of packages to return
     * @return list of packages, sorted on modification timestamp
     */
    List<Package> latestScans(int limit);

    /**
     * Lists all versions of a package.
     *
     * @param purl package id
     * @return all known persistent package version instances
     */
    List<Package> findPackageVersions(PackageURL purl);

    /**
     * Creates a new field record for a package.
     *
     * @param pkg   package containing the field
     * @param field name of the field
     * @return persistent field instance
     */
    Attribute createField(Package pkg, Field field);

    /**
     * Find packages matching provided (parts of) parameters.
     *
     * @return search results
     */
    List<Package> findPackages(String type, String namespace, String name, String version);
}
