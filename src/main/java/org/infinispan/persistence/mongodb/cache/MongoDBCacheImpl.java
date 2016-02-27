package org.infinispan.persistence.mongodb.cache;

import com.mongodb.*;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.persistence.mongodb.store.MongoDBEntry;
import org.infinispan.util.TimeService;

import java.util.*;

/**
 * An implementation of the MongoDBCache interface.
 *
 * @param <K> - key
 * @param <V> - value
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class MongoDBCacheImpl<K, V> implements MongoDBCache<K, V> {
    private MongoClient mongoClient;
    private DB database;
    private DBCollection collection;
    private final TimeService timeService;

    private MongoDBStoreConfiguration mongoCacheConfiguration;

    private static final int pagingSize = 1024;

    public MongoDBCacheImpl(MongoDBStoreConfiguration mongoCacheConfiguration, TimeService timeService) throws Exception {
        this.mongoCacheConfiguration = mongoCacheConfiguration;
        this.timeService = timeService;
        init();
    }

    private void init() throws Exception {
        MongoClientOptions.Builder mongoClientOptionsBuilder = MongoClientOptions.builder();

        mongoClientOptionsBuilder
                .connectTimeout(mongoCacheConfiguration.timeout())
                .writeConcern(new WriteConcern(mongoCacheConfiguration.acknowledgment()));

        ServerAddress serverAddress = new ServerAddress(mongoCacheConfiguration.hostname(), mongoCacheConfiguration.port());

        mongoClient = new MongoClient(serverAddress, mongoClientOptionsBuilder.build());

        database = mongoClient.getDB(mongoCacheConfiguration.database());

        if (!"".equals(mongoCacheConfiguration.username()) && mongoCacheConfiguration.username() != null) {
            database.authenticate(mongoCacheConfiguration.username(), mongoCacheConfiguration.password().toCharArray());
        }

        collection = database.getCollection(mongoCacheConfiguration.collection());
    }

    @Override
    public int size() {
        return (int) collection.count();
    }


    @Override
    public void clear() {
        collection.drop();
    }

    @Override
    public boolean remove(byte[] key) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", key);
        return collection.findAndRemove(query) != null;
    }

    @Override
    public MongoDBEntry<K, V> get(byte[] key) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", key);

        DBCursor cursor = collection.find(query);

        if(!cursor.hasNext()){
            return null;
        }
        try {
            return createEntry(cursor);
        } finally {
            cursor.close();
        }
    }

    private MongoDBEntry<K,V> createEntry(DBCursor cursor) {
        BasicDBObject result = (BasicDBObject) cursor.next();

        byte[] k = (byte[]) result.get("_id");
        byte[] v = (byte[]) result.get("value");
        byte[] m = (byte[]) result.get("metadata");

        MongoDBEntry.Builder mongoDBEntryBuilder = MongoDBEntry.builder();

        mongoDBEntryBuilder
                .keyBytes(k)
                .valueBytes(v)
                .metadataBytes(m);

        return mongoDBEntryBuilder.create();
    }

    public boolean containsKey(byte[] key) {
        return get(key) != null;
    }

    @Override
    public List<MongoDBEntry<K, V>> getPagedEntries(byte[] lastKey) {
        QueryBuilder queryBuilder = QueryBuilder.start();

        if(lastKey != null) {
            queryBuilder.put("_id").lessThan(lastKey);
        }
        DBObject query = queryBuilder.get();
        DBCursor cursor = collection.find(query).sort(new BasicDBObject("_id", -1)).limit(pagingSize);

        List<MongoDBEntry<K,V>> entries = getListFromCursor(cursor);
        return entries;
    }


    private List<MongoDBEntry<K,V>> getListFromCursor(DBCursor cursor) {
        List<MongoDBEntry<K,V>> entries = new ArrayList<>(cursor.size());
        try {
            while (cursor.hasNext()) {
                entries.add(createEntry(cursor));
            }
            return entries;
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<MongoDBEntry<K, V>> removeExpiredData(byte[] lastKey) {
        QueryBuilder queryBuilder = QueryBuilder.start();

        long time = timeService.wallClockTime();

        queryBuilder
                .put("expiryTime")
                .lessThanEquals(new Date(time))
                .greaterThan(new Date(-1));

        if(lastKey != null) {
            queryBuilder.put("_id").lessThan(lastKey);
        }

        DBObject query = queryBuilder.get();
        List<MongoDBEntry<K, V>> listOfExpiredEntries;
        try(DBCursor cursor = collection.find(query).sort(new BasicDBObject("_id", -1)).limit(pagingSize)) {
            listOfExpiredEntries = getListFromCursor(cursor);
        }
        collection.remove(query);
        return listOfExpiredEntries;
    }

    @Override
    public void put(MongoDBEntry<K, V> entry) {
        BasicDBObject object = new BasicDBObject();

        object.put("_id", entry.getKeyBytes());
        object.put("value", entry.getValueBytes());
        object.put("metadata", entry.getMetadataBytes());
        object.put("expiryTime", entry.getExpiryTime());

        if (containsKey(entry.getKeyBytes())) {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", entry.getKeyBytes());
            BasicDBObject target = (BasicDBObject) collection.findOne(query);
            collection.update(target, object);
            return;
        }

        collection.insert(object);
    }
}
