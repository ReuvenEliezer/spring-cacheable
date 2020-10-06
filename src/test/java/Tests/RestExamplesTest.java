package Tests;

import app.TestApp;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import services.CacheService;
import utils.WsAddressConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApp.class)
public class RestExamplesTest {

    private static final Logger logger = Logger.getLogger(RestExamplesTest.class.getName());
//    private static final String CACHE_NAMES = "MAP_CACHE";


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

    private Map<Integer, String> map = new HashMap<>();

    @Test
    public void cacheTest() {
        cacheService.addValue(1, "a");
        String value1 = cacheService.getValue(1);
        String value2 = cacheService.getValue(1);
        cacheService.addValue(1, "a");
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
    public void PostTest() {
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
        logger.debug(result);
    }

}
