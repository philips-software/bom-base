package com.philips.research.metabase.activity.domain;

import com.philips.research.metabase.activity.Field;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Package {
    private static final FieldValue<Object> NO_FIELD_VALUE = new FieldValue<>(Object.class);
    private static final Map<Field, Class<?>> TYPES = new HashMap<>();

    private final String type;
    private final String name;
    private final String version;
    private final Map<Field, FieldValue<?>> fields = new HashMap<>();

    public Package(String type, String name, String version) {
        this.type = type;
        this.name = name;
        this.version = version;
    }

    public static void register(Field field, Class<?> type) {
        TYPES.put(field, type);
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
        return fields.entrySet().stream()
                .filter(e -> e.getValue().getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue().get()));
    }

    public <T> Optional<T> getValue(Field field) {
        return this.<T>getFieldValue(field).getValue();
    }

    public <T> void setValue(Field field, T value) {
        final Class<?> fieldClass = TYPES.get(field);
        if (!fieldClass.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Value " + value + " can not be assigned to field " + field + " of type " + fieldClass.getSimpleName());
        }
        getOrCreateFieldValue(field).setValue(value);
    }

    private <T> FieldValue<T> getFieldValue(Field field) {
        //noinspection unchecked
        return (FieldValue<T>) fields.getOrDefault(field, NO_FIELD_VALUE);
    }

    private <T> FieldValue<T> getOrCreateFieldValue(Field field) {
        //noinspection unchecked
        return (FieldValue<T>) fields.computeIfAbsent(field, (f) -> new FieldValue<>(TYPES.get(f)));
    }
}
