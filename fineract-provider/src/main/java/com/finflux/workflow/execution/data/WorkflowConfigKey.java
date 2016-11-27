package com.finflux.workflow.execution.data;

/**
 * Created by dhirendra on 27/11/16.
 */
public enum WorkflowConfigKey {
        CLIENT_ID("client_id"),
        OFFICE_ID("office_id"),
        LOANAPPLICATION_ID("loanapplication_id"),
        DATATABLE_NAME("datatablename"),
        SURVEY_NAME("surveyname"),
        TITLE("title"),
        BODY("body");

        private String value;

        WorkflowConfigKey(String value) {
                this.value = value;
        }

        public String getValue() {
                return value;
        }
}
