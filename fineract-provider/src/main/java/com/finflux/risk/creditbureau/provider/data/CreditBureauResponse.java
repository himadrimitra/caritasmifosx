package com.finflux.risk.creditbureau.provider.data;

import java.util.List;

public class CreditBureauResponse {

    final CreditScore creditScore;
    final List<CreditBureauExistingLoan> creditBureauExistingLoans;
    final CreditBureauReportFile creditBureauReportFile;
    final EnquiryResponse enquiryResponse;

    public CreditBureauResponse(final EnquiryResponse enquiryResponse, final CreditScore creditScore,
            final List<CreditBureauExistingLoan> creditBureauExistingLoans, final CreditBureauReportFile creditBureauReportFile) {
        this.enquiryResponse = enquiryResponse;
        this.creditScore = creditScore;
        this.creditBureauExistingLoans = creditBureauExistingLoans;
        this.creditBureauReportFile = creditBureauReportFile;
    }

    public EnquiryResponse getEnquiryResponse() {
        return enquiryResponse;
    }

    public CreditScore getCreditScore() {
        return creditScore;
    }

    public List<CreditBureauExistingLoan> getCreditBureauExistingLoans() {
        return creditBureauExistingLoans;
    }

    public CreditBureauReportFile getCreditBureauReportFile() {
        return creditBureauReportFile;
    }
}
