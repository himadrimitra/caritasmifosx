package com.finflux.risk.creditbureau.provider.highmark.service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.REQUESTREQUESTFILE;
import org.apache.fineract.infrastructure.configuration.data.HighmarkCredentialsData;

import java.util.Map;

public interface HighmarkIssueService {

    CreditBureauResponse sendHighmarkIssue(LoanEnquiryReferenceData enquiryReferenceData, HighmarkCredentialsData highmarkCredentialsData);
}
