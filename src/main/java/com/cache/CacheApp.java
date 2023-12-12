package com.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.cache.controllers",
        "com.cache.services",
        "com.cache.configuration"
})
@EnableCaching
public class CacheApp {

    public static void main(String[] args) {
        SpringApplication.run(CacheApp.class, args);
    }

}