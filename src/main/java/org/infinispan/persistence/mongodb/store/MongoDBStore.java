package org.infinispan.persistence.mongodb.store;

import net.jcip.annotations.ThreadSafe;
import org.infinispan.commons.configuration.ConfiguredBy;
import org.infinispan.commons.marshall.StreamingMarshaller;
import org.infinispan.commons.persistence.Store;
import org.infinispan.executors.ExecutorAllCompletionService;
import org.infinispan.filter.KeyFilter;
import org.infinispan.marshall.core.MarshalledEntry;
import org.infinispan.metadata.InternalMetadata;
import org.infinispan.persistence.TaskContextImpl;
import org.infinispan.persistence.mongodb.cache.MongoDBCache;
import org.infinispan.persistence.mongodb.cache.MongoDBCacheImpl;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.infinispan.persistence.spi.InitializationContext;
import org.infinispan.persistence.spi.PersistenceException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * AdvancedLoadWriteStore implementation based on MongoDB. <br/>
 * This class is fully thread safe
 *
 * @param <K>
 * @param <V>
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@ThreadSafe
@Store
@ConfiguredBy(MongoDBStoreConfiguration.class)
public class MongoDBStore<K, V> implements AdvancedLoadWriteStore<K, V> {
   private InitializationContext context;

   private MongoDBCache<K, V> cache;
   private MongoDBStoreConfiguration configuration;

   @Override
   public void init(InitializationContext ctx) {
      context = ctx;
      configuration = ctx.getConfiguration();
      try {
         cache = new MongoDBCacheImpl<>(configuration, ctx.getTimeService());
      } catch (Exception e) {
         throw new PersistenceException(e);
      }
   }

   @Override
   public void process(final KeyFilter<? super K> filter, final CacheLoaderTask<K, V> task, Executor executor, boolean fetchValue, boolean fetchMetadata) {
      ExecutorAllCompletionService eacs = new ExecutorAllCompletionService(executor);
      final TaskContextImpl taskContext = new TaskContextImpl();

      //A while loop since we have to hit the db again for paging.
      boolean shouldContinue = true;
      byte[] id = null;
      while (shouldContinue) {
         final List<MongoDBEntry<K, V>> entries = cache.getPagedEntries(id);
         shouldContinue = !entries.isEmpty();
         if (taskContext.isStopped()) {
            break;
         }
         if (shouldContinue) {
            eacs.submit(() -> {
               for (final MongoDBEntry<K, V> entry : entries) {
                  if (taskContext.isStopped()) {
                     break;
                  }
                  final K marshalledKey = (K) toObject(entry.getKeyBytes());
                  if (filter == null || filter.accept(marshalledKey)) {
                     final MarshalledEntry<K, V> marshalledEntry = getMarshalledEntry(entry);
                     if (marshalledEntry != null) {
                        task.processEntry(marshalledEntry, taskContext);
                     }
                  }
               }
               return null;
            });
            //get last key so we can get more entries.
            id = entries.get(entries.size() - 1).getKeyBytes();
         }
      }
      eacs.waitUntilAllCompleted();
      if (eacs.isExceptionThrown()) {
         throw new PersistenceException("Execution exception!", eacs.getFirstException());
      }

   }

   @Override
   public int size() {
      return cache.size();
   }

   @Override
   public void clear() {
      cache.clear();
   }

   @Override
   public void purge(Executor threadPool, PurgeListener listener) {

      byte[] lastKey = null;
      boolean shouldContinue = true;
      while (shouldContinue) {
         List<MongoDBEntry<K, V>> expired = cache.removeExpiredData(lastKey);
         expired.forEach(kvMongoDBEntry -> listener.entryPurged(kvMongoDBEntry.getKey(marshaller())));
         shouldContinue = !expired.isEmpty();
         if (shouldContinue) {
            lastKey = expired.get(expired.size() - 1).getKeyBytes();
         }
      }
   }

   @Override
   public void write(MarshalledEntry<? extends K, ? extends V> entry) {
      MongoDBEntry.Builder<K, V> mongoDBEntryBuilder = MongoDBEntry.builder();

      mongoDBEntryBuilder
              .keyBytes(toByteArray(entry.getKey()))
              .valueBytes(toByteArray(entry.getValue()))
              .metadataBytes(toByteArray(entry.getMetadata()))
              .expiryTime(entry.getMetadata() != null ? new Date(entry.getMetadata().expiryTime()) : null);

      MongoDBEntry<K, V> mongoDBEntry = mongoDBEntryBuilder.create();

      cache.put(mongoDBEntry);
   }

   @Override
   public boolean delete(Object key) {
      return cache.remove(toByteArray(key));
   }


   @Override
   public MarshalledEntry<K, V> load(Object key) {
      return load(key, false);
   }

   private MarshalledEntry<K, V> load(Object key, boolean binaryData) {
      MongoDBEntry<K, V> mongoDBEntry = cache.get(toByteArray(key));

      if (mongoDBEntry == null) {
         return null;
      }

      K k = mongoDBEntry.getKey(marshaller());
      V v = mongoDBEntry.getValue(marshaller());

      InternalMetadata metadata;

      metadata = (InternalMetadata) toObject(mongoDBEntry.getMetadataBytes());

      MarshalledEntry result = context.getMarshalledEntryFactory().newMarshalledEntry(k, v, metadata);

      if (isExpired(result)) {
         return null;
      }

      return result;
   }

   private MarshalledEntry<K, V> getMarshalledEntry(MongoDBEntry<K, V> mongoDBEntry) {

      if (mongoDBEntry == null) {
         return null;
      }

      K k = mongoDBEntry.getKey(marshaller());
      V v = mongoDBEntry.getValue(marshaller());

      InternalMetadata metadata;

      metadata = (InternalMetadata) toObject(mongoDBEntry.getMetadataBytes());

      MarshalledEntry result = context.getMarshalledEntryFactory().newMarshalledEntry(k, v, metadata);
      return result;
   }

   @Override
   public boolean contains(Object key) {
      MongoDBEntry<K, V> mongoDBEntry = cache.get(toByteArray(key));
      MarshalledEntry result = getMarshalledEntry(mongoDBEntry);
      if (mongoDBEntry == null || isExpired(result)) {
         return false;
      }
      return true;
   }

   @Override
   public void start() {
      try {
         cache.start();
      } catch (Exception e) {
         throw new PersistenceException(e);
      }
      if (configuration.purgeOnStartup()) {
         cache.clear();
      }
   }

   @Override
   public void stop() {

      cache.stop();
   }

   private boolean isExpired(MarshalledEntry result) {
      if (result.getMetadata() == null) {
         return false;
      }

      return result.getMetadata().isExpired(context.getTimeService().wallClockTime());
   }

   private Object toObject(byte[] bytes) {
      try {
         return marshaller().objectFromByteBuffer(bytes);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      return null;
   }

   private byte[] toByteArray(Object obj) {
      try {
         return marshaller().objectToByteBuffer(obj);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      return null;
   }

   private StreamingMarshaller marshaller() {
      return context.getMarshaller();
   }

   public InitializationContext getContext() {
      return context;
   }

   public void setContext(InitializationContext context) {
      this.context = context;
   }

   public MongoDBCache<K, V> getCache() {
      return cache;
   }
}
