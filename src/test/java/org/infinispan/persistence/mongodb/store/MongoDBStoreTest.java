package org.infinispan.persistence.mongodb.store;

import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferFactory;
import org.infinispan.commons.marshall.StreamingMarshaller;
import org.infinispan.marshall.core.MarshalledEntry;
import org.infinispan.marshall.core.MarshalledEntryFactory;
import org.infinispan.metadata.InternalMetadata;
import org.infinispan.persistence.mongodb.cache.Cache;
import org.infinispan.persistence.mongodb.store.entry.CacheEntry;
import org.infinispan.persistence.mongodb.store.entry.CacheEntryBuilder;
import org.infinispan.persistence.mongodb.store.entry.KeyEntry;
import org.infinispan.persistence.spi.InitializationContext;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Created by gabriel on 2/22/14.
 */
public class MongoDBStoreTest<K extends KeyEntry<K>, V> {
    private MongoDBStore<K, V> store;
    private Cache<K, V> cache;
    private InitializationContext context;

    private static final Integer CACHE_SIZE = 5;
    private static final byte[] BYTES_MOCK = new byte[1];

    @BeforeClass
    public void testInit() throws Exception {
        MockitoAnnotations.initMocks(this);

        store = new MongoDBStore<K, V>();
        cache = mock(Cache.class);
        context = mock(InitializationContext.class);

        ByteBufferFactory byteBufferFactory = mock(ByteBufferFactory.class);
        StreamingMarshaller streamingMarshaller = mock(StreamingMarshaller.class);
        MarshalledEntryFactory marshalledEntryFactory = mock(MarshalledEntryFactory.class);

        ByteBuffer metadataByteBuffer = mock(ByteBuffer.class);

        when(byteBufferFactory.newByteBuffer(BYTES_MOCK, 1, 1)).thenReturn(metadataByteBuffer);

        when(context.getByteBufferFactory()).thenReturn(byteBufferFactory);
        when(context.getMarshaller()).thenReturn(streamingMarshaller);
        when(context.getMarshalledEntryFactory()).thenReturn(marshalledEntryFactory);

        store.setCache(cache);
        store.setContext(context);
    }

    @Test
    public void testSize() throws Exception {
        when(cache.size()).thenReturn(CACHE_SIZE);
        assertTrue(store.size() == CACHE_SIZE);
    }

    @Test
    public void testWrite() throws Exception {
        MarshalledEntry<K, V> entry = createMarshalledEntry();
        CacheEntryBuilder<V> builder = createCacheEntryBuilder(entry);
        store.write(entry);
        verify(cache).put(new KeyEntry<K>(entry.getKey()), builder.create());
    }

    @Test
    public void testDelete() throws Exception {
        MarshalledEntry<K, V> marshalledEntry = createMarshalledEntry();
        CacheEntry<V> entry = createCacheEntryBuilder(marshalledEntry).create();
        KeyEntry<K> key = new KeyEntry<K>(marshalledEntry.getKey());
        when(cache.remove(key)).thenReturn(entry);
        store.delete(key.getKey());
        verify(cache).remove(key);
    }

    @Test
    public void testLoad() throws Exception {
        MarshalledEntry<K, V> marshalledEntry = createMarshalledEntry();
        CacheEntry<V> entry = createCacheEntryBuilder(marshalledEntry).create();
        KeyEntry<K> key = new KeyEntry<K>(marshalledEntry.getKey());
        when(cache.get(key)).thenReturn(entry);
        store.load(marshalledEntry.getKey());
        verify(cache).get(key);
    }

    private CacheEntryBuilder<V> createCacheEntryBuilder(MarshalledEntry<K, V> entry) {
        CacheEntryBuilder<V> builder = new CacheEntryBuilder<V>();

        builder.value(entry.getValue())
                .expiration(entry.getMetadata().expiryTime())
                .keyByteBuffer(entry.getKeyBytes())
                .valueByteBuffer(entry.getValueBytes())
                .metadataByteBuffer(entry.getMetadataBytes());

        return builder;
    }

    private MarshalledEntry<K, V> createMarshalledEntry() {
        MarshalledEntry<K, V> entry = mock(MarshalledEntry.class);

        InternalMetadata internalMetadata = mock(InternalMetadata.class);
        when(internalMetadata.expiryTime()).thenReturn(1000l);
        when(entry.getMetadata()).thenReturn(internalMetadata);

        ByteBuffer keyByteBuffer = mock(ByteBuffer.class);
        when(keyByteBuffer.getBuf()).thenReturn(BYTES_MOCK);
        when(keyByteBuffer.getLength()).thenReturn(1);
        when(keyByteBuffer.getOffset()).thenReturn(1);
        when(entry.getKeyBytes()).thenReturn(keyByteBuffer);

        ByteBuffer valueByteBuffer = mock(ByteBuffer.class);
        when(valueByteBuffer.getBuf()).thenReturn(BYTES_MOCK);
        when(valueByteBuffer.getLength()).thenReturn(1);
        when(valueByteBuffer.getOffset()).thenReturn(1);
        when(entry.getValueBytes()).thenReturn(valueByteBuffer);

        ByteBuffer metadataByteBuffer = mock(ByteBuffer.class);
        when(metadataByteBuffer.getBuf()).thenReturn(BYTES_MOCK);
        when(metadataByteBuffer.getLength()).thenReturn(1);
        when(metadataByteBuffer.getOffset()).thenReturn(1);
        when(entry.getMetadataBytes()).thenReturn(metadataByteBuffer);

        return entry;
    }
}
