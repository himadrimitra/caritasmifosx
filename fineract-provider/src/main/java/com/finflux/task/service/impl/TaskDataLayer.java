package com.finflux.task.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.execution.service.DataLayer;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;

/**
 * Created by dhirendra on 22/09/16.
 */

public class TaskDataLayer implements DataLayer {

    private final RuleCacheService ruleCacheService;
    private final Map<String, Object> keyValueMap;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;

    public TaskDataLayer(Long loanApplicationId, Long clientId, DataLayerReadPlatformService dataLayerReadPlatformService,
						 RuleCacheService ruleCacheService) {
        keyValueMap = dataLayerReadPlatformService.getAllMatrix(clientId);
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.ruleCacheService = ruleCacheService;
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
}
