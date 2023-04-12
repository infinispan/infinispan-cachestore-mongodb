package org.infinispan.persistence.mongodb;

/**
 * Specifies how data (keys and values) are persisted to MongoDB.
 */
public enum DataFormat {
    /**
     * Persists data in binary format.
     * <p>
     * This should be the most efficient format.
     */
    BINARY,
    /**
     * Persists data in structured (BSON) format.
     * <p>
     * This allows keys and values to be inspected easily on MongoDB.
     */
    STRUCTURED
}
