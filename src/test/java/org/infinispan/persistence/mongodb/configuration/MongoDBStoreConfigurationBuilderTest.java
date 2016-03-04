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
   private static final String CONNECTION_URI = "mongodb://localhost";
   private static final String COLLECTION = "collection";

   @Test
   public void testBuild() {
      ConfigurationBuilder builder = new ConfigurationBuilder();

      Configuration conf = builder
              .persistence()
              .addStore(MongoDBStoreConfigurationBuilder.class)
              .connectionURI(CONNECTION_URI)
              .collection(COLLECTION)
              .build();

      MongoDBStoreConfiguration configuration = (MongoDBStoreConfiguration) conf.persistence().stores().get(0);

      assertTrue(CONNECTION_URI.equals(configuration.getConnectionURI()));
      assertTrue(COLLECTION.equals(configuration.collection()));
   }
}
