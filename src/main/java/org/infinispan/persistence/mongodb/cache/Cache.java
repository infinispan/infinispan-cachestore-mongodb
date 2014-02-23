package org.infinispan.persistence.mongodb.cache;

import org.infinispan.persistence.mongodb.store.entry.CacheEntry;
import org.infinispan.persistence.mongodb.store.entry.KeyEntry;

import java.util.Set;

/**
 * Created by gabriel on 2/22/14.
 */
public interface Cache<K, V> {
    int size();

    void clear();

    CacheEntry<V> remove(KeyEntry<K> keyEntry);

    CacheEntry<V> get(KeyEntry<K> keyEntry);

    boolean containsKey(KeyEntry<K> keyEntry);

    Set<KeyEntry<K>> keySet();

    void removeExpiredData();

    void put(KeyEntry<K> keyEntry, CacheEntry<V> cacheEntry);
}
