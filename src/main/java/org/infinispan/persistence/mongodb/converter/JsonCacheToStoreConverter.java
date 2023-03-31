package org.infinispan.persistence.mongodb.converter;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.infinispan.AdvancedCache;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.encoding.DataConversion;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.persistence.spi.InitializationContext;


public class JsonCacheToStoreConverter<K, V> extends AbstractCacheToStoreConverter<K, V> {
    private final DataConversion keyDataConversion;
    private final DataConversion valueDataConversion;

    public JsonCacheToStoreConverter(InitializationContext context, Marshaller marshaller) {
        super(context, marshaller);

        @SuppressWarnings("unchecked")
        AdvancedCache<K, V> advancedCache = context.getCache().getAdvancedCache();
        MediaType jsonStringType = MediaType.fromString(MediaType.APPLICATION_JSON_TYPE + ";type=String");
        // This seems like a bug that `withRequestMediaType` isn't injected...
        this.keyDataConversion = advancedCache.getKeyDataConversion().withRequestMediaType(jsonStringType);
        this.valueDataConversion = advancedCache.getValueDataConversion().withRequestMediaType(jsonStringType);

        ComponentRegistry componentRegistry = advancedCache.getComponentRegistry();
        componentRegistry.wireDependencies(keyDataConversion, true);
        componentRegistry.wireDependencies(valueDataConversion, true);
    }


    @Override
    public Bson toStoreKey(Object cacheKey) {
        return BasicDBObject.parse((String) keyDataConversion.fromStorage(cacheKey));
    }

    @Override
    @SuppressWarnings("unchecked")
    public K toCacheKey(Object storeKey) {
        return storeKey == null ? null : (K) keyDataConversion.toStorage(((Document) storeKey).toJson());
    }

    @Override
    public Bson toStoreValue(V cacheValue) {
        return BasicDBObject.parse((String) valueDataConversion.fromStorage(cacheValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public V toCacheValue(Object storeValue) {
        return storeValue == null ? null : (V) valueDataConversion.toStorage(((Document) storeValue).toJson());
    }
}
