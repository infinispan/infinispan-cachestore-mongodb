package org.infinispan.persistence.mongodb.converter;

import org.bson.Document;
import org.infinispan.persistence.spi.MarshallableEntry;

public interface CacheToStoreConverter<K, V> {
    Object toStoreKey(Object cacheKey);

    K toCacheKey(Object storeKey);

    Object toStoreValue(V cacheValue);

    V toCacheValue(Object storeValue);

    Document toStoreEntry(Object storeKey, MarshallableEntry<? extends K, ? extends V> marshallableEntry);

    MarshallableEntry<K, V> toCacheEntry(Object cacheKey, Document document);


    default MarshallableEntry<K, V> toCacheEntry(Document document) {
        Object cacheKey = toCacheKey(document.get("_id"));
        return toCacheEntry(cacheKey, document);
    }
}
