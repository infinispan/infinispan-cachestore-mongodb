package org.infinispan.persistence.mongodb.store;

import org.infinispan.commons.marshall.Marshaller;

import java.util.Date;

/**
 * This is a representation of a MongoDBStore entry. <br/>
 * This class IS NOT persisted to MongoDB, only its byte array and expiryTime attributes.
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class MongoDBEntry<K, V> {
    private K key;
    private V value;

    private byte[] keyBytes;
    private byte[] valueBytes;
    private byte[] metadataBytes;

    private Date expiryTime;

    public MongoDBEntry(byte[] keyBytes, byte[] valueBytes, byte[] metadataBytes, Date expiryTime) {
        this.keyBytes = keyBytes;
        this.valueBytes = valueBytes;
        this.metadataBytes = metadataBytes;
        this.expiryTime = expiryTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] getKeyBytes() {
        return keyBytes;
    }

    public void setKeyBytes(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }

    public byte[] getValueBytes() {
        return valueBytes;
    }

    public void setValueBytes(byte[] valueBytes) {
        this.valueBytes = valueBytes;
    }

    public byte[] getMetadataBytes() {
        return metadataBytes;
    }

    public void setMetadataBytes(byte[] metadataBytes) {
        this.metadataBytes = metadataBytes;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public K getKey(Marshaller marshaller) {
        try {
            return (K) marshaller.objectFromByteBuffer(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public V getValue(Marshaller marshaller) {
        try {
            return (V) marshaller.objectFromByteBuffer(valueBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder<K, V> {
        private byte[] keyBytes;
        private byte[] valueBytes;
        private byte[] metadataBytes;

        private Date expiryTime;

        private Builder() {
        }

        public Builder<K, V> keyBytes(byte[] keyBytes) {
            this.keyBytes = keyBytes;
            return this;
        }

        public Builder<K, V> valueBytes(byte[] valueBytes) {
            this.valueBytes = valueBytes;
            return this;
        }

        public Builder<K, V> metadataBytes(byte[] metadataBytes) {
            this.metadataBytes = metadataBytes;
            return this;
        }

        public Builder<K, V> expiryTime(Date expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public MongoDBEntry<K, V> create() {
            return new MongoDBEntry<K, V>(keyBytes, valueBytes, metadataBytes, expiryTime);
        }
    }
}
