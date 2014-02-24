package org.infinispan.persistence.mongodb.store.entry;

import org.infinispan.commons.io.ByteBuffer;

import java.util.Date;

/**
 * A CacheEntry builder.
 *
 * @param <V>
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class CacheEntryBuilder<V> {
    private SerializableByteBuffer keyByteBuffer;
    private SerializableByteBuffer valueByteBuffer;
    private SerializableByteBuffer metadataByteBuffer;

    private V value;

    private Date expiration;

    public CacheEntryBuilder<V> expiration(Long expiration) {
        this.expiration = new Date(expiration);
        return this;
    }

    public CacheEntryBuilder<V> expiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }

    public CacheEntryBuilder<V> value(V value) {
        this.value = value;
        return this;
    }

    public CacheEntryBuilder<V> keyByteBuffer(ByteBuffer byteBuffer) {
        keyByteBuffer = new SerializableByteBuffer(byteBuffer.getBuf(), byteBuffer.getLength(), byteBuffer.getOffset());
        return this;
    }

    public CacheEntryBuilder<V> valueByteBuffer(ByteBuffer byteBuffer) {
        valueByteBuffer = new SerializableByteBuffer(byteBuffer.getBuf(), byteBuffer.getLength(), byteBuffer.getOffset());
        return this;
    }

    public CacheEntryBuilder<V> metadataByteBuffer(ByteBuffer byteBuffer) {
        metadataByteBuffer = new SerializableByteBuffer(byteBuffer.getBuf(), byteBuffer.getLength(), byteBuffer.getOffset());
        return this;
    }

    public CacheEntry<V> create() {
        return new CacheEntry<V>(keyByteBuffer, valueByteBuffer, metadataByteBuffer, value, expiration);
    }
}