package com.finflux.risk.creditbureau.provider.highmark.data;

public class AT03RequestData {

    final String batchId;
    final String inquiryNo;

    public AT03RequestData(String batchId, String inquiryNo) {
        super();
        this.batchId = batchId;
        this.inquiryNo = inquiryNo;
    }

    public String getInquiryNo() {
        return this.inquiryNo;
    }

    public String getBatchId() {
        return this.batchId;
    }

}
