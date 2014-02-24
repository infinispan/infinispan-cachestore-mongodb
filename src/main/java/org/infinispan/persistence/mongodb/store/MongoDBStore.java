package org.infinispan.persistence.mongodb.store;

import net.jcip.annotations.ThreadSafe;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.executors.ExecutorAllCompletionService;
import org.infinispan.marshall.core.MarshalledEntry;
import org.infinispan.metadata.InternalMetadata;
import org.infinispan.persistence.TaskContextImpl;
import org.infinispan.persistence.mongodb.cache.Cache;
import org.infinispan.persistence.mongodb.cache.MongoDBCache;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfiguration;
import org.infinispan.persistence.mongodb.store.entry.CacheEntry;
import org.infinispan.persistence.mongodb.store.entry.CacheEntryBuilder;
import org.infinispan.persistence.mongodb.store.entry.KeyEntry;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.infinispan.persistence.spi.InitializationContext;
import org.infinispan.persistence.spi.PersistenceException;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * AdvancedLoadWriteStore implementation based on MongoDB. <br/>
 * This class is fully thread safe
 *
 * @param <K>
 * @param <V>
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
@ThreadSafe
public class MongoDBStore<K, V> implements AdvancedLoadWriteStore<K, V> {
    private InitializationContext context;

    private Cache<K, V> cache;
    private MongoDBStoreConfiguration configuration;

    @Override
    public void init(InitializationContext ctx) {
        context = ctx;
        configuration = ctx.getConfiguration();
        try {
            cache = new MongoDBCache(configuration);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void process(KeyFilter<K> filter, final CacheLoaderTask<K, V> task, Executor executor, boolean fetchValue, boolean fetchMetadata) {
        ExecutorAllCompletionService eacs = new ExecutorAllCompletionService(executor);
        synchronized (cache) {
            final TaskContextImpl taskContext = new TaskContextImpl();
            for (final KeyEntry<K> keyEntry : cache.keySet()) {
                if (filter == null || filter.shouldLoadKey(keyEntry.getKey())) {
                    if (taskContext.isStopped()) {
                        break;
                    }
                    eacs.submit(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            try {
                                final MarshalledEntry marshalledEntry = load(keyEntry.getKey());
                                if (marshalledEntry != null) {
                                    task.processEntry(marshalledEntry, taskContext);
                                }
                                return null;
                            } catch (Exception e) {
                                throw e;
                            }
                        }
                    });
                }
            }
        }
        eacs.waitUntilAllCompleted();
        if (eacs.isExceptionThrown()) {
            throw new PersistenceException("Execution exception!", eacs.getFirstException());
        }
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void purge(Executor threadPool, PurgeListener listener) {
        synchronized (cache) {
            cache.removeExpiredData();
        }
    }

    @Override
    public void write(MarshalledEntry<K, V> entry) {
        CacheEntryBuilder<V> builder = new CacheEntryBuilder<V>();

        builder.value(entry.getValue())
                .expiration(entry.getMetadata().expiryTime())
                .keyByteBuffer(entry.getKeyBytes())
                .valueByteBuffer(entry.getValueBytes())
                .metadataByteBuffer(entry.getMetadataBytes());

        CacheEntry<V> serializable = builder.create();

        synchronized (cache) {
            cache.put(createKeyEntry(entry.getKey()), serializable);
        }
    }

    @Override
    public boolean delete(K key) {
        synchronized (cache) {
            return cache.remove(createKeyEntry(key)) != null;
        }
    }

    @Override
    public MarshalledEntry<K, V> load(K key) {
        return load(key, false);
    }

    private MarshalledEntry<K, V> load(K key, boolean binaryData) {
        CacheEntry<V> value = null;

        synchronized (cache) {
            KeyEntry<K> keyEntry = createKeyEntry(key);
            value = cache.get(keyEntry);

            if (value == null) {
                return null;
            }

            try {
                ByteBuffer metadataBytes = context.getByteBufferFactory().newByteBuffer(value.getMetadataByteBuffer().getBuf(), value.getMetadataByteBuffer().getOffset(),
                        value.getMetadataByteBuffer().getLength());

                InternalMetadata metadata = (InternalMetadata) context.getMarshaller().objectFromByteBuffer(metadataBytes.getBuf());

                if (binaryData) {
                    ByteBuffer keyBuffer = context.getByteBufferFactory().newByteBuffer(value.getKeyByteBuffer().getBuf(), value.getKeyByteBuffer().getOffset(),
                            value.getKeyByteBuffer().getLength());

                    ByteBuffer valueBuffer = context.getByteBufferFactory().newByteBuffer(value.getValueByteBuffer().getBuf(), value.getValueByteBuffer().getOffset(),
                            value.getValueByteBuffer().getLength());

                    return context.getMarshalledEntryFactory().newMarshalledEntry(keyBuffer, valueBuffer, metadataBytes);
                }

                return context.getMarshalledEntryFactory().newMarshalledEntry(keyEntry, value.getValue(), metadata);
            } catch (Exception e) {
                throw new PersistenceException("Error while loading object from cache", e);
            }
        }
    }

    @Override
    public boolean contains(K key) {
        return cache.containsKey(createKeyEntry(key));
    }

    @Override
    public void start() {
        if (configuration.purgeOnStartup()) {
            clear();
        }
    }

    @Override
    public void stop() {

    }

    private KeyEntry<K> createKeyEntry(K key) {
        return new KeyEntry<K>(key);
    }

    public InitializationContext getContext() {
        return context;
    }

    public void setContext(InitializationContext context) {
        this.context = context;
    }

    public Cache<K, V> getCache() {
        return cache;
    }

    public void setCache(Cache<K, V> cache) {
        this.cache = cache;
    }
}
