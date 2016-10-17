package com.finflux.ruleengine.configuration.service;

import com.finflux.ruleengine.configuration.data.FieldData;
import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.lib.data.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 22/09/16.
 */
@Scope("singleton")
@Service
public class RuleCacheService {

    private Map<String,Rule> ruleCacheMap;
    private Map<String,FieldData> fieldDataCacheMap;
    private Map<Long,Rule> ruleIdCacheMap;
    private RiskConfigReadPlatformService riskConfigReadPlatformService;

    @Autowired
    public RuleCacheService(RiskConfigReadPlatformService riskConfigReadPlatformService){
        ruleCacheMap = new HashMap<>();
        ruleIdCacheMap = new HashMap<>();
        fieldDataCacheMap = new HashMap<>();
        this.riskConfigReadPlatformService = riskConfigReadPlatformService;
    }



    public Rule getRuleByUname(String ruleName){
        if(ruleCacheMap == null || ruleCacheMap.isEmpty()){
            populateRuleCache();
        }
        return ruleCacheMap.get(ruleName);
    }

    public FieldData getFieldDataByUname(String fieldName){
        if(fieldDataCacheMap == null || fieldDataCacheMap.isEmpty()){
            populateFieldDataCache();
        }
        return fieldDataCacheMap.get(fieldName);
    }

    private void populateRuleCache() {
        List<RuleData> rules = riskConfigReadPlatformService.getAllRules();
        Map<String,Rule> newMap = new HashMap<>() ;
        Map<Long,Rule> newIdMap = new HashMap<>() ;
        for(RuleData ruleData:rules){
            Rule rule = ruleData.getRule();
            newMap.put(rule.getUname(),rule);
            newIdMap.put(rule.getId(),rule);
        }
        Map tmpMap = ruleCacheMap;
        Map tmpIdMap = ruleIdCacheMap;
        ruleCacheMap = newMap;
        ruleIdCacheMap = newIdMap;
        tmpMap.clear();
        tmpIdMap.clear();
    }

    private void populateFieldDataCache() {
        List<FieldData> fieldDatas = riskConfigReadPlatformService.getAllFields();
        Map<String,FieldData> newMap = new HashMap<>() ;
        for(FieldData fieldData:fieldDatas){
            newMap.put(fieldData.getUname(),fieldData);
        }
        Map tmpMap = fieldDataCacheMap;
        fieldDataCacheMap = newMap;
        tmpMap.clear();
    }

    public Rule getRuleById(Long ruleId){
        if(ruleIdCacheMap == null || ruleIdCacheMap.isEmpty()){
            populateRuleCache();
        }
        return ruleIdCacheMap.get(ruleId);
    }


    public void clearCache() {
        ruleCacheMap.clear();
        ruleIdCacheMap.clear();
    }
}
