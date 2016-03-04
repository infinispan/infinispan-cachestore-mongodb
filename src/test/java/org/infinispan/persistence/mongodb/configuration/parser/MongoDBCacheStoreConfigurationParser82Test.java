package org.infinispan.persistence.mongodb.configuration.parser;

import org.infinispan.configuration.cache.StoreConfiguration;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.infinispan.test.TestingUtil.INFINISPAN_START_TAG_NO_SCHEMA;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Test for MongoDBCacheStoreConfigurationParser82Test
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@Test(groups = "unit", testName = "persistence.mongodb.configuration.parser.MongoDBCacheStoreConfigurationParser82Test")
public class MongoDBCacheStoreConfigurationParser82Test extends AbstractInfinispanTest {
   private EmbeddedCacheManager cacheManager;

   @AfterMethod(alwaysRun = true)
   public void cleanup() {
      TestingUtil.killCacheManagers(cacheManager);
   }

   public void testMongoDBCacheStore() throws Exception {
      String uri = "mongodb://mongoUser:mongoPass@localhost:27017/infinispan_test_database?w=0&amp;connectTimeoutMS=2000";
      String config = INFINISPAN_START_TAG_NO_SCHEMA + "\n" +
              "   <cache-container default-cache=\"defaultCache\">\n" +
              "     <local-cache name=\"defaultCache\">\n" +
              "       <persistence>\n" +
              "         <mongodbStore xmlns=\"urn:infinispan:config:mongodb:8.2\" >\n" +
              "           <connection uri=\"" + uri + "\" collection=\"infinispan_cachestore\"/>\n" +
              "         </mongodbStore>\n" +
              "       </persistence>\n" +
              "     </local-cache>\n" +
              "   </cache-container>\n" +
              TestingUtil.INFINISPAN_END_TAG;

      MongoDBStoreConfiguration store = (MongoDBStoreConfiguration) buildCacheManagerWithCacheStore(config);
      assertEquals(store.getConnectionURI(), uri.replaceAll("amp;",""));
      assertEquals(store.collection(), "infinispan_cachestore");
      assertFalse(store.fetchPersistentState());
      assertFalse(store.purgeOnStartup());
      assertFalse(store.ignoreModifications());
      assertFalse(store.async().enabled());
   }

   private StoreConfiguration buildCacheManagerWithCacheStore(final String config) throws IOException {
      InputStream is = new ByteArrayInputStream(config.getBytes());
      cacheManager = TestCacheManagerFactory.fromStream(is);
      assert cacheManager.getDefaultCacheConfiguration().persistence().stores().size() == 1;
      return cacheManager.getDefaultCacheConfiguration().persistence().stores().get(0);
   }
}