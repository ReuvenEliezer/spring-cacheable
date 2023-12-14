package com.cache;

import com.cache.services.CacheService;
import com.cache.services.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CacheTest {

    @MockBean
    private DemoService demoService;

    @Autowired
    private CacheService cacheService;

    @Test
    void getValue() {
        cacheService.getValue(1);
        cacheService.getValue(1);
        verify(demoService, times(1)).getValue(1);
        cacheService.addValue(1,"1");
        cacheService.getValue(1);
        verify(demoService, times(2)).getValue(1);
        cacheService.getValue(1);
        verify(demoService, times(2)).getValue(1);
    }

}
