package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.ConfigurationFor;
import org.infinispan.commons.configuration.attributes.AttributeDefinition;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;
import org.infinispan.configuration.cache.AsyncStoreConfiguration;
import org.infinispan.configuration.serializing.SerializedWith;
import org.infinispan.persistence.mongodb.DataFormat;
import org.infinispan.persistence.mongodb.MongoDbStore;


/**
 * The configuration of {@link MongoDbStore}.
 * <p>
 * This class wraps all the MongoDB information for the connection.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 */
@BuiltBy(MongoDbStoreConfigurationBuilder.class)
@ConfigurationFor(MongoDbStore.class)
@SerializedWith(MongoDbStoreConfigurationSerializer.class)
public class MongoDbStoreConfiguration extends AbstractStoreConfiguration {

    static final AttributeDefinition<DataFormat> FORMAT = AttributeDefinition.builder(Attribute.FORMAT, null, DataFormat.class).immutable().build();

    public static AttributeSet attributeDefinitionSet() {
        return new AttributeSet(MongoDbStoreConfiguration.class, AbstractStoreConfiguration.attributeDefinitionSet(), FORMAT);
    }


    private final org.infinispan.commons.configuration.attributes.Attribute<DataFormat> format;
    private final ConnectionConfiguration connectionConfiguration;

    public MongoDbStoreConfiguration(AttributeSet attributes, AsyncStoreConfiguration async, ConnectionConfiguration connectionConfiguration) {
        super(attributes, async);
        this.format = attributes.attribute(FORMAT);
        this.connectionConfiguration = connectionConfiguration;
    }

    public DataFormat format() {
        return format.get();
    }

    public ConnectionConfiguration connection() {
        return connectionConfiguration;
    }


    @Override
    public boolean segmented() {
        return false;
    }
}
