package com.cache;

import com.cache.entities.Data;
import com.cache.entities.SelfExpiringData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import utils.WsAddressConstants;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = CacheApp.class)
class SelfExpiredTest {

    private static final Logger logger = LogManager.getLogger(SelfExpiredTest.class);

    @Autowired
    private RestTemplate restTemplate;


//    @BeforeClass
//    public static void beforeClass() {
//        TestApp.main(new String[]{});
//        restTemplate = new RestTemplate();
//    }


    @Test
    void expiredTest() {
        logger.info("starting.. Thread ID: {}", Thread.currentThread().getId());
        for (long i = 0; i < 1; i++) {
            restTemplate.postForObject(WsAddressConstants.selfExpiredFullUrl + "startInteger", new SelfExpiringData(i + 1, i + 20, Duration.ofSeconds((i + 1) * 10)), Void.class);
            restTemplate.postForObject(WsAddressConstants.selfExpiredFullUrl + "removeInteger", i + 1, Void.class);

            restTemplate.postForObject(WsAddressConstants.selfExpiredFullUrl + "startString", new SelfExpiringData(i + 1, "value " + (i + 1), Duration.ofSeconds((i + 1) * 2)), Void.class);
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
