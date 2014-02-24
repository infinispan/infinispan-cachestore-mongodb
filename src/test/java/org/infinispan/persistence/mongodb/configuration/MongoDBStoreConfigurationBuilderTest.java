package org.infinispan.persistence.mongodb.configuration;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Test for MongoDBStoreConfigurationBuilder
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class MongoDBStoreConfigurationBuilderTest {
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 27017;
    private static final String DATABASE = "database";
    private static final Boolean SECURE = true;
    private static final String COLLECTION = "collection";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Test
    public void testBuild() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration conf = builder
                .persistence()
                .addStore(MongoDBStoreConfigurationBuilder.class)
                .hostname(HOSTNAME)
                .port(PORT)
                .database(DATABASE)
                .collection(COLLECTION)
                .secure(SECURE)
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        MongoDBStoreConfiguration configuration = (MongoDBStoreConfiguration) conf.persistence().stores().get(0);

        assertTrue(HOSTNAME.equals(configuration.hostname()));
        assertTrue(PORT.equals(configuration.port()));
        assertTrue(DATABASE.equals(configuration.database()));
        assertTrue(COLLECTION.equals(configuration.collection()));
        assertTrue(SECURE.equals(configuration.secure()));
        assertTrue(USERNAME.equals(configuration.username()));
        assertTrue(PASSWORD.equals(configuration.password()));
    }
}
