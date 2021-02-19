/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta;

import com.philips.research.bombase.PackageUrl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * API for managing the storage of metadata.
 */
public interface MetaService {
    /**
     * Registers an observer for metadata value changes.
     *
     * @param listener observer
     */
    void addListener(PackageListener listener);

    /**
     * Updates selected fields of a package.
     * Listeners are automatically notified of changes.
     *
     * @param purl   package id
     * @param values new value per field
     */
    void update(PackageUrl purl, Map<Field, Object> values);

    /**
     * Reads all stored metadata for a package.
     *
     * @param purl package id
     * @return value per field
     * @throws UnknownPackageException if the package does not exist
     */
    Map<Field, Object> value(PackageUrl purl);

    /**
     * Callbacks to optionally create an asynchronous task.
     */
    interface PackageListener {
        /**
         * Notifies given fields were updated.
         *
         * @param purl    package id
         * @param updated modified fields
         * @param values  current package metadata
         * @return (optional) operation to queue for execution
         */
        Optional<Runnable> onUpdated(PackageUrl purl, Set<Field> updated, Map<Field, ?> values);
    }
}
