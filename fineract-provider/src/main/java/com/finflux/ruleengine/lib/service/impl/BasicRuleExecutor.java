package com.finflux.ruleengine.lib.service.impl;

import com.finflux.ruleengine.lib.*;
import com.finflux.ruleengine.lib.data.*;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import com.finflux.ruleengine.lib.service.RuleExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dhirendra on 06/09/16.
 */
@Service
@Scope("singleton")
public class BasicRuleExecutor implements RuleExecutor {

     private final RuleUtils ruleUtils;
     private final ExpressionExecutor expressionExecutor;
     
     @Autowired
     public BasicRuleExecutor(RuleUtils ruleUtils, MyExpressionExecutor expressionExecutor){
          this.ruleUtils = ruleUtils;
          this.expressionExecutor = expressionExecutor;
     }
     
     @Override
     public RuleResult executeRule(Rule rule, Map<String, Object> keyValueMap) {
          List<Bucket> bucketList = rule.getBuckets();
          Set<String> expressionParameters = ruleUtils.getRuleParameters(rule);
          Map<String, Object> entityKeyValueMap = new HashMap<>();
          List<RuleResult> subRuleResults = new ArrayList<>();

          if(!EntityRuleType.FACTOR.equals(rule.getEntity())) {
               for (String parameter : expressionParameters) {
                    Rule tmpRule = ruleUtils.getRule(parameter);
                    RuleResult ruleResult = executeRule(tmpRule,keyValueMap);
                    subRuleResults.add(ruleResult);
                    entityKeyValueMap.put(parameter, ruleResult.getOutput().getValue());
               }
          }else{
               entityKeyValueMap = keyValueMap;
          }
          OutputReason outputReason = OutputReason.NOT_MATCHED;
          String error = null;
          String outputBucketName = null;
          String outputValue = null;
          for(Bucket bucket: bucketList){
               try {
                    if(expressionExecutor.executeExpression(bucket.getFilter(),entityKeyValueMap)){
                         outputValue = bucket.getOutput();
                         outputBucketName = bucket.getName();
                         outputReason = OutputReason.MATCHED;
                         break;
                    }
               } catch (FieldUndefinedException e) {
                    error = e.getMessage();
                    outputReason =OutputReason.INSUFFICENT_DATA;

               } catch (Exception e) {
                    error = e.getMessage();
                    outputReason =OutputReason.EXECUTION_EXCEPTION;
               }
          }
          if(outputValue == null && rule.getOutputConfiguration().getDefaultValue()!=null){
               outputValue = rule.getOutputConfiguration().getDefaultValue();
          }
          return new RuleResult(rule.getEntity(), rule.getName(),rule.getOutputConfiguration().getValueType(),outputValue,
                  subRuleResults,outputReason, outputBucketName, error);
     }

     @Override
     public List<String> getRequiredFields(Rule rule) {
          List<String> array = new ArrayList<>();
          array.addAll(ruleUtils.getRequiredFields(rule));
          return array;
     }
}
