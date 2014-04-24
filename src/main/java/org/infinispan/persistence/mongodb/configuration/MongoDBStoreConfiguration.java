package org.infinispan.persistence.mongodb.configuration;

import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.ConfigurationFor;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;
import org.infinispan.configuration.cache.AsyncStoreConfiguration;
import org.infinispan.configuration.cache.SingletonStoreConfiguration;
import org.infinispan.persistence.mongodb.store.MongoDBStore;

import java.util.Properties;

/**
 * The configuration of MongoDBStore. <br/>
 * This class wraps all the MongoDB information for the connection.
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@ConfigurationFor(MongoDBStore.class)
@BuiltBy(MongoDBStoreConfigurationBuilder.class)
public class MongoDBStoreConfiguration extends AbstractStoreConfiguration {
    private String hostname;
    private int port;
    private int timeout;
    private int acknowledgment;
    private String database;
    private String collection;
    private String username;
    private String password;

    public MongoDBStoreConfiguration(boolean purgeOnStartup, boolean fetchPersistentState, boolean ignoreModifications, AsyncStoreConfiguration async, SingletonStoreConfiguration singletonStore, boolean preload, boolean shared, Properties properties, String hostname, int port, int timeout, int acknowledgment, String database, String collection, String username, String password) {
        super(purgeOnStartup, fetchPersistentState, ignoreModifications, async, singletonStore, preload, shared, properties);
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
        this.acknowledgment = acknowledgment;
        this.database = database;
        this.collection = collection;
        this.username = username;
        this.password = password;
    }

    public String hostname() {
        return hostname;
    }

    public int port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String collection() {
        return collection;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public int acknowledgment() {
        return acknowledgment;
    }

    public int timeout() {
        return timeout;
    }
}
