package org.infinispan.persistence.mongodb.configuration.parser;

import org.infinispan.configuration.cache.StoreConfiguration;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Test for MongoDBCacheStoreConfigurationParserTest
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@Test(groups = "unit", testName = "org.infinispan.persistence.mongodb.configuration.parser.MongoDBCacheStoreConfigurationParserTest")
public class MongoDBCacheStoreConfigurationParserTest extends AbstractInfinispanTest {
   private EmbeddedCacheManager cacheManager;

   @AfterMethod(alwaysRun = true)
   public void cleanup() {
      TestingUtil.killCacheManagers(cacheManager);
   }

   public void testMongoDBCacheStore() throws Exception {
      MongoDBStoreConfiguration store = (MongoDBStoreConfiguration) buildCacheManagerWithCacheStore("config/mongodb-config.xml");
      assertEquals("mongodb://mongoUser:mongoPass@localhost:27017/infinispan_test_database?w=0&connectTimeoutMS=2000", store.getConnectionURI());
      assertEquals("infinispan_cachestore", store.collection());
      assertFalse(store.fetchPersistentState());
      assertFalse(store.purgeOnStartup());
      assertFalse(store.ignoreModifications());
      assertTrue(store.async().enabled());
   }

   private StoreConfiguration buildCacheManagerWithCacheStore(String config) throws IOException {
      cacheManager = TestCacheManagerFactory.fromXml(config);
      List<StoreConfiguration> stores = cacheManager.getCacheConfiguration("cache").persistence().stores();
      assertEquals(1, stores.size());
      return stores.get(0);
   }
}