package org.infinispan.persistence.mongodb.config;

import org.infinispan.configuration.cache.StoreConfiguration;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Test for {@link MongoDbStoreConfigurationParser}.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 * @author Gabriel Francisco &lt;gabfssilva@gmail.com&gt;
 */
@Test(groups = "unit", testName = "org.infinispan.persistence.mongodb.config.MongoDbStoreConfigurationParserTest")
public class MongoDbStoreConfigurationParserTest extends AbstractInfinispanTest {
    private EmbeddedCacheManager cacheManager;

    @AfterMethod
    public void cleanup() {
        TestingUtil.killCacheManagers(cacheManager);
    }

    @Test
    public void testMongoDBCacheStore() throws Exception {
        MongoDbStoreConfiguration store = (MongoDbStoreConfiguration) buildCacheManagerWithCacheStore("configs/all/mongodb-config.xml");
        assertEquals("mongodb://mongoUser:mongoPass@localhost:27017/infinispan_test_database?w=0&connectTimeoutMS=2000", store.connection().uri());
        assertEquals("infinispan_cachestore", store.connection().collection());
        assertTrue(store.async().enabled());
    }

    private StoreConfiguration buildCacheManagerWithCacheStore(String config) throws IOException {
        cacheManager = TestCacheManagerFactory.fromXml(config);
        List<StoreConfiguration> stores = cacheManager.getCacheConfiguration("cache").persistence().stores();
        assertEquals(1, stores.size());
        return stores.get(0);
    }
}
