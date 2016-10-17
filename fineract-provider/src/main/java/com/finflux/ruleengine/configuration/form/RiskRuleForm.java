package com.finflux.ruleengine.configuration.form;

import com.finflux.ruleengine.lib.data.Bucket;
import com.finflux.ruleengine.lib.data.EntityRuleType;
import com.finflux.ruleengine.lib.data.OutputConfiguration;
import com.finflux.ruleengine.lib.data.Rule;

import java.util.List;

/**
 * Created by dhirendra on 19/09/16.
 */
public class RiskRuleForm {

    private String name;

    private String uname;

    private String description;

    private OutputConfiguration outputConfiguration;

    private List<Bucket> buckets;

    private Boolean isActive;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
