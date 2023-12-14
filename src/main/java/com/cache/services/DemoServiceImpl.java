package com.cache.services;

import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String getValue(Integer key) {
        return "1";
    }
}
