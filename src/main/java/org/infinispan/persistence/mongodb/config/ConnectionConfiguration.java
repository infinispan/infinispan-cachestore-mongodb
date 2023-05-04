package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.configuration.attributes.AttributeDefinition;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.commons.configuration.attributes.ConfigurationElement;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;


public class ConnectionConfiguration extends ConfigurationElement<ConnectionConfiguration> {

    static final AttributeDefinition<String> URI = AttributeDefinition.builder(Attribute.CONNECTION_URI, null, String.class).immutable().build();
    static final AttributeDefinition<String> DATABASE = AttributeDefinition.builder(Attribute.DATABASE, null, String.class).immutable().build();
    static final AttributeDefinition<String> COLLECTION = AttributeDefinition.builder(Attribute.COLLECTION, null, String.class).immutable().build();

    public static AttributeSet attributeDefinitionSet() {
        return new AttributeSet(ConnectionConfiguration.class, AbstractStoreConfiguration.attributeDefinitionSet(),
                URI, DATABASE, COLLECTION
        );
    }


    public ConnectionConfiguration(AttributeSet attributes) {
        super(Element.CONNECTION, attributes);
    }

    public String uri() {
        return attributes.attribute(URI).get();
    }

    public String database() {
        return attributes.attribute(DATABASE).get();
    }

    public String collection() {
        return attributes.attribute(COLLECTION).get();
    }
}
