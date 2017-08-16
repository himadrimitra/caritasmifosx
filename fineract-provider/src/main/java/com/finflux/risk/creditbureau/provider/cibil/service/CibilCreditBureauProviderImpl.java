package com.finflux.risk.creditbureau.provider.cibil.service;

import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.service.CreditBureauProvider;

@Service
@Scope("singleton")
public class CibilCreditBureauProviderImpl implements CreditBureauProvider {

	private final static Logger logger = LoggerFactory.getLogger(CibilCreditBureauProviderImpl.class);
	final CibilRequestService cibilRequestService;
	final CibilIssueService cibilIssueService;
	final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;

	private static final String KEY = "india.cibil.tuef";

	@Autowired
	public CibilCreditBureauProviderImpl(final CibilRequestService cibilRequestService,
			final CibilIssueService cibilIssueService,
			ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService) {
		this.cibilRequestService = cibilRequestService;
		this.cibilIssueService = cibilIssueService;
		this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public CreditBureauResponse enquireCreditBureau(final EnquiryReferenceData enquiryReferenceData) {
		CreditBureauResponse response = cibilRequestService.sendCibilEnquiry(enquiryReferenceData,
				this.externalServicesPropertiesReadPlatformService.getCibilCredentials());
		return response;
	}

	@Override
	public CreditBureauResponse fetchCreditBureauReport(final LoanEnquiryReferenceData enquiryData) {
		return cibilIssueService.sendEquifaxIssue(enquiryData,
				this.externalServicesPropertiesReadPlatformService.getCibilCredentials());
	}

}
