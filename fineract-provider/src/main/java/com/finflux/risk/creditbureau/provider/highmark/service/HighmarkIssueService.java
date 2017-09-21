package com.finflux.risk.creditbureau.provider.highmark.service;

import org.apache.fineract.infrastructure.configuration.data.HighmarkCredentialsData;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;

public interface HighmarkIssueService {

    CreditBureauResponse sendHighmarkIssue(LoanEnquiryReferenceData enquiryReferenceData, HighmarkCredentialsData highmarkCredentialsData);
}
