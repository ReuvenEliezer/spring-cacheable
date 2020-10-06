package Tests;

import app.TestApp;
import entities.SelfExpiringData;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import utils.WsAddressConstants;

import java.time.Duration;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApp.class)
public class SelfExpiredTest {

    private static final Logger logger = Logger.getLogger(SelfExpiredTest.class.getName());
    @Autowired
    private RestTemplate restTemplate;

//    @BeforeClass
//    public static void beforeClass() {
//        TestApp.main(new String[]{});
//        restTemplate = new RestTemplate();
//    }

    @Test
    public void ExpiredTest() {
//        logger.debug("start, Thread ID:" + Thread.currentThread().getId());
        for (long i = 0; i < 1; i++) {
            restTemplate.postForObject(WsAddressConstants.selfExpiredFullUrl + "startInteger", new SelfExpiringData(i + 1, i + 20, Duration.ofSeconds((i+1) * 10)), Void.class);
            restTemplate.postForObject(WsAddressConstants.selfExpiredFullUrl + "removeInteger", i + 1, Void.class);

            restTemplate.postForObject(WsAddressConstants.selfExpiredFullUrl + "startString", new SelfExpiringData(i + 1, "value " + (i + 1), Duration.ofSeconds((i+1) * 2)), Void.class);
        }

        sleep(Duration.ofMinutes(2));
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
