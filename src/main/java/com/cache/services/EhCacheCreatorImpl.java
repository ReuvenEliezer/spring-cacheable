package com.cache.services;

import com.cache.entities.cache.EhCacheConfigData;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Service
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EhCacheCreatorImpl<K, V> implements EhCacheCreator<K, V> {

    private final CacheManager cacheManager;

    public EhCacheCreatorImpl(@Qualifier("jCacheCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Cache<K, V> createCache(EhCacheConfigData<K, V> ehCacheConfigData) {
        CacheConfiguration<K, V> cacheConfig = createCacheConfig(
                ehCacheConfigData.keyClass(),
                ehCacheConfigData.valueClass(),
                ehCacheConfigData.heapSize(),
                ehCacheConfigData.heapResourceUnit(),
                ehCacheConfigData.offHeapSize(),
                ehCacheConfigData.offHeapMemoryUnit(),
                ehCacheConfigData.timeToLive(),
                ehCacheConfigData.timeToIdle().isPresent() && ehCacheConfigData.timeToIdle().get().equals(Duration.ZERO) ? Optional.empty() : ehCacheConfigData.timeToIdle()
        );
        javax.cache.configuration.Configuration<K, V> jcacheConfig = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfig);
        if (cacheManager instanceof JCacheCacheManager jCacheCacheManager) {
            javax.cache.CacheManager cacheMng = jCacheCacheManager.getCacheManager();
            return cacheMng.createCache(ehCacheConfigData.cacheName(), jcacheConfig);
        } else {
            throw new IllegalStateException("CacheManager should be instance of JCacheCacheManager");
        }
    }

    private CacheConfiguration<K, V> createCacheConfig(Class<K> keyClass, Class<V> valueClass,
                                                       Long heapSize, ResourceUnit heapResourceUnit,
                                                       Optional<Long> offHeapSize, Optional<MemoryUnit> offHeapMemoryUnit,
                                                       Duration timeToLive,
                                                       Optional<Duration> timeToIdle) {
        ResourcePools resourcePools = buildResourcePools(heapSize, heapResourceUnit, offHeapSize, offHeapMemoryUnit);
        ExpiryPolicy<K, V> expiryPolicy = createExpiryPolicy(timeToLive, timeToIdle);
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, resourcePools)
                .withExpiry(expiryPolicy)
                .build();
    }

    private static ResourcePools buildResourcePools(Long heapSize,ResourceUnit resourceUnit,
                                                    Optional<Long> offHeapSize, Optional<MemoryUnit> offHeapMemoryUnit) {
        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder
                .newResourcePoolsBuilder()
                .heap(heapSize, resourceUnit);
        if (offHeapSize.isPresent() && offHeapMemoryUnit.isPresent()) {
            resourcePoolsBuilder = resourcePoolsBuilder.offheap(offHeapSize.get(), offHeapMemoryUnit.get());
        }
        return resourcePoolsBuilder.build();
    }

    private ExpiryPolicy<K, V> createExpiryPolicy(Duration timeToLive, Optional<Duration> timeToIdle) {
        return new ExpiryPolicy<>() {
            @Override
            public Duration getExpiryForCreation(K key, V value) {
                return timeToLive;
            }

            @Override
            public Duration getExpiryForAccess(K key, Supplier<? extends V> value) {
                return timeToIdle.orElse(null);
            }

            @Override
            public Duration getExpiryForUpdate(K key, Supplier<? extends V> oldValue, V newValue) {
                return timeToLive;
            }
        };
    }
}
