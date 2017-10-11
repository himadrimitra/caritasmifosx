package com.finflux.ruleengine.lib.data;

import java.util.List;

/**
 * Created by dhirendra on 06/09/16.
 */
public class RuleResult {
    private EntityRuleType ruleEntity;
    private String name;
    private RuleOutput output;
    private List<RuleResult> ruleResultHierarchy;

    public RuleResult(EntityRuleType ruleEntity, String name, ValueType type, String outputValue,
                      List<RuleResult> ruleResultHirearchy, OutputReason outputReason, String bucket,
                      String error) {
        this.ruleEntity = ruleEntity;
        this.name = name;
        this.output = new RuleOutput(type,outputValue,outputReason,bucket,error);
        this.ruleResultHierarchy = ruleResultHirearchy;
    }

    public List<RuleResult> getRuleResultHierarchy() {
        return ruleResultHierarchy;
    }

    public void setRuleResultHierarchy(List<RuleResult> ruleResultHierarchy) {
        this.ruleResultHierarchy = ruleResultHierarchy;
    }

    public EntityRuleType getRuleEntity() {
        return ruleEntity;
    }

    public void setRuleEntity(EntityRuleType ruleEntity) {
        this.ruleEntity = ruleEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleOutput getOutput() {
        return output;
    }

    public void setOutput(RuleOutput output) {
        this.output = output;
    }
}
