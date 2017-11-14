package org.infinispan.persistence.mongodb.configuration.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * All valid elements to configure a MongoDB cachestore
 * See also {@link Attribute} to have the complete list of attributes
 *
 * @author Guillaume Scheibel <guillaume.scheibel@gmail.com>
 */
public enum Element {
   UNKNOWN(null),
    MONGODB_STORE("mongodb-store"),
   CONNECTION("connection");

   private static final Map<String, Element> elements;

   static {
      final Map<String, Element> map = new HashMap<>(3);
      for (Element element : values()) {
         final String name = element.getName();
         if (name != null) {
            map.put(name, element);
         }
      }
      elements = map;
   }

   private final String name;

   Element(final String name) {
      this.name = name;
   }

   public static Element forName(final String localName) {
      final Element element = elements.get(localName);
      return element == null ? UNKNOWN : element;
   }

   /**
    * Get the name of the current element
    *
    * @return the name
    */
   public String getName() {
      return name;
   }
}