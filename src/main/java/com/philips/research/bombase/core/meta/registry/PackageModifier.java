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

//TODO Rename to better reveal intention (FieldEditor?)
public class PackageModifier {
    private final Package pkg;
    private final Set<Field> modifiedFields = new HashSet<>();

    PackageModifier(Package pkg) {
        this.pkg = pkg;
    }

    Set<Field> getModifiedFields() {
        return Collections.unmodifiableSet(modifiedFields);
    }

    public <T> Optional<T> get(Field field) {
        return pkg.getAttributeFor(field).flatMap(Attribute::getValue);
    }

    public PackageModifier update(Field field, @NullOr Object value) {
        if (value == null) {
            return this;
        }
        getOrCreateAttr(field).setValue(value);
        modifiedFields.add(field);
        return this;
    }

    public PackageModifier set(Field field, String value) {
        getOrCreateAttr(field).override(value);
        modifiedFields.add(field);
        return this;
    }

    private Attribute getOrCreateAttr(Field field) {
        return pkg.getAttributeFor(field).orElseGet(() -> createAttribute(field));
    }

    protected Attribute createAttribute(Field field) {
        return pkg.add(new Attribute(field));
    }
}
