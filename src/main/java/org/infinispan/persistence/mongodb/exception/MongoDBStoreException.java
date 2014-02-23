package org.infinispan.persistence.mongodb.exception;

/**
 * Created by gabriel on 2/22/14.
 */
public class MongoDBStoreException extends Exception {
    public MongoDBStoreException() {
    }

    public MongoDBStoreException(String message) {
        super(message);
    }

    public MongoDBStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoDBStoreException(Throwable cause) {
        super(cause);
    }
}
