package com.finflux.risk.creditbureau.provider.highmark.service;

import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import org.apache.fineract.infrastructure.configuration.data.HighmarkCredentialsData;

public interface HighmarkRequestService {

    CreditBureauResponse sendHighmarkEnquiry(EnquiryReferenceData enquiryReferenceData, HighmarkCredentialsData highmarkCredentialsData);
}