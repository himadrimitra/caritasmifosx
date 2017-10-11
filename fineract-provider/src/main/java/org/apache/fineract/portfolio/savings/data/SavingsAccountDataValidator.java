/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.savings.data;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.accountNoParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.activeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.allowOverdraftParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.amountParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.chargeIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.chargesParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.clientIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.dateParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.externalIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.feeIntervalParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.feeOnMonthDayParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.fieldOfficerIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.groupIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestCalculationDaysInYearTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestCalculationTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestCompoundingPeriodTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestPostingPeriodTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.lockinPeriodFrequencyParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.lockinPeriodFrequencyTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.minOverdraftForInterestCalculationParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.minRequiredOpeningBalanceParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.nominalAnnualInterestRateOverdraftParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.nominalAnnualInterestRateParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.overdraftLimitParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.productIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.submittedOnDateParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.withHoldTaxParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.withdrawalFeeForTransfersParamName;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.apache.fineract.portfolio.savings.SavingsDpLimitCalculationType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationType;
import org.apache.fineract.portfolio.savings.SavingsPostingInterestPeriodType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class SavingsAccountDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SavingsAccountDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreateActiveSavingsApplication(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                SavingsApiConstants.SAVINGS_ACCOUNT_CREATE_OR_ACTIVATE_DATA_PARAMETER);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long clientId = this.fromApiJsonHelper.extractLongNamed(clientIdParamName, element);
        baseDataValidator.reset().parameter(clientIdParamName).value(clientId).notNull().longGreaterThanZero();

        final Long productId = this.fromApiJsonHelper.extractLongNamed(productIdParamName, element);
        baseDataValidator.reset().parameter(productIdParamName).value(productId).notNull().integerGreaterThanZero();

        final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(dateParamName, element);
        baseDataValidator.reset().parameter(dateParamName).value(submittedOnDate).notNull();

        if (this.fromApiJsonHelper.parameterExists(activeParamName, element)) {
            final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(activeParamName, element);
            baseDataValidator.reset().parameter(activeParamName).value(isActive).ignoreIfNull().validateForBooleanValue();
        }

        final Long fieldOfficerId = this.fromApiJsonHelper.extractLongNamed(fieldOfficerIdParamName, element);
        baseDataValidator.reset().parameter(fieldOfficerIdParamName).value(fieldOfficerId);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForSubmit(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SavingsApiConstants.SAVINGS_ACCOUNT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long clientId = this.fromApiJsonHelper.extractLongNamed(clientIdParamName, element);
        if (clientId != null) {
            baseDataValidator.reset().parameter(clientIdParamName).value(clientId).longGreaterThanZero();
        }

        final Long groupId = this.fromApiJsonHelper.extractLongNamed(groupIdParamName, element);
        if (groupId != null) {
            baseDataValidator.reset().parameter(groupIdParamName).value(groupId).longGreaterThanZero();
        }

        if (clientId == null && groupId == null) {
            baseDataValidator.reset().parameter(clientIdParamName).value(clientId).notNull().integerGreaterThanZero();
        }

        final Long productId = this.fromApiJsonHelper.extractLongNamed(productIdParamName, element);
        baseDataValidator.reset().parameter(productIdParamName).value(productId).notNull().integerGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(fieldOfficerIdParamName, element)) {
            final Long fieldOfficerId = this.fromApiJsonHelper.extractLongNamed(fieldOfficerIdParamName, element);
            baseDataValidator.reset().parameter(fieldOfficerIdParamName).value(fieldOfficerId).ignoreIfNull().integerGreaterThanZero();
        }

        final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(submittedOnDateParamName, element);
        baseDataValidator.reset().parameter(submittedOnDateParamName).value(submittedOnDate).notNull();

        if (this.fromApiJsonHelper.parameterExists(accountNoParamName, element)) {
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(accountNoParamName, element);
            baseDataValidator.reset().parameter(accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (this.fromApiJsonHelper.parameterExists(externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(externalIdParamName, element);
            baseDataValidator.reset().parameter(externalIdParamName).value(externalId).notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(nominalAnnualInterestRateParamName, element)) {
            final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(nominalAnnualInterestRateParamName,
                    element);
            baseDataValidator.reset().parameter(nominalAnnualInterestRateParamName).value(interestRate).notNull().zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(interestCompoundingPeriodTypeParamName, element)) {
            final Integer interestCompoundingPeriodType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(interestCompoundingPeriodTypeParamName, element);
            baseDataValidator.reset().parameter(interestCompoundingPeriodTypeParamName).value(interestCompoundingPeriodType).notNull()
                    .isOneOfTheseValues(SavingsCompoundingInterestPeriodType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(interestPostingPeriodTypeParamName, element)) {
            final Integer interestPostingPeriodType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(interestPostingPeriodTypeParamName, element);
            baseDataValidator.reset().parameter(interestPostingPeriodTypeParamName).value(interestPostingPeriodType).notNull()
                    .isOneOfTheseValues(SavingsPostingInterestPeriodType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(interestCalculationTypeParamName, element)) {
            final Integer interestCalculationType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(interestCalculationTypeParamName,
                    element);
            baseDataValidator.reset().parameter(interestCalculationTypeParamName).value(interestCalculationType).notNull()
                    .isOneOfTheseValues(SavingsInterestCalculationType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(interestCalculationDaysInYearTypeParamName, element)) {
            final Integer interestCalculationDaysInYearType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(interestCalculationDaysInYearTypeParamName, element);
            baseDataValidator.reset().parameter(interestCalculationDaysInYearTypeParamName).value(interestCalculationDaysInYearType)
                    .notNull().isOneOfTheseValues(SavingsInterestCalculationDaysInYearType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(minRequiredOpeningBalanceParamName, element)) {
            final BigDecimal minOpeningBalance = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minRequiredOpeningBalanceParamName,
                    element);
            baseDataValidator.reset().parameter(minRequiredOpeningBalanceParamName).value(minOpeningBalance).zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyParamName, element)) {

            final Integer lockinPeriodFrequency = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(lockinPeriodFrequencyParamName,
                    element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyParamName).value(lockinPeriodFrequency).integerZeroOrGreater();

            if (lockinPeriodFrequency != null) {
                final Integer lockinPeriodFrequencyType = this.fromApiJsonHelper
                        .extractIntegerSansLocaleNamed(lockinPeriodFrequencyTypeParamName, element);
                baseDataValidator.reset().parameter(lockinPeriodFrequencyTypeParamName).value(lockinPeriodFrequencyType).notNull()
                        .inMinMaxRange(0, 3);
            }
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyTypeParamName, element)) {
            final Integer lockinPeriodFrequencyType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(lockinPeriodFrequencyTypeParamName, element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyTypeParamName).value(lockinPeriodFrequencyType).inMinMaxRange(0, 3);

            if (lockinPeriodFrequencyType != null) {
                final Integer lockinPeriodFrequency = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(lockinPeriodFrequencyParamName,
                        element);
                baseDataValidator.reset().parameter(lockinPeriodFrequencyParamName).value(lockinPeriodFrequency).notNull()
                        .integerZeroOrGreater();
            }
        }

        if (this.fromApiJsonHelper.parameterExists(withdrawalFeeForTransfersParamName, element)) {
            final Boolean isWithdrawalFeeApplicableForTransfers = this.fromApiJsonHelper
                    .extractBooleanNamed(withdrawalFeeForTransfersParamName, element);
            baseDataValidator.reset().parameter(withdrawalFeeForTransfersParamName).value(isWithdrawalFeeApplicableForTransfers)
                    .ignoreIfNull().validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists("releaseguarantor", element)) {
            final Boolean isReleaseGuarantor = this.fromApiJsonHelper.extractBooleanNamed("releaseguarantor", element);
            baseDataValidator.reset().parameter("releaseguarantor").value(isReleaseGuarantor).ignoreIfNull().validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists(withHoldTaxParamName, element)) {
            final String withHoldTax = this.fromApiJsonHelper.extractStringNamed(withHoldTaxParamName, element);
            baseDataValidator.reset().parameter(withHoldTaxParamName).value(withHoldTax).ignoreIfNull().validateForBooleanValue();
        }

        validateSavingsCharges(element, baseDataValidator);

        validateOverdraftParams(baseDataValidator, element);

        validateDPParamsForSubmit(baseDataValidator, element);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateDPParamsForSubmit(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.allowOverdraftParamName, element)
                && this.fromApiJsonHelper.parameterExists(SavingsApiConstants.allowDpLimitParamName, element)) {
            final boolean allowOverdraft = this.fromApiJsonHelper.extractBooleanNamed(SavingsApiConstants.allowOverdraftParamName, element);
            final boolean allowDpLimit = this.fromApiJsonHelper.extractBooleanNamed(SavingsApiConstants.allowDpLimitParamName, element);
            if (allowOverdraft && allowDpLimit) {

                final BigDecimal overdraftLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(overdraftLimitParamName, element);
                baseDataValidator.reset().parameter(overdraftLimitParamName).value(overdraftLimit).notNull().positiveAmount();

                final BigDecimal dpLimitAmount = this.fromApiJsonHelper
                        .extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpLimitAmountParamName, element);
                baseDataValidator.reset().parameter(SavingsApiConstants.dpLimitAmountParamName).value(dpLimitAmount).notNull()
                        .positiveAmount();

                validateDpLimitAmount(baseDataValidator, element);

                final Integer savingsDpLimitCalculationType = this.fromApiJsonHelper
                        .extractIntegerSansLocaleNamed(SavingsApiConstants.savingsDpLimitCalculationTypeParamName, element);
                baseDataValidator.reset().parameter(SavingsApiConstants.savingsDpLimitCalculationTypeParamName)
                        .value(savingsDpLimitCalculationType).notNull().isOneOfTheseValues(SavingsDpLimitCalculationType.FLAT.getValue(),
                                SavingsDpLimitCalculationType.PERCENT_OF_AMOUNT.getValue());

                final Integer dpDuration = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(SavingsApiConstants.dpDurationParamName,
                        element);
                baseDataValidator.reset().parameter(SavingsApiConstants.dpDurationParamName).value(dpDuration).notNull()
                        .integerGreaterThanZero();

                final BigDecimal dpCalculateOnAmount = this.fromApiJsonHelper
                        .extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpCalculateOnAmountParamName, element);
                baseDataValidator.reset().parameter(SavingsApiConstants.dpCalculateOnAmountParamName).value(dpCalculateOnAmount).notNull()
                        .positiveAmount();

                validateDpCalculateOnAmount(baseDataValidator, element);

                final LocalDate dpStartDate = this.fromApiJsonHelper.extractLocalDateNamed(SavingsApiConstants.dpStartDateParamName,
                        element);
                baseDataValidator.reset().parameter(SavingsApiConstants.dpStartDateParamName).value(dpStartDate).notNull();
            }
        }
    }

    private void validateDpCalculateOnAmount(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        BigDecimal dpLimitAmount = BigDecimal.ZERO;
        BigDecimal dpCalculateOnAmount = BigDecimal.ZERO;
        if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpLimitAmountParamName, element)) {
            dpLimitAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpLimitAmountParamName, element);
        }
        if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpCalculateOnAmountParamName, element)) {
            dpCalculateOnAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpCalculateOnAmountParamName,
                    element);
        }
        final Integer savingsDpLimitCalculationType = this.fromApiJsonHelper
                .extractIntegerSansLocaleNamed(SavingsApiConstants.savingsDpLimitCalculationTypeParamName, element);
        if (SavingsDpLimitCalculationType.fromInt(savingsDpLimitCalculationType).isPercentOfAmount()) {
            baseDataValidator.reset().parameter(SavingsApiConstants.dpCalculateOnAmountParamName).value(dpCalculateOnAmount)
                    .notGreaterThanMax(BigDecimal.valueOf(100.00));
        } else {
            if (MathUtility.isGreater(dpCalculateOnAmount, dpLimitAmount)) {
                final String errorCode = "should.not.be.greater.than.dpLimitAmount";
                final String userMessage = "" + dpCalculateOnAmount + " amount should not be greater than " + dpLimitAmount + " amount";
                baseDataValidator.reset().parameter(SavingsApiConstants.dpCalculateOnAmountParamName).value(dpCalculateOnAmount)
                        .failWithCode(errorCode, userMessage, dpCalculateOnAmount, dpLimitAmount);
            }
        }
    }

    private void validateDpLimitAmount(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        BigDecimal overdraftLimit = BigDecimal.ZERO;
        boolean allowDpLimit = false;
        BigDecimal dpLimitAmount = BigDecimal.ZERO;
        Boolean allowOverdraft = false;
        if (this.fromApiJsonHelper.parameterExists(overdraftLimitParamName, element)) {
            overdraftLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(overdraftLimitParamName, element);
        }
        if (this.fromApiJsonHelper.parameterExists(allowOverdraftParamName, element)) {
            allowOverdraft = this.fromApiJsonHelper.extractBooleanNamed(allowOverdraftParamName, element);
        }
        if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.allowDpLimitParamName, element)) {
            allowDpLimit = this.fromApiJsonHelper.extractBooleanNamed(SavingsApiConstants.allowDpLimitParamName, element);
        }
        if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpLimitAmountParamName, element)) {
            dpLimitAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpLimitAmountParamName, element);
        }
        if (allowOverdraft && allowDpLimit && MathUtility.isGreater(dpLimitAmount, overdraftLimit)) {
            final String errorCode = "should.not.be.greater.than.overdraftLimit";
            final String userMessage = "" + dpLimitAmount + " amount should not be greater than " + overdraftLimit + " amount";
            baseDataValidator.reset().parameter(SavingsApiConstants.allowDpLimitParamName).value(allowDpLimit).failWithCode(errorCode,
                    userMessage, dpLimitAmount, overdraftLimit);
        }
    }

    private void validateSavingsCharges(final JsonElement element, final DataValidatorBuilder baseDataValidator) {

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
            final String monthDayFormat = this.fromApiJsonHelper.extractMonthDayFormatParameter(topLevelJsonElement);
            if (topLevelJsonElement.has(chargesParamName) && topLevelJsonElement.get(chargesParamName).isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get(chargesParamName).getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject savingsChargeElement = array.get(i).getAsJsonObject();

                    // final Long id =
                    // this.fromApiJsonHelper.extractLongNamed(idParamName,
                    // savingsChargeElement);

                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed(chargeIdParamName, savingsChargeElement);
                    baseDataValidator.reset().parameter(chargeIdParamName).value(chargeId).longGreaterThanZero();

                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed(amountParamName, savingsChargeElement, locale);
                    baseDataValidator.reset().parameter(amountParamName).value(amount).notNull().positiveAmount();

                    if (this.fromApiJsonHelper.parameterExists(feeOnMonthDayParamName, savingsChargeElement)) {
                        final MonthDay monthDay = this.fromApiJsonHelper.extractMonthDayNamed(feeOnMonthDayParamName, savingsChargeElement,
                                monthDayFormat, locale);
                        baseDataValidator.reset().parameter(feeOnMonthDayParamName).value(monthDay).notNull();
                    }

                    if (this.fromApiJsonHelper.parameterExists(feeIntervalParamName, savingsChargeElement)) {
                        final Integer feeInterval = this.fromApiJsonHelper.extractIntegerNamed(feeIntervalParamName, savingsChargeElement,
                                Locale.getDefault());
                        baseDataValidator.reset().parameter(feeIntervalParamName).value(feeInterval).notNull().inMinMaxRange(1, 12);
                    }
                }
            }
        }
    }

    public void validateForUpdate(final SavingsAccount account, final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SavingsApiConstants.SAVINGS_ACCOUNT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        Long clientId = null;
        if (this.fromApiJsonHelper.parameterExists(clientIdParamName, element)) {
            clientId = this.fromApiJsonHelper.extractLongNamed(clientIdParamName, element);
            baseDataValidator.reset().parameter(clientIdParamName).value(clientId).ignoreIfNull().longGreaterThanZero();

            Long groupId = null;
            if (this.fromApiJsonHelper.parameterExists(productIdParamName, element)) {
                groupId = this.fromApiJsonHelper.extractLongNamed(groupIdParamName, element);
                baseDataValidator.reset().parameter(groupIdParamName).value(groupId).ignoreIfNull().longGreaterThanZero();
            }

            if (clientId == null && groupId == null) {
                // either clientId or groupId must exists if param passed for
                // update.
                baseDataValidator.reset().parameter(clientIdParamName).value(clientId).notNull().integerGreaterThanZero();
            }
        }

        Long groupId = null;
        if (this.fromApiJsonHelper.parameterExists(groupIdParamName, element)) {
            groupId = this.fromApiJsonHelper.extractLongNamed(groupIdParamName, element);
            baseDataValidator.reset().parameter(groupIdParamName).value(groupId).ignoreIfNull().longGreaterThanZero();

            if (this.fromApiJsonHelper.parameterExists(clientIdParamName, element)) {
                clientId = this.fromApiJsonHelper.extractLongNamed(clientIdParamName, element);
                baseDataValidator.reset().parameter(clientIdParamName).value(clientId).ignoreIfNull().longGreaterThanZero();
            }

            if (clientId == null && groupId == null) {
                // either clientId or groupId must exists if param passed for
                // update.
                baseDataValidator.reset().parameter(clientIdParamName).value(clientId).notNull().integerGreaterThanZero();
            }
        }

        if (this.fromApiJsonHelper.parameterExists(productIdParamName, element)) {
            final Long productId = this.fromApiJsonHelper.extractLongNamed(productIdParamName, element);
            baseDataValidator.reset().parameter(productIdParamName).value(productId).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(fieldOfficerIdParamName, element)) {
            final Long fieldOfficerId = this.fromApiJsonHelper.extractLongNamed(fieldOfficerIdParamName, element);
            baseDataValidator.reset().parameter(fieldOfficerIdParamName).value(fieldOfficerId).ignoreIfNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(submittedOnDateParamName, element);
            baseDataValidator.reset().parameter(submittedOnDateParamName).value(submittedOnDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(accountNoParamName, element)) {
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(accountNoParamName, element);
            baseDataValidator.reset().parameter(accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (this.fromApiJsonHelper.parameterExists(externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(externalIdParamName, element);
            baseDataValidator.reset().parameter(externalIdParamName).value(externalId).notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(nominalAnnualInterestRateParamName, element)) {
            final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(nominalAnnualInterestRateParamName,
                    element);
            baseDataValidator.reset().parameter(nominalAnnualInterestRateParamName).value(interestRate).notNull().zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(interestCompoundingPeriodTypeParamName, element)) {
            final Integer interestCompoundingPeriodType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(interestCompoundingPeriodTypeParamName, element);
            baseDataValidator.reset().parameter(interestCompoundingPeriodTypeParamName).value(interestCompoundingPeriodType).notNull()
                    .isOneOfTheseValues(SavingsCompoundingInterestPeriodType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(interestPostingPeriodTypeParamName, element)) {
            final Integer interestPostingPeriodType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(interestPostingPeriodTypeParamName, element);
            baseDataValidator.reset().parameter(interestPostingPeriodTypeParamName).value(interestPostingPeriodType).notNull()
                    .isOneOfTheseValues(SavingsPostingInterestPeriodType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(interestCalculationTypeParamName, element)) {
            final Integer interestCalculationType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(interestCalculationTypeParamName,
                    element);
            baseDataValidator.reset().parameter(interestCalculationTypeParamName).value(interestCalculationType).notNull()
                    .isOneOfTheseValues(SavingsInterestCalculationType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(interestCalculationDaysInYearTypeParamName, element)) {
            final Integer interestCalculationDaysInYearType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(interestCalculationDaysInYearTypeParamName, element);
            baseDataValidator.reset().parameter(interestCalculationDaysInYearTypeParamName).value(interestCalculationDaysInYearType)
                    .notNull().isOneOfTheseValues(SavingsInterestCalculationDaysInYearType.integerValues());
        }

        if (this.fromApiJsonHelper.parameterExists(minRequiredOpeningBalanceParamName, element)) {
            final BigDecimal minOpeningBalance = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minRequiredOpeningBalanceParamName,
                    element);
            baseDataValidator.reset().parameter(minRequiredOpeningBalanceParamName).value(minOpeningBalance).ignoreIfNull()
                    .zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyParamName, element)) {
            final Integer lockinPeriodFrequency = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(lockinPeriodFrequencyParamName,
                    element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyParamName).value(lockinPeriodFrequency).ignoreIfNull()
                    .integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyTypeParamName, element)) {
            final Integer lockinPeriodFrequencyType = this.fromApiJsonHelper
                    .extractIntegerSansLocaleNamed(lockinPeriodFrequencyTypeParamName, element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyTypeParamName).value(lockinPeriodFrequencyType).inMinMaxRange(0, 3);
        }

        if (this.fromApiJsonHelper.parameterExists(withdrawalFeeForTransfersParamName, element)) {
            final Boolean isWithdrawalFeeApplicableForTransfers = this.fromApiJsonHelper
                    .extractBooleanNamed(withdrawalFeeForTransfersParamName, element);
            baseDataValidator.reset().parameter(withdrawalFeeForTransfersParamName).value(isWithdrawalFeeApplicableForTransfers)
                    .ignoreIfNull().validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists(withHoldTaxParamName, element)) {
            final String withHoldTax = this.fromApiJsonHelper.extractStringNamed(withHoldTaxParamName, element);
            baseDataValidator.reset().parameter(withHoldTaxParamName).value(withHoldTax).ignoreIfNull().validateForBooleanValue();
        }

        validateOverdraftParams(baseDataValidator, element);

        if (account.getSavingsAccountDpDetails() != null) {
            validateDPParamsForUpdate(baseDataValidator, element);
        } else {
            validateDPParamsForSubmit(baseDataValidator, element);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateDPParamsForUpdate(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.allowOverdraftParamName, element)
                && this.fromApiJsonHelper.parameterExists(SavingsApiConstants.allowDpLimitParamName, element)) {
            final boolean allowOverdraft = this.fromApiJsonHelper.extractBooleanNamed(SavingsApiConstants.allowOverdraftParamName, element);
            final boolean allowDpLimit = this.fromApiJsonHelper.extractBooleanNamed(SavingsApiConstants.allowDpLimitParamName, element);
            if (allowOverdraft && allowDpLimit) {

                if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpLimitAmountParamName, element)) {

                    final BigDecimal overdraftLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(overdraftLimitParamName,
                            element);
                    baseDataValidator.reset().parameter(overdraftLimitParamName).value(overdraftLimit).notNull().positiveAmount();

                    final BigDecimal dpLimitAmount = this.fromApiJsonHelper
                            .extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpLimitAmountParamName, element);
                    baseDataValidator.reset().parameter(SavingsApiConstants.dpLimitAmountParamName).value(dpLimitAmount).notNull()
                            .positiveAmount();

                    validateDpLimitAmount(baseDataValidator, element);
                }

                if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.savingsDpLimitCalculationTypeParamName, element)) {
                    final Integer savingsDpLimitCalculationType = this.fromApiJsonHelper
                            .extractIntegerSansLocaleNamed(SavingsApiConstants.savingsDpLimitCalculationTypeParamName, element);
                    baseDataValidator.reset().parameter(SavingsApiConstants.savingsDpLimitCalculationTypeParamName)
                            .value(savingsDpLimitCalculationType).notNull()
                            .isOneOfTheseValues(SavingsDpLimitCalculationType.FLAT.getValue(),
                                    SavingsDpLimitCalculationType.PERCENT_OF_AMOUNT.getValue());
                }

                if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpDurationParamName, element)) {
                    final Integer dpDuration = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(SavingsApiConstants.dpDurationParamName,
                            element);
                    baseDataValidator.reset().parameter(SavingsApiConstants.dpDurationParamName).value(dpDuration).notNull()
                            .integerGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpCalculateOnAmountParamName, element)) {
                    final BigDecimal dpCalculateOnAmount = this.fromApiJsonHelper
                            .extractBigDecimalWithLocaleNamed(SavingsApiConstants.dpCalculateOnAmountParamName, element);
                    baseDataValidator.reset().parameter(SavingsApiConstants.dpCalculateOnAmountParamName).value(dpCalculateOnAmount)
                            .notNull().positiveAmount();
                    validateDpCalculateOnAmount(baseDataValidator, element);
                }

                if (this.fromApiJsonHelper.parameterExists(SavingsApiConstants.dpStartDateParamName, element)) {
                    final LocalDate dpStartDate = this.fromApiJsonHelper.extractLocalDateNamed(SavingsApiConstants.dpStartDateParamName,
                            element);
                    baseDataValidator.reset().parameter(SavingsApiConstants.dpStartDateParamName).value(dpStartDate).notNull();
                }
            }
        }
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void validateOverdraftParams(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        if (this.fromApiJsonHelper.parameterExists(allowOverdraftParamName, element)) {
            final Boolean allowOverdraft = this.fromApiJsonHelper.extractBooleanNamed(allowOverdraftParamName, element);
            baseDataValidator.reset().parameter(allowOverdraftParamName).value(allowOverdraft).ignoreIfNull().validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists(overdraftLimitParamName, element)) {
            final BigDecimal overdraftLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(overdraftLimitParamName, element);
            baseDataValidator.reset().parameter(overdraftLimitParamName).value(overdraftLimit).ignoreIfNull().zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(nominalAnnualInterestRateOverdraftParamName, element)) {
            final BigDecimal nominalAnnualInterestRateOverdraft = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(nominalAnnualInterestRateOverdraftParamName, element);
            baseDataValidator.reset().parameter(nominalAnnualInterestRateOverdraftParamName).value(nominalAnnualInterestRateOverdraft)
                    .ignoreIfNull().zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(minOverdraftForInterestCalculationParamName, element)) {
            final BigDecimal minOverdraftForInterestCalculation = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(minOverdraftForInterestCalculationParamName, element);
            baseDataValidator.reset().parameter(minOverdraftForInterestCalculationParamName).value(minOverdraftForInterestCalculation)
                    .ignoreIfNull().zeroOrPositiveAmount();
        }

    }

    public void validateForAssignSavingsOfficer(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> supportedParameters = new HashSet<>(
                Arrays.asList("fromSavingsOfficerId", "toSavingsOfficerId", "assignmentDate", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long toSavingsOfficerId = this.fromApiJsonHelper.extractLongNamed("toSavingsOfficerId", element);
        baseDataValidator.reset().parameter("toSavingsOfficerId").value(toSavingsOfficerId).notNull().integerGreaterThanZero();

        final String assignmentDateStr = this.fromApiJsonHelper.extractStringNamed("assignmentDate", element);
        baseDataValidator.reset().parameter("assignmentDate").value(assignmentDateStr).notBlank();

        if (!StringUtils.isBlank(assignmentDateStr)) {
            final LocalDate assignmentDate = this.fromApiJsonHelper.extractLocalDateNamed("assignmentDate", element);
            baseDataValidator.reset().parameter("assignmentDate").value(assignmentDate).notNull();
        }
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }

    }

    public void validateForUnAssignSavingsOfficer(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> supportedParameters = new HashSet<>(Arrays.asList("unassignedDate", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String unassignedDateStr = this.fromApiJsonHelper.extractStringNamed("unassignedDate", element);
        baseDataValidator.reset().parameter("unassignedDate").value(unassignedDateStr).notBlank();

        if (!StringUtils.isBlank(unassignedDateStr)) {
            final LocalDate unassignedDate = this.fromApiJsonHelper.extractLocalDateNamed("unassignedDate", element);
            baseDataValidator.reset().parameter("unassignedDate").value(unassignedDate).notNull();
        }
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}