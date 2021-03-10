/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.philips.research.bombase.core.meta.MetaException;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.List;

/**
 * Supported metadata fields.
 */
public enum Field {
    TITLE(String.class), // Short name of the package
    DESCRIPTION(String.class), // More elaborate description of the package
    HOME_PAGE(URI.class), // Project home page
    //TODO Is there a way around type-erasure?
    ATTRIBUTION(List.class), // Copyright owners
    DOWNLOAD_LOCATION(URI.class), // URL for the distribution representation
    SHA1(String.class), // Hash of distribution artifact
    SHA256(String.class), // Hash of distribution artifact
    SOURCE_LOCATION(URI.class), // URL for the source code
    DECLARED_LICENSE(String.class), // License string according to the distributor
    DETECTED_LICENSE(String.class); // License string according to authors

    private final Class<?> typeClass;

    Field(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public <T> @NullOr T validate(@NullOr T value) {
        if (value != null && !typeClass.isAssignableFrom(value.getClass())) {
            throw new MetaException("Field " + this + " cannot hold a value of type " + value.getClass());
        }
        return value;
    }
}
