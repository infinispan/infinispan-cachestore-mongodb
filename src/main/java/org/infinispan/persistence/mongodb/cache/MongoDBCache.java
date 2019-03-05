package org.infinispan.persistence.mongodb.cache;

import org.infinispan.persistence.mongodb.store.MongoDBEntry;

import java.util.List;

/**
 * A simple Cache interface
 *
 * @param <K> - key
 * @param <V> - value
 * @author Gabriel Francisco &lt;gabfssilva@gmail.com&gt;
 */
public interface MongoDBCache<K, V> {
   /**
    * Size of the cache
    *
    * @return size
    */
   int size();

   /**
    * Purge all data from cache
    */
   void clear();

   /**
    * Remove the entry which has the same key of the parameter
    *
    * @param key
    * @return the removed cache entry
    */
   boolean remove(byte[] key);

   /**
    * Get a cache entry which has the same key of the parameter
    *
    * @param key
    * @return the entry corresponding to the key
    */
   MongoDBEntry<K, V> get(byte[] key);

   /**
    * Verify if the cache contains the key passed on parameter
    *
    * @param key
    * @return true if there is some entry, false if there is not
    */
   boolean containsKey(byte[] key);

   /**
    * Since mongo doesn't support actual paging results,
    * we have to sort and do a less than on the last id.
    *
    * @param lastKey
    * @return a list of entries corresponding to a "page" ending with lastKey
    */
   List<MongoDBEntry<K, V>> getPagedEntries(byte[] lastKey);

   /**
    * This method must remove all data which are expired. <br/>
    * What means delete all entries that have the expiryTime parameter less than the current date.
    */
   List<MongoDBEntry<K, V>> removeExpiredData(byte[] lastKey);

   /**
    * Put an entry to the cache
    *
    * @param entry
    */
   void put(MongoDBEntry<K, V> entry);

   void start() throws Exception;

   void stop();
}
