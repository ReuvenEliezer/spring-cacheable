package com.cache.controllers;

import com.cache.entities.Data;
import jakarta.websocket.server.PathParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import utils.WsAddressConstants;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(WsAddressConstants.restExamplesLogicalUrl)
public class RestExamplesController {

    private static final Logger logger = LogManager.getLogger(RestExamplesController.class);

    @PostMapping(value = "post")
    public void postExample(@RequestBody String s) {
        logger.debug(s);
    }

    @PostMapping(value = "post/{id}")
    public String concatenate(@PathVariable("id") String id, @RequestBody String string) {
        return id + string;
    }

    @PostMapping(value = "data")
    public String concatenate(@RequestBody Data string) {
        return "ok";
    }


    @GetMapping(path = "get")
    public String postExample() {
        return "s";
    }

    @GetMapping(value = "get/{valueType}")
//    @RequestMapping(method = RequestMethod.GET, value = "/{mcuId:.+}")
    public String postExample1(@PathVariable("valueType") String valueType) {
        return valueType;
    }

    @GetMapping()
    public List<Double> getHourlyForecasts(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude) {
        List<Double> result = new ArrayList<>();
        result.add(latitude);
        result.add(longitude);
        return result;
    }
}
