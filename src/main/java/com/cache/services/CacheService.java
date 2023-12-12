package com.cache.services;

public interface CacheService {

    void addValue(Integer key, String value);

    String getValue(Integer key);

    void cacheEvict();

}
