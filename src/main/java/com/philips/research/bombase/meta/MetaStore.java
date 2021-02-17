/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta;

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
     * @param type    type of the package
     * @param name    namespace and name of the package
     * @param version version of the package
     * @return persistent package instance
     */
    Package createPackage(String type, String name, String version);

    /**
     * Retrieves a package for the provided coordinates if one exists.
     *
     * @param type    type of the package
     * @param name    namespace and name of the package
     * @param version version of the package
     * @return persistent package instance, if one exists
     */
    Optional<Package> findPackage(String type, String name, String version);

    /**
     * Lists all versions of a package.
     *
     * @param type type of the package
     * @param name namespace and name of the package
     * @return all known persistent package version instances
     */
    List<Package> findPackages(String type, String name);

    /**
     * Creates a new field record for a package.
     *
     * @param pkg   package containing the field
     * @param field name of the field
     * @return persistent field instance
     */
    FieldValue<?> createField(Package pkg, String field);
}
