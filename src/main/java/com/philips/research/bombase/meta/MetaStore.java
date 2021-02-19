/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.domain.FieldValue;
import com.philips.research.bombase.meta.domain.Package;

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
    Package createPackage(PackageUrl purl);

    /**
     * Retrieves a package for the provided coordinates if one exists.
     *
     * @param purl package id
     * @return persistent package instance, if one exists
     */
    Optional<Package> findPackage(PackageUrl purl);

    /**
     * Lists all versions of a package.
     *
     * @param purl package id
     * @return all known persistent package version instances
     */
    List<Package> findPackageVersions(PackageUrl purl);

    /**
     * Creates a new field record for a package.
     *
     * @param pkg   package containing the field
     * @param field name of the field
     * @return persistent field instance
     */
    FieldValue createField(Package pkg, Field field);
}
