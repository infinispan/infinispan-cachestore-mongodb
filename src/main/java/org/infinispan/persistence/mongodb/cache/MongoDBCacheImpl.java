package org.infinispan.persistence.mongodb.cache;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.persistence.mongodb.store.MongoDBEntry;
import org.infinispan.util.TimeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * An implementation of the MongoDBCache interface.
 *
 * @param <K> - key
 * @param <V> - value
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 * @author gustavonalle
 */
public class MongoDBCacheImpl<K, V> implements MongoDBCache<K, V> {
   private static final int pagingSize = 1024;
   private final TimeService timeService;
   private MongoClient mongoClient;
   private MongoCollection<Document> collection;
   private MongoDBStoreConfiguration mongoCacheConfiguration;

   public MongoDBCacheImpl(MongoDBStoreConfiguration mongoCacheConfiguration, TimeService timeService) throws Exception {
      this.mongoCacheConfiguration = mongoCacheConfiguration;
      this.timeService = timeService;
      init();
   }

   private void init() throws Exception {
      start();
   }

   public void start() throws Exception {
      MongoClientURI mongoClientURI = new MongoClientURI(mongoCacheConfiguration.getConnectionURI());
      this.mongoClient = new MongoClient(mongoClientURI);
      MongoDatabase database = mongoClient.getDatabase(mongoClientURI.getDatabase());
      this.collection = database.getCollection(mongoCacheConfiguration.collection());
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
      return collection.findOneAndDelete(query) != null;
   }

   @Override
   public MongoDBEntry<K, V> get(byte[] key) {
      BasicDBObject query = new BasicDBObject();
      query.put("_id", key);

      MongoCursor<Document> iterator = collection.find(query).iterator();
      if (!iterator.hasNext()) {
         return null;
      }
      return createEntry(iterator.next());

   }

   private MongoDBEntry<K, V> createEntry(Document document) {
      byte[] k = ((Binary) document.get("_id")).getData();
      byte[] v = ((Binary) document.get("value")).getData();
      byte[] m = ((Binary) document.get("metadata")).getData();

      MongoDBEntry.Builder<K, V> mongoDBEntryBuilder = MongoDBEntry.builder();

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
      FindIterable<Document> iterable = lastKey != null ? collection.find(lt("_id", lastKey)) : collection.find();
      iterable.sort(descending("_id")).limit(pagingSize);

      List<MongoDBEntry<K, V>> entries = new ArrayList<>();
      iterable.map(this::createEntry).into(entries);

      return entries;
   }


   @Override
   public List<MongoDBEntry<K, V>> removeExpiredData(byte[] lastKey) {
      long time = timeService.wallClockTime();

      Bson filter = and(lte("expiryTime", new Date(time)), gt("expiryTime", new Date(-1)));

      if (lastKey != null) {
         filter = and(filter, lt("_id", lastKey));
      }

      FindIterable<Document> iterable = collection.find(filter).sort(descending("_id")).limit(pagingSize);

      List<MongoDBEntry<K, V>> listOfExpiredEntries = new ArrayList<>();
      iterable.map(this::createEntry).into(listOfExpiredEntries);

      collection.deleteMany(filter);
      return listOfExpiredEntries;
   }

   @Override
   public void put(MongoDBEntry<K, V> entry) {
      Document document = new Document("_id", entry.getKeyBytes())
              .append("value", entry.getValueBytes())
              .append("metadata", entry.getMetadataBytes())
              .append("expiryTime", entry.getExpiryTime());

      if (containsKey(entry.getKeyBytes())) {
         collection.replaceOne(eq("_id", entry.getKeyBytes()), document);

      } else {
         collection.insertOne(document);
      }
   }

   @Override
   public void stop() {
      mongoClient.close();
   }
}
