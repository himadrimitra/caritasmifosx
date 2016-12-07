package com.finflux.ruleengine.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.ruleengine.lib.data.EntityRuleType;
import com.finflux.ruleengine.lib.data.ValueType;

@Entity
@Table(name = "f_risk_rule", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "uname" }, name = "uk_f_risk_rule_uname")})
public class RuleModel extends AbstractPersistable< Long> {

    @Column(name = "entity_type", nullable = false)
    private Integer entityType;//Criteria,Dimension,Factor

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "uname", length = 100, nullable = false)
    private String uname;

    @Column(name = "description", length = 512, nullable = false)
    private String description;

    @Column(name = "default_value", length = 32)
    private String defaultValue;

    @Column(name = "value_type", nullable = false)
    private Integer valueType;

    @Column(name = "possible_outputs", length = 1024)
    private String possibleOutputs;

    @Column(name = "expression", length = 2048, nullable = false)
    private String expression;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    protected RuleModel() {}


    private RuleModel(EntityRuleType entityType, String name, String uname, String description,
                      String defaultValue, ValueType valueType, String possibleOutputs, String bucketExpression, Boolean active) {
        this.entityType = entityType.getValue();
        this.name = name;
        this.uname = uname;
        this.description = description;
        this.defaultValue = defaultValue;
        this.valueType = valueType.getValue();
        this.possibleOutputs = possibleOutputs;
        this.expression = bucketExpression;
        this.isActive = active;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getPossibleOutputs() {
        return possibleOutputs;
    }

    public void setPossibleOutputs(String possibleOutputs) {
        this.possibleOutputs = possibleOutputs;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public static RuleModel create(EntityRuleType entityType, String name, String uname, String description,
                                   String defaultValue, ValueType valueType, String possibleOutputs,
                                   String bucketExpression, Boolean active) {
        return new RuleModel(entityType,name,uname,description, defaultValue, valueType, possibleOutputs,
                bucketExpression,active);
    }
}