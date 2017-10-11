package com.finflux.risk.creditbureau.provider.service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;

/**
 * Created by dhirendra on 23/08/16.
 */
public interface CreditBureauProvider {

    String getKey();

    CreditBureauResponse enquireCreditBureau(EnquiryReferenceData enquiryReferenceData);

    CreditBureauResponse fetchCreditBureauReport(LoanEnquiryReferenceData data);
}
