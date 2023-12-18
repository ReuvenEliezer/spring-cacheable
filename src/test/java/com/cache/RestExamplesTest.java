package com.cache;

import com.cache.services.CacheService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import utils.WsAddressConstants;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = CacheApp.class)
class RestExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger(RestExamplesTest.class);
//    private static final String CACHE_NAMES = "MY_CACHE";



    @Autowired
    private CacheService cacheService;

//    @Resource
//    private RestExamplesTest self;

    @Autowired
    private RestTemplate restTemplate;

//    @BeforeClass
//    public static void beforeClass() {
//        TestApp.main(new String[]{});
//        restTemplate = new RestTemplate();
//    }

    @Test
    void cacheTest() {
        cacheService.addValueAndCleanCache(1, "a");
        String value1 = cacheService.getValue(1);
        String value2 = cacheService.getValue(1);
        cacheService.addValueAndCleanCache(1, "a");
        String value3 = cacheService.getValue(1);

    }

//    @Cacheable(cacheNames = {CACHE_NAMES})
//    public String getValue(int key) {
//        return map.get(key);
//    }

//    @CacheEvict(allEntries = true, cacheNames = {CACHE_NAMES})
//    public void cacheEvict() {
//        logger.debug("clearing alarms cache");
//    }
//
//    private void addValue(Integer key, String value) {
//        map.put(key,value);
//        cacheEvict();
//    }

    @Test
    void postTest() {
        restTemplate.postForObject(WsAddressConstants.restExamplesFullUrl + "post", "ss", Void.class);
        String string1 = restTemplate.postForObject(WsAddressConstants.restExamplesFullUrl + "post/2", "string1", String.class);
        logger.debug(string1);
        String string2 = restTemplate.postForObject(WsAddressConstants.restExamplesFullUrl + "post/3", "string2", String.class);
        logger.debug(string2);


        String get = restTemplate.getForObject(WsAddressConstants.restExamplesFullUrl + "get", String.class);
        logger.debug(get);
        get = restTemplate.getForObject(WsAddressConstants.restExamplesFullUrl + "get/value", String.class);
        logger.debug(get);
        List<Double> result = restTemplate.getForObject(WsAddressConstants.restExamplesFullUrl + "?latitude=33&longitude=34", List.class);
        logger.info("{}",result);
    }

}
