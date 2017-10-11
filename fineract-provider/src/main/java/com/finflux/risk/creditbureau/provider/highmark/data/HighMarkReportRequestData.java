package com.finflux.risk.creditbureau.provider.highmark.data;

public class HighMarkReportRequestData {

    final String batchId;

    final String inquiryNumber;

    public HighMarkReportRequestData(String batchId, String inquiryNumber) {
        this.batchId = batchId;
        this.inquiryNumber = inquiryNumber;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public String getInquiryNumber() {
        return this.inquiryNumber;
    }



}
