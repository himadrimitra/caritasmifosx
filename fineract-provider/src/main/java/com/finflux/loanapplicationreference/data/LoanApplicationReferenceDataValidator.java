package com.finflux.loanapplicationreference.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.loanapplicationreference.api.LoanApplicationReferenceApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class LoanApplicationReferenceDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanApplicationReferenceDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanApplicationReferenceApiConstants.CREATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String accountType = this.fromApiJsonHelper.extractStringNamed(LoanApplicationReferenceApiConstants.accountTypeParamName,
                element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.accountTypeParamName).value(accountType).notBlank()
                .isOneOfTheseValues(AccountType.codeNames());

        if (AccountType.fromName(accountType).isIndividualAccount()) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).notNull()
                    .longGreaterThanZero();
        } else if (AccountType.fromName(accountType).isGroupAccount()) {
            final Long groupId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.groupIdParamName).value(groupId).notNull()
                    .longGreaterThanZero();

            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).ignoreIfNull()
                    .longGreaterThanZero();
        } else if (AccountType.fromName(accountType).isJLGAccount()) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).notNull()
                    .longGreaterThanZero();

            final Long groupId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.groupIdParamName).value(groupId).notNull()
                    .longGreaterThanZero();
        } else {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).ignoreIfNull()
                    .longGreaterThanZero();

            final Long groupId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.groupIdParamName).value(groupId).ignoreIfNull()
                    .longGreaterThanZero();
        }
        
		final String expectedDisbursalPaymentTypeParameterName = LoanApplicationReferenceApiConstants.expectedDisbursalPaymentTypeParamName;
		if (this.fromApiJsonHelper.parameterExists(expectedDisbursalPaymentTypeParameterName, element)) {
			final Long expectedDisbursalPaymentTypeId = this.fromApiJsonHelper
					.extractLongNamed(expectedDisbursalPaymentTypeParameterName, element);
			baseDataValidator.reset().parameter(expectedDisbursalPaymentTypeParameterName)
					.value(expectedDisbursalPaymentTypeId).ignoreIfNull().integerGreaterThanZero();
		}

		final String expectedRepaymentPaymentTypeParameterName = LoanApplicationReferenceApiConstants.expectedRepaymentPaymentTypeParamName;
		if (this.fromApiJsonHelper.parameterExists(expectedDisbursalPaymentTypeParameterName, element)) {
			final Long expectedRepaymentPaymentTypeId = this.fromApiJsonHelper
					.extractLongNamed(expectedRepaymentPaymentTypeParameterName, element);
			baseDataValidator.reset().parameter(expectedRepaymentPaymentTypeParameterName)
					.value(expectedRepaymentPaymentTypeId).ignoreIfNull().integerGreaterThanZero();
		}

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName,
                element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanProductIdParamName).value(loanProductId).notNull()
                .longGreaterThanZero();

        final Long loanOfficerId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.loanOfficerIdParamName,
                element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanOfficerIdParamName).value(loanOfficerId)
                .ignoreIfNull().longGreaterThanZero();

        final Integer loanPurposeId = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.loanPurposeIdParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanPurposeIdParamName).value(loanPurposeId).notNull()
                .integerGreaterThanZero();

        final BigDecimal loanAmountRequested = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.loanAmountRequestedParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanAmountRequestedParamName).value(loanAmountRequested)
                .notBlank().positiveAmount();

        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName).value(numberOfRepayments)
                .notNull().integerGreaterThanZero();

        final Integer repaymentPeriodFrequencyEnum = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName)
                .value(repaymentPeriodFrequencyEnum).notNull().isOneOfTheseValues(PeriodFrequencyType.integerValues());

        final Integer repayEvery = this.fromApiJsonHelper.extractIntegerNamed(LoanApplicationReferenceApiConstants.repayEveryParamName,
                element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repayEveryParamName).value(repayEvery).notNull()
                .integerGreaterThanZero();

        final Integer termPeriodFrequencyEnum = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName)
                .value(termPeriodFrequencyEnum).notNull().isOneOfTheseValues(PeriodFrequencyType.integerValues());

        final Integer termFrequency = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.termFrequencyParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.termFrequencyParamName).value(termFrequency).notNull()
                .integerGreaterThanZero();

        final BigDecimal fixedEmiAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.fixedEmiAmountParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName).value(fixedEmiAmount)
                .ignoreIfNull().zeroOrPositiveAmount();

        final Integer noOfTranche = this.fromApiJsonHelper.extractIntegerNamed(LoanApplicationReferenceApiConstants.noOfTrancheParamName,
                element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.noOfTrancheParamName).value(noOfTranche).ignoreIfNull()
                .integerGreaterThanZero();

        final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanApplicationReferenceApiConstants.submittedOnDateParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();

        final String chargesParameterName = "charges";
        if (element.isJsonObject() && this.fromApiJsonHelper.parameterExists(chargesParameterName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            if (topLevelJsonElement.get(chargesParameterName).isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 1; i <= array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i - 1).getAsJsonObject();

                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    baseDataValidator.reset().parameter("charges").parameterAtIndexArray("chargeId", i).value(chargeId).notNull()
                            .longGreaterThanZero();

                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    baseDataValidator.reset().parameter("charges").parameterAtIndexArray("amount", i).value(amount).notNull()
                            .positiveAmount();

                    this.fromApiJsonHelper.extractLocalDateNamed("dueDate", loanChargeElement, dateFormat, locale);
                }
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateLoanAmountRequestedMinMaxConstraint(final String json, final LoanProduct loanProduct) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanApplicationReferenceApiConstants.CREATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String loanAmountRequestedParameterName = LoanApplicationReferenceApiConstants.loanAmountRequestedParamName;
        final BigDecimal loanAmountRequested = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(loanAmountRequestedParameterName,
                element);

        final BigDecimal minPrincipalAmount = loanProduct.getMinPrincipalAmount().getAmount();
        final BigDecimal maxPrincipalAmount = loanProduct.getMaxPrincipalAmount().getAmount();

        if ((minPrincipalAmount != null && minPrincipalAmount.compareTo(BigDecimal.ZERO) == 1)
                && (maxPrincipalAmount != null && maxPrincipalAmount.compareTo(BigDecimal.ZERO) == 1)) {
            baseDataValidator.reset().parameter(loanAmountRequestedParameterName).value(loanAmountRequested)
                    .inMinAndMaxAmountRange(minPrincipalAmount, maxPrincipalAmount);
        } else {
            if (minPrincipalAmount != null && minPrincipalAmount.compareTo(BigDecimal.ZERO) == 1) {
                baseDataValidator.reset().parameter(loanAmountRequestedParameterName).value(loanAmountRequested)
                        .notLessThanMin(minPrincipalAmount);
            } else if (maxPrincipalAmount != null && maxPrincipalAmount.compareTo(BigDecimal.ZERO) == 1) {
                baseDataValidator.reset().parameter(loanAmountRequestedParameterName).value(loanAmountRequested)
                        .notGreaterThanMax(maxPrincipalAmount);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanApplicationReferenceApiConstants.UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String accountType = this.fromApiJsonHelper.extractStringNamed(LoanApplicationReferenceApiConstants.accountTypeParamName,
                element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.accountTypeParamName).value(accountType).notBlank()
                .isOneOfTheseValues(AccountType.codeNames());

        if (AccountType.fromName(accountType).isIndividualAccount()) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).notNull()
                    .longGreaterThanZero();
        } else if (AccountType.fromName(accountType).isGroupAccount()) {
            final Long groupId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.groupIdParamName).value(groupId).notNull()
                    .longGreaterThanZero();

            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).ignoreIfNull()
                    .longGreaterThanZero();
        } else if (AccountType.fromName(accountType).isJLGAccount()) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).notNull()
                    .longGreaterThanZero();

            final Long groupId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.groupIdParamName).value(groupId).notNull()
                    .longGreaterThanZero();
        } else {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.clientIdParamName).value(clientId).ignoreIfNull()
                    .longGreaterThanZero();

            final Long groupId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.groupIdParamName).value(groupId).ignoreIfNull()
                    .longGreaterThanZero();
        }

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName,
                element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanProductIdParamName).value(loanProductId).notNull()
                .longGreaterThanZero();

        final Long loanOfficerId = this.fromApiJsonHelper.extractLongNamed(LoanApplicationReferenceApiConstants.loanOfficerIdParamName,
                element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanOfficerIdParamName).value(loanOfficerId)
                .ignoreIfNull().longGreaterThanZero();

        final Integer loanPurposeId = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.loanPurposeIdParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanPurposeIdParamName).value(loanPurposeId).notNull()
                .integerGreaterThanZero();

        final BigDecimal loanAmountRequested = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.loanAmountRequestedParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanAmountRequestedParamName).value(loanAmountRequested)
                .notBlank().positiveAmount();

        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName).value(numberOfRepayments)
                .notNull().integerGreaterThanZero();

        final Integer repaymentPeriodFrequencyEnum = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName)
                .value(repaymentPeriodFrequencyEnum).notNull().isOneOfTheseValues(PeriodFrequencyType.integerValues());

        final Integer repayEvery = this.fromApiJsonHelper.extractIntegerNamed(LoanApplicationReferenceApiConstants.repayEveryParamName,
                element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repayEveryParamName).value(repayEvery).notNull()
                .integerGreaterThanZero();

        final Integer termPeriodFrequencyEnum = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName)
                .value(termPeriodFrequencyEnum).notNull().isOneOfTheseValues(PeriodFrequencyType.integerValues());

        final Integer termFrequency = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.termFrequencyParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.termFrequencyParamName).value(termFrequency).notNull()
                .integerGreaterThanZero();

        final BigDecimal fixedEmiAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.fixedEmiAmountParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName).value(fixedEmiAmount)
                .ignoreIfNull().zeroOrPositiveAmount();

        final Integer noOfTranche = this.fromApiJsonHelper.extractIntegerNamed(LoanApplicationReferenceApiConstants.noOfTrancheParamName,
                element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.noOfTrancheParamName).value(noOfTranche).ignoreIfNull()
                .integerGreaterThanZero();

        final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanApplicationReferenceApiConstants.submittedOnDateParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();
		
        final String expectedDisbursalPaymentTypeParameterName = LoanApplicationReferenceApiConstants.expectedDisbursalPaymentTypeParamName;
		if (this.fromApiJsonHelper.parameterExists(expectedDisbursalPaymentTypeParameterName, element)) {
			final Long expectedDisbursalPaymentTypeId = this.fromApiJsonHelper
					.extractLongNamed(expectedDisbursalPaymentTypeParameterName, element);
			baseDataValidator.reset().parameter(expectedDisbursalPaymentTypeParameterName)
					.value(expectedDisbursalPaymentTypeId).ignoreIfNull();
		}
		final String expectedRepaymentPaymentTypeParameterName = LoanApplicationReferenceApiConstants.expectedRepaymentPaymentTypeParamName;
		if (this.fromApiJsonHelper.parameterExists(expectedDisbursalPaymentTypeParameterName, element)) {
			final Long expectedRepaymentPaymentTypeId = this.fromApiJsonHelper
					.extractLongNamed(expectedRepaymentPaymentTypeParameterName, element);
			baseDataValidator.reset().parameter(expectedRepaymentPaymentTypeParameterName)
					.value(expectedRepaymentPaymentTypeId).ignoreIfNull();
		}

        // charges
        final String chargesParameterName = "charges";
        if (element.isJsonObject() && this.fromApiJsonHelper.parameterExists(chargesParameterName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            if (topLevelJsonElement.get(chargesParameterName).isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 1; i <= array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i - 1).getAsJsonObject();

                    final Long loanAppChargeId = this.fromApiJsonHelper.extractLongNamed("loanAppChargeId", loanChargeElement);
                    baseDataValidator.reset().parameter("loanAppChargeId").parameterAtIndexArray("i", i).value(loanAppChargeId)
                            .ignoreIfNull().longGreaterThanZero();

                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    baseDataValidator.reset().parameter("charges").parameterAtIndexArray("chargeId", i).value(chargeId).notNull()
                            .longGreaterThanZero();

                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    baseDataValidator.reset().parameter("charges").parameterAtIndexArray("amount", i).value(amount).notNull()
                            .positiveAmount();

                    this.fromApiJsonHelper.extractLocalDateNamed("dueDate", loanChargeElement, dateFormat, locale);
                }
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForApprove(final String json, final LocalDate submittedOnDate) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanApplicationReferenceApiConstants.APPROVE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final BigDecimal loanAmountApproved = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.loanAmountApprovedParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName).value(loanAmountApproved)
                .notBlank().positiveAmount();

        final LocalDate expectedDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName)
                .value(expectedDisbursementDate).notNull();

        final LocalDate repaymentsStartingFromDate = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName)
                .value(repaymentsStartingFromDate).ignoreIfNull().validateDateAfter(expectedDisbursementDate);

        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName).value(numberOfRepayments)
                .notNull().integerGreaterThanZero();

        final Integer repaymentPeriodFrequencyEnum = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName)
                .value(repaymentPeriodFrequencyEnum).notNull().isOneOfTheseValues(PeriodFrequencyType.integerValues());

        final Integer repayEvery = this.fromApiJsonHelper.extractIntegerNamed(LoanApplicationReferenceApiConstants.repayEveryParamName,
                element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.repayEveryParamName).value(repayEvery).notNull()
                .integerGreaterThanZero();

        final Integer termPeriodFrequencyEnum = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName)
                .value(termPeriodFrequencyEnum).notNull().isOneOfTheseValues(PeriodFrequencyType.integerValues());

        final Integer termFrequency = this.fromApiJsonHelper.extractIntegerNamed(
                LoanApplicationReferenceApiConstants.termFrequencyParamName, element, locale);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.termFrequencyParamName).value(termFrequency).notNull()
                .integerGreaterThanZero();

        final BigDecimal fixedEmiAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.fixedEmiAmountParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName).value(fixedEmiAmount)
                .ignoreIfNull().zeroOrPositiveAmount();

        final BigDecimal maxOutstandingLoanBalance = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApplicationReferenceApiConstants.maxOutstandingLoanBalanceParamName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.maxOutstandingLoanBalanceParamName)
                .value(maxOutstandingLoanBalance).ignoreIfNull().zeroOrPositiveAmount();

        final String chargesParameterName = "charges";
        if (element.isJsonObject() && this.fromApiJsonHelper.parameterExists(chargesParameterName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            if (topLevelJsonElement.get(chargesParameterName).isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 1; i <= array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i - 1).getAsJsonObject();

                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    baseDataValidator.reset().parameter("charges").parameterAtIndexArray("chargeId", i).value(chargeId).notNull()
                            .longGreaterThanZero();

                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    baseDataValidator.reset().parameter("charges").parameterAtIndexArray("amount", i).value(amount).notNull()
                            .positiveAmount();

                    this.fromApiJsonHelper.extractLocalDateNamed("dueDate", loanChargeElement, dateFormat, locale);
                }
            }
        }
        
        if (this.fromApiJsonHelper.parameterExists(LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName,
                element)) {
            final JsonArray trancheDataArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                    LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName, element);
            baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName)
                    .value(trancheDataArray).notBlank().jsonArrayNotEmpty();

            for (final JsonElement trancheData : trancheDataArray) {
                final BigDecimal trancheAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        LoanApplicationReferenceApiConstants.trancheAmountParamName, trancheData);
                baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.trancheAmountParamName).value(trancheAmount)
                        .notBlank().positiveAmount();

                /*
                 * final BigDecimal fixedEmiAmount =
                 * this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                 * LoanApplicationReferenceApiConstants.fixedEmiAmountParamName,
                 * trancheData); baseDataValidator.reset().parameter(
                 * LoanApplicationReferenceApiConstants
                 * .fixedEmiAmountParamName).value(fixedEmiAmount)
                 * .ignoreIfNull().zeroOrPositiveAmount();
                 */

                final LocalDate expectedTrancheDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(
                        LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName, trancheData);
                baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)
                        .value(expectedTrancheDisbursementDate).notNull();
            }

            validateLoanMultiDisbursementdate(element, baseDataValidator, expectedDisbursementDate, loanAmountApproved);
        }

        final LocalDate approvedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanApplicationReferenceApiConstants.approvedOnDateParaName, element);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.approvedOnDateParaName).value(approvedOnDate).notNull()
                .validateDateAfter(submittedOnDate).validateDateBeforeOrEqual(expectedDisbursementDate);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateLoanMultiDisbursementdate(final JsonElement element, final DataValidatorBuilder baseDataValidator,
            final LocalDate expectedDisbursementDate, final BigDecimal loanAmountApproved) {

        this.validateDisbursementsAreDatewiseOrdered(element, baseDataValidator);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
        if (this.fromApiJsonHelper.parameterExists(LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName,
                element) && expectedDisbursementDate != null && loanAmountApproved != null) {
            BigDecimal totalTrancheAmount = BigDecimal.ZERO;
            boolean isFirstinstallmentOnExpectedDisbursementDate = false;
            final JsonArray variationArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                    LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName, element);
            final List<LocalDate> expectedTrancheDisbursementDates = new ArrayList<>();
            if (variationArray != null && variationArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = variationArray.get(i).getAsJsonObject();
                    if (jsonObject.has(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)
                            && jsonObject.has(LoanApplicationReferenceApiConstants.trancheAmountParamName)) {
                        LocalDate expectedTrancheDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(
                                LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName, jsonObject, dateFormat,
                                locale);
                        if (expectedTrancheDisbursementDates.contains(expectedTrancheDisbursementDate)) {
                            baseDataValidator.reset()
                                    .parameter(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)
                                    .failWithCode(LoanApiConstants.DISBURSEMENT_DATE_UNIQUE_ERROR);
                        }
                        if (expectedTrancheDisbursementDate.isBefore(expectedDisbursementDate)) {
                            baseDataValidator.reset()
                                    .parameter(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)
                                    .failWithCode(LoanApiConstants.DISBURSEMENT_DATE_BEFORE_ERROR);
                        }
                        expectedTrancheDisbursementDates.add(expectedTrancheDisbursementDate);

                        BigDecimal trancheAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                                LoanApplicationReferenceApiConstants.trancheAmountParamName, jsonObject, locale);
                        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.trancheAmountParamName)
                                .parameterAtIndexArray(LoanApplicationReferenceApiConstants.trancheAmountParamName, i).value(trancheAmount)
                                .notBlank();
                        if (trancheAmount != null) {
                            totalTrancheAmount = totalTrancheAmount.add(trancheAmount);
                        }

                        baseDataValidator.reset()
                                .parameter(LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName)
                                .parameterAtIndexArray(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName, i)
                                .value(expectedTrancheDisbursementDate).notNull();

                        if (expectedDisbursementDate.equals(expectedTrancheDisbursementDate)) {
                            isFirstinstallmentOnExpectedDisbursementDate = true;
                        }

                    }
                    i++;
                } while (i < variationArray.size());
                if (!isFirstinstallmentOnExpectedDisbursementDate) {
                    baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)
                            .failWithCode(LoanApiConstants.DISBURSEMENT_DATE_START_WITH_ERROR);
                }

                if (totalTrancheAmount.compareTo(loanAmountApproved) == 1) {
                    baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.trancheAmountParamName)
                            .failWithCode(LoanApiConstants.APPROVED_AMOUNT_IS_LESS_THAN_SUM_OF_TRANCHES);
                }
            }

        }
    }

    private void validateDisbursementsAreDatewiseOrdered(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
        final JsonArray variationArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                LoanApplicationReferenceApiConstants.loanApplicationSanctionTrancheDatasParamName, element);
        if (variationArray != null) {
            for (int i = 0; i < variationArray.size(); i++) {
                final JsonObject jsonObject1 = variationArray.get(i).getAsJsonObject();
                if (jsonObject1.has(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)) {
                    LocalDate date1 = this.fromApiJsonHelper.extractLocalDateNamed(
                            LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName, jsonObject1, dateFormat, locale);

                    for (int j = i + 1; j < variationArray.size(); j++) {
                        final JsonObject jsonObject2 = variationArray.get(j).getAsJsonObject();
                        if (jsonObject2.has(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)) {
                            LocalDate date2 = this.fromApiJsonHelper.extractLocalDateNamed(
                                    LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName, jsonObject2, dateFormat,
                                    locale);
                            if (date1.isAfter(date2)) {
                                baseDataValidator.reset()
                                        .parameter(LoanApplicationReferenceApiConstants.expectedTrancheDisbursementDateParaName)
                                        .failWithCode(LoanApiConstants.DISBURSEMENT_DATES_NOT_IN_ORDER);
                            }
                        }
                    }
                }

            }
        }
    }

    public void validateForDisburse(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject object = element.getAsJsonObject();

        final JsonObject submitApplication = object.getAsJsonObject("submitApplication");
        final JsonElement submitApplicationElement = this.fromApiJsonHelper.parse(submitApplication.toString());
        final BigDecimal loanAmountApproved = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed("principal", submitApplicationElement);
        baseDataValidator.reset().parameter(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName).value(loanAmountApproved)
                .notBlank().positiveAmount();

        final JsonObject disburse = object.getAsJsonObject("disburse");
        final JsonElement disburseElement = this.fromApiJsonHelper.parse(disburse.toString());
        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", disburseElement);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notBlank().positiveAmount();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

        if (transactionAmount.compareTo(loanAmountApproved) > 0) {
            final String defaultUserMessage = "Disbursal amount cannot be greater than loan application approved amount.";
            throw new LoanApplicationDateException("disbursal.amount.cannot.be.greater.than.loan.application.approved.amount",
                    defaultUserMessage, transactionAmount.toString(), loanAmountApproved.toString());
        }

    }

    public void validateLoanTermAndRepaidEveryValues(final Integer minimumNoOfRepayments, final Integer maximumNoOfRepayments,
            final Integer actualNumberOfRepayments) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        /**
         * For multi-disbursal loans where schedules are auto-generated based on
         * a fixed EMI, ensure the number of repayments is within the
         * permissible range defined by the loan product
         **/

        // validate actual number of repayments is > minimum number of
        // repayments
        if (minimumNoOfRepayments != null && minimumNoOfRepayments != 0 && actualNumberOfRepayments < minimumNoOfRepayments) {
            final ApiParameterError error = ApiParameterError.generalError(
                    "validation.msg.loan.numberOfRepayments.lesser.than.minimumNumberOfRepayments",
                    "The total number of calculated repayments for this loan " + actualNumberOfRepayments
                            + " is lesser than the allowed minimum of " + minimumNoOfRepayments, actualNumberOfRepayments,
                    minimumNoOfRepayments);
            dataValidationErrors.add(error);
        }

        // validate actual number of repayments is < maximum number of
        // repayments
        if (maximumNoOfRepayments != null && maximumNoOfRepayments != 0 && actualNumberOfRepayments > maximumNoOfRepayments) {
            final ApiParameterError error = ApiParameterError.generalError(
                    "validation.msg.loan.numberOfRepayments.greater.than.maximumNumberOfRepayments",
                    "The total number of calculated repayments for this loan " + actualNumberOfRepayments
                            + " is greater than the allowed maximum of " + maximumNoOfRepayments, actualNumberOfRepayments,
                    maximumNoOfRepayments);
            dataValidationErrors.add(error);
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
