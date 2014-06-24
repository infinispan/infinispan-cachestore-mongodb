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
@Test(groups = "unit", testName = "persistence.mongodb.configuration.MongoDBStoreConfigurationBuilderTest")
public class MongoDBStoreConfigurationBuilderTest {
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 27017;
    private static final String DATABASE = "database";
    private static final String COLLECTION = "collection";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final Integer TIMEOUT = 1000;
    private static final Integer ACKNOWLEDGMENT = 10;

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
                .username(USERNAME)
                .password(PASSWORD)
                .acknowledgment(ACKNOWLEDGMENT)
                .timeout(TIMEOUT)
                .build();

        MongoDBStoreConfiguration configuration = (MongoDBStoreConfiguration) conf.persistence().stores().get(0);

        assertTrue(HOSTNAME.equals(configuration.hostname()));
        assertTrue(PORT.equals(configuration.port()));
        assertTrue(DATABASE.equals(configuration.database()));
        assertTrue(COLLECTION.equals(configuration.collection()));
        assertTrue(USERNAME.equals(configuration.username()));
        assertTrue(PASSWORD.equals(configuration.password()));
    }
}
