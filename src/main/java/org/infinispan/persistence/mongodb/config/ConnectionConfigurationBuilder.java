package org.infinispan.persistence.mongodb.config;

import com.mongodb.ConnectionString;
import org.infinispan.commons.configuration.Builder;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.commons.logging.LogFactory;
import org.infinispan.persistence.mongodb.logging.Log;

import static org.infinispan.persistence.mongodb.config.Attribute.COLLECTION;
import static org.infinispan.persistence.mongodb.config.Attribute.CONNECTION_URI;
import static org.infinispan.persistence.mongodb.config.Attribute.DATABASE;


public class ConnectionConfigurationBuilder implements Builder<ConnectionConfiguration> {
    private static final Log log = LogFactory.getLog(ConnectionConfigurationBuilder.class, Log.class);


    private final MongoDbStoreConfigurationBuilder storeBuilder;
    private final AttributeSet attributes;

    public ConnectionConfigurationBuilder(MongoDbStoreConfigurationBuilder storeBuilder) {
        this.storeBuilder = storeBuilder;
        this.attributes = ConnectionConfiguration.attributeDefinitionSet();
    }

    public ConnectionConfigurationBuilder uri(String uri) {
        attributes.attribute(CONNECTION_URI).set(uri);
        return this;
    }

    public ConnectionConfigurationBuilder database(String database) {
        attributes.attribute(DATABASE).set(database);
        return this;
    }

    public ConnectionConfigurationBuilder collection(String collection) {
        attributes.attribute(COLLECTION).set(collection);
        return this;
    }

    public MongoDbStoreConfigurationBuilder store() {
        return storeBuilder;
    }


    @Override
    public void validate() {
        Builder.super.validate();

        String uri = attributes.<String>attribute(CONNECTION_URI).get();
        ConnectionString connString = new ConnectionString(uri);
        String uriDatabase = connString.getDatabase();
        String configDatabase = attributes.<String>attribute(DATABASE).get();
        if ((uriDatabase == null || uriDatabase.isBlank()) && (configDatabase == null || configDatabase.isBlank())) {
            throw log.missingDatabase();
        }
        if (uriDatabase != null && !uriDatabase.isBlank() && configDatabase != null && !configDatabase.isBlank()) {
            throw log.duplicateDatabase(uriDatabase, configDatabase);
        }

        String collection = attributes.<String>attribute(COLLECTION).get();
        if (collection == null || collection.isBlank()) {
            throw log.missingCollection();
        }
    }

    @Override
    public ConnectionConfiguration create() {
        return new ConnectionConfiguration(attributes.protect());
    }

    @Override
    public Builder<?> read(ConnectionConfiguration template) {
        attributes.read(template.attributes());
        return this;
    }

    @Override
    public AttributeSet attributes() {
        return attributes;
    }
}
