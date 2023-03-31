package org.infinispan.persistence.mongodb;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.persistence.BaseNonBlockingStoreTest;
import org.infinispan.persistence.mongodb.config.MongoDbStoreConfigurationBuilder;
import org.infinispan.persistence.spi.NonBlockingStore;
import org.infinispan.persistence.spi.PersistenceException;
import org.testcontainers.containers.MongoDBContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Test for {@link MongoDbStore].
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 */
@Test(groups = "functional", testName = "org.infinispan.persistence.mongodb.MongoDbStoreTest")
public class MongoDbStoreTest extends BaseNonBlockingStoreTest {

    private static final String DATABASE = "databaseName";
    private static final String COLLECTION = "collectionName";

    private static MongoDBContainer mongoDbContainer;
    private MongoDbStore<Object, Object> mongoDbStore;


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
    protected NonBlockingStore<Object, Object> createStore() {
        this.mongoDbStore = new MongoDbStore<>();
        return mongoDbStore;
    }

    @AfterMethod
    public void tearDown() {
        mongoDbStore.clear();
    }


    @Override
    protected Configuration buildConfig(ConfigurationBuilder configurationBuilder) {
        return configurationBuilder
                .persistence()
                .addStore(MongoDbStoreConfigurationBuilder.class)
                .connection()
                .uri(mongoDbContainer.getConnectionString() + "/" + DATABASE + "?connectTimeoutMS=1000&w=1")
                .collection(COLLECTION)
                .store()
                .build();
    }
}
