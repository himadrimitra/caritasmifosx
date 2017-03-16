package com.finflux.risk.creditbureau.provider.equifax.service;

import org.apache.fineract.infrastructure.configuration.data.EquifaxCredentialsData;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;

public interface EquifaxRequestService {

    CreditBureauResponse sendEquifaxEnquiry(EnquiryReferenceData enquiryReferenceData, EquifaxCredentialsData equifaxCredentialsData);
}