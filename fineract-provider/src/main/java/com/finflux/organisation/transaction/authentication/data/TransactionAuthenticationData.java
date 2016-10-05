package com.finflux.organisation.transaction.authentication.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;

public class TransactionAuthenticationData {

	private final Long authenticationRuleId;
	// portfolio type id
	private final Integer portfolioTypeId;
	private final Integer transactionTypeId;
	private final Long paymentTypeId;
	private final BigDecimal amount;
	private final Long secondAppUserRoleId;
	private final boolean isSecondAppUserEnabled;
	private final Long authenticationTypeId;
	private final String authenticationType;

	private final PaymentTypeData paymentType;
	// portfolioType String
	private final String portfolioType;
	private final String transactionType;

	// data for templates
	private final Collection<EnumOptionData> transactionAuthenticationAppliesTo;//
	private final Collection<PaymentTypeData> paymentOptions;//
	private final Collection<EnumOptionData> loanTransactionTypeOptions;
	private final Collection<EnumOptionData> savingsTransactionTypeoptions;
	private final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices;

	private TransactionAuthenticationData(final Long id, final Integer portfolioTypeId, final Integer transactionTypeId,
			final Long paymentTypeId, final BigDecimal amount, final Long secondAppUserRoleId,
			final boolean isSecondAppUserEnabled, Long authenticationTypeId,
			final Collection<EnumOptionData> transactionAuthenticationAppliesTo,
			final Collection<PaymentTypeData> paymentOptions,
			final Collection<EnumOptionData> loanTransactionTypeOptions,
			final Collection<EnumOptionData> savingsTransactionTypeoptions,
			final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices,
			final String authenticationType, final PaymentTypeData paymentType, final String portfolioType,
			final String transactionType) {
		this.authenticationRuleId = id;
		this.portfolioTypeId = portfolioTypeId;
		this.transactionTypeId = transactionTypeId;
		this.paymentTypeId = paymentTypeId;
		this.amount = amount;
		this.secondAppUserRoleId = secondAppUserRoleId;
		this.isSecondAppUserEnabled = isSecondAppUserEnabled;
		this.authenticationTypeId = authenticationTypeId;
		this.transactionAuthenticationAppliesTo = transactionAuthenticationAppliesTo;
		this.paymentOptions = paymentOptions;
		this.loanTransactionTypeOptions = loanTransactionTypeOptions;
		this.savingsTransactionTypeoptions = savingsTransactionTypeoptions;
		this.availableAuthenticationServices = availableAuthenticationServices;
		this.authenticationType = authenticationType;
		this.paymentType = paymentType;
		this.transactionType = transactionType;
		this.portfolioType = portfolioType;
	}

	public static TransactionAuthenticationData retriveTemplate(
			final Collection<EnumOptionData> transactionAuthenticationAppliesTo,
			final Collection<PaymentTypeData> paymentOptions,
			final Collection<EnumOptionData> loanTransactionTypeOptions,
			final Collection<EnumOptionData> savingsTransactionTypeoptions,
			final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices) {
		final Long id = null;
		final Integer portfolioTypeId = null;
		final Integer transactionTypeId = null;
		final Long paymentTypeId = null;
		final BigDecimal amount = null;
		final Long secondAppUserRoleId = null;
		final boolean isSecondAppUserEnabled = false;
		final Long authenticationTypeId = null;
		final String authenticationType = null;
		final PaymentTypeData paymentType = null;
		final String portfolioType = null;
		final String transactionType = null;
		return new TransactionAuthenticationData(id, portfolioTypeId, transactionTypeId, paymentTypeId, amount,
				secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId, transactionAuthenticationAppliesTo,
				paymentOptions, loanTransactionTypeOptions, savingsTransactionTypeoptions,
				availableAuthenticationServices, authenticationType, paymentType, portfolioType, transactionType);
	}

	public static TransactionAuthenticationData instance(final Long id, final Integer portfolioTypeId,
			final Integer transactionTypeId, final Long paymentTypeId, final BigDecimal amount,
			final Long secondAppUserRoleId, final boolean isSecondAppUserEnabled, final Long authenticationTypeId) {
		final Collection<EnumOptionData> transactionAuthenticationAppliesTo = null;//
		final Collection<PaymentTypeData> paymentOptions = null;//
		final Collection<EnumOptionData> loanTransactionTypeOptions = null;
		final Collection<EnumOptionData> savingsTransactionTypeoptions = null;
		final String authenticationType = null;
		final PaymentTypeData paymentType = null;
		final String portfolioType = null;
		final String transactionType = null;
		final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices = null;
		return new TransactionAuthenticationData(id, portfolioTypeId, transactionTypeId, paymentTypeId, amount,
				secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId, transactionAuthenticationAppliesTo,
				paymentOptions, loanTransactionTypeOptions, savingsTransactionTypeoptions,
				availableAuthenticationServices, authenticationType, paymentType, portfolioType, transactionType);
	}

