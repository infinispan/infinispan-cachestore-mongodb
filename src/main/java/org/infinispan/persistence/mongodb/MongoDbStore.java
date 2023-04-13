package org.infinispan.persistence.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.infinispan.commons.configuration.ConfiguredBy;
import org.infinispan.commons.util.IntSet;
import org.infinispan.commons.util.concurrent.CompletableFutures;
import org.infinispan.persistence.mongodb.config.ConnectionConfiguration;
import org.infinispan.persistence.mongodb.config.MongoDbStoreConfiguration;
import org.infinispan.persistence.mongodb.converter.BinaryCacheToStoreConverter;
import org.infinispan.persistence.mongodb.converter.CacheToStoreConverter;
import org.infinispan.persistence.mongodb.converter.StructuredCacheToStoreConverter;
import org.infinispan.persistence.spi.InitializationContext;
import org.infinispan.persistence.spi.MarshallableEntry;
import org.infinispan.persistence.spi.NonBlockingStore;
import org.kohsuke.MetaInfServices;
import org.reactivestreams.Publisher;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Predicate;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static org.infinispan.persistence.mongodb.DataFormat.STRUCTURED;


/**
 * {@link NonBlockingStore} implementation for MongoDB.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 */
@MetaInfServices
@ConfiguredBy(MongoDbStoreConfiguration.class)
public class MongoDbStore<K, V> implements NonBlockingStore<K, V> {
    public static final String VALUE = "value";
    public static final String METADATA = "metadata";
    public static final String EXPIRY_TIME = "expiryTime";
    public static final String CREATED = "created";
    public static final String LAST_USED = "lastUsed";

    private InitializationContext context;
    private MongoClient mongoClient;
    private MongoCollection<Document> collection;
    private CacheToStoreConverter<K, V> cacheToStoreConverter;


    @Override
    public Set<Characteristic> characteristics() {
        return EnumSet.of(Characteristic.SHAREABLE, Characteristic.BULK_READ, Characteristic.EXPIRATION);
    }


    @Override
    public CompletionStage<Void> start(InitializationContext ctx) {
        this.context = ctx;

        MongoDbStoreConfiguration configuration = ctx.getConfiguration();
        ConnectionConfiguration connectionConfiguration = configuration.connection();

        this.cacheToStoreConverter = configuration.format() == STRUCTURED
                ? new StructuredCacheToStoreConverter<>(ctx, ctx.getPersistenceMarshaller())
                : new BinaryCacheToStoreConverter<>(ctx, ctx.getPersistenceMarshaller());

        String connectionUri = connectionConfiguration.uri();
        ConnectionString connString = new ConnectionString(connectionUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        this.mongoClient = MongoClients.create(settings);

        String databaseName;
        if (connString.getDatabase() != null) {
            databaseName = connString.getDatabase();
        } else {
            databaseName = connectionConfiguration.database();
        }
        String collectionName = connectionConfiguration.collection();
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);

        return CompletableFutures.completedNull();
    }

    @Override
    public CompletionStage<Void> stop() {
        mongoClient.close();
        return CompletableFutures.completedNull();
    }


    @Override
    public CompletionStage<Long> size(IntSet segments) {
        return Single.fromPublisher(collection.countDocuments()).toCompletionStage();
    }

    @Override
    public CompletionStage<Long> approximateSize(IntSet segments) {
        return Single.fromPublisher(collection.estimatedDocumentCount()).toCompletionStage();
    }


    @Override
    public CompletionStage<Boolean> containsKey(int segment, Object key) {
        return load(segment, key).thenApply(Objects::nonNull);
    }

    @Override
    public CompletionStage<MarshallableEntry<K, V>> load(int segment, Object key) {
        long now = context.getTimeService().wallClockTime();
        Object storeKey = cacheToStoreConverter.toStoreKey(key);
        Bson filter = and(eq("_id", storeKey), or(gt(EXPIRY_TIME, now), lte(EXPIRY_TIME, -1L)));

        return Maybe.fromPublisher(collection.find(filter))
                .map(document -> cacheToStoreConverter.toCacheEntry(key, document))
                .toCompletionStage(null);
    }

    @Override
    public CompletionStage<Void> write(int segment, MarshallableEntry<? extends K, ? extends V> marshallableEntry) {
        Object storeKey = cacheToStoreConverter.toStoreKey(marshallableEntry.getKey());
        Document storeEntry = cacheToStoreConverter.toStoreEntry(storeKey, marshallableEntry);

        Publisher<UpdateResult> publisher = collection.replaceOne(eq("_id", storeKey), storeEntry, new ReplaceOptions().upsert(true));
        return Single.fromPublisher(publisher)
                .ignoreElement()
                .toCompletionStage(null);
    }

    @Override
    public CompletionStage<Boolean> delete(int segment, Object key) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", cacheToStoreConverter.toStoreKey(key));

        return Single.fromPublisher(collection.deleteOne(query))
                .map(e -> e.getDeletedCount() > 0)
                .toCompletionStage();
    }

    @Override
    public CompletionStage<Void> clear() {
        return Completable.fromPublisher(collection.drop())
                .toCompletionStage(null);
    }


    @Override
    public Publisher<MarshallableEntry<K, V>> publishEntries(IntSet segments, Predicate<? super K> filter, boolean includeValues) {
        return Flowable.defer(() -> {
            FindPublisher<Document> iterable = collection.find();

            if (!includeValues) {
                iterable.projection(Projections.include("_id"));
            }

            return Flowable.fromPublisher(iterable)
                    .filter(e -> filter == null || filter.test(cacheToStoreConverter.toCacheKey(e.get("_id"))))
                    .map(cacheToStoreConverter::toCacheEntry);
        });
    }

    @Override
    public Publisher<MarshallableEntry<K, V>> purgeExpired() {
        long now = context.getTimeService().wallClockTime();
        Bson filter = and(lte(EXPIRY_TIME, now), gt(EXPIRY_TIME, -1L));

        return Flowable.fromPublisher(collection.find(filter))
                .map(cacheToStoreConverter::toCacheEntry)
                .concatWith(Flowable.defer(() -> Flowable.fromPublisher(collection.deleteMany(filter)).mapOptional(x -> Optional.empty())));
    }
}
