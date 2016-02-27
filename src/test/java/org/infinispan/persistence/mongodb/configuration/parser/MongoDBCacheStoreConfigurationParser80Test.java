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

/**
 * Test for MongoDBCacheStoreConfigurationParser80Test
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@Test(groups = "unit", testName = "persistence.mongodb.configuration.parser.MongoDBCacheStoreConfigurationParser80Test")
public class MongoDBCacheStoreConfigurationParser80Test extends AbstractInfinispanTest {
    private EmbeddedCacheManager cacheManager;

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        TestingUtil.killCacheManagers(cacheManager);
    }

    public void testMongoDBCacheStore() throws Exception {
        String config = INFINISPAN_START_TAG_NO_SCHEMA + "\n" +
                "   <default>\n" +
                "     <persistence>\n" +
                "       <mongodbStore xmlns=\"urn:infinispan:config:mongodb:6.0\" >\n" +
                "         <connection hostname=\"localhost\" port=\"27017\" timeout=\"2000\" acknowledgment=\"0\"/>\n" +
                "		  <authentication username=\"mongoUser\" password=\"mongoPass\" />\n" +
                "		  <storage database=\"infinispan_test_database\" collection=\"infispan_cachestore\" />\n" +
                "       </mongodbStore>\n" +
                "     </persistence>\n" +
                "   </default>\n" +
                TestingUtil.INFINISPAN_END_TAG;

        MongoDBStoreConfiguration store = (MongoDBStoreConfiguration) buildCacheManagerWithCacheStore(config);
        assert store.hostname().equals("localhost");
        assert store.port() == 27017;
        assert store.username().equals("mongoUser");
        assert store.password().equals("mongoPass");
        assert store.database().equals("infinispan_test_database");
        assert store.collection().equals("infispan_cachestore");
        assert store.acknowledgment() == 0;
        assert !store.fetchPersistentState();
        assert !store.purgeOnStartup();
        assert !store.ignoreModifications();
        assert !store.async().enabled();
    }

    private StoreConfiguration buildCacheManagerWithCacheStore(final String config) throws IOException {
        InputStream is = new ByteArrayInputStream(config.getBytes());
        cacheManager = TestCacheManagerFactory.fromStream(is);
        assert cacheManager.getDefaultCacheConfiguration().persistence().stores().size() == 1;
        return cacheManager.getDefaultCacheConfiguration().persistence().stores().get(0);
    }
}