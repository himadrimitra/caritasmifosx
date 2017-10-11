package com.finflux.risk.creditbureau.provider.equifax.service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;

public interface EquifaxIssueService {

    CreditBureauResponse sendEquifaxIssue(LoanEnquiryReferenceData enquiryReferenceData);
}
