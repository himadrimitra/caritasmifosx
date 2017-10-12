/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.domain.Fund;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class FundDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;

    @Autowired
    public FundDataValidator(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepositoryWrapper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
    }

    public void validate(final JsonCommand command) {
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Set<String> requestDataParameter = FundApiConstants.FUND_CREATE_REQUEST_DATA_PARAMETERS;
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, requestDataParameter);
        Locale locale = this.fromApiJsonHelper.extractLocaleParameter(command.parsedJson().getAsJsonObject()); 
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FundApiConstants.FUND_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        boolean isOwn = false;

        final String name = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(FundApiConstants.nameParamName).value(name).notBlank().notExceedingLengthOf(100);

        final Integer facilityType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.facilityTypeParamName, element);
        baseDataValidator.reset().parameter(FundApiConstants.facilityTypeParamName).value(facilityType).notNull().integerGreaterThanZero();
        if (facilityType != null) {
            CodeValue facilityTypeValue = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                    FundApiConstants.FACILITY_TYPE_CODE_VALUE, Long.valueOf(facilityType));
            if (facilityTypeValue.label().equalsIgnoreCase(FundApiConstants.ownParam)) {
                isOwn = true;
            }
        }

        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.externalIdParamName).value(externalId).notBlank()
                    .notExceedingLengthOf(100);
        }
        if (!isOwn) {
            final Integer fundSource = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.fundSourceParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.fundSourceParamName).value(fundSource).notNull().integerGreaterThanZero();

            final Integer fundCategory = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.fundCategoryParamName,
                    element);
            baseDataValidator.reset().parameter(FundApiConstants.fundCategoryParamName).value(fundCategory).notNull()
                    .integerGreaterThanZero();

            final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    FundApiConstants.fundRepaymentFrequencyParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.fundRepaymentFrequencyParamName).value(repaymentFrequencyType).notNull()
                    .integerGreaterThanZero();

            final LocalDate assignmentStartDate = this.fromApiJsonHelper.extractLocalDateNamed(
                    FundApiConstants.assignmentStartDateParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.assignmentStartDateParamName).value(assignmentStartDate).notNull();

            final LocalDate assignmentEndDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.assignmentEndDateParamName,
                    element);
            baseDataValidator.reset().parameter(FundApiConstants.assignmentEndDateParamName).value(assignmentEndDate).notNull()
                    .validateDateAfter(assignmentStartDate);

            JsonElement fundLoanPurpose = element.getAsJsonObject().get(FundApiConstants.fundLoanPurposeParamName);
            baseDataValidator.reset().parameter(FundApiConstants.fundLoanPurposeParamName).value(fundLoanPurpose).notNull();
            if(fundLoanPurpose != null){
                for (JsonElement fundloanPurposeData : fundLoanPurpose.getAsJsonArray()) {
                    Integer loanPurposeId = this.fromApiJsonHelper.extractIntegerNamed("loanPurposeId", fundloanPurposeData, locale);
                    baseDataValidator.reset().parameter("loanPurposeId").value(loanPurposeId).notNull().integerGreaterThanZero();
                    BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("loanPurposeAmount", fundloanPurposeData, locale);
                    baseDataValidator.reset().parameter("loanPurposeAmount").value(amount).notNull().integerGreaterThanZero();
                }
            }

            final LocalDate sanctionedDate = this.fromApiJsonHelper
                    .extractLocalDateNamed(FundApiConstants.sanctionedDateParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.sanctionedDateParamName).value(sanctionedDate).notNull()
                    .validateDateAfter(assignmentStartDate);

            final BigDecimal sanctionedAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    FundApiConstants.sanctionedAmountParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.sanctionedAmountParamName).value(sanctionedAmount).notNull()
                    .positiveAmount();

            final LocalDate disbursedDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.disbursedDateParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.disbursedDateParamName).value(disbursedDate).notNull()
                    .validateDateAfter(sanctionedDate);

            final BigDecimal disbursedAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    FundApiConstants.disbursedAmountParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.disbursedAmountParamName).value(disbursedAmount).notNull()
                    .positiveAmount().notGreaterThanMax(sanctionedAmount);

            final LocalDate maturityDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.maturityDateParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.maturityDateParamName).value(maturityDate).notNull()
                    .validateDateAfter(disbursedDate);

            final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(FundApiConstants.interestRateParamName,
                    element);
            baseDataValidator.reset().parameter(FundApiConstants.interestRateParamName).value(interestRate).notNull().positiveAmount();

            final Integer tenure = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.tenureParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.tenureParamName).value(tenure).notNull().integerGreaterThanZero();

            final Integer tenureFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.tenureFrequencyParamName,
                    element);
            baseDataValidator.reset().parameter(FundApiConstants.tenureFrequencyParamName).value(tenureFrequency).notNull();

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.morotoriumParamName, element)
                    && this.fromApiJsonHelper.parameterExists(FundApiConstants.morotoriumFrequencyParamName, element)) {
                final Integer morotorium = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.morotoriumParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.morotoriumParamName).value(morotorium).notNull()
                        .integerGreaterThanZero();

                final Integer morotoriumFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                        FundApiConstants.morotoriumFrequencyParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.morotoriumFrequencyParamName).value(morotoriumFrequency).notNull();

            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.loanPortfolioFeeParamName, element)) {
                final BigDecimal loanPortfolioFee = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.loanPortfolioFeeParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.loanPortfolioFeeParamName).value(loanPortfolioFee).notNull()
                        .positiveAmount();

            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.bookDebtHypothecationParamName, element)) {
                final BigDecimal bookDebtHypothecation = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.bookDebtHypothecationParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.bookDebtHypothecationParamName).value(bookDebtHypothecation).notNull()
                        .positiveAmount();

            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.cashCollateralParamName, element)) {
                final BigDecimal cashCollateral = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.cashCollateralParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.cashCollateralParamName).value(cashCollateral).notNull()
                        .positiveAmount();

            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.personalGuranteeParamName, element)) {
                final String personalGurantee = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.personalGuranteeParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.personalGuranteeParamName).value(personalGurantee).notNull()
                        .notBlank().notExceedingLengthOf(250);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final JsonCommand command, Fund fund) {
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, FundApiConstants.FUND_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FundApiConstants.FUND_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        boolean isOwn = false;
        boolean isChangedFromOwn = false;
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.nameParamName, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.nameParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.nameParamName).value(name).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(FundApiConstants.externalIdParamName).value(externalId).notBlank()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.facilityTypeParamName, element)) {
            final Integer facilityType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.facilityTypeParamName,
                    element);
            baseDataValidator.reset().parameter(FundApiConstants.facilityTypeParamName).value(facilityType).notNull()
                    .integerGreaterThanZero();
            if (facilityType != null) {
                CodeValue facilityTypeValue = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        FundApiConstants.FACILITY_TYPE_CODE_VALUE, Long.valueOf(facilityType));
                if (facilityTypeValue.label().equalsIgnoreCase(FundApiConstants.ownParam)) {
                    isOwn = true;
                } else if (fund.getFacilityType().label().equalsIgnoreCase(FundApiConstants.ownParam)) {
                    isChangedFromOwn = true;
                }
            }
        }

        if (!isOwn) {
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.fundSourceParamName, element) || isChangedFromOwn) {
                final Integer fundSource = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.fundSourceParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.fundSourceParamName).value(fundSource).notNull()
                        .integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.fundCategoryParamName, element) || isChangedFromOwn) {
                final Integer fundCategory = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.fundCategoryParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.fundCategoryParamName).value(fundCategory).notNull()
                        .integerGreaterThanZero();
            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.fundRepaymentFrequencyParamName, element) || isChangedFromOwn) {
                final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                        FundApiConstants.fundRepaymentFrequencyParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.fundRepaymentFrequencyParamName).value(repaymentFrequencyType)
                        .notNull().integerGreaterThanZero();
            }
            LocalDate assignmentStartDate = null;
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.assignmentStartDateParamName, element) || isChangedFromOwn) {
                assignmentStartDate = this.fromApiJsonHelper.extractLocalDateNamed(
                        FundApiConstants.assignmentStartDateParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.assignmentStartDateParamName).value(assignmentStartDate).notNull();
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.assignmentEndDateParamName, element) || isChangedFromOwn) {
                final LocalDate assignmentEndDate = this.fromApiJsonHelper.extractLocalDateNamed(
                        FundApiConstants.assignmentEndDateParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.assignmentEndDateParamName).value(assignmentEndDate).notNull()
                        .validateDateAfter(assignmentStartDate);
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.fundLoanPurposeParamName, element) || isChangedFromOwn) {

                JsonElement fundLoanPurpose = element.getAsJsonObject().get(FundApiConstants.fundLoanPurposeParamName);
                baseDataValidator.reset().parameter(FundApiConstants.fundLoanPurposeParamName).value(fundLoanPurpose).notNull();
                for (JsonElement fundloanPurposeData : fundLoanPurpose.getAsJsonArray()) {
                    Integer loanPurposeId = this.fromApiJsonHelper
                            .extractIntegerNamed("loanPurposeId", fundloanPurposeData, Locale.ENGLISH);
                    baseDataValidator.reset().parameter("loanPurposeId").value(loanPurposeId).notNull().integerGreaterThanZero();
                    BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("loanPurposeAmount", fundloanPurposeData,
                            Locale.ENGLISH);
                    baseDataValidator.reset().parameter("loanPurposeAmount").value(amount).notNull().integerGreaterThanZero();
                }
            }
            LocalDate sanctionedDate = null;
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.sanctionedDateParamName, element) || isChangedFromOwn) {
                sanctionedDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.sanctionedDateParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.sanctionedDateParamName).value(sanctionedDate).notNull()
                        .validateDateAfter(assignmentStartDate);
            }
            BigDecimal sanctionedAmount = null;
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.sanctionedAmountParamName, element) || isChangedFromOwn) {
                sanctionedAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.sanctionedAmountParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.sanctionedAmountParamName).value(sanctionedAmount).notNull()
                        .positiveAmount();
            }
            LocalDate disbursedDate = null;
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.disbursedDateParamName, element) || isChangedFromOwn) {
                disbursedDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.disbursedDateParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.disbursedDateParamName).value(disbursedDate).notNull()
                        .validateDateAfter(sanctionedDate);
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.disbursedAmountParamName, element) || isChangedFromOwn) {
                final BigDecimal disbursedAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.disbursedAmountParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.disbursedAmountParamName).value(disbursedAmount).notNull()
                        .positiveAmount().notGreaterThanMax(sanctionedAmount);
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.maturityDateParamName, element) || isChangedFromOwn) {
                final LocalDate maturityDate = this.fromApiJsonHelper
                        .extractLocalDateNamed(FundApiConstants.maturityDateParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.maturityDateParamName).value(maturityDate).notNull()
                        .validateDateAfter(disbursedDate);
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.interestRateParamName, element) || isChangedFromOwn) {
                final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.interestRateParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.interestRateParamName).value(interestRate).notNull().positiveAmount();
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.tenureFrequencyParamName, element) || isChangedFromOwn) {
                final Integer tenure = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.tenureParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.tenureParamName).value(tenure).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.tenureFrequencyParamName, element) || isChangedFromOwn) {
                final Integer tenureFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                        FundApiConstants.tenureFrequencyParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.tenureFrequencyParamName).value(tenureFrequency).notNull();
            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.morotoriumParamName, element)) {
                final Integer morotorium = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.morotoriumParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.morotoriumParamName).value(morotorium).ignoreIfNull()
                        .integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.morotoriumFrequencyParamName, element)) {
                final Integer morotoriumFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                        FundApiConstants.morotoriumFrequencyParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.morotoriumFrequencyParamName).value(morotoriumFrequency).notNull();
            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.loanPortfolioFeeParamName, element)) {
                final BigDecimal loanPortfolioFee = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.loanPortfolioFeeParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.loanPortfolioFeeParamName).value(loanPortfolioFee).ignoreIfNull()
                        .positiveAmount();

            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.bookDebtHypothecationParamName, element)) {
                final BigDecimal bookDebtHypothecation = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.bookDebtHypothecationParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.bookDebtHypothecationParamName).value(bookDebtHypothecation).ignoreIfNull()
                        .positiveAmount();

            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.cashCollateralParamName, element)) {
                final BigDecimal cashCollateral = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.cashCollateralParamName, element);
                baseDataValidator.reset().parameter(FundApiConstants.cashCollateralParamName).value(cashCollateral).ignoreIfNull()
                        .positiveAmount();

            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.personalGuranteeParamName, element)) {
                final String personalGurantee = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.personalGuranteeParamName,
                        element);
                baseDataValidator.reset().parameter(FundApiConstants.personalGuranteeParamName).value(personalGurantee).notNull()
                        .notBlank().notExceedingLengthOf(250);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateSearchData(final JsonCommand command) {
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final JsonElement element = command.parsedJson();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, FundApiConstants.FUND_SUMMARY_SEARCH_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FundApiConstants.FUND_RESOURCE_NAME);

        String[] criteriaList = this.fromApiJsonHelper.extractArrayNamed(FundApiConstants.selectedCriteriaListParamName, element);
        baseDataValidator.reset().parameter(FundApiConstants.selectedCriteriaListParamName).value(criteriaList).arrayNotEmpty();
        if (criteriaList != null && criteriaList.length > 0) {
        Set<String> selectedCriteriaList = new TreeSet<>(Arrays.asList(criteriaList));
        String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(element.getAsJsonObject());
        Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());        
            for (String param : selectedCriteriaList) {
                if (FundApiConstants.groupByColumns.contains(param)) {
                    final String[] paramValues = this.fromApiJsonHelper.extractArrayNamed(param, element);
                    baseDataValidator.reset().parameter(param).value(paramValues).arrayNotEmpty();
                } else if (FundApiConstants.datesColumns.contains(param)) {
                    final JsonElement dateElement = element.getAsJsonObject().get(param);
                    baseDataValidator.reset().parameter(param).value(dateElement).notNull();
                    if (dateElement != null) {
                        final LocalDate minDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.minParamName, dateElement,
                                dateFormat, locale);
                        baseDataValidator.reset().parameter(param + "." + FundApiConstants.minParamName).value(minDate).notNull();
                        final String operator = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.operatorParamName, dateElement);
                        baseDataValidator.reset().parameter(param + "." + FundApiConstants.operatorParamName).value(operator).notBlank();
                        if (operator.equalsIgnoreCase(FundApiConstants.betweenParamName)) {
                            final LocalDate maxDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.maxParamName,
                                    dateElement, dateFormat, locale);
                            baseDataValidator.reset().parameter(param + "." + FundApiConstants.maxParamName).value(maxDate).notNull();
                        }
                    }

                } else if (FundApiConstants.operatorBasedColumns.contains(param)) {
                    final JsonElement values = element.getAsJsonObject().get(param);
                    baseDataValidator.reset().parameter(param).value(values).notNull();
                    if (values != null) {
                        final String operator = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.operatorParamName, values);
                        baseDataValidator.reset().parameter(param + "." + FundApiConstants.operatorParamName).value(operator).notNull()
                                .notBlank();
                        if (param.equalsIgnoreCase("principalOutstanding")) {
                            BigDecimal min = this.fromApiJsonHelper.extractBigDecimalNamed(FundApiConstants.minParamName, values, locale);
                            baseDataValidator.reset().parameter(param + "." + FundApiConstants.minParamName).value(min).notNull();
                            if (operator.equalsIgnoreCase(FundApiConstants.betweenParamName)) {
                                BigDecimal max = this.fromApiJsonHelper.extractBigDecimalNamed(FundApiConstants.maxParamName, values,
                                        locale);
                                baseDataValidator.reset().parameter(param + "." + FundApiConstants.maxParamName).value(max).notNull();
                            }
                        } else {
                            Integer min = this.fromApiJsonHelper.extractIntegerNamed(FundApiConstants.minParamName, values, locale);
                            baseDataValidator.reset().parameter(param + "." + FundApiConstants.minParamName).value(min).notNull();
                            if (operator.equalsIgnoreCase(FundApiConstants.betweenParamName)) {
                                Integer max = this.fromApiJsonHelper.extractIntegerNamed(FundApiConstants.maxParamName, values, locale);
                                baseDataValidator.reset().parameter(param + "." + FundApiConstants.maxParamName).value(max).notNull();
                            }
                        }

                    }

                } else if (param.equalsIgnoreCase(FundApiConstants.trancheDisburseParam)) {
                    Boolean isTranche = this.fromApiJsonHelper.extractBooleanNamed(FundApiConstants.trancheDisburseParam, element);
                    baseDataValidator.reset().parameter(FundApiConstants.trancheDisburseParam).value(isTranche).notNull()
                            .trueOrFalseRequired(true);
                }
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

}
