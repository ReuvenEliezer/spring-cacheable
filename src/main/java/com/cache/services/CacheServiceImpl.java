package com.cache.services;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CacheServiceImpl implements CacheService {
    private static final Logger logger = LogManager.getLogger(CacheServiceImpl.class);
    private static final String CACHE_NAMES = "MAP_CACHE";
    private Map<Integer, String> map = new HashMap<>();


    @Resource
    private CacheService self;

    @Override
    public void addValue(Integer key, String value) {
        map.put(key,value);
        self.cacheEvict();
    }

    @Override
    @CacheEvict(allEntries = true, cacheNames = {CACHE_NAMES})
    public void cacheEvict() {
        logger.debug("clearing cache");
    }

    @Override
    @Cacheable(cacheNames = {CACHE_NAMES})
    public String getValue(Integer key) {
        logger.debug("return value");
        return map.get(key);
    }
}
