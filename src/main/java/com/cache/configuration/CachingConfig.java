package com.cache.configuration;


import com.cache.services.CacheServiceImpl;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.github.benmanes.caffeine.cache.Expiry;
//import com.hazelcast.cache.HazelcastCachingProvider;
//import com.hazelcast.config.*;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.config.SizedResourcePoolImpl;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.config.AbstractJCacheConfiguration;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ConcurrentLruCache;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {

    private static final Logger logger = LogManager.getLogger(CachingConfig.class);

    private static final String MY_CACHE = "MY_CACHE";

//    @Bean
//    public CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        cacheManager.setCaches(Arrays.asList(
//                new ConcurrentMapCache(MY_CACHE)
////                , new ConcurrentLruCache(16, "addresses")
//        ));
//        return cacheManager;
//    }

//    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager(MY_CACHE) {
//            @Override
//            protected Cache createConcurrentMapCache(final String name) {
//                return new CaffeineCache(name, Caffeine.newBuilder()
//                        .expireAfterWrite(Duration.ofMinutes(1))
//                        .maximumSize(2)
//                        .maximumWeight(2)
//                        .expireAfter(new Expiry<>() {
//                            @Override
//                            public long expireAfterCreate(Object key, Object value, long currentTime) {
//                                return TimeUnit.MINUTES.toNanos(1);
//                            }
//
//                            @Override
//                            public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
//                                return currentDuration;
//                            }
//
//                            @Override
//                            public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
//                                return currentDuration;
//                            }
//                        })
//                        .evictionListener((key, value, cause) ->
//                                logger.info("Evicted key={}, cause={}", key, cause.name()))
//                        .build());
//            }
//        };
//    }

//    @Bean
//    public CacheManager cacheManager() {
//        CachingProvider provider = Caching.getCachingProvider();
//        javax.cache.CacheManager cacheManager = provider.getCacheManager();
//        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder
//                .heap(3)
////                .withReplacing(new SizedResourcePoolImpl<>())
//                .offheap(10, MemoryUnit.MB)
//                ;
//
//
//        CacheConfiguration<Integer, Object> configuration = CacheConfigurationBuilder
//                .newCacheConfigurationBuilder(Integer.class, Object.class, resourcePoolsBuilder)
//                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
//                .build();
//
//        javax.cache.configuration.Configuration<Integer, Object> cacheConfiguration =
//                Eh107Configuration.fromEhcacheCacheConfiguration(configuration);
//
//        cacheManager.createCache(MY_CACHE, cacheConfiguration);
//        return new JCacheCacheManager(cacheManager);
//    }

    @Bean
    public CacheManager jCacheCacheManager() {
        Map<String, CacheConfiguration<?, ?>> cacheMap = new HashMap<>();

        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder
                .heap(3)
//                .withReplacing(new SizedResourcePoolImpl<>())
//                .offheap(10, MemoryUnit.MB)
//                .disk(100, MemoryUnit.MB, false)
                ;
        ExpiryPolicy<Object, Object> expiryPolicy = createExpiryPolicy(Duration.ofMinutes(10), Duration.ofMinutes(5));

        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Object.class, Object.class, resourcePoolsBuilder)
                .withExpiry(expiryPolicy)
                .build();
        cacheMap.put(MY_CACHE, cacheConfiguration);
        EhcacheCachingProvider ehcacheCachingProvider = (EhcacheCachingProvider) Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration(cacheMap, ehcacheCachingProvider.getDefaultClassLoader());
        javax.cache.CacheManager cacheManager = ehcacheCachingProvider.getCacheManager(ehcacheCachingProvider.getDefaultURI(), defaultConfiguration);
        return new JCacheCacheManager(cacheManager);
    }

    private static ExpiryPolicy<Object, Object> createExpiryPolicy(Duration timeToLive, Duration timeToIdle) {
        return ExpiryPolicyBuilder
                .expiry()
                .create(timeToLive)
                .access(timeToIdle)
//                .update(Duration.ofMinutes(30))
                .build();
    }


}




