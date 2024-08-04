package com.cache.services;

import com.cache.entities.cache.EhCacheConfigData;

import javax.cache.Cache;

public interface EhCacheCreator<K, V> {

    Cache<K, V> createCache(EhCacheConfigData<K, V> ehCacheConfigData);

}
