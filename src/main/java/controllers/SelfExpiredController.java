package controllers;

import entities.SelfExpiringData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import services.selfexpired.SelfExpiredDataManager;
import utils.WsAddressConstants;

@RestController
@RequestMapping(WsAddressConstants.selfExpiredLogicalUrl)
public class SelfExpiredController {

    @Autowired
    private SelfExpiredDataManager selfExpiredDataManager;

    @RequestMapping(method = RequestMethod.POST, value = "startString")
    public void startString(@RequestBody SelfExpiringData selfExpiringData) {
        selfExpiredDataManager.addStringSelfExpiringHashMap(selfExpiringData);
    }

    @RequestMapping(method = RequestMethod.POST, value = "removeString")
    public void removeString(@RequestBody Long selfExpiringKey) {
        selfExpiredDataManager.removeStringSelfExpiringHashMap(selfExpiringKey);
    }

    @RequestMapping(method = RequestMethod.POST, value = "startInteger")
    public void startInteger(@RequestBody SelfExpiringData selfExpiringData) {
        selfExpiredDataManager.addIntegerSelfExpiringHashMap(selfExpiringData);
    }

    @RequestMapping(method = RequestMethod.POST, value = "removeInteger")
    public void removeInteger(@RequestBody Long selfExpiringKey) {
        selfExpiredDataManager.removeIntegerSelfExpiringHashMap(selfExpiringKey);
    }

}
