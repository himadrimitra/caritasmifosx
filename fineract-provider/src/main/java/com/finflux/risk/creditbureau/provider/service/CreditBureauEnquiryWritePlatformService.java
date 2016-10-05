package com.finflux.risk.creditbureau.provider.service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;

public interface CreditBureauEnquiryWritePlatformService {

    void saveEnquiryResponseDetails(EnquiryReferenceData enquiryReferenceData, CreditBureauResponse responseData);

    void saveReportResponseDetails(LoanEnquiryReferenceData loanEnquiryReferenceData, CreditBureauResponse responseData);

    CreditBureauEnquiry createNewEnquiry(CreditBureauEnquiry creditBureauEnquiry);
}