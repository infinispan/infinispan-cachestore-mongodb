package org.infinispan.persistence.mongodb.store;

import org.infinispan.commons.io.ByteBufferFactoryImpl;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.SingleFileStoreConfigurationBuilder;
import org.infinispan.marshall.core.MarshalledEntryFactoryImpl;
import org.infinispan.persistence.BaseStoreTest;
import org.infinispan.persistence.DummyInitializationContext;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfigurationBuilder;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Test for MongoDBStoreTest
 *
 * In order to run the test suite, you need to start a MongoDB server instance and set
 * the MONGODB_HOSTNAME and MONGODB_PORT variables. <br/>
 * For example, if your MongoDB is running local, you should set MONGODB_HOSTNAME as localhost and MONGODB_PORT as 27017 (or your mongodb port)
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@Test(groups = "unit", testName = "persistence.mongodb.MongoDBStoreTest")
public class MongoDBStoreTest extends BaseStoreTest {

    public static final String DATABASE = "mongostoretest";
    public static final String COLLECTION = "mongostoretest";

    private MongoDBStore mongoDBStore;

    @Override
    protected AdvancedLoadWriteStore createStore() throws Exception {
        String hostname = System.getProperty("MONGODB_HOSTNAME");
        if (hostname == null || "".equals(hostname)) {
            hostname = "127.0.0.1";
        }

        int port = 27017;
        String configurationPort = System.getProperty("MONGODB_PORT");
        try {
            if (configurationPort != null && !"".equals(configurationPort)) {
                port = Integer.parseInt(configurationPort);
            }
        } catch (NumberFormatException e) {
            throw e;
        }

        ConfigurationBuilder builder = TestCacheManagerFactory.getDefaultCacheConfiguration(false);
        builder
                .persistence()
                .addStore(MongoDBStoreConfigurationBuilder.class)
                .hostname(hostname)
                .port(port)
                .database(DATABASE)
                .collection(COLLECTION)
                .timeout(1000)
                .acknowledgment(1).create();

        mongoDBStore = new MongoDBStore();
        mongoDBStore.init(createContext(builder.build()));

        return mongoDBStore;
    }

    @AfterMethod
    public void tearDown()  {
        mongoDBStore.clear();
    }
}