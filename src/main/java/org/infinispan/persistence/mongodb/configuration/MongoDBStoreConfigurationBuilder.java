package org.infinispan.persistence.mongodb.configuration;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

/**
 * A MongoDBStoreConfiguration Builder. <br/>
 * This class creates a MongoDBStoreConfiguration, wrapping all the connection data to the MongoDB.
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class MongoDBStoreConfigurationBuilder extends AbstractStoreConfigurationBuilder<MongoDBStoreConfiguration, MongoDBStoreConfigurationBuilder> {
    private String hostname;
    private int port;
    private String database;
    private String collection;
    private String username;
    private String password;
    private boolean secure;

    public MongoDBStoreConfigurationBuilder(PersistenceConfigurationBuilder builder) {
        super(builder);
    }

    @Override
    public MongoDBStoreConfiguration create() {
        return new MongoDBStoreConfiguration(purgeOnStartup, fetchPersistentState, ignoreModifications,
                async.create(), singletonStore.create(), preload, shared, properties, hostname,
                port, database, collection, username, password, secure);
    }

    @Override
    public Builder<?> read(MongoDBStoreConfiguration template) {
        this.fetchPersistentState = template.fetchPersistentState();
        this.ignoreModifications = template.ignoreModifications();
        this.properties = template.properties();
        this.purgeOnStartup = template.purgeOnStartup();
        this.async.read(template.async());
        this.singletonStore.read(template.singletonStore());

        this.hostname = template.hostname();
        this.port = template.port();
        this.database = template.database();
        this.collection = template.collection();
        this.username = template.username();
        this.password = template.password();
        this.secure = template.secure();

        return self();
    }


    public MongoDBStoreConfigurationBuilder hostname(String hostname) {
        this.hostname = hostname;
        return self();
    }

    public MongoDBStoreConfigurationBuilder port(int port) {
        this.port = port;
        return self();
    }

    public MongoDBStoreConfigurationBuilder collection(String collection) {
        this.collection = collection;
        return self();
    }

    public MongoDBStoreConfigurationBuilder database(String database) {
        this.database = database;
        return self();
    }

    public MongoDBStoreConfigurationBuilder username(String username) {
        this.username = username;
        return self();
    }

    public MongoDBStoreConfigurationBuilder password(String password) {
        this.password = password;
        return self();
    }

    public MongoDBStoreConfigurationBuilder secure(boolean secure) {
        this.secure = secure;
        return self();
    }

    @Override
    public MongoDBStoreConfigurationBuilder self() {
        return this;
    }
}
