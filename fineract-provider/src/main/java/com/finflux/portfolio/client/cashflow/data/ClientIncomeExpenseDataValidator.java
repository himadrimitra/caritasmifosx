package com.finflux.portfolio.client.cashflow.data;

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

import com.finflux.portfolio.client.cashflow.api.ClientIncomeExpenseApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class ClientIncomeExpenseDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientIncomeExpenseDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientIncomeExpenseApiConstants.CREATE_CLIENT_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientIncomeExpenseApiConstants.CLIENT_INCOME_EXPENSE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long familyDetailsId = this.fromApiJsonHelper.extractLongNamed(ClientIncomeExpenseApiConstants.familyDetailsIdParamName,
                element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.familyDetailsIdParamName).value(familyDetailsId).ignoreIfNull()
                .longGreaterThanZero();

        final Long incomeExpenseId = this.fromApiJsonHelper.extractLongNamed(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName,
                element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName).value(incomeExpenseId).notNull()
                .longGreaterThanZero();

        final BigDecimal quintity = this.fromApiJsonHelper.extractBigDecimalNamed(ClientIncomeExpenseApiConstants.quintityParamName,
                element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.quintityParamName).value(quintity).ignoreIfNull();

        final BigDecimal totalIncome = this.fromApiJsonHelper.extractBigDecimalNamed(ClientIncomeExpenseApiConstants.totalIncomeParamName,
                element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.totalIncomeParamName).value(totalIncome).ignoreIfNull();

        final BigDecimal totalExpense = this.fromApiJsonHelper.extractBigDecimalNamed(
                ClientIncomeExpenseApiConstants.totalExpenseParamName, element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.totalExpenseParamName).value(totalExpense).ignoreIfNull();

        final Boolean isMonthWiseIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName).value(isMonthWiseIncome)
                .ignoreIfNull();

        final Boolean isPrimaryIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName).value(isPrimaryIncome).ignoreIfNull();

        final Boolean isRemmitanceIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName).value(isRemmitanceIncome).ignoreIfNull();
        
        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ClientIncomeExpenseApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        final JsonArray clientMonthWiseIncomeExpense = this.fromApiJsonHelper.extractJsonArrayNamed(
                ClientIncomeExpenseApiConstants.clientMonthWiseIncomeExpenseParamName, element);

        if (clientMonthWiseIncomeExpense != null && clientMonthWiseIncomeExpense.size() > 0) {
            for (int i = 0; i < clientMonthWiseIncomeExpense.size(); i++) {
                final JsonObject clientMonthWiseIncomeExpenseObject = clientMonthWiseIncomeExpense.get(i).getAsJsonObject();
                validateForClientMonthWiseIncomeExpense(clientMonthWiseIncomeExpenseObject, baseDataValidator);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateForClientMonthWiseIncomeExpense(final JsonObject element, final DataValidatorBuilder baseDataValidator) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer month = this.fromApiJsonHelper.extractIntegerNamed(ClientIncomeExpenseApiConstants.monthParamName, element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.monthParamName).value(month).notNull().inMinMaxRange(1, 12);

        final Integer year = this.fromApiJsonHelper.extractIntegerNamed(ClientIncomeExpenseApiConstants.yearParamName, element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.yearParamName).value(year).notNull().inMinMaxRange(1900, 9999);

        final BigDecimal incomeAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                ClientIncomeExpenseApiConstants.incomeAmountParamName, element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.incomeAmountParamName).value(incomeAmount).ignoreIfNull();

        final BigDecimal expenseAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                ClientIncomeExpenseApiConstants.expenseAmountParamName, element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.expenseAmountParamName).value(expenseAmount).ignoreIfNull();
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientIncomeExpenseApiConstants.UPDATE_CLIENT_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientIncomeExpenseApiConstants.CLIENT_INCOME_EXPENSE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long familyDetailsId = this.fromApiJsonHelper.extractLongNamed(ClientIncomeExpenseApiConstants.familyDetailsIdParamName,
                element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.familyDetailsIdParamName).value(familyDetailsId).ignoreIfNull()
                .longGreaterThanZero();

        final Long incomeExpenseId = this.fromApiJsonHelper.extractLongNamed(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName,
                element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName).value(incomeExpenseId).notNull()
                .longGreaterThanZero();

        final BigDecimal quintity = this.fromApiJsonHelper.extractBigDecimalNamed(ClientIncomeExpenseApiConstants.quintityParamName,
                element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.quintityParamName).value(quintity).ignoreIfNull();

        final BigDecimal totalIncome = this.fromApiJsonHelper.extractBigDecimalNamed(ClientIncomeExpenseApiConstants.totalIncomeParamName,
                element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.totalIncomeParamName).value(totalIncome).ignoreIfNull();

        final BigDecimal totalExpense = this.fromApiJsonHelper.extractBigDecimalNamed(
                ClientIncomeExpenseApiConstants.totalExpenseParamName, element, locale);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.totalExpenseParamName).value(totalExpense).ignoreIfNull();

        final Boolean isMonthWiseIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName).value(isMonthWiseIncome)
                .ignoreIfNull();

        final Boolean isPrimaryIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName).value(isPrimaryIncome).ignoreIfNull();
        
        final Boolean isRemmitanceIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName).value(isRemmitanceIncome).ignoreIfNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ClientIncomeExpenseApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(ClientIncomeExpenseApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        final JsonArray clientMonthWiseIncomeExpense = this.fromApiJsonHelper.extractJsonArrayNamed(
                ClientIncomeExpenseApiConstants.clientMonthWiseIncomeExpenseParamName, element);

        if (clientMonthWiseIncomeExpense != null && clientMonthWiseIncomeExpense.size() > 0) {
            for (int i = 0; i < clientMonthWiseIncomeExpense.size(); i++) {
                final JsonObject clientMonthWiseIncomeExpenseObject = clientMonthWiseIncomeExpense.get(i).getAsJsonObject();
                validateForClientMonthWiseIncomeExpense(clientMonthWiseIncomeExpenseObject, baseDataValidator);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}