package com.finflux.organisation.transaction.authentication.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class OtpRequestHandlerServiceForLoanImpl implements OtpRequestHandlerService {
	private final String KEY = "loan";
	private final LoanReadPlatformService loanReadPlatformService;
	private final TransactionAuthenticationService transactionAuthenticationService;

	@Override
	public String getKey() {
		return KEY;
	}
	
	@Autowired
	public OtpRequestHandlerServiceForLoanImpl(final LoanReadPlatformService loanReadPlatformService,
			final TransactionAuthenticationService transactionAuthenticationService) {
		this.loanReadPlatformService = loanReadPlatformService;
		this.transactionAuthenticationService = transactionAuthenticationService;
	}

	@Override
	public Object sendOtp(final Long entityId, String jsonCommand) {
		//entityId here is loanId
		Map<String, Object> data = loanReadPlatformService.retrieveLoanProductIdApprovedAmountClientId(entityId);
		Long clientId = (Long)data.get("clientId");
		return this.transactionAuthenticationService.sendOtpForTheCLient(clientId, jsonCommand);
	}

}
