package com.finflux.risk.creditbureau.provider.cibil.service;

import org.apache.fineract.infrastructure.configuration.data.CibilCredentialsData;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;

public interface CibilRequestService {

    CreditBureauResponse sendCibilEnquiry(EnquiryReferenceData enquiryReferenceData, CibilCredentialsData cibilCredentialsData);
}