package org.infinispan.persistence.mongodb.cache;

import org.infinispan.persistence.mongodb.store.MongoDBEntry;

import java.util.Set;

/**
 * A simple Cache interface
 *
 * @param <K> - key
 * @param <V> - value
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
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
     * @return
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
     * The Set of keys of the cache
     *
     * @return set of keys
     */
    Set<byte[]> keySet();

    /**
     * This method must remove all data which are expired. <br/>
     * What means delete all entries that have the expiryTime parameter less than the current date.
     */
    void removeExpiredData();

    /**
     * Put an entry to the cache
     *
     * @param entry
     */
    void put(MongoDBEntry<K, V> entry);
}
