package com.cache.services;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheServiceImpl implements CacheService {
    private static final Logger logger = LogManager.getLogger(CacheServiceImpl.class);
    private static final String CACHE_NAME = "MY_CACHE";
    private static final Map<Integer, String> map = new HashMap<>();

    private static final Map<Integer, List<String>> map1 = new HashMap<>();

    private final DemoService demoService;


    @Resource
    private CacheService self;

    public CacheServiceImpl(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public void addValueAndCleanCache(Integer key, String value) {
        addValue(key, value);
        self.cacheEvict();
    }

    @Override
    public void addValue(Integer key, String value) {
        map.put(key, value);
    }

    @Override
    @CacheEvict(allEntries = true, cacheNames = {CACHE_NAME})
    public void cacheEvict() {
        logger.debug("clearing cache");
    }

    @Override
    @Cacheable(cacheNames = {CACHE_NAME})
    public String getValue(Integer key) {
        logger.info("return value");
        demoService.getValue(key);
        return map.get(key);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#key1 + #key2")
    public List<String> getValue2(Integer key1, Integer key2) {
        logger.info("return value");
        demoService.getValue(key1 + key2);
        return map1.get(key1 + key2);
    }

    @Override
    @CachePut(cacheNames = {CACHE_NAME},
            cacheManager = "jCacheCacheManager",
            key = "#key1.concat('-').concat(#key2)"
    )
    public List<String> putValue(String key1, String key2, List<String> values) {
        logger.info("put values {} to key {}", values, key1 + "-" + key2);
        return demoService.putValue(key1 + "-" + key2, values);
    }

    @Override
    @CacheEvict(cacheNames = {CACHE_NAME},
            cacheManager = "jCacheCacheManager",
            key = "#key1.concat('-').concat(#key2)"
    )
    public String removeKey(String key1, String key2) {
        logger.info("remove key value {}", key1 + "-" + key2);
        return demoService.removeKey(key1 + "-" + key2);
    }

    @Override
    @Cacheable(
            value = CACHE_NAME,
            cacheManager = "jCacheCacheManager",
            key = "#key1.concat('-').concat(#key2)"
    )
    public List<String> getValue2(String key1, String key2) {
        String key = key1.concat("-").concat(key2);
        List<String> value = demoService.getValue(key);
        logger.info("return values {}", value);
       return value;
//        return map1.get(key);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public List<String> getValue1(Integer key) {
        logger.info("return value");
        demoService.getValue(key);
        return map1.get(key);
    }

}
