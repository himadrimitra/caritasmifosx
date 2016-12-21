package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.execution.data.DataLayerKey;

import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 22/09/16.
 */
public interface DataLayer {

    public Map<String,Object> getValues(List<String> keys);

    public Object getValue(String key);

    public void build(Map<DataLayerKey,Long> dataLayerEntities);
}
