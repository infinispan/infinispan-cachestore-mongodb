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
 * This class is a AbstractStoreConfiguration child
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@ConfigurationFor(MongoDBStore.class)
@BuiltBy(MongoDBStoreConfigurationBuilder.class)
public class MongoDBStoreConfiguration extends AbstractStoreConfiguration {
    private String hostname;
    private int port;
    private String database;
    private String collection;
    private String username;
    private String password;
    private boolean secure;

    public MongoDBStoreConfiguration(boolean purgeOnStartup, boolean fetchPersistentState, boolean ignoreModifications, AsyncStoreConfiguration async, SingletonStoreConfiguration singletonStore, boolean preload, boolean shared, Properties properties, String hostname, int port, String database, String collection, String username, String password, boolean secure) {
        super(purgeOnStartup, fetchPersistentState, ignoreModifications, async, singletonStore, preload, shared, properties);
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.collection = collection;
        this.username = username;
        this.password = password;
        this.secure = secure;
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

    public boolean secure() {
        return secure;
    }
}
