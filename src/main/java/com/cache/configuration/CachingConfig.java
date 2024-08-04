package com.cache.configuration;


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
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.Caching;
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

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache(MY_CACHE)
//                , new ConcurrentLruCache(16, "addresses")
        ));
        return cacheManager;
    }

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        return new ConcurrentMapCacheManager(MY_CACHE) {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new CaffeineCache(name, Caffeine.newBuilder()
//                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(3)
//                        .maximumWeight(2000)
                        .expireAfter(new Expiry<>() {
                            @Override
                            public long expireAfterCreate(Object key, Object value, long currentTime) {
                                return TimeUnit.MINUTES.toNanos(1);
                            }

                            @Override
                            public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
                                return currentDuration;
                            }

                            @Override
                            public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                                return currentDuration;
                            }
                        })
                        .evictionListener((key, value, cause) ->
                                logger.info("Evicted key={}, cause={}", key, cause.name()))
                        .build());
            }
        };
    }

    @Bean
    public CacheManager jCacheCacheManager() {
        Map<String, CacheConfiguration<?, ?>> cacheMap = new HashMap<>();
        EhcacheCachingProvider ehcacheCachingProvider = (EhcacheCachingProvider) Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration(cacheMap, ehcacheCachingProvider.getDefaultClassLoader());
        javax.cache.CacheManager cacheManager = ehcacheCachingProvider.getCacheManager(ehcacheCachingProvider.getDefaultURI(), defaultConfiguration);
        return new JCacheCacheManager(cacheManager);
    }


}




