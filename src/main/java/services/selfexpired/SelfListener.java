package services.selfexpired;

import org.apache.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SelfListener {

    private static final Logger logger = Logger.getLogger(SelfListener.class.getName());

    @EventListener
    public void selfListener(String string) {
        logger.debug(string);
    }

    @EventListener
    public void selfListener(Integer integer) {
        logger.debug(integer);
    }

}
