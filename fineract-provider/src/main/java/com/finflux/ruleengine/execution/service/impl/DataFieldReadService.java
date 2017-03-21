package com.finflux.ruleengine.execution.service.impl;

import java.util.List;
import java.util.Map;

import com.finflux.ruleengine.execution.data.DataLayerKey;

public interface DataFieldReadService {
    
    Map<String,Object> getAllFieldValues(List<String> keys,Map<String,Object> fieldParams);

}
