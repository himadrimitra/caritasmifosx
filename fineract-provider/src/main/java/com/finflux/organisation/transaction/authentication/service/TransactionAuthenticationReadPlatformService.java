package com.finflux.organisation.transaction.authentication.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;

public interface TransactionAuthenticationReadPlatformService {
	TransactionAuthenticationData retriveTemplate();

	List<TransactionAuthenticationData> findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmount(
			final Integer productTypeId, final Integer transactionTypeId, final Long paymentTypeId,
			final BigDecimal amountGreaterThan);

	List<TransactionAuthenticationData> findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmountAndAuthenticationTypeId(
			final Integer productTypeId, final Integer transactionTypeId, final Long paymentTypeId,
			final BigDecimal amountGreaterThan, final Long authenticationId);

	TransactionAuthenticationData retrieveOneById(final Long id);

	TransactionAuthenticationData retriveTransactionAuthenticationDetails(final Integer productTypeId,
			final Integer transactionTypeId, final Long paymentTypeId, BigDecimal amount);

	Collection<TransactionAuthenticationData> retriveTransactionAuthenticationDetails(final Integer productTypeId,
			final Integer transactionTypeId, final BigDecimal approvedPrincipal);

	Collection<TransactionAuthenticationData> retiveTransactionAuthenticationDetailsForTemplate(
			final Integer productTypeId, final Integer transactionTypeId, final BigDecimal approvedPrincipal);

	Collection<TransactionAuthenticationData> retriveAllTransactionAuthenticationDeatails();
}
