package com.finflux.portfolio.cashflow.data;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.cashflow.api.IncomeExpenseApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class IncomeExpenseDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public IncomeExpenseDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                IncomeExpenseApiConstants.CREATE_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(IncomeExpenseApiConstants.INCOME_EXPENSE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long cashFlowCategoryId = this.fromApiJsonHelper.extractLongNamed(IncomeExpenseApiConstants.cashFlowCategoryIdParamName,
                element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.cashFlowCategoryIdParamName).value(cashFlowCategoryId).notNull();

        final String name = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);

        final String description = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.descriptionParamName).value(description).ignoreIfNull()
                .notExceedingLengthOf(500);

        final Boolean isQuantifierNeeded = this.fromApiJsonHelper.extractBooleanNamed(
                IncomeExpenseApiConstants.isQuantifierNeededParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.isQuantifierNeededParamName).value(isQuantifierNeeded).ignoreIfNull();

        final String quantifierLabel = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.quantifierLabelParamName,
                element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.quantifierLabelParamName).value(quantifierLabel).ignoreIfNull()
                .notExceedingLengthOf(100);

        final Boolean isCaptureMonthWiseIncome = this.fromApiJsonHelper.extractBooleanNamed(
                IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName).value(isCaptureMonthWiseIncome)
                .ignoreIfNull();

        final Integer stabilityEnumId = this.fromApiJsonHelper.extractIntegerNamed(IncomeExpenseApiConstants.stabilityEnumIdParamName,
                element, locale);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.stabilityEnumIdParamName).value(stabilityEnumId).ignoreIfNull();

        final BigDecimal defaultIncome = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                IncomeExpenseApiConstants.defaultIncomeParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.defaultIncomeParamName).value(defaultIncome).ignoreIfNull()
                .positiveAmount();

        final BigDecimal defaultExpense = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                IncomeExpenseApiConstants.defaultExpenseParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.defaultExpenseParamName).value(defaultExpense).ignoreIfNull()
                .positiveAmount();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(IncomeExpenseApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                IncomeExpenseApiConstants.UPDATE_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(IncomeExpenseApiConstants.INCOME_EXPENSE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final String name = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);

        final String description = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.descriptionParamName).value(description).ignoreIfNull()
                .notExceedingLengthOf(500);

        final Boolean isQuantifierNeeded = this.fromApiJsonHelper.extractBooleanNamed(
                IncomeExpenseApiConstants.isQuantifierNeededParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.isQuantifierNeededParamName).value(isQuantifierNeeded).ignoreIfNull();

        final String quantifierLabel = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.quantifierLabelParamName,
                element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.quantifierLabelParamName).value(quantifierLabel).ignoreIfNull()
                .notExceedingLengthOf(100);

        final Boolean isCaptureMonthWiseIncome = this.fromApiJsonHelper.extractBooleanNamed(
                IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName).value(isCaptureMonthWiseIncome)
                .ignoreIfNull();

        final Integer stabilityEnumId = this.fromApiJsonHelper.extractIntegerNamed(IncomeExpenseApiConstants.stabilityEnumIdParamName,
                element, locale);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.stabilityEnumIdParamName).value(stabilityEnumId).ignoreIfNull();

        final BigDecimal defaultIncome = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                IncomeExpenseApiConstants.defaultIncomeParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.defaultIncomeParamName).value(defaultIncome).ignoreIfNull()
                .positiveAmount();

        final BigDecimal defaultExpense = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                IncomeExpenseApiConstants.defaultExpenseParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.defaultExpenseParamName).value(defaultExpense).ignoreIfNull()
                .positiveAmount();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(IncomeExpenseApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(IncomeExpenseApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}