package com.finflux.organisation.transaction.authentication.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.service.LoanApplicationReferenceReadPlatformService;

@Service
public class OtpRequestHandlerServiceForLoanApplicationImpl implements OtpRequestHandlerService {
	private final String KEY = "loanApplication";
	private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
	private final TransactionAuthenticationService transactionAuthenticationService;

	@Override
	public String getKey() {
		return KEY;
	}

	@Autowired
	public OtpRequestHandlerServiceForLoanApplicationImpl(final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
			final TransactionAuthenticationService transactionAuthenticationService) {
		this.loanApplicationReferenceReadPlatformService = loanApplicationReferenceReadPlatformService;
		this.transactionAuthenticationService = transactionAuthenticationService;
	}
	
	@Override
	public Object sendOtp(final Long entityId, String jsonCommand) {
		//entityId here is loanApplicationReferenceId
		Map<String, Object> data = this.loanApplicationReferenceReadPlatformService
				.retrieveLoanProductIdApprovedAmountClientId(entityId);
		Long clientId = (Long) data.get("clientId");
		return this.transactionAuthenticationService.sendOtpForTheCLient(clientId, jsonCommand);
	}

}
