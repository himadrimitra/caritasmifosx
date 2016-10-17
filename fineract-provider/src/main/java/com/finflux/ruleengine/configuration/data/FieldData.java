package com.finflux.ruleengine.configuration.data;

import com.finflux.ruleengine.lib.data.KeyValue;
import com.finflux.ruleengine.lib.data.ValueType;

import java.util.List;

/**
 * Created by dhirendra on 16/09/16.
 */
public class FieldData {

    private String name;

    private String uname;

    private ValueType valueType;

    private List<KeyValue> options;

    private FieldType fieldType;

    public FieldData( String name, String uname, ValueType valueType, List<KeyValue> optionList) {
        this.name = name;
        this.uname = uname;
        this.valueType = valueType;
        this.options = optionList;
        this.fieldType = FieldType.DATA;
    }

    public FieldData(String name, String uname, ValueType valueType, List<KeyValue> optionList, FieldType fieldType) {
        this.name = name;
        this.uname = uname;
        this.valueType = valueType;
        this.options = optionList;
        this.fieldType = fieldType;
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

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
}
