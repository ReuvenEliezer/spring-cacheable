package controllers;

import entities.SelfExpiringData;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.WsAddressConstants;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(WsAddressConstants.restExamplesLogicalUrl)
public class RestExamplesController {

    private static final Logger logger = Logger.getLogger(RestExamplesController.class.getName());

    @RequestMapping(method = RequestMethod.POST, value = "post")
    public void postExample(@RequestBody String s) {
        logger.debug(s);
    }

    @RequestMapping(method = RequestMethod.POST, value = "post/{id}")
    public String concatenate(@PathVariable("id") String id, @RequestBody String string) {
        return id + string;
    }

    @RequestMapping(method = RequestMethod.GET, path = "get")
    public String postExample() {
        return "s";
    }

    @RequestMapping(method = RequestMethod.GET, value = "get/{valueType}")
//    @RequestMapping(method = RequestMethod.GET, value = "/{mcuId:.+}")
    public String postExample1(@PathVariable("valueType") String valueType) {
        return valueType;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Double> getHourlyForecasts(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude) {
        List<Double> result = new ArrayList<>();
        result.add(latitude);
        result.add(longitude);
        return result;
    }
}
