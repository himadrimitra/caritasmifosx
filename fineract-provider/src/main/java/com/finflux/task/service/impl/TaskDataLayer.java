package com.finflux.task.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.service.DataLayer;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.task.data.TaskConfigKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dhirendra on 22/09/16.
 */

public class TaskDataLayer implements DataLayer {

    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private Map<String, Object> keyValueMap;

    public TaskDataLayer(DataLayerReadPlatformService dataLayerReadPlatformService) {
        keyValueMap = new HashMap<>();
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
    }

    @Override
    public Map<String, Object> getValues(List<String> keys) {
        Map<String, Object> myMap = new HashMap<>();
        for (String key : keys) {
            myMap.put(key, keyValueMap.get(key));
        }
        return myMap;
    }

    @Override
    public Object getValue(String key) {
        return keyValueMap.get(key);
    }

    @Override
    public void build(Map<DataLayerKey,Long> dataLayerEntities) {
        keyValueMap.clear();
        if(dataLayerEntities.get(DataLayerKey.CLIENT_ID)!=null){
            Map clientDataMap = dataLayerReadPlatformService.getAllClientMatrix(dataLayerEntities.get(DataLayerKey.CLIENT_ID));
            keyValueMap.putAll(clientDataMap);
        }
        if(dataLayerEntities.get(DataLayerKey.LOANAPPLICATION_ID)!=null){
            Map loanApplicationDataMap = dataLayerReadPlatformService.getAllLoanApplicationMatrix(dataLayerEntities.get(DataLayerKey.LOANAPPLICATION_ID));
            keyValueMap.putAll(loanApplicationDataMap);
        }
    }
}
