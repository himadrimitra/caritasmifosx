package com.finflux.workflow.execution.data;

import java.util.List;

public class WorkFlowSummaryData {

    private String stepName;
    private Long noOfCount;
    private List<StepSummaryData> stepSummaries;

    public WorkFlowSummaryData() {}

    public WorkFlowSummaryData(final String stepName, final Long noOfCount) {
        this.stepName = stepName;
        this.noOfCount = noOfCount;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Long getNoOfCount() {
        return this.noOfCount;
    }

    public void setNoOfCount(Long noOfCount) {
        this.noOfCount = noOfCount;
    }

    public List<StepSummaryData> getStepSummaries() {
        return this.stepSummaries;
    }

    public void setStepSummaries(List<StepSummaryData> stepSummaries) {
        this.stepSummaries = stepSummaries;
    }
}