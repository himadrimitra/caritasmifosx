package com.finflux.risk.creditbureau.provider.data;

public class ReportRequestData {

    final String acknowledgementNumber;

    final String enquiryNumber;

    public ReportRequestData(String acknowledgementNumber, String enquiryNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
        this.enquiryNumber = enquiryNumber;
    }

//    public ReportRequestData(EnquiryReferenceData loanEnquiryReferenceData) {
//        this.acknowledgementNumber = loanEnquiryReferenceData.getAcknowledgementNumber();
//        this.enquiryNumber = loanEnquiryReferenceData.enquiryRefNumber;
//    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public String getEnquiryNumber() {
        return enquiryNumber;
    }
}
