package com.finflux.risk.creditbureau.provider.cibil.service;

import org.apache.fineract.infrastructure.configuration.data.CibilCredentialsData;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;

public interface CibilIssueService {

    CreditBureauResponse sendEquifaxIssue(LoanEnquiryReferenceData enquiryReferenceData, final CibilCredentialsData cibilCredentials);
}
