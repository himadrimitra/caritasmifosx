package com.finflux.ruleengine.lib.data;

/**
 * Created by dhirendra on 06/09/16.
 */
public class Expression {

    private String parameter;

    private ComparatorType comparator;

    private String value;

    private ValueType valueType;

    public Expression(String parameter, ComparatorType comparator, String value,
                      ValueType valueType) {
        this.parameter = parameter;
        this.comparator = comparator;
        this.value = value;
        this.valueType = valueType;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public ComparatorType getComparator() {
        return comparator;
    }

    public void setComparator(ComparatorType comparator) {
        this.comparator = comparator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}
