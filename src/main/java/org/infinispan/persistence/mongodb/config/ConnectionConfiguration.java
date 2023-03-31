package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.configuration.attributes.Attribute;
import org.infinispan.commons.configuration.attributes.AttributeDefinition;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.commons.configuration.attributes.ConfigurationElement;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;


public class ConnectionConfiguration extends ConfigurationElement<ConnectionConfiguration> {

    static final AttributeDefinition<String> URI = AttributeDefinition.builder(org.infinispan.persistence.mongodb.config.Attribute.CONNECTION_URI, null, String.class).immutable().build();
    static final AttributeDefinition<String> DATABASE = AttributeDefinition.builder(org.infinispan.persistence.mongodb.config.Attribute.DATABASE, null, String.class).immutable().build();
    static final AttributeDefinition<String> COLLECTION = AttributeDefinition.builder(org.infinispan.persistence.mongodb.config.Attribute.COLLECTION, null, String.class).immutable().build();

    public static AttributeSet attributeDefinitionSet() {
        return new AttributeSet(ConnectionConfiguration.class, AbstractStoreConfiguration.attributeDefinitionSet(),
                URI, DATABASE, COLLECTION
        );
    }


    private final Attribute<String> uri;
    private final Attribute<String> database;
    private final Attribute<String> collection;

    public ConnectionConfiguration(AttributeSet attributes) {
        super(Element.CONNECTION, attributes);
        this.uri = attributes.attribute(URI);
        this.database = attributes.attribute(DATABASE);
        this.collection = attributes.attribute(COLLECTION);
    }

    public String uri() {
        return uri.get();
    }

    public String database() {
        return database.get();
    }

    public String collection() {
        return collection.get();
    }
}
