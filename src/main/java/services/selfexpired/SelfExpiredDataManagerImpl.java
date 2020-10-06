package services.selfexpired;

import entities.SelfExpiringData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Component
public class SelfExpiredDataManagerImpl implements SelfExpiredDataManager {

    private static final Logger logger = Logger.getLogger(SelfExpiredDataManagerImpl.class.getName());

    private SelfExpiringMapImpl<Long, String> stringSelfExpiringMapImpl = null;
    private SelfExpiringMapImpl<Long, Integer> integerSelfExpiringMapImpl = null;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void start() {
        stringSelfExpiringMapImpl = new SelfExpiringMapImpl<>(Duration.ofSeconds(10), taskScheduler, publisher);
        integerSelfExpiringMapImpl = new SelfExpiringMapImpl<>(Duration.ofSeconds(10), taskScheduler, publisher);
    }

    @Override
    public void addStringSelfExpiringHashMap(SelfExpiringData selfExpiringData) {
        if (!stringSelfExpiringMapImpl.containsKey(selfExpiringData.getKey())) {
//            logger.debug("addStringSelfExpiringHashMap, Thread ID:" + Thread.currentThread().getId());
            stringSelfExpiringMapImpl.put(selfExpiringData.getKey(), (String) selfExpiringData.getValue(), selfExpiringData.getDurationToExpired().toMillis());
        }
    }

    @Override
    public void addIntegerSelfExpiringHashMap(SelfExpiringData selfExpiringData) {
        if (!integerSelfExpiringMapImpl.containsKey(selfExpiringData.getKey())) {
//            logger.debug("addIntegerSelfExpiringHashMap, Thread ID:" + Thread.currentThread().getId());
            integerSelfExpiringMapImpl.put(selfExpiringData.getKey(), (Integer) selfExpiringData.getValue(), selfExpiringData.getDurationToExpired().toMillis());
        }
    }

    @Override
    public void removeIntegerSelfExpiringHashMap(Long selfExpiringKey) {
        integerSelfExpiringMapImpl.remove(selfExpiringKey);
    }

    @Override
    public void removeStringSelfExpiringHashMap(Long selfExpiringKey) {
        stringSelfExpiringMapImpl.remove(selfExpiringKey);
    }

}
