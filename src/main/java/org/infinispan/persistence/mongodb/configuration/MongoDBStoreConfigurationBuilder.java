package org.infinispan.persistence.mongodb.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

/**
 * A MongoDBStoreConfiguration Builder. <br/>
 *
 * This class creates a MongoDBStoreConfiguration.
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
    private int timeout;
    private int acknowledgment;

    public MongoDBStoreConfigurationBuilder(PersistenceConfigurationBuilder builder) {
        super(builder);
    }

    @Override
    public MongoDBStoreConfiguration create() {
        return new MongoDBStoreConfiguration(purgeOnStartup, fetchPersistentState, ignoreModifications,
                async.create(), singletonStore.create(), preload, shared, properties, hostname, port, timeout,
                acknowledgment, database, collection, username, password);
    }

    @Override
    public MongoDBStoreConfigurationBuilder read(MongoDBStoreConfiguration template) {
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
        this.acknowledgment = template.acknowledgment();
        this.timeout = template.timeout();

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

    public MongoDBStoreConfigurationBuilder timeout(int timeout) {
        this.timeout = timeout;
        return self();
    }

    public MongoDBStoreConfigurationBuilder acknowledgment(int acknowledgment) {
        this.acknowledgment = acknowledgment;
        return self();
    }

    @Override
    public MongoDBStoreConfigurationBuilder self() {
        return this;
    }
}
