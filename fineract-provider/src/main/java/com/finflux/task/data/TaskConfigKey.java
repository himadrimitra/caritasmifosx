package com.finflux.task.data;

/**
 * Created by dhirendra on 27/11/16.
 */
public enum TaskConfigKey {
    CLIENT_ID("clientId"), //
    CENTER_ID("centerId"), //
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
    LOAN_TRANSACTION_ID("loanTransactionId"),
    TASKTEMPLATEENTITY_TYPE("taskTemplateEntityType"),
    TASKTEMPLATEENTITY_ID("taskTemplateEntityId"),
    BANK_ID("bankId"),
    LOANAPPLICATION_COAPPLICANT_ID("loanApplicationCoApplicantId"),
    DISTRICT_ID("districtId");
    private String value;

    TaskConfigKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
