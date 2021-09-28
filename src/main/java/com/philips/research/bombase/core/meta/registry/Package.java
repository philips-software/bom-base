/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Package {
    private final PackageURL purl;
    private final Set<Attribute<?>> attributes = new HashSet<>();
    private Instant lastUpdated = Instant.now();

    public Package(PackageURL purl) {
        this.purl = purl;
    }

    public PackageURL getPurl() {
        return purl;
    }

    public void setUpdated() {
        this.lastUpdated = Instant.now();
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public synchronized <T> Attribute<T> add(Attribute<T> attribute) {
        if (attributes.contains(attribute)) {
            throw new IllegalArgumentException("The " + attribute.getField().name() + " attribute already exists in package " + purl);
        }
        attributes.add(attribute);
        return attribute;
    }

    public synchronized <T> Optional<Attribute<T>> getAttributeFor(Field field) {
        //noinspection unchecked
        return attributes.stream()
                .map(attr -> (Attribute<T>) attr)
                .filter(attr -> attr.getField() == field)
                .findFirst();
    }

    public Stream<Attribute<?>> getAttributes() {
        return attributes.stream();
    }
}
