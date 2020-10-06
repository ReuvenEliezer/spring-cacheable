package entities;

import java.time.Duration;

public class SelfExpiringData {

    private Long key;
    private Object value;
    private Duration durationToExpired;

    protected SelfExpiringData() {
    }

    public SelfExpiringData(Long key, Object value, Duration durationToExpired) {
        this.key = key;
        this.value = value;
        this.durationToExpired = durationToExpired;
    }

    public Long getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Duration getDurationToExpired() {
        return durationToExpired;
    }
}
