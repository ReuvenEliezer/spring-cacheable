package com.cache.services;

import java.util.List;

public interface DemoService {

    String getValue(Integer key);

    List<String> getValue(String key);

    List<String> putValue(String key, List<String> value);

    String removeKey(String key);
}
