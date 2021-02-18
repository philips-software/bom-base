/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import com.philips.research.bombase.meta.Field;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Representation of an item with its properties that can appear in a bill-of-materials.
 */
public class Package {
    private final String type;
    private final String name;
    private final String version;
    private final Map<Field, FieldValue> fields = new HashMap<>();

    public Package(String type, String name, String version) {
        this.type = type;
        this.name = name;
        this.version = version;
    }

    static Package from(URI purl) {
        final var path = purl.getSchemeSpecificPart();
        final var pos = path.indexOf('@');
        final var first = path.indexOf('/');
        final var type = path.substring(0, Math.min(first, pos));
        final var name = path.substring(Math.min(first, pos) + 1, pos);
        final var version = path.substring(pos + 1);
        return new Package(type, name, version);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Map<Field, Object> getValues() {
        final Map<Field, Object> result = fields.entrySet().stream()
                .filter(e -> e.getValue().getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue().get()));
        result.put(Field.TYPE, type);
        result.put(Field.NAME, name);
        result.put(Field.VERSION, version);
        return result;
    }

    public void setValues(Map<Field, ?> values) {
        values.forEach(this::setValue);
    }

    public Optional<Object> getValue(Field field) {
        return this.getFieldValue(field).flatMap(FieldValue::getValue);
    }

    public void setValue(Field field, Object value) {
        getOrCreateFieldValue(field).setValue(value);
    }

    private Optional<FieldValue> getFieldValue(Field field) {
        return Optional.ofNullable(fields.get(field));
    }

    private FieldValue getOrCreateFieldValue(Field field) {
        return fields.computeIfAbsent(field, FieldValue::new);
    }
}
