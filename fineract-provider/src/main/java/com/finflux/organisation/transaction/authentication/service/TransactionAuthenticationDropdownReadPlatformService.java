package com.finflux.organisation.transaction.authentication.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public interface TransactionAuthenticationDropdownReadPlatformService {
	Collection<EnumOptionData> retrieveApplicableToTypes();

	Collection<EnumOptionData> retrieveLoanTransactionTypes();

	Collection<EnumOptionData> retrieveSavingTransactionTypes();

}
