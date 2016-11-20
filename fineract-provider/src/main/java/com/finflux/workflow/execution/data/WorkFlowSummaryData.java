package com.finflux.workflow.execution.data;

import java.util.List;

public class WorkFlowSummaryData {

    private String stepName;
    private String stepShortName;
    private Long noOfCount;
    private List<StepSummaryData> stepSummaries;

    public WorkFlowSummaryData() {}

    public WorkFlowSummaryData(final String stepName, final String stepShortName, final Long noOfCount) {
        this.stepName = stepName;
        this.stepShortName = stepShortName;
        this.noOfCount = noOfCount;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepShortName() {
        return this.stepShortName;
    }

    public void setStepShortName(String stepShortName) {
        this.stepShortName = stepShortName;
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