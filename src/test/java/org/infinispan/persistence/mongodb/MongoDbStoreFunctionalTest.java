package org.infinispan.persistence.mongodb;

import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.mongodb.config.MongoDbStoreConfigurationBuilder;
import org.infinispan.persistence.spi.PersistenceException;
import org.testcontainers.containers.MongoDBContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Functional test for {@link MongoDbStore].
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 */
@Test(groups = "functional", testName = "org.infinispan.persistence.mongodb.MongoDbStoreFunctionalTest")
public class MongoDbStoreFunctionalTest extends BaseStoreFunctionalTest {

    private static final String DATABASE = "databaseName";
    private static final String COLLECTION = "collectionName";

    private static MongoDBContainer mongoDbContainer;


    @BeforeClass
    public static void setupContainer() throws PersistenceException {
        mongoDbContainer = new MongoDBContainer("mongo:6.0.5");
        mongoDbContainer.start();
    }

    @AfterClass
    public static void teardownContainer() {
        mongoDbContainer.stop();
    }


    @Override
    protected PersistenceConfigurationBuilder createCacheStoreConfig(PersistenceConfigurationBuilder persistence, String cacheName, boolean preload) {
        persistence
                .addStore(MongoDbStoreConfigurationBuilder.class)
                .preload(preload)
                .connection()
                .uri(mongoDbContainer.getConnectionString() + "/" + DATABASE + "?connectTimeoutMS=1000&w=1")
                .collection(COLLECTION);
        return persistence;
    }


    @Test(enabled = false, description = "Not applicable")
    @Override
    public void testTwoCachesSameCacheStore() {
        // Stores are always shared
    }
}
