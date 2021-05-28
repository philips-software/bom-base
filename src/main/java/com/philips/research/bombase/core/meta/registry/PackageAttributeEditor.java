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
    public Map<Field, @NullOr Object> getValues() {
        return pkg.getAttributes()
                .filter(a -> a.getValue().isPresent())
                .collect(Collectors.toMap(Attribute::getField, attribute -> attribute.getValue().get()));
    }

    /**
     * Updates the value of a field using the provided score as priority
     *
     * @param score percentage indicating how trustworthy the value is
     */
    public PackageAttributeEditor update(Field field, int score, @NullOr Object value) {
        if (value == null) {
            return this;
        }

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
