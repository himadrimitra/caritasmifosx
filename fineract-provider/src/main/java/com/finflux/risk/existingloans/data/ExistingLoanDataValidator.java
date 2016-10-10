package com.finflux.risk.existingloans.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;
import com.finflux.risk.existingloans.exception.ExistingLoanDateException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class ExistingLoanDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ExistingLoanDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ExistingLoanApiConstants.EXISTING_LOAN_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ExistingLoanApiConstants.EXISTINGLOAN_RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);
        final JsonObject parentElementObj = parentElement.getAsJsonObject();

        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.existingLoansParamName, parentElement)) {
            validateEachJsonObjectForCreate(parentElement.getAsJsonObject(), baseDataValidator);
        } else if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.existingLoansParamName, parentElement)) {
            final JsonArray array = parentElementObj.get(ExistingLoanApiConstants.existingLoansParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateEachJsonObjectForCreate(element, baseDataValidator);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachJsonObjectForCreate(final JsonObject element, DataValidatorBuilder baseDataValidator) {

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanApplicationIdParamName, element)) {
            final Long loanApplicationId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanApplicationIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanApplicationIdParamName).value(loanApplicationId)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanIdParamName, element)) {
            final Long loanId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanIdParamName).value(loanId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.sourceCvIdParamName, element)) {
            final Integer sourceId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.sourceCvIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.sourceCvIdParamName).value(sourceId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanEnquiryIdParamName, element)) {
            final Integer bureauId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.loanEnquiryIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanEnquiryIdParamName).value(bureauId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.lenderCvIdParamName, element)) {
            final Integer lenderId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.lenderCvIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.lenderCvIdParamName).value(lenderId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.lenderNameParamName, element)) {
            final String lenderNotListed = this.fromApiJsonHelper.extractStringNamed(ExistingLoanApiConstants.lenderNameParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.lenderNameParamName).value(lenderNotListed).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanTypeCvIdParamName, element)) {
            final Integer sourceId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.loanTypeCvIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanTypeCvIdParamName).value(sourceId).integerGreaterThanZero();
        }
        final BigDecimal amountBorrowed = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountBorrowed", element);
        baseDataValidator.reset().parameter("amountBorrowed").value(amountBorrowed).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.currentOutstandingIdParamName, element)) {
            final BigDecimal currentOutstanding = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    ExistingLoanApiConstants.currentOutstandingIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.currentOutstandingIdParamName).value(currentOutstanding)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.amtOverdueParamName, element)) {
            final BigDecimal amountOverdue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    ExistingLoanApiConstants.amtOverdueParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.amtOverdueParamName).value(amountOverdue).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.writtenoffamountParamName, element)) {
            final BigDecimal writtenOffAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    ExistingLoanApiConstants.writtenoffamountParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.writtenoffamountParamName).value(writtenOffAmount)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanTenureParamName, element)) {
            final Integer loanTenure = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.loanTenureParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanTenureParamName).value(loanTenure).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanTenurePeriodTypeParamName, element)) {
            final Integer loanTenurePeriodType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.loanTenurePeriodTypeParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanTenurePeriodTypeParamName).value(loanTenurePeriodType)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.repaymentFrequencyParamName, element)) {
            final Integer repaymentFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.repaymentFrequencyParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.repaymentFrequencyParamName).value(repaymentFrequency)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName, element)) {
            final Integer repaymentFrequencyMultipleOf = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName)
                    .value(repaymentFrequencyMultipleOf).integerGreaterThanZero();
        }
        final BigDecimal installMentAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("installmentAmount", element);
        baseDataValidator.reset().parameter("installmentAmount").value(installMentAmount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName, element)) {
            final Integer externalLoanPuropeseId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.externalLoanPurposeCvIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName).value(externalLoanPuropeseId)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanStatusIdParamName, element)) {
            final Integer loanStatusId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.loanStatusIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanStatusIdParamName).value(loanStatusId)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.disbursedDateParamName, element)) {
            final LocalDate disbursementOnDate = this.fromApiJsonHelper.extractLocalDateNamed(
                    ExistingLoanApiConstants.disbursedDateParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.disbursedDateParamName).value(disbursementOnDate);
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.maturityDateParamName, element)) {
            final LocalDate maturityDate = this.fromApiJsonHelper.extractLocalDateNamed(ExistingLoanApiConstants.maturityDateParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.maturityDateParamName).value(maturityDate);
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.gt0dpd3mthsParamName, element)) {
            final Integer gt0dpd3mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.gt0dpd3mthsParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.gt0dpd3mthsParamName).value(gt0dpd3mths).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.dpd30mths12ParamName, element)) {
            final Integer dpd3012mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.dpd30mths12ParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.dpd30mths12ParamName).value(dpd3012mths).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.dpd30mths24ParamName, element)) {
            final Integer dpd3024mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.dpd30mths24ParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.dpd30mths24ParamName).value(dpd3024mths).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.dpd60mths24ParamName, element)) {
            final Integer dpd6024mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.dpd60mths24ParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.dpd60mths24ParamName).value(dpd6024mths).integerGreaterThanZero();
        }
        final Integer archive = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.archiveParamName, element);
        baseDataValidator.reset().parameter(ExistingLoanApiConstants.archiveParamName).value(archive).integerGreaterThanZero().notNull();
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper
                .checkForUnsupportedParameters(typeOfMap, json, ExistingLoanApiConstants.EXISTING_LOAN_UPDATE_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ExistingLoanApiConstants.EXISTINGLOAN_RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.existingLoansParamName, parentElement)) {
            validateEachObjectForUpdate(parentElement.getAsJsonObject(), baseDataValidator);
        } else if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.existingLoansParamName, parentElement)) {
            final JsonArray array = parentElementObj.get(ExistingLoanApiConstants.existingLoansParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateEachObjectForUpdate(element, baseDataValidator);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachObjectForUpdate(final JsonObject element, final DataValidatorBuilder baseDataValidator) {

        final Long id = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.idParamName, element);
        baseDataValidator.reset().parameter(ExistingLoanApiConstants.idParamName).value(id).notBlank().longGreaterThanZero();

        boolean atLeastOneParameterPassedForUpdate = false;
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanApplicationIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Long loanApplicationId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanApplicationIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanApplicationIdParamName).value(loanApplicationId)
                    .integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanIdParamName, element)) {

            atLeastOneParameterPassedForUpdate = true;
            final Long loanId = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.loanIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanIdParamName).value(loanId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.sourceCvIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer sourceId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.sourceCvIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.sourceCvIdParamName).value(sourceId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanEnquiryIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer bureauId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.loanEnquiryIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanEnquiryIdParamName).value(bureauId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.lenderCvIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer lenderId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.lenderCvIdParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.lenderCvIdParamName).value(lenderId).integerGreaterThanZero();
        }
        final BigDecimal amountBorrowed = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountBorrowed", element);
        baseDataValidator.reset().parameter("amountBorrowed").value(amountBorrowed).notNull().positiveAmount();

        final BigDecimal installMentAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("installmentAmount", element);
        baseDataValidator.reset().parameter("installmentAmount").value(installMentAmount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.lenderNameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String lenderName = this.fromApiJsonHelper.extractStringNamed(ExistingLoanApiConstants.lenderNameParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.lenderNameParamName).value(lenderName).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.currentOutstandingIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final BigDecimal currentOutstanding = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    ExistingLoanApiConstants.currentOutstandingIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.currentOutstandingIdParamName).value(currentOutstanding)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.amtOverdueParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final BigDecimal amountOverdue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    ExistingLoanApiConstants.amtOverdueParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.amtOverdueParamName).value(amountOverdue).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.writtenoffamountParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final BigDecimal writtenOffAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    ExistingLoanApiConstants.writtenoffamountParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.writtenoffamountParamName).value(writtenOffAmount)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanTenureParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer loanTenure = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.loanTenureParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanTenureParamName).value(loanTenure).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanTenurePeriodTypeParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer loanTenurePeriodType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.loanTenurePeriodTypeParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanTenurePeriodTypeParamName).value(loanTenurePeriodType)
                    .integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.repaymentFrequencyParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer repaymentFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.repaymentFrequencyParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.repaymentFrequencyParamName).value(repaymentFrequency)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer repaymentFrequencyMultipleOf = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName)
                    .value(repaymentFrequencyMultipleOf).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer externalLoanPuropeseId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.externalLoanPurposeCvIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName).value(externalLoanPuropeseId)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.loanStatusIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer loanStatusId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ExistingLoanApiConstants.loanStatusIdParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.loanStatusIdParamName).value(loanStatusId)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.disbursedDateParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate disbursementOnDate = this.fromApiJsonHelper.extractLocalDateNamed(
                    ExistingLoanApiConstants.disbursedDateParamName, element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.disbursedDateParamName).value(disbursementOnDate);
        }
        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.maturityDateParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate maturityDate = this.fromApiJsonHelper.extractLocalDateNamed(ExistingLoanApiConstants.maturityDateParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.maturityDateParamName).value(maturityDate);
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.gt0dpd3mthsParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer gt0dpd3mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.gt0dpd3mthsParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.gt0dpd3mthsParamName).value(gt0dpd3mths).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.dpd30mths12ParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer dpd3012mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.dpd30mths12ParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.dpd30mths12ParamName).value(dpd3012mths).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.dpd30mths24ParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer dpd3024mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.dpd30mths24ParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.dpd30mths24ParamName).value(dpd3024mths).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ExistingLoanApiConstants.dpd60mths24ParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer dpd6024mths = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.dpd60mths24ParamName,
                    element);
            baseDataValidator.reset().parameter(ExistingLoanApiConstants.dpd60mths24ParamName).value(dpd6024mths).integerGreaterThanZero();
        }
        final Integer archive = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ExistingLoanApiConstants.archiveParamName, element);
        baseDataValidator.reset().parameter(ExistingLoanApiConstants.archiveParamName).value(archive).integerGreaterThanZero();

        if (!atLeastOneParameterPassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }
    }

    public void validateMaturityOnDate(final LocalDate disbursedDate, final LocalDate maturityDate) {
        String defaultUserMessage = "";
        if (maturityDate != null && disbursedDate != null)
            if (maturityDate.isBefore(disbursedDate)) {
                defaultUserMessage = "maturityDate cannot be before the disbursedDate.";
                throw new ExistingLoanDateException("submitted.on.date.cannot.be.before.the.loan.product.start.date", defaultUserMessage,
                        maturityDate.toString(), disbursedDate.toString());
            }
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
