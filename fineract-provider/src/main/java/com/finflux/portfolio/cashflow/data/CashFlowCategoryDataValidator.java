package com.finflux.portfolio.cashflow.data;

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

import com.finflux.portfolio.cashflow.api.CashFlowCategoryApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class CashFlowCategoryDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CashFlowCategoryDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper
                .checkForUnsupportedParameters(typeOfMap, json, CashFlowCategoryApiConstants.CREATE_CASH_FLOW_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CashFlowCategoryApiConstants.CASH_FLOW_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String name = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);

        final String shortName = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.shortNameParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.shortNameParamName).value(shortName).notNull().notExceedingLengthOf(30);

        final String description = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.descriptionParamName).value(description).ignoreIfNull()
                .notExceedingLengthOf(500);

        final Integer categoryEnumId = this.fromApiJsonHelper.extractIntegerNamed(CashFlowCategoryApiConstants.categoryEnumIdParamName, element,
                locale);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.categoryEnumIdParamName).value(categoryEnumId).notNull();

        final Integer typeEnumId = this.fromApiJsonHelper.extractIntegerNamed(CashFlowCategoryApiConstants.typeEnumIdParamName, element, locale);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.typeEnumIdParamName).value(typeEnumId).notNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(CashFlowCategoryApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper
                .checkForUnsupportedParameters(typeOfMap, json, CashFlowCategoryApiConstants.UPDATE_CASH_FLOW_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CashFlowCategoryApiConstants.CASH_FLOW_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String name = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);

        final String description = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.descriptionParamName).value(description).ignoreIfNull()
                .notExceedingLengthOf(500);

        final Integer categoryEnumId = this.fromApiJsonHelper.extractIntegerNamed(CashFlowCategoryApiConstants.categoryEnumIdParamName, element,
                locale);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.categoryEnumIdParamName).value(categoryEnumId).notNull();

        final Integer typeEnumId = this.fromApiJsonHelper.extractIntegerNamed(CashFlowCategoryApiConstants.typeEnumIdParamName, element, locale);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.typeEnumIdParamName).value(typeEnumId).notNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(CashFlowCategoryApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(CashFlowCategoryApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}