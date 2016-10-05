package com.finflux.organisation.transaction.authentication.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticaionTransactionTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthentication;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class TransactionAuthenticationDataValidator {

	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public TransactionAuthenticationDataValidator(final FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	public void validateForCreate(final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();

		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_REQUEST_DATA_PARAMETER);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_RESOURCE_NAME);

		JsonElement element = this.fromJsonHelper.parse(json);
		JsonObject object = element.getAsJsonObject();

		final Locale locale = this.fromJsonHelper.extractLocaleParameter(object);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.LOCALE).value(locale).notNull();

		final Integer transactionTypeId = this.fromJsonHelper.extractIntegerNamed("transactionTypeId", element, locale);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID)
				.value(transactionTypeId).notNull().integerGreaterThanZero();

		final Integer portfolioTypeId = this.fromJsonHelper.extractIntegerNamed("portfolioTypeId", element, locale);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID)
				.value(portfolioTypeId).notNull().integerGreaterThanZero();

		final Long paymentTypeId = this.fromJsonHelper.extractLongNamed("paymentTypeId", element);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID)
				.value(paymentTypeId).notNull().longGreaterThanZero();

		final BigDecimal amountGreaterThan = this.fromJsonHelper.extractBigDecimalNamed("amount", element, locale);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.AMOUNT).value(amountGreaterThan)
				.notNull().zeroOrPositiveAmount();

		final Long authenticationTypeId = this.fromJsonHelper
				.extractLongNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID, element);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)
				.value(authenticationTypeId).notNull().longGreaterThanZero();

		// check of the transaction type actually applies to the product type
		// provided.
		// eg., Loan product can have transaction type disbursement or
		// re-payment ..
		checkForValidTransactionTypes(portfolioTypeId, transactionTypeId, dataValidatorBuilder);

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void checkForValidTransactionTypes(final Integer portfolioTypeId, final Integer transactionTypeId,
			final DataValidatorBuilder dataValidatorBuilder) {
		// product type
		SupportedAuthenticationPortfolioTypes appliesTo = SupportedAuthenticationPortfolioTypes
				.fromInt(portfolioTypeId);
		final Integer[] transactionTypeValues = getTransactionTypesForProductType(appliesTo);

		// check for valid transaction type for the product
		if (transactionTypeValues != null) {
			SupportedAuthenticaionTransactionTypes supportedAuthenticaionTransactionTypes = SupportedAuthenticaionTransactionTypes.INVALID;
			for (Integer value : transactionTypeValues) {
				if (value == transactionTypeId) {
					supportedAuthenticaionTransactionTypes = SupportedAuthenticaionTransactionTypes.fromInt(value);
				}
			}
			if (supportedAuthenticaionTransactionTypes.equals(SupportedAuthenticaionTransactionTypes.INVALID)) {
				dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID)
						.value(SupportedAuthenticaionTransactionTypes.fromInt(transactionTypeId))
						.failWithCode(appliesTo + ".transactionType.mismatch",
								"transaction type mismatch for the product type " + appliesTo);
			}
		} else {
			dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE)
					.value(appliesTo).failWithCode("Invalid.productType", "Please provide a proper product type");
		}
	}

	// get the transaction types associated with the product type
	private Integer[] getTransactionTypesForProductType(final SupportedAuthenticationPortfolioTypes appliesTo) {
		final Integer[] transactionTypeValues;
		// get the transaction types associated with the product type
		switch (appliesTo) {
		case LOANS:
			transactionTypeValues = (Integer[]) SupportedAuthenticaionTransactionTypes.validLoanValues();
			break;
		case SAVINGS:
			transactionTypeValues = (Integer[]) SupportedAuthenticaionTransactionTypes.validSavingValues();
			break;
		default:
			transactionTypeValues = null;
			break;
		}
		return transactionTypeValues;
	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			//
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
	}

	public void validateTransactionTypeForUpdate(final JsonCommand command,
			final TransactionAuthentication transactionAuthentication) {
		final JsonElement element = this.fromJsonHelper.parse(command.json());
		final Locale locale = this.fromJsonHelper.extractLocaleParameter(element.getAsJsonObject());
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_RESOURCE_NAME);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.LOCALE).value(locale).notNull();

		Integer transactionTypeId = null;
		if (command.hasParameter(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID)) {
			transactionTypeId = this.fromJsonHelper.extractIntegerNamed("transactionTypeId", element, locale);
			dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID)
					.value(transactionTypeId).notNull().integerGreaterThanZero();
		} else {
			transactionTypeId = transactionAuthentication.getTransactionTypeId();
		}

		Integer portfolioTypeId;
		if (command.hasParameter(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID)) {
			portfolioTypeId = this.fromJsonHelper.extractIntegerNamed("portfolioTypeId", element, locale);
			dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE)
					.value(portfolioTypeId).notNull().integerGreaterThanZero();
		} else {
			portfolioTypeId = transactionAuthentication.getPortfolioType();
		}

		checkForValidTransactionTypes(portfolioTypeId, transactionTypeId, dataValidatorBuilder);

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	public void validateForDisbursement(final JsonCommand command) {
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_RESOURCE_NAME);

		final Long paymentTypeId = command
				.longValueOfParameterNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID)
				.value(paymentTypeId).notNull().integerGreaterThanZero();

		final BigDecimal amountGreaterThan = command
				.bigDecimalValueOfParameterNamed(TransactionAuthenticationApiConstants.TRANSACTION_AMOUNT);
		dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.TRANSACTION_AMOUNT)
				.value(amountGreaterThan).zeroOrPositiveAmount();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

}
