package com.cache.services.selfexpired;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SelfListener {

    private static final Logger logger = LogManager.getLogger(SelfListener.class);

    @EventListener
    public void selfListener(String string) {
        logger.debug(string);
    }

    @EventListener
    public void selfListener(Integer integer) {
        logger.debug(integer);
    }

}
