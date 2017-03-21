package com.finflux.risk.profilerating.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.service.DataLayer;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;

public class ProfileRatingDataLayer implements DataLayer {

    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private Map<String, Object> keyValueMap;

    public ProfileRatingDataLayer(final DataLayerReadPlatformService dataLayerReadPlatformService) {
        this.keyValueMap = new HashMap<>();
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
    }

    @Override
    public Map<String, Object> getValues(List<String> keys) {
        Map<String, Object> myMap = new HashMap<>();
        for (String key : keys) {
            myMap.put(key, this.keyValueMap.get(key));
        }
        return myMap;
    }

    @Override
    public Object getValue(String key) {
        return this.keyValueMap.get(key);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void build(Map<String, Object> dataLayerEntities) {
        this.keyValueMap.clear();
        if (dataLayerEntities.get(DataLayerKey.CLIENT_ID.getValue()) != null) {
            final Map clientDataMap = this.dataLayerReadPlatformService.getAllClientMatrix((Long)dataLayerEntities.get(DataLayerKey.CLIENT_ID.getValue()));
            this.keyValueMap.putAll(clientDataMap);
        }
        this.keyValueMap.putAll(dataLayerEntities);
    }

    @Override
    public Map<String, Object> getParamsMap() {
        return keyValueMap;
    }

}
