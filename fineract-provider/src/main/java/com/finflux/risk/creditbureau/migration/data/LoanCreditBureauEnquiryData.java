package com.finflux.risk.creditbureau.migration.data;

public class LoanCreditBureauEnquiryData {

    private final Long id;
    private final String request;
    private final String response;
    private final byte[] reportData;

    public LoanCreditBureauEnquiryData(final Long id, final String request, final String response, final byte[] reportData) {
        super();
        this.id = id;
        this.request = request;
        this.response = response;
        this.reportData = reportData;
    }

    public Long getId() {
        return this.id;
    }

    public String getRequest() {
        return this.request;
    }

    public String getResponse() {
        return this.response;
    }

    public byte[] getReportData() {
        return this.reportData;
    }

}
