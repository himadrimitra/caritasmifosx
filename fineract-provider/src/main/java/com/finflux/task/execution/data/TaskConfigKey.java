package com.finflux.task.execution.data;

/**
 * Created by dhirendra on 27/11/16.
 */
public enum TaskConfigKey {
    CLIENT_ID("clientId"), //
    OFFICE_ID("officeId"), //
    LOANAPPLICATION_ID("loanApplicationId"), //
    DATATABLE_NAME("datatablename"), //
    SURVEY_NAME("surveyname"), //
    TITLE("title"), //
    BODY("body");

    private String value;

    TaskConfigKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
