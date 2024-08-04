package com.cache.entities.cache;

import io.micrometer.common.util.StringUtils;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.units.MemoryUnit;

import java.time.Duration;
import java.util.Optional;


public record EhCacheConfigData<K, V>(String cacheName,
                                      Class<K> keyClass, Class<V> valueClass,
                                      Long heapSize, ResourceUnit heapResourceUnit,
                                      Optional<Long> offHeapSize, Optional<MemoryUnit> offHeapMemoryUnit,
                                      Duration timeToLive,
                                      Optional<Duration> timeToIdle) {

    public EhCacheConfigData(String cacheName,
                             Class<K> keyClass,
                             Class<V> ValueClass,
                             Long heapSize, ResourceUnit heapResourceUnit,
                             Duration timeToLive) {
        this(cacheName, keyClass, ValueClass, heapSize, heapResourceUnit, timeToLive, Optional.empty());
    }

    public EhCacheConfigData(String cacheName,
                             Class<K> keyClass,
                             Class<V> ValueClass,
                             Long heapSize, ResourceUnit heapResourceUnit,
                             Duration timeToLive,
                             Optional<Duration> timeToIdle) {
        this(cacheName, keyClass, ValueClass, heapSize, heapResourceUnit, Optional.empty(), Optional.empty(), timeToLive, timeToIdle);
    }

    public EhCacheConfigData {
        validateConfigData(cacheName, keyClass, valueClass, heapSize, heapResourceUnit, offHeapSize, offHeapMemoryUnit);
    }

    private void validateConfigData(String cacheName,
                                    Class<K> keyClass, Class<V> valueClass,
                                    Long heapSize,
                                    ResourceUnit heapResourceUnit,
                                    Optional<Long> offHeapSize,
                                    Optional<MemoryUnit> offHeapMemoryUnit) {
        if (StringUtils.isEmpty(cacheName)) {
            throw new IllegalArgumentException("cacheName should not be null or empty");
        }
        if (keyClass == null || valueClass == null) {
            throw new IllegalArgumentException("key and value cache type must be not null");
        }
        if (heapSize == null || heapResourceUnit == null) {
            throw new IllegalArgumentException("heapSize and heapResourceUnit must be configure");
        }
        if (heapSize <= 0) {
            throw new IllegalArgumentException("heapSize should be positive value");
        }
        if (offHeapSize.isPresent() != offHeapMemoryUnit.isPresent()) {
            throw new IllegalArgumentException("offHeapSize and offHeapMemoryUnit should be both present or absent");
        }
        if (offHeapSize.isPresent() && offHeapSize.get() <= 0) {
            throw new IllegalArgumentException("offHeapSize should be null or positive value");
        }
    }

}
