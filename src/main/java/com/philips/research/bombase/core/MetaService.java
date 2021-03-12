/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core;

import com.github.packageurl.PackageURL;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
    Map<String, Object> getAttributes(PackageURL purl);

    /**
     * Updates selected attributes of a package.
     * Listeners are automatically notified of changes.
     *
     * @param purl   package id
     * @param values new value per field
     * @Param origin source of the updated value(s)
     */
    void setAttributes(PackageURL purl, Map<String, Object> values);

    /**
     * @return Most recent scans
     */
    List<PackageURL> latestScans();
}
