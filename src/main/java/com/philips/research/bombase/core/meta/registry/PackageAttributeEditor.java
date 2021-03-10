/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Tracks edits of package field values.
 */
public class PackageAttributeEditor {
    private final Package pkg;
    private final Set<Field> modifiedFields = new HashSet<>();

    PackageAttributeEditor(Package pkg) {
        this.pkg = pkg;
    }

    /**
     * @return current value of the indicated field
     */
    public <T> Optional<T> get(Field field) {
        return pkg.getAttributeFor(field).flatMap(Attribute::getValue);
    }

    /**
     * Updates the value of a field using the provided score as priority
     *
     * @param score percentage indicating how trustworthy the value is
     */
    public PackageAttributeEditor update(Field field, int score, @NullOr Object value) {
        final var modified = getOrCreateAttr(field).setValue(score, value);
        if (modified) {
            modifiedFields.add(field);
        }
        return this;
    }

    private Attribute getOrCreateAttr(Field field) {
        return pkg.getAttributeFor(field).orElseGet(() -> createAttribute(field));
    }

    // Necessary to allow generation of a tracked persistence entity.
    protected Attribute createAttribute(Field field) {
        return pkg.add(new Attribute(field));
    }

    /**
     * @return all fields of which the value has been modified
     */
    Set<Field> getModifiedFields() {
        return Collections.unmodifiableSet(modifiedFields);
    }
}