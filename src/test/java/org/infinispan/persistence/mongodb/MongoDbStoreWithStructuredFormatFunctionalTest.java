package org.infinispan.persistence.mongodb;

import com.mongodb.reactivestreams.client.MongoClients;
import io.reactivex.rxjava3.core.Flowable;
import org.bson.Document;
import org.infinispan.Cache;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.mongodb.config.MongoDbStoreConfigurationBuilder;
import org.infinispan.persistence.spi.PersistenceException;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.data.Address;
import org.infinispan.test.data.Person;
import org.infinispan.test.data.Sex;
import org.testcontainers.containers.MongoDBContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.infinispan.persistence.mongodb.MongoDbStore.CREATED;
import static org.infinispan.persistence.mongodb.MongoDbStore.EXPIRY_TIME;
import static org.infinispan.persistence.mongodb.MongoDbStore.LAST_USED;
import static org.infinispan.persistence.mongodb.MongoDbStore.VALUE;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;


/**
 * Functional test for {@link MongoDbStore} using the {@link DataFormat#STRUCTURED} format.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 */
@Test(groups = "functional", testName = "org.infinispan.persistence.mongodb.MongoDbStoreWithStructuredFormatFunctionalTest")
public class MongoDbStoreWithStructuredFormatFunctionalTest extends BaseStoreFunctionalTest {

    private static final String DATABASE = "databaseName";
    private static final String COLLECTION = "collectionName";

    private static MongoDBContainer mongoDbContainer;


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
    protected ConfigurationBuilder getDefaultCacheConfiguration() {
        ConfigurationBuilder builder = super.getDefaultCacheConfiguration();
        builder.encoding().mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE);
        return builder;
    }

    @Override
    protected PersistenceConfigurationBuilder createCacheStoreConfig(PersistenceConfigurationBuilder persistence, String cacheName, boolean preload) {
        persistence
                .addStore(MongoDbStoreConfigurationBuilder.class)
                .preload(preload)
                .format(DataFormat.STRUCTURED)
                .connection()
                .uri(mongoDbContainer.getConnectionString() + "/" + DATABASE + "?connectTimeoutMS=1000&w=1")
                .collection(COLLECTION);
        return persistence;
    }


    @Test(enabled = false, description = "Not applicable")
    @Override
    public void testTwoCachesSameCacheStore() {
        // Stores are always shared
    }


    public void testCheckMongodbValue() {
        ConfigurationBuilder cb = getDefaultCacheConfiguration();
        createCacheStoreConfig(cb.persistence(), "testPreloadStoredAsBinary", false);
        TestingUtil.defineConfiguration(cacheManager, "testPreloadStoredAsBinary", cb.build());
        Cache<String, Person> cache = cacheManager.getCache("testPreloadStoredAsBinary");
        cache.start();

        byte[] pictureBytes = new byte[]{1, 82, 123, 19};

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        calendar.set(2009, Calendar.MARCH, 18, 3, 22, 57);
        // Chop off the last milliseconds as some databases don't have that high of accuracy
        calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
        Date birthDate = calendar.getTime();

        var person = new Person("1",
                new Address("Street", "City", 12345),
                pictureBytes, Sex.MALE, birthDate, true, 4.6, 5.6f, 8.4, 9.2f
        );
        cache.put("k1", person, 111111, TimeUnit.MILLISECONDS, 222222, TimeUnit.MILLISECONDS);

        // Just to prove nothing in memory even with stop
        cache.getAdvancedCache().getDataContainer().clear();

        assertPersonEqual(person, cache.get("k1"));

        try (var mongoClient = MongoClients.create(mongoDbContainer.getConnectionString() + "/" + DATABASE + "?connectTimeoutMS=1000&w=1")) {
            var database = mongoClient.getDatabase(DATABASE);
            var collection = database.getCollection(COLLECTION);

            var documents = Flowable.fromPublisher(collection.find()).toList().toCompletionStage().toCompletableFuture().get();
            assertEquals(1, documents.size());

            var personDocument = documents.get(0);

            Document key = (Document) personDocument.get("_id");
            assertEquals("string", key.getString("_type"));
            assertEquals("k1", key.getString("_value"));

            Document value = (Document) personDocument.get(VALUE);
            assertEquals("org.infinispan.test.core.Person", value.getString("_type"));
            assertEquals(person.getName(), value.getString("name"));
            assertEquals("AVJ7Ew==", value.getString("picture"));
            assertEquals(person.getSex().name(), value.getString("sex"));
            assertEquals(person.getBirthDate().getTime(), value.get("birthDate"));
            assertEquals(Boolean.valueOf(person.isAcceptedToS()), value.getBoolean("accepted_tos"));
            assertEquals(person.getMoneyOwned(), value.get("moneyOwned"));
            assertEquals(5.6, value.get("moneyOwed"));
            assertEquals(person.getDecimalField(), value.get("decimalField"));
            assertEquals(9.2, value.get("realField"));

            Document address = (Document) value.get("address");
            assertEquals(person.getAddress().getStreet(), address.getString("street"));
            assertEquals(person.getAddress().getCity(), address.getString("city"));
            assertEquals(person.getAddress().getZip(), address.get("zip"));

            assertNotNull(personDocument.getLong(CREATED));
            assertNotNull(personDocument.getLong(EXPIRY_TIME));
            assertNotNull(personDocument.getLong(LAST_USED));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
