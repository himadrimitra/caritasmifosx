package com.finflux.risk.creditbureau.provider.highmark.service;

import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.service.CreditBureauProvider;

@Service
public class HighmarkCreditBureauProviderImpl implements CreditBureauProvider {

    final HighmarkRequestService highmarkRequestService;
    final HighmarkIssueService highmarkIssueService;
    final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;

    private static final String KEY = "india.highmark.olp";

    @Autowired
    public HighmarkCreditBureauProviderImpl(final HighmarkRequestService highmarkRequestService,
            final HighmarkIssueService highmarkIssueService,
            ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService) {
        this.highmarkRequestService = highmarkRequestService;
        this.highmarkIssueService = highmarkIssueService;
        this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public CreditBureauResponse enquireCreditBureau(EnquiryReferenceData enquiryReferenceData) {
        return highmarkRequestService.sendHighmarkEnquiry(enquiryReferenceData,
                this.externalServicesPropertiesReadPlatformService.getHighmarkCredentials());
    }

    @Override
    public CreditBureauResponse fetchCreditBureauReport(LoanEnquiryReferenceData enquiryData) {
        return highmarkIssueService.sendHighmarkIssue(enquiryData,
                this.externalServicesPropertiesReadPlatformService.getHighmarkCredentials());
    }

}
