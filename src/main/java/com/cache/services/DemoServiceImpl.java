package com.cache.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String getValue(Integer key) {
        return "1";
    }

    @Override
    public List<String> getValue(String key) {
        return List.of("1");
    }

    @Override
    public List<String> putValue(String key, List<String> value) {
        return List.of("updated");
    }

    @Override
    public String removeKey(String key) {
        return "removed";
    }
}
