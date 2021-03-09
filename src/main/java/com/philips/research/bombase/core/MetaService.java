/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core;

import java.net.URI;
import java.util.Map;

/**
 * API for managing the storage of metadata.
 */
public interface MetaService {
    /**
     * Reads all stored metadata for a package.
     *
     * @param purl package id
     * @return value per field
     * @throws UnknownPackageException if the package does not exist
     */
    Map<String, Object> valuesOf(URI purl);

    /**
     * Updates selected fields of a package.
     * Listeners are automatically notified of changes.
     *
     * @param purl   package id
     * @param values new value per field
     * @Param origin source of the updated value(s)
     */
    void update(URI purl, Map<String, Object> values);
}
