package com.finflux.organisation.transaction.authentication.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.springframework.stereotype.Service;

import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticaionTransactionTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedTransactionTypeEnumerations;

@Service
public class TransactionAuthenticationDropdownReadPlatformServiceImpl
		implements TransactionAuthenticationDropdownReadPlatformService {

	
	@Override
	public Collection<EnumOptionData> retrieveApplicableToTypes() {
		final Collection<EnumOptionData> transactionAuthenticationAppliesToTypes = new ArrayList<>();
		for (final SupportedAuthenticationPortfolioTypes appliesTo : SupportedAuthenticationPortfolioTypes.values()) {
			if (SupportedAuthenticationPortfolioTypes.INVALID.equals(appliesTo)) {
				continue;
			}
			transactionAuthenticationAppliesToTypes.add(SupportedAuthenticationPortfolioTypes.chargeAppliesTo(appliesTo));
		}
		return transactionAuthenticationAppliesToTypes;
	}

	@Override
	public Collection<EnumOptionData> retrieveLoanTransactionTypes() {
		return Arrays.asList(SupportedTransactionTypeEnumerations.supportedAuthenticaionTransactionTypes(SupportedAuthenticaionTransactionTypes.DISBURSEMENT),
				SupportedTransactionTypeEnumerations.supportedAuthenticaionTransactionTypes(SupportedAuthenticaionTransactionTypes.REYPAYMENT));
	}

	@Override
	public Collection<EnumOptionData> retrieveSavingTransactionTypes() {
		return Arrays.asList(SupportedTransactionTypeEnumerations.supportedAuthenticaionTransactionTypes(SupportedAuthenticaionTransactionTypes.WITHDRAWAL),
				SupportedTransactionTypeEnumerations.supportedAuthenticaionTransactionTypes(SupportedAuthenticaionTransactionTypes.DEPOSIT));
	}

}
