package org.infinispan.persistence.mongodb.converter;

import org.bson.Document;
import org.bson.types.Binary;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.metadata.Metadata;
import org.infinispan.metadata.impl.PrivateMetadata;
import org.infinispan.persistence.spi.InitializationContext;
import org.infinispan.persistence.spi.MarshallableEntry;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.io.IOException;

import static org.infinispan.persistence.mongodb.MongoDbStore.CREATED;
import static org.infinispan.persistence.mongodb.MongoDbStore.EXPIRY_TIME;
import static org.infinispan.persistence.mongodb.MongoDbStore.INTERNAL_METADATA;
import static org.infinispan.persistence.mongodb.MongoDbStore.LAST_USED;
import static org.infinispan.persistence.mongodb.MongoDbStore.METADATA;
import static org.infinispan.persistence.mongodb.MongoDbStore.VALUE;


public abstract class AbstractCacheToStoreConverter<K, V> implements CacheToStoreConverter<K, V> {
    private static final Log log = LogFactory.getLog(AbstractCacheToStoreConverter.class, Log.class);

    private final InitializationContext context;
    private final Marshaller marshaller;

    public AbstractCacheToStoreConverter(InitializationContext context, Marshaller marshaller) {
        this.context = context;
        this.marshaller = marshaller;
    }


    @Override
    public Document toStoreEntry(Object storeKey, MarshallableEntry<? extends K, ? extends V> marshallableEntry) {
        Document document = new Document("_id", storeKey)
                .append(VALUE, toStoreValue(marshallableEntry.getValue()))
                .append(METADATA, toByteArray(marshallableEntry.getMetadata()))
                .append(INTERNAL_METADATA, toByteArray(marshallableEntry.getInternalMetadata()))
                .append(EXPIRY_TIME, marshallableEntry.expiryTime())
                .append(CREATED, marshallableEntry.created())
                .append(LAST_USED, marshallableEntry.lastUsed());
        return document;
    }

    @Override
    public MarshallableEntry<K, V> toCacheEntry(Object cacheKey, Document document) {
        Object storeValue = document.get(VALUE);
        Binary metadataBinary = (Binary) document.get(METADATA);
        Binary internalMetadataBinary = (Binary) document.get(INTERNAL_METADATA);
        Long created = document.getLong(CREATED);
        Long lastUsed = document.getLong(LAST_USED);

        Object cacheValue = storeValue == null ? null : toCacheValue(storeValue);
        Metadata metadata = metadataBinary == null ? null : (Metadata) toObject(metadataBinary.getData());
        PrivateMetadata internalMetadata = internalMetadataBinary == null ? null : (PrivateMetadata) toObject(internalMetadataBinary.getData());

        @SuppressWarnings("unchecked")
        MarshallableEntry<K, V> result = (MarshallableEntry<K, V>) context.getMarshallableEntryFactory()
                .create(cacheKey, cacheValue, metadata, internalMetadata, created == null ? -1 : created, lastUsed == null ? -1 : lastUsed);
        return result;
    }


    protected Object toObject(byte[] bytes) {
        try {
            return bytes == null ? null : marshaller.objectFromByteBuffer(bytes);
        } catch (IOException | ClassNotFoundException e) {
            log.errorf("failed to deserialize object from byte array", e);
        }
        return null;
    }

    protected byte[] toByteArray(Object obj) {
        try {
            return obj == null ? null : marshaller.objectToByteBuffer(obj);
        } catch (IOException | InterruptedException e) {
            log.errorf("failed to serialize object to byte array", e);
        }
        return null;
    }
}
