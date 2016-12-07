package com.finflux.task.execution.data;

import java.util.List;

import org.apache.fineract.organisation.office.data.OfficeData;

public class LoanProductTaskSummaryData {

    private List<OfficeData> offices;

    public LoanProductTaskSummaryData() {}

    public List<OfficeData> getOfficeWorkFlowSummaries() {
        return this.offices;
    }

    public void setOfficeSummaries(List<OfficeData> offices) {
        this.offices = offices;
    }
}