package com.finflux.portfolio.loan.purpose.data;

import java.lang.reflect.Type;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class LoanPurposeGroupDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanPurposeGroupDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreateLoanPurposeGroup(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanPurposeGroupApiConstants.CREATE_LOAN_PURPOSE_GROUP_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanPurposeGroupApiConstants.LOAN_PURPOSE_GROUP_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String name = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.nameParamName).value(name).notNull();

        final String systemCode = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.systemCodeParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.systemCodeParamName).value(systemCode).notNull()
                .notExceedingLengthOf(50);

        final String description = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.descriptionParamName).value(description).ignoreIfNull();

        final Integer loanPurposeGroupTypeId = this.fromApiJsonHelper.extractIntegerNamed(
                LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName, element, locale);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName).value(loanPurposeGroupTypeId)
                .notNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanPurposeGroupApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForUpdateLoanPurposeGroup(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanPurposeGroupApiConstants.UPDATE_LOAN_PURPOSE_GROUP_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanPurposeGroupApiConstants.LOAN_PURPOSE_GROUP_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String name = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.nameParamName).value(name).notNull();

        final String description = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.descriptionParamName).value(description).ignoreIfNull();

        final Integer loanPurposeGroupTypeId = this.fromApiJsonHelper.extractIntegerNamed(
                LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName, element, locale);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName).value(loanPurposeGroupTypeId)
                .notNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanPurposeGroupApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForCreateLoanPurpose(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanPurposeGroupApiConstants.CREATE_LOAN_PURPOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanPurposeGroupApiConstants.LOAN_PURPOSE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.nameParamName).value(name).notNull();

        final String systemCode = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.systemCodeParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.systemCodeParamName).value(systemCode).notNull()
                .notExceedingLengthOf(50);

        final String description = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.descriptionParamName).value(description).ignoreIfNull();

        if (this.fromApiJsonHelper.parameterExists(LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName, element)) {
            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
                    LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName, element);
            baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName).value(loanPurposeGroupIds)
                    .notBlank().arrayNotEmpty();
        }

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanPurposeGroupApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdateLoanPurpose(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanPurposeGroupApiConstants.UPDATE_LOAN_PURPOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanPurposeGroupApiConstants.LOAN_PURPOSE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.nameParamName).value(name).notNull();

        final String description = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.descriptionParamName).value(description).ignoreIfNull();

        if (this.fromApiJsonHelper.parameterExists(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName, element)) {
            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
                    LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName, element);
            baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName).value(loanPurposeGroupIds)
                    .notBlank().arrayNotEmpty();
        }

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanPurposeGroupApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(LoanPurposeGroupApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}