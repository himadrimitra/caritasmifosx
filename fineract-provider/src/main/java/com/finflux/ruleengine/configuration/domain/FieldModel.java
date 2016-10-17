package com.finflux.ruleengine.configuration.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_risk_field")
public class FieldModel extends AbstractPersistable<Long> {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "uname", length = 100, nullable = false)
    private String uname;

    @Column(name = "value_type", length = 1, nullable = false)
    private Integer valueType;

    @Column(name = "options", length = 1024, nullable = false)
    private String options;

    @Column(name = "code_name", length = 100, nullable = false)
    private String codeName;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    protected FieldModel() {}

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

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}