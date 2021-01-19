package com.philips.research.metabase.activity.domain;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Package {
    private final String type;
    private final String name;
    private final String version;
    private final Map<String, FieldValue> fields = new HashMap<>();

    static Package from(URI purl) {
        final var path = purl.getSchemeSpecificPart();
        final var pos = path.indexOf('@');
        final var first = path.indexOf('/');
        final var type = path.substring(0, Math.min(first, pos));
        final var name = path.substring(Math.min(first, pos) + 1, pos);
        final var version = path.substring(pos + 1);
        return new Package(type, name, version);
    }

    public Package(String type, String name, String version) {
        this.type = type;
        this.name = name;
        this.version = version;
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

    public Map<String, FieldValue> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public FieldValue getField(String field) {
        return fields.computeIfAbsent(field, (f) -> new FieldValue( ));
    }
}
