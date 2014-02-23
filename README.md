infinispan-cachestore-mongodb
=============================

This project is Infinispan 6.x.x cache store that uses MongoDB as store. 

How to use:

``` 
Configuration sample = builder
    .eviction()
       .strategy(EvictionStrategy.LIRS)
       .maxEntries(2000000)
    .expiration()
       .lifespan(10, TimeUnit.MINUTES)
       .maxIdle(10, TimeUnit.MINUTES)
    .jmxStatistics()
    .persistence()
       .addStore(MongoDBStoreConfigurationBuilder.class)
       .preload(true)
       .fetchPersistentState(true)
       .hostname("localhost")
       .port(27017)
       .database("example")
       .collection("example")
       .build();

 EmbeddedCacheManager cacheManager = new DefaultCacheManager();
 cacheManager.defineConfiguration("mongoCache", sample);
 Cache<String, String> cache = cacheManager.getCache("mongoCache");
```
