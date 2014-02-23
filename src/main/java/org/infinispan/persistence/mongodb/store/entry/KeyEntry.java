package org.infinispan.persistence.mongodb.store.entry;

import java.io.Serializable;

/**
 * Created by gabriel on 2/20/14.
 */
public class KeyEntry<K> implements Serializable {
    private K key;

    public KeyEntry(K key) {
        this.key = key;
    }

    public KeyEntry() {
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyEntry)) return false;

        KeyEntry keyEntry = (KeyEntry) o;

        if (key != null ? !key.equals(keyEntry.key) : keyEntry.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
