/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta;

/**
 * Supported metadata fields.
 */
public enum Field {
    TYPE, // Package distribution repository indication
    NAMESPACE, // Package grouping as defined by package repository
    NAME, // Package name
    VERSION, // Package version
    TITLE, // Short name of the package
    DESCRIPTION, // More elaborate description of the package
    DOWNLOAD_LOCATION, // URL for the distribution representation
    SOURCE_LOCATION, // URL for the source code
    DECLARED_LICENSE, // License according to the distributor
    DETECTED_LICENSE // License according to authors
}
