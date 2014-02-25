package org.infinispan.persistence.mongodb.cache;

import org.infinispan.persistence.mongodb.store.entry.CacheEntry;
import org.infinispan.persistence.mongodb.store.entry.KeyEntry;

import java.util.Set;

/**
 * A simple Cache interface
 *
 * @param <K> - key
 * @param <V> - value
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public interface Cache<K, V> {
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
     * @param keyEntry
     * @return the removed cache entry
     */
    CacheEntry<V> remove(KeyEntry<K> keyEntry);

    /**
     * Get a cache entry which has the same key of the parameter
     *
     * @param keyEntry
     * @return
     */
    CacheEntry<V> get(KeyEntry<K> keyEntry);

    /**
     * Verify if the cache contains the key passed on parameter
     *
     * @param keyEntry
     * @return true if there is some entry, false if there is not
     */
    boolean containsKey(KeyEntry<K> keyEntry);

    /**
     * The Set of keys of the cache
     *
     * @return set of keys
     */
    Set<KeyEntry<K>> keySet();

    /**
     * This method must remove all data which are expired. <br/>
     * What means delete all entries that have the expiration parameter less than the current date.
     */
    void removeExpiredData();

    /**
     * Put an entry to the cache
     *
     * @param keyEntry
     * @param cacheEntry
     */
    void put(KeyEntry<K> keyEntry, CacheEntry<V> cacheEntry);
}
