package com.finflux.workflow.execution.data;

import java.util.List;

import org.apache.fineract.organisation.office.data.OfficeData;

public class LoanProductWorkFlowSummaryData {

    private List<OfficeData> offices;

    public LoanProductWorkFlowSummaryData() {}

    public List<OfficeData> getOfficeWorkFlowSummaries() {
        return this.offices;
    }

    public void setOfficeSummaries(List<OfficeData> offices) {
        this.offices = offices;
    }
}