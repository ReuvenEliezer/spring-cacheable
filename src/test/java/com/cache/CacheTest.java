package com.cache;

import com.cache.entities.Data;
import com.cache.entities.cache.EhCacheConfigData;
import com.cache.services.EhCacheCreator;
import com.cache.services.CacheService;
import com.cache.services.DemoService;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.cache.Caching;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CacheTest {

    @MockBean
    private DemoService demoService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private EhCacheCreator<Integer, String> ehCacheCreator;

    @Autowired
    private EhCacheCreator<String, List<String>> ehCacheCreator2;

    @Autowired
    @Qualifier("jCacheCacheManager")
    private CacheManager cacheManager;

    @BeforeEach
    void init() {
        reset(demoService);
        EhCacheConfigData<Integer, String> myCache1ConfigData = new EhCacheConfigData<>(
                "MY_CACHE",
                Integer.class, String.class,
                100L, EntryUnit.ENTRIES,
                Duration.ofSeconds(5));
        ehCacheCreator.createCache(myCache1ConfigData);
    }

    @AfterEach
    void tearDown() {
//        cacheManager.getCacheNames()
//                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName))
//                        .clear());
        destroyCaches();
        reset(demoService);
    }

    private void destroyCaches() {
        EhcacheCachingProvider ehcacheCachingProvider = (EhcacheCachingProvider) Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        javax.cache.CacheManager jCacheManager = ehcacheCachingProvider.getCacheManager();
        for (String cacheName : jCacheManager.getCacheNames()) {
            jCacheManager.destroyCache(cacheName);
        }
//        jCacheManager.close();
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
    void cachePutAndRemovalValueTest() {
        destroyCaches();
        EhCacheConfigData<String, List<String>> myCache2ConfigData = new EhCacheConfigData<>(
                "MY_CACHE",
                String.class, (Class<List<String>>) ((Class) List.class),
                100L, EntryUnit.ENTRIES,
                Duration.ofSeconds(5)
        );
        ehCacheCreator2.createCache(myCache2ConfigData);

        String key1 = "1";
        String key2 = "2";

        List<String> value = cacheService.getValue2(key1, key2);
        List<String> put = cacheService.putValue(key1, key2, List.of("1"));
        List<String> get1_nonEntered = cacheService.getValue2(key1, key2);
        verify(demoService, times(1)).putValue(key1 + "-" + key2, List.of("1"));
        verify(demoService, times(1)).getValue(key1 + "-" + key2);
        String removalKey = cacheService.removeKey(key1, key2);
        verify(demoService, times(1)).removeKey(key1 + "-" + key2);
        List<String> get2_After_removal_Entered = cacheService.getValue2(key1, key2);
        verify(demoService, times(2)).getValue(key1 + "-" + key2);
    }

    @Test
    void cachePutAndRemovalObjTest() {
        destroyCaches();
        EhCacheConfigData<String, List<String>> myCache2ConfigData = new EhCacheConfigData<>(
                "CACHE_NAME_OBJECT",
                String.class, (Class<List<String>>) ((Class) List.class),
                100L, EntryUnit.ENTRIES,
                Duration.ofMinutes(5)
        );
        javax.cache.Cache<String, List<String>> cache = ehCacheCreator2.createCache(myCache2ConfigData);
        String key1 = "1";
        String key2 = "2";

        List<String> value = cacheService.getByData(new Data(key1, key2));
        List<String> value1 = cacheService.getByData(new Data(key1, key2));
        verify(demoService, times(1)).getValue(key1);

        List<String> strings = cache.get(key1 + "-" + key2);
        Cache cacheNameObject = cacheManager.getCache("CACHE_NAME_OBJECT");
        Cache.ValueWrapper valueWrapper = cacheNameObject.get(key1 + "-" + key2);
        Object o = valueWrapper.get();
        assertThat(strings).isNotEmpty();
        assertThat(strings).isEqualTo(value);

    }


}
