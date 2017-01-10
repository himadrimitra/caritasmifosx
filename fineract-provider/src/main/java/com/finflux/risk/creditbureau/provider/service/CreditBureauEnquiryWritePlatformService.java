package com.finflux.risk.creditbureau.provider.service;

import java.util.List;

import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoan;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;

public interface CreditBureauEnquiryWritePlatformService {

    void saveEnquiryResponseDetails(EnquiryReferenceData enquiryReferenceData, CreditBureauResponse responseData);

    void saveReportResponseDetails(LoanEnquiryReferenceData loanEnquiryReferenceData, CreditBureauResponse responseData);

    CreditBureauEnquiry createNewEnquiry(CreditBureauEnquiry creditBureauEnquiry);

    void saveCreditBureauExistingLoans(final Long loanApplicationId, final List<CreditBureauExistingLoan> creditBureauExistingLoans,
            final Long loanId, final Long trancheDisbursalId);
}