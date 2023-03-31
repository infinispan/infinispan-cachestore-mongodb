package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.mongodb.ConverterType;

import static org.infinispan.persistence.mongodb.config.Attribute.CONVERTER;


/**
 * A builder to create {@link MongoDbStoreConfiguration}s.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 * @author Gabriel Francisco &lt;gabfssilva@gmail.com&gt;
 */
public class MongoDbStoreConfigurationBuilder extends AbstractStoreConfigurationBuilder<MongoDbStoreConfiguration, MongoDbStoreConfigurationBuilder> {
    private final ConnectionConfigurationBuilder connectionConfigurationBuilder;

    public MongoDbStoreConfigurationBuilder(PersistenceConfigurationBuilder builder) {
        super(builder, MongoDbStoreConfiguration.attributeDefinitionSet());
        this.connectionConfigurationBuilder = new ConnectionConfigurationBuilder(this);
    }

    public MongoDbStoreConfigurationBuilder converter(String converter) {
        return converter(ConverterType.valueOf(converter.toUpperCase()));
    }

    public MongoDbStoreConfigurationBuilder converter(ConverterType converter) {
        attributes.attribute(CONVERTER).set(converter);
        return this;
    }

    public ConnectionConfigurationBuilder connection() {
        return connectionConfigurationBuilder;
    }


    @Override
    public MongoDbStoreConfiguration create() {
        return new MongoDbStoreConfiguration(attributes.protect(), async.create(), connectionConfigurationBuilder.create());
    }

    @Override
    public void validate() {
        super.validate();
        connectionConfigurationBuilder.validate();
    }

    @Override
    public Builder<?> read(MongoDbStoreConfiguration template) {
        attributes.read(template.attributes());
        connectionConfigurationBuilder.read(template.connection());
        return this;
    }

    @Override
    public MongoDbStoreConfigurationBuilder self() {
        return this;
    }
}
