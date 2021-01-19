package com.philips.research.metabase.activity.domain;

public class ValueConverter {
    private final Class<?> valueClass;

    public ValueConverter(Class<?> valueClass) {
        this.valueClass = valueClass;
    }

    String toString(Object value) {
        if (!valueClass.isInstance(value)) {
            throw new IllegalArgumentException("Value " + value + " is not an instance of " + valueClass.getSimpleName());
        }
        return value.toString();
    }

    Object parse(String string) {
        try {
            if (valueClass == String.class) {
                return string;
            } else if (valueClass == Integer.class) {
                return Integer.parseInt(string);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse value " + string + " to " + valueClass.getSimpleName());
        }
        throw new UnsupportedOperationException("No decoder supported for " + valueClass.getSimpleName());
    }
}
