package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.CacheConfigurationException;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.persistence.mongodb.DataFormat;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;


/**
 * Test for {@link MongoDbStoreConfigurationBuilder}.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 * @author Gabriel Francisco &lt;gabfssilva@gmail.com&gt;
 */
@Test(groups = "unit", testName = "org.infinispan.persistence.mongodb.config.MongoDbStoreConfigurationBuilderTest")
public class MongoDbStoreConfigurationBuilderTest {
    private static final String CONNECTION_URI_WITH_DATABASE = "mongodb://localhost/uriDatabaseName";
    private static final String CONNECTION_URI_WITHOUT_DATABASE = "mongodb://localhost";
    private static final String DATABASE = "databaseName";
    private static final String COLLECTION = "collectionName";

    @Test
    public void testBuild() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        MongoDbStoreConfigurationBuilder mongoDbStoreConfigurationBuilder = builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class);
        mongoDbStoreConfigurationBuilder.async().enable();
        mongoDbStoreConfigurationBuilder.connection().uri(CONNECTION_URI_WITH_DATABASE);
        mongoDbStoreConfigurationBuilder.connection().collection(COLLECTION);
        Configuration config = builder.build();

        MongoDbStoreConfiguration storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertEquals(CONNECTION_URI_WITH_DATABASE, storeConfig.connection().uri());
        assertNull(storeConfig.connection().database());
        assertEquals(COLLECTION, storeConfig.connection().collection());

        builder = new ConfigurationBuilder();
        builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class).read(storeConfig);
        config = builder.build();
        storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertEquals(CONNECTION_URI_WITH_DATABASE, storeConfig.connection().uri());
        assertNull(storeConfig.connection().database());
        assertEquals(COLLECTION, storeConfig.connection().collection());
    }

    @Test
    public void testBuildWithDatabaseConfig() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        MongoDbStoreConfigurationBuilder mongoDbStoreConfigurationBuilder = builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class);
        mongoDbStoreConfigurationBuilder.async().enable();
        mongoDbStoreConfigurationBuilder.connection().uri(CONNECTION_URI_WITHOUT_DATABASE);
        mongoDbStoreConfigurationBuilder.connection().database(DATABASE);
        mongoDbStoreConfigurationBuilder.connection().collection(COLLECTION);
        Configuration config = builder.build();

        MongoDbStoreConfiguration storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertEquals(CONNECTION_URI_WITHOUT_DATABASE, storeConfig.connection().uri());
        assertEquals(DATABASE, storeConfig.connection().database());
        assertEquals(COLLECTION, storeConfig.connection().collection());

        builder = new ConfigurationBuilder();
        builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class).read(storeConfig);
        config = builder.build();
        storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertEquals(CONNECTION_URI_WITHOUT_DATABASE, storeConfig.connection().uri());
        assertEquals(DATABASE, storeConfig.connection().database());
        assertEquals(COLLECTION, storeConfig.connection().collection());
    }

    @Test(expectedExceptions = CacheConfigurationException.class, expectedExceptionsMessageRegExp =
            ".*No database name configured\\. Either specify it in the 'uri' or use the 'database' config\\.")
    public void testBuildWithoutDatabase() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        MongoDbStoreConfigurationBuilder mongoDbStoreConfigurationBuilder = builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class);
        mongoDbStoreConfigurationBuilder.async().enable();
        mongoDbStoreConfigurationBuilder.connection().uri(CONNECTION_URI_WITHOUT_DATABASE);
        mongoDbStoreConfigurationBuilder.connection().collection(COLLECTION);
        Configuration ignored = builder.build();
    }

    @Test(expectedExceptions = CacheConfigurationException.class, expectedExceptionsMessageRegExp =
            ".*Duplicate database name configured \\('uriDatabaseName' vs 'databaseName'\\)\\. Either specify it in the 'uri' or use the 'database' config\\.")
    public void testBuildWithDuplicateDatabase() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        MongoDbStoreConfigurationBuilder mongoDbStoreConfigurationBuilder = builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class);
        mongoDbStoreConfigurationBuilder.async().enable();
        mongoDbStoreConfigurationBuilder.connection().uri(CONNECTION_URI_WITH_DATABASE);
        mongoDbStoreConfigurationBuilder.connection().database(DATABASE);
        mongoDbStoreConfigurationBuilder.connection().collection(COLLECTION);
        Configuration ignored = builder.build();
    }

    @Test
    public void testBuildWithConverter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        MongoDbStoreConfigurationBuilder mongoDbStoreConfigurationBuilder = builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class);
        mongoDbStoreConfigurationBuilder.async().enable();
        mongoDbStoreConfigurationBuilder.format("binary");
        mongoDbStoreConfigurationBuilder.connection().uri(CONNECTION_URI_WITH_DATABASE);
        mongoDbStoreConfigurationBuilder.connection().collection(COLLECTION);
        Configuration config = builder.build();

        MongoDbStoreConfiguration storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertEquals(DataFormat.BINARY, storeConfig.format());

        builder = new ConfigurationBuilder();
        builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class).read(storeConfig);
        config = builder.build();
        storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertEquals(DataFormat.BINARY, storeConfig.format());
    }

    @Test
    public void testBuildWithCommonAttributes() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        MongoDbStoreConfigurationBuilder mongoDbStoreConfigurationBuilder = builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class);
        mongoDbStoreConfigurationBuilder.async().enable();
        mongoDbStoreConfigurationBuilder.preload(true);
        mongoDbStoreConfigurationBuilder.connection().uri(CONNECTION_URI_WITH_DATABASE);
        mongoDbStoreConfigurationBuilder.connection().collection(COLLECTION);
        Configuration config = builder.build();

        MongoDbStoreConfiguration storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertTrue(storeConfig.preload());

        builder = new ConfigurationBuilder();
        builder.persistence().addStore(MongoDbStoreConfigurationBuilder.class).read(storeConfig);
        config = builder.build();
        storeConfig = (MongoDbStoreConfiguration) config.persistence().stores().get(0);

        assertTrue(storeConfig.preload());
    }
}
