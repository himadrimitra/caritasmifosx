package com.finflux.ruleengine.execution.service.impl;

import com.finflux.ruleengine.execution.service.DataLayer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
public class TestDataLayer implements DataLayer {

    private final Map<String, Object> keyValueMap;

    public TestDataLayer(){
        keyValueMap = new HashMap<>();
        keyValueMap.put("age", 25L);
//        keyValueMap.put("sex", "M");
//        keyValueMap.put("isMarried", true);
        keyValueMap.put("name", "dhirendra");
    }

    @Override
    public Map<String, Object> getValues(List<String> keys) {
        Map<String,Object> myMap = new HashMap<>();
        for(String key: keys){
            myMap.put(key,keyValueMap.get(key));
        }
        return myMap;
    }

    @Override
    public Object getValue(String key) {
        return keyValueMap.get(key);
    }
}
