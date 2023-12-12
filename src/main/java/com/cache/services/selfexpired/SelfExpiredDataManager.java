package com.cache.services.selfexpired;


import com.cache.entities.SelfExpiringData;

public interface SelfExpiredDataManager {

    void addStringSelfExpiringHashMap(SelfExpiringData selfExpiringData);

    void addIntegerSelfExpiringHashMap(SelfExpiringData selfExpiringData);

    void removeIntegerSelfExpiringHashMap(Long selfExpiringKey);

    void removeStringSelfExpiringHashMap(Long selfExpiringKey);
}
