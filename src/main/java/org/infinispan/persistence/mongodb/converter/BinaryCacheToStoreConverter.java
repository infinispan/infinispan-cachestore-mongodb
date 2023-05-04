package org.infinispan.persistence.mongodb.converter;

import org.bson.types.Binary;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.persistence.spi.InitializationContext;


public class BinaryCacheToStoreConverter<K, V> extends AbstractCacheToStoreConverter<K, V> {
    public BinaryCacheToStoreConverter(InitializationContext context, Marshaller marshaller) {
        super(context, marshaller);
    }


    @Override
    public byte[] toStoreKey(Object cacheKey) {
        return toByteArray(cacheKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public K toCacheKey(Object storeKey) {
        return (K) toObject(((Binary) storeKey).getData());
    }

    @Override
    public byte[] toStoreValue(V cacheValue) {
        return toByteArray(cacheValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V toCacheValue(Object storeValue) {
        return (V) toObject(((Binary) storeValue).getData());
    }
}
