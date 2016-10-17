package com.finflux.ruleengine.lib.data;

import java.util.List;

/**
 * Created by dhirendra on 06/09/16.
 */
public class Rule {

    private Long id;

    private String name;

    private String uname;

    private OutputConfiguration outputConfiguration;

    private EntityRuleType entity;

    private List<Bucket> buckets;

    public Rule(Long id, EntityRuleType entityType, String name, String uname, String description, String defaultValue, ValueType valueType,
                List<KeyValue> possibleOutputs, List<Bucket> buckets) {
        this.id = id;
        this.entity = entityType;
        this.name = name;
        this.uname = uname;
        this.outputConfiguration = new OutputConfiguration(valueType, possibleOutputs, defaultValue);
        this.buckets = buckets;
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

    public OutputConfiguration getOutputConfiguration() {
        return outputConfiguration;
    }

    public void setOutputConfiguration(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = outputConfiguration;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    public EntityRuleType getEntity() {
        return entity;
    }

    public void setEntity(EntityRuleType entity) {
        this.entity = entity;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
