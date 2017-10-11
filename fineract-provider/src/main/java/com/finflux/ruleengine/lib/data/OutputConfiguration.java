package com.finflux.ruleengine.lib.data;

import java.util.List;

/**
 * Created by dhirendra on 06/09/16.
 */
public class OutputConfiguration {
    private ValueType valueType;
    private List<KeyValue> options;
    private String defaultValue;

    public OutputConfiguration(ValueType valueType, List<KeyValue> options, String defaultValue) {
        this.valueType = valueType;
        this.options = options;
        this.defaultValue = defaultValue;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public List<KeyValue> getOptions() {
        return options;
    }

    public void setOptions(List<KeyValue> options) {
        this.options = options;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