	public static TransactionAuthenticationData instance(final Long id, final Integer portfolioTypeId,
			final Integer transactionTypeId, final Long paymentTypeId, final BigDecimal amount,
			final Long authenticationTypeId) {
		final Collection<EnumOptionData> transactionAuthenticationAppliesTo = null;//
		final Collection<PaymentTypeData> paymentOptions = null;//
		final Collection<EnumOptionData> loanTransactionTypeOptions = null;
		final Collection<EnumOptionData> savingsTransactionTypeoptions = null;
		final Long secondAppUserRoleId = null;
		final Boolean isSecondAppUserEnabled = false;
		final String authenticationType = null;
		final PaymentTypeData paymentType = null;
		final String portfolioType = null;
		final String transactionType = null;
		final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices = null;
		return new TransactionAuthenticationData(id, portfolioTypeId, transactionTypeId, paymentTypeId, amount,
				secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId, transactionAuthenticationAppliesTo,
				paymentOptions, loanTransactionTypeOptions, savingsTransactionTypeoptions,
				availableAuthenticationServices, authenticationType, paymentType, portfolioType, transactionType);
	}

	public static TransactionAuthenticationData instance(final Long id, final Integer portfolioTypeId,
			final Integer transactionTypeId, final Long paymentTypeId, final BigDecimal amount,
			final String authenticationType) {
		final Collection<EnumOptionData> transactionAuthenticationAppliesTo = null;//
		final Collection<PaymentTypeData> paymentOptions = null;//
		final Collection<EnumOptionData> loanTransactionTypeOptions = null;
		final Collection<EnumOptionData> savingsTransactionTypeoptions = null;
		final Long secondAppUserRoleId = null;
		final Boolean isSecondAppUserEnabled = false;
		final Long authenticationTypeId = null;
		final PaymentTypeData paymentType = null;
		final String portfolioType = null;
		final String transactionType = null;
		final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices = null;
		return new TransactionAuthenticationData(id, portfolioTypeId, transactionTypeId, paymentTypeId, amount,
				secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId, transactionAuthenticationAppliesTo,
				paymentOptions, loanTransactionTypeOptions, savingsTransactionTypeoptions,
				availableAuthenticationServices, authenticationType, paymentType, portfolioType, transactionType);
	}

	public static TransactionAuthenticationData newInstance(final Long id, final String portfolioType,
			final String transactionType, final BigDecimal amount, final PaymentTypeData paymentTypeData,
			final String authenticationType) {
		final Collection<EnumOptionData> transactionAuthenticationAppliesTo = null;//
		final Collection<PaymentTypeData> paymentOptions = null;//
		final Collection<EnumOptionData> loanTransactionTypeOptions = null;
		final Collection<EnumOptionData> savingsTransactionTypeoptions = null;
		final Long secondAppUserRoleId = null;
		final Boolean isSecondAppUserEnabled = false;
		final Long authenticationTypeId = null;
		final Integer portfolioTypeId = null;
		final Integer transactionTypeId = null;
		final Long paymentTypeId = null;
		final Collection<ExternalAuthenticationServiceData> availableAuthenticationServices = null;
		return new TransactionAuthenticationData(id, portfolioTypeId, transactionTypeId, paymentTypeId, amount,
				secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId, transactionAuthenticationAppliesTo,
				paymentOptions, loanTransactionTypeOptions, savingsTransactionTypeoptions,
				availableAuthenticationServices, authenticationType, paymentTypeData, portfolioType, transactionType);
	}

	public Long getId() {
		return this.authenticationRuleId;
	}

	public Integer getPortfolioTypeId() {
		return this.portfolioTypeId;
	}

	public Integer getTransactionTypeId() {
		return this.transactionTypeId;
	}

	public Long getPaymentTypeId() {
		return this.paymentTypeId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public Long getSecondAppUserRoleId() {
		return this.secondAppUserRoleId;
	}

	public boolean isSecondAppUserEnabled() {
		return this.isSecondAppUserEnabled;
	}

	public Long getAuthenticationTypeId() {
		return this.authenticationTypeId;
	}

}
