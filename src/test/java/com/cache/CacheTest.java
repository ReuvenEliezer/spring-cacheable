package com.cache;

import com.cache.services.CacheService;
import com.cache.services.DemoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CacheTest {

    @MockBean
    private DemoService demoService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    @Qualifier("jCacheCacheManager")
    private CacheManager cacheManager;

    @AfterEach
    void tearDown() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName))
                        .clear());
    }

    @Test
    void getValue() {
        /**
         * https://stackoverflow.com/questions/34694920/lru-with-caffeine
         * https://stackoverflow.com/questions/17759560/what-is-the-difference-between-lru-and-lfu
         */
        cacheService.getValue(1);
        cacheService.getValue(1);
        verify(demoService, times(1)).getValue(1);
        cacheService.addValueAndCleanCache(1, "1");
        cacheService.getValue(1);
        verify(demoService, times(2)).getValue(1);
        cacheService.getValue(1);
        verify(demoService, times(2)).getValue(1);
    }

    @Test
    void getValue2() {
        cacheService.getValue1(1);
        cacheService.getValue1(1);
        verify(demoService, times(1)).getValue(1);
        cacheService.addValueAndCleanCache(1, "1");
        cacheService.getValue1(1);
        verify(demoService, times(2)).getValue(1);
        cacheService.getValue1(1);
        verify(demoService, times(2)).getValue(1);
    }

    @Test
    void getValue3() {
        cacheService.getValue2(1, 2);
        cacheService.getValue2(1, 2);
        verify(demoService, times(1)).getValue(3);
        cacheService.addValueAndCleanCache(1, "1");
        cacheService.getValue2(1, 2);
        verify(demoService, times(2)).getValue(3);
        cacheService.getValue2(1, 2);
        verify(demoService, times(2)).getValue(3);

        cacheService.getValue2(1, 1);
        verify(demoService, times(1)).getValue(2);


        Collection<String> cacheNames = cacheManager.getCacheNames();
        String next = cacheNames.iterator().next();
        org.springframework.cache.Cache cache = cacheManager.getCache(next);
    }

    @Test
    void getValue4() {
        List<String> value2 = cacheService.getValue2("1", "1");
        List<String> value21 = cacheService.getValue2("1", "1");
        verify(demoService, times(1)).getValue("1-1");
    }


    @Test
    void getValue1() {
        cacheService.addValue(1,"1");
        cacheService.addValue(2,"2");
        cacheService.addValue(3,"3");
        cacheService.addValue(4,"4");
        cacheService.addValue(5,"5");
        cacheService.addValue(6,"6");

        cacheService.getValue(1);
        cacheService.getValue(2);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(3);
        cacheService.getValue(4);
        cacheService.getValue(5);
        cacheService.getValue(6);
        Cache mapCache = cacheManager.getCache("MY_CACHE");
        String valueWrapper1 = mapCache.get(1, String.class);
        String valueWrapper2 = mapCache.get(2, String.class);
        String valueWrapper3 = mapCache.get(3, String.class);
    }


}
