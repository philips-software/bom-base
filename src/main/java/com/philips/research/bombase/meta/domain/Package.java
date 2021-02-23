/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.Origin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Representation of an item with its properties that can appear in a bill-of-materials.
 */
public class Package {
    private final PackageUrl purl;
    private final Map<Field, FieldValue> fields = new HashMap<>();

    public Package(PackageUrl purl) {
        this.purl = purl;
    }

    public PackageUrl getPurl() {
        return purl;
    }

    public Map<Field, Object> getValues() {
        return fields.entrySet().stream()
                .filter(e -> e.getValue().getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue().get()));
    }

    public void setValues(Origin origin, Map<Field, ?> values) {
        values.forEach((field, value) -> setValue(origin, field, value));
    }

    public Optional<Object> getValue(Field field) {
        return this.getFieldValue(field).flatMap(FieldValue::getValue);
    }

    public void setValue(Origin origin, Field field, Object value) {
        getOrCreateFieldValue(field).setValue(origin, value);
    }

    private Optional<FieldValue> getFieldValue(Field field) {
        return Optional.ofNullable(fields.get(field));
    }

    private FieldValue getOrCreateFieldValue(Field field) {
        return fields.computeIfAbsent(field, FieldValue::new);
    }
}
