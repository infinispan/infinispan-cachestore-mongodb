package org.infinispan.persistence.mongodb.cache;

import com.google.gson.Gson;
import com.mongodb.*;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.persistence.mongodb.store.entry.CacheEntry;
import org.infinispan.persistence.mongodb.store.entry.KeyEntry;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the Cache interface based on MongoDB
 *
 * @param <K> - key
 * @param <V> - value
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class MongoDBCache<K, V> implements Cache<K, V> {
    private MongoClient mongoClient;
    private DB database;
    private DBCollection collection;

    private MongoDBStoreConfiguration mongoCacheConfiguration;

    public MongoDBCache(MongoDBStoreConfiguration mongoCacheConfiguration) throws Exception {
        this.mongoCacheConfiguration = mongoCacheConfiguration;
        init();
    }

    private void init() throws Exception {
        mongoClient = new MongoClient(mongoCacheConfiguration.hostname(), mongoCacheConfiguration.port());
        database = mongoClient.getDB(mongoCacheConfiguration.database());

        if (mongoCacheConfiguration.secure()) {
            database.authenticate(mongoCacheConfiguration.username(), mongoCacheConfiguration.password().toCharArray());
        }

        collection = database.getCollection(mongoCacheConfiguration.collection());

        collection.ensureIndex(new BasicDBObject("key", ""), new BasicDBObject("unique", true));
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
    public CacheEntry<V> remove(KeyEntry<K> keyEntry) {
        BasicDBObject obj = new BasicDBObject();
        obj.put("key", keyEntry);
        return (CacheEntry<V>) collection.remove(obj).getField("value");
    }

    @Override
    public CacheEntry<V> get(KeyEntry<K> keyEntry) {
        Gson gson = new Gson();
        String k = gson.toJson(keyEntry);

        BasicDBObject obj = new BasicDBObject();
        obj.put("key", k);

        BasicDBObject result = (BasicDBObject) collection.findOne(obj);

        if (result == null) {
            return null;
        }

        String v = result.getString("value");
        CacheEntry<V> entry = gson.fromJson(v, CacheEntry.class);

        return entry;
    }

    @Override
    public boolean containsKey(KeyEntry<K> keyEntry) {
        return get(keyEntry) != null;
    }

    @Override
    public Set<KeyEntry<K>> keySet() {
        Gson gson = new Gson();

        Set<KeyEntry<K>> set = new HashSet<KeyEntry<K>>();
        DBCursor dbCursor = collection.find();

        while (dbCursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) dbCursor.next();
            String v = obj.getString("key");
            KeyEntry<K> entry = gson.fromJson(v, KeyEntry.class);
            set.add(entry);
        }

        return set;
    }

    @Override
    public void removeExpiredData() {
        Date d = new Date();
        DBObject query = QueryBuilder.start().put("expiration").lessThanEquals(d).get();
        collection.remove(query);
    }

    @Override
    public void put(KeyEntry<K> keyEntry, CacheEntry<V> cacheEntry) {
        cleanUnusedObjects(cacheEntry);

        BasicDBObject obj = new BasicDBObject();

        Gson gson = new Gson();

        String k = gson.toJson(keyEntry);
        String v = gson.toJson(cacheEntry);

        obj.put("key", k);
        obj.put("value", v);
        obj.put("expiration", cacheEntry.getExpiration());

        if (containsKey(keyEntry)) {
            BasicDBObject query = new BasicDBObject();
            query.put("key", k);
            collection.update(query, obj);
            return;
        }

        collection.insert(obj);
    }

    private void cleanUnusedObjects(CacheEntry<V> cacheEntry) {
        cacheEntry.setKeyByteBuffer(null);
        cacheEntry.setValueByteBuffer(null);
    }
}
