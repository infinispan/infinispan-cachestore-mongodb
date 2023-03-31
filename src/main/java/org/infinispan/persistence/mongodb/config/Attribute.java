package org.infinispan.persistence.mongodb.config;

import java.util.HashMap;
import java.util.Map;

public enum Attribute {
    // must be first
    UNKNOWN(null),

    CONNECTION_URI("uri"),
    DATABASE("database"),
    COLLECTION("collection"),
    CONVERTER("converter");

    private static final Map<String, Attribute> attributes;

    private final String name;

    Attribute(final String name) {
        this.name = name;
    }

    static {
        final Map<String, Attribute> map = new HashMap<>();
        for (Attribute attribute : values()) {
            if (attribute.name != null) {
                map.put(attribute.name, attribute);
            }
        }
        attributes = map;
    }

    public static Attribute forName(final String localName) {
        final Attribute attribute = attributes.get(localName);
        return attribute == null ? UNKNOWN : attribute;
    }

    @Override
    public String toString() {
        return name;
    }
}
