package com.finflux.workflow.execution.data;

public class StepSummaryData {

    private String stepStatus;
    private Long noOfCount;

    public StepSummaryData() {}

    public StepSummaryData(final String stepStatus, final Long noOfCount) {
        this.stepStatus = stepStatus;
        this.noOfCount = noOfCount;
    }

    public String getStepStatus() {
        return this.stepStatus;
    }

    public void setStepStatus(final String stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Long getNoOfCount() {
        return this.noOfCount;
    }

    public void setNoOfCount(final Long noOfCount) {
        this.noOfCount = noOfCount;
    }

}
