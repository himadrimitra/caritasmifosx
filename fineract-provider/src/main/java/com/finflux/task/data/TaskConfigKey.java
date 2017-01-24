package com.finflux.task.data;

/**
 * Created by dhirendra on 27/11/16.
 */
public enum TaskConfigKey {
    CLIENT_ID("clientId"), //
    CENETR_ID("centerId"), //
    GROUP_ID("groupId"), //
    VILLAGE_ID("villageId"), //
    OFFICE_ID("officeId"), //
    LOANAPPLICATION_ID("loanApplicationId"), //
    DATATABLE_NAME("datatablename"), //
    SURVEY_ID("surveyId"), //
    TITLE("title"), //
    BODY("body"),
    BANK_TRANSACTION_ID("bankTransactionId"),
    LOAN_ID("loanId"),
    LOAN_TRANSACTION_ID("loanTransactionId");

    private String value;

    TaskConfigKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
