package com.finflux.risk.creditbureau.provider.equifax.service;

import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.service.CreditBureauProvider;

@Service
public class EquifaxCreditBureauProviderImpl implements CreditBureauProvider {

	final EquifaxRequestService equifaxRequestService;
	final EquifaxIssueService equifaxIssueService;
	final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;

	private static final String KEY = "india.equifax.mcs";

	@Autowired
	public EquifaxCreditBureauProviderImpl(final EquifaxRequestService equifaxRequestService,
			final EquifaxIssueService equifaxIssueService,
			ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService) {
		this.equifaxRequestService = equifaxRequestService;
		this.equifaxIssueService = equifaxIssueService;
		this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public CreditBureauResponse enquireCreditBureau(final EnquiryReferenceData enquiryReferenceData) {
		return equifaxRequestService.sendEquifaxEnquiry(enquiryReferenceData,
				this.externalServicesPropertiesReadPlatformService.getEquifaxCredentials());
	}

	@Override
	public CreditBureauResponse fetchCreditBureauReport(final LoanEnquiryReferenceData enquiryData) {
		return equifaxIssueService.sendEquifaxIssue(enquiryData);
	}

}
