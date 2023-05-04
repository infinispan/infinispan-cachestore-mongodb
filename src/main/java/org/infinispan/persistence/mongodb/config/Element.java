package org.infinispan.persistence.mongodb.config;

import java.util.HashMap;
import java.util.Map;

public enum Element {
    // must be first
    UNKNOWN(null),

    MONGODB_STORE("mongodb-store"),
    CONNECTION("connection");

    private static final Map<String, Element> elements;

    private final String name;

    Element(final String name) {
        this.name = name;
    }

    static {
        final Map<String, Element> map = new HashMap<>();
        for (Element element : values()) {
            if (element.name != null) {
                map.put(element.name, element);
            }
        }
        elements = map;
    }

    public static Element forName(final String localName) {
        final Element element = elements.get(localName);
        return element == null ? UNKNOWN : element;
    }

    @Override
    public String toString() {
        return name;
    }
}
