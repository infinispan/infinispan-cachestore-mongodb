package org.infinispan.loaders.mongodb;

import org.infinispan.loaders.BaseCacheStoreTest;
import org.infinispan.loaders.mongodb.configuration.MongoDBCacheStoreConfiguration;
import org.infinispan.loaders.mongodb.configuration.MongoDBCacheStoreConfigurationBuilder;
import org.infinispan.loaders.mongodb.logging.Log;
import org.infinispan.loaders.spi.CacheStore;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.util.logging.LogFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * @author Guillaume Scheibel <guillaume.scheibel@gmail.com>
 */
@Test(testName = "loaders.remote.MongoDBCacheStoreTest", groups = "mongodb")
public class MongoDBCacheStoreTest extends BaseCacheStoreTest {
   private static final Log log = LogFactory.getLog(MongoDBCacheStoreTest.class, Log.class);

   private MongoDBCacheStore cacheStore;

   @Override
   protected CacheStore createCacheStore() throws Exception {
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
         throw log.mongoPortIllegalValue(configurationPort);
      }
      log.runningTest(hostname, port);

      MongoDBCacheStoreConfiguration storeConfiguration = TestCacheManagerFactory.getDefaultCacheConfiguration(false)
            .loaders()
               .addLoader(MongoDBCacheStoreConfigurationBuilder.class)
                  .host(hostname)
                  .port(port)
                  .timeout(2000)
                  .database(UUID.randomUUID().toString())
                  .collection("infinispan_indexes")
                  .acknowledgment(-1)
               .purgeSynchronously(true)
               .create();
      cacheStore = new MongoDBCacheStore();
      cacheStore.init(storeConfiguration, getCache(), getMarshaller());
      cacheStore.start();
      return cacheStore;
   }

   @Override
   @AfterMethod(alwaysRun = true)
   public void tearDown() {
      cacheStore.drop();
   }
}
