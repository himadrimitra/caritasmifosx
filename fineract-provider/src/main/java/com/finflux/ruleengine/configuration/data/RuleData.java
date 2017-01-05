package com.finflux.ruleengine.configuration.data;

import com.finflux.ruleengine.lib.data.*;

import java.util.List;

/**
 * Created by dhirendra on 16/09/16.
 */
public class RuleData {

    private Long id;

    private String name;

    private String uname;

    private String description;

    private EntityRuleType entityType;

    private EntityRuleType paramEntityType;

    private ValueType valueType;

    private String defaultValue;

    private Boolean isActive;

    private List<KeyValue> possibleOutputs;

    private List<Bucket> buckets;

    public RuleData(Long id, EntityRuleType entityType, String name, String uname, String description, String defaultValue,
            ValueType valueType, List<KeyValue> possibleOutputs, List<Bucket> buckets, Boolean isActive) {
        this.id = id;
        this.entityType = entityType;
        this.name = name;
        this.uname = uname;
        this.description = description;
        this.defaultValue = defaultValue;
        this.possibleOutputs = possibleOutputs;
        this.buckets = buckets;
        this.valueType = valueType;
        this.isActive = isActive;
    }

    public RuleData(Long id, String name, String uname, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.uname = uname;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EntityRuleType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityRuleType entityType) {
        this.entityType = entityType;
    }

    public EntityRuleType getParamEntityType() {
        return paramEntityType;
    }

    public void setParamEntityType(EntityRuleType paramEntityType) {
        this.paramEntityType = paramEntityType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<KeyValue> getPossibleOutputs() {
        return possibleOutputs;
    }

    public void setPossibleOutputs(List<KeyValue> possibleOutputs) {
        this.possibleOutputs = possibleOutputs;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    public Rule getRule() {
        Rule rule = new Rule(id, entityType, name, uname, description, defaultValue, valueType, possibleOutputs, buckets);

        return rule;
    }
}
