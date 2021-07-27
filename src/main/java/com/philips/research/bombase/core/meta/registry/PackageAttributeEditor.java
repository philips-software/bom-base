/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracks edits of package field values.
 */
public class PackageAttributeEditor {
    private final Package pkg;
    private final Set<Field> modifiedFields = new HashSet<>();

    public PackageAttributeEditor(Package pkg) {
        this.pkg = pkg;
    }

    /**
     * @return Package URL of the edited package.
     */
    public PackageURL getPurl() {
        return pkg.getPurl();
    }

    /**
     * @return current value of the indicated field
     */
    public <T> Optional<T> get(Field field) {
        //noinspection unchecked
        return pkg.getAttributeFor(field)
                .flatMap(Attribute::getValue)
                .map(a -> (T) a);
    }

    /**
     * @return snapshot of the current fields with values
     */
    public Map<Field, Object> getValues() {
        return pkg.getAttributes()
                .filter(a -> a.getValue().isPresent())
                .collect(Collectors.toMap(Attribute::getField, attribute -> attribute.getValue().get()));
    }

    /**
     * @return trust of the current value for the indicated field
     */
    public Trust trust(Field field) {
        return pkg.getAttributeFor(field)
                .map(Attribute::getScore)
                .map(Trust::of)
                .orElse(Trust.NONE);
    }

    /**
     * Optionally updates the value of a field based on its relative trust.
     *
     * @param field the metadata attribute to overwrite
     * @param trust indicates how reliable the value is
     * @param value new value to assign
     */
    public PackageAttributeEditor update(Field field, Trust trust, @NullOr Object value) {
        if (value == null) {
            return this;
        }

        final var modified = getOrCreateAttr(field).setValue(trust, value);
        if (modified) {
            modifiedFields.add(field);
        }
        return this;
    }

    private <T> Attribute<T> getOrCreateAttr(Field field) {
        return pkg.<T>getAttributeFor(field).orElseGet(() -> createAttribute(field));
    }

    // Necessary to allow generation of a tracked persistence entity.
    protected <T> Attribute<T> createAttribute(Field field) {
        return pkg.add(new Attribute<>(field));
    }

    /**
     * @return all fields of which the value has been modified
     */
    Set<Field> getModifiedFields() {
        return Collections.unmodifiableSet(modifiedFields);
    }

    /**
     * @return true if this editor modified the package
     */
    boolean isModified() {
        final var modified = !modifiedFields.isEmpty();
        if (modified) {
            pkg.setUpdated();
        }
        return modified;
    }
}
