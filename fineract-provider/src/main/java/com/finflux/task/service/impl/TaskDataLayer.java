package com.finflux.task.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.service.DataLayer;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;

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
    public void build(Map<String, Object> dataLayerEntities) {
        keyValueMap.clear();
        if(dataLayerEntities.get(DataLayerKey.CLIENT_ID.getValue())!=null){
            Map clientDataMap = dataLayerReadPlatformService.getAllClientMatrix(((Long) dataLayerEntities.get(DataLayerKey.CLIENT_ID.getValue())).longValue());
            keyValueMap.putAll(clientDataMap);
            keyValueMap.put(DataLayerKey.CLIENT_ID.getValue(),((Long) dataLayerEntities.get(DataLayerKey.CLIENT_ID.getValue())).longValue() );
        }
        if(dataLayerEntities.get(DataLayerKey.LOANAPPLICATION_ID.getValue())!=null){
            Map loanApplicationDataMap = dataLayerReadPlatformService.getAllLoanApplicationMatrix(((Long) dataLayerEntities.get(DataLayerKey.LOANAPPLICATION_ID.getValue())).longValue());
            keyValueMap.putAll(loanApplicationDataMap);
            keyValueMap.put(DataLayerKey.LOANAPPLICATION_ID.getValue(),(Long) dataLayerEntities.get(DataLayerKey.LOANAPPLICATION_ID.getValue()) );
        }
        this.keyValueMap.putAll(dataLayerEntities);
    }

    @Override
    public Map<String, Object> getParamsMap() {
        return keyValueMap;
    }
}
