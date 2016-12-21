package com.finflux.ruleengine.execution.data;

/**
 * Created by dhirendra on 27/11/16.
 */
public enum DataLayerKey {
    CLIENT_ID("clientId"), //
    LOANAPPLICATION_ID("loanApplicationId");//

    private String value;

    DataLayerKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
