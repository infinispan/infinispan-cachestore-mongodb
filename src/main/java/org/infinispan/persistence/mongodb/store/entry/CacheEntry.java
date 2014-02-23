package org.infinispan.persistence.mongodb.store.entry;

import java.io.Serializable;
import java.util.Date;

public class CacheEntry<V> implements Serializable {
    private static final long serialVersionUID = -850818539998912635L;

    private SerializableByteBuffer keyByteBuffer;
    private SerializableByteBuffer valueByteBuffer;
    private SerializableByteBuffer metadataByteBuffer;

    private V value;

    private Date expiration;

    public CacheEntry(SerializableByteBuffer keyByteBuffer, SerializableByteBuffer valueByteBuffer, SerializableByteBuffer metadataByteBuffer, V value, Date expiration) {
        this.keyByteBuffer = keyByteBuffer;
        this.valueByteBuffer = valueByteBuffer;
        this.metadataByteBuffer = metadataByteBuffer;
        this.value = value;
        this.expiration = expiration;
    }

    public CacheEntry() {
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public SerializableByteBuffer getKeyByteBuffer() {
        return keyByteBuffer;
    }

    public void setKeyByteBuffer(SerializableByteBuffer keyByteBuffer) {
        this.keyByteBuffer = keyByteBuffer;
    }

    public SerializableByteBuffer getValueByteBuffer() {
        return valueByteBuffer;
    }

    public void setValueByteBuffer(SerializableByteBuffer valueByteBuffer) {
        this.valueByteBuffer = valueByteBuffer;
    }

    public SerializableByteBuffer getMetadataByteBuffer() {
        return metadataByteBuffer;
    }

    public void setMetadataByteBuffer(SerializableByteBuffer metadataByteBuffer) {
        this.metadataByteBuffer = metadataByteBuffer;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheEntry)) return false;

        CacheEntry that = (CacheEntry) o;

        if (expiration != null ? !expiration.equals(that.expiration) : that.expiration != null) return false;
        if (keyByteBuffer != null ? !keyByteBuffer.equals(that.keyByteBuffer) : that.keyByteBuffer != null)
            return false;
        if (metadataByteBuffer != null ? !metadataByteBuffer.equals(that.metadataByteBuffer) : that.metadataByteBuffer != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (valueByteBuffer != null ? !valueByteBuffer.equals(that.valueByteBuffer) : that.valueByteBuffer != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = keyByteBuffer != null ? keyByteBuffer.hashCode() : 0;
        result = 31 * result + (valueByteBuffer != null ? valueByteBuffer.hashCode() : 0);
        result = 31 * result + (metadataByteBuffer != null ? metadataByteBuffer.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
        return result;
    }
}