package com.cache.services;


import java.util.List;

public interface CacheService {

    void addValue(Integer key, String value);

    void addValueAndCleanCache(Integer key, String value);

    String getValue(Integer key);

    List<String> getValue2(String key1, String key2);

    List<String> getValue1(Integer key);

    List<String> getValue2(Integer key1, Integer key2);

    void cacheEvict();
}
