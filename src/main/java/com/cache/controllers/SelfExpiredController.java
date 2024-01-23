package com.cache.controllers;

import com.cache.entities.SelfExpiringData;
import com.cache.services.selfexpired.SelfExpiredDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utils.WsAddressConstants;

@RestController
@RequestMapping(WsAddressConstants.selfExpiredLogicalUrl)
public class SelfExpiredController {

    @Autowired
    private SelfExpiredDataManager selfExpiredDataManager;

    @PostMapping(value = "startString")
    public void startString(@RequestBody SelfExpiringData selfExpiringData) {
        selfExpiredDataManager.addStringSelfExpiringHashMap(selfExpiringData);
    }

    @PostMapping(value = "removeString")
    public void removeString(@RequestBody Long selfExpiringKey) {
        selfExpiredDataManager.removeStringSelfExpiringHashMap(selfExpiringKey);
    }

    @PostMapping(value = "startInteger")
    public void startInteger(@RequestBody SelfExpiringData selfExpiringData) {
        selfExpiredDataManager.addIntegerSelfExpiringHashMap(selfExpiringData);
    }

    @PostMapping(value = "removeInteger")
    public void removeInteger(@RequestBody Long selfExpiringKey) {
        selfExpiredDataManager.removeIntegerSelfExpiringHashMap(selfExpiringKey);
    }

}
