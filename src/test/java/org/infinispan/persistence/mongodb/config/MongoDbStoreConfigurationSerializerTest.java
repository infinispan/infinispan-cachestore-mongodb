package org.infinispan.persistence.mongodb.config;

import org.infinispan.configuration.cache.StoreConfiguration;
import org.infinispan.configuration.serializer.AbstractConfigurationSerializerTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(testName = "org.infinispan.persistence.mongodb.config.MongoDbStoreConfigurationSerializerTest", groups = "functional")
public class MongoDbStoreConfigurationSerializerTest extends AbstractConfigurationSerializerTest {
    @Override
    protected void compareStoreConfiguration(String name, StoreConfiguration beforeStore, StoreConfiguration afterStore) {
        super.compareStoreConfiguration(name, beforeStore, afterStore);
        MongoDbStoreConfiguration before = (MongoDbStoreConfiguration) beforeStore;
        MongoDbStoreConfiguration after = (MongoDbStoreConfiguration) afterStore;
        assertEquals(before.attributes(), after.attributes());
        assertEquals(before.connection().attributes(), after.connection().attributes());
    }
}
