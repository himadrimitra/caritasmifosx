package com.finflux.risk.creditbureau.provider.data;

import java.util.List;

public class CreditBureauResponse {

    final List<CreditScore> creditScores;
    final List<CreditBureauExistingLoan> creditBureauExistingLoans;
    final CreditBureauReportFile creditBureauReportFile;
    final EnquiryResponse enquiryResponse;
    public CreditBureauResponse(final EnquiryResponse enquiryResponse, final List<CreditScore> creditScores,
            final List<CreditBureauExistingLoan> creditBureauExistingLoans, final CreditBureauReportFile creditBureauReportFile) {
        this.enquiryResponse = enquiryResponse;
        this.creditScores = creditScores;
        this.creditBureauExistingLoans = creditBureauExistingLoans;
        this.creditBureauReportFile = creditBureauReportFile;
    }

    public EnquiryResponse getEnquiryResponse() {
        return enquiryResponse;
    }

    public List<CreditScore> getCreditScore() {
        return creditScores;
    }

    public List<CreditBureauExistingLoan> getCreditBureauExistingLoans() {
        return creditBureauExistingLoans;
    }

    public CreditBureauReportFile getCreditBureauReportFile() {
        return creditBureauReportFile;
    }
}
