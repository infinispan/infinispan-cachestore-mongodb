package org.infinispan.persistence.mongodb.configuration.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * All valid attributes used to configure a MongoDB cachestore
 * Refer to {@link Element} to have the list of available configuration elements
 *
 * @author Guillaume Scheibel &lt;guillaume.scheibel@gmail.com&gt;
 */
public enum Attribute {
   UNKNOWN(null),

   /**
    * Attributes of Element.CONNECTION
    */
   CONNECTION_URI("uri"),
   COLLECTION("collection");

   private static final Map<String, Attribute> attributes;

   static {
      final Map<String, Attribute> map = new HashMap<String, Attribute>(64);
      for (Attribute attribute : values()) {
         final String name = attribute.getName();
         if (name != null) {
            map.put(name, attribute);
         }
      }
      attributes = map;
   }

   private final String name;

   private Attribute(final String name) {
      this.name = name;
   }

   public static Attribute forName(final String localName) {
      final Attribute attribute = attributes.get(localName);
      return attribute == null ? UNKNOWN : attribute;
   }

   /**
    * @return the name of the attribute
    */
   public String getName() {
      return name;
   }
}
