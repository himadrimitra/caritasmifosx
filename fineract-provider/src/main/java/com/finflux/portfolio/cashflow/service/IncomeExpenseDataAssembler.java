package com.finflux.portfolio.cashflow.service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.api.IncomeExpenseApiConstants;
import com.finflux.portfolio.cashflow.domain.CashFlowCategory;
import com.finflux.portfolio.cashflow.domain.CashFlowCategoryRepositoryWrapper;
import com.finflux.portfolio.cashflow.domain.IncomeExpense;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class IncomeExpenseDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CashFlowCategoryRepositoryWrapper cashFlowCategoryRepository;

    @Autowired
    public IncomeExpenseDataAssembler(final FromJsonHelper fromApiJsonHelper,
            final CashFlowCategoryRepositoryWrapper cashFlowCategoryRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.cashFlowCategoryRepository = cashFlowCategoryRepository;
    }

    public IncomeExpense assembleCreateForm(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long cashFlowCategoryId = this.fromApiJsonHelper.extractLongNamed(IncomeExpenseApiConstants.cashFlowCategoryIdParamName,
                element);
        final CashFlowCategory cashFlowCategory = this.cashFlowCategoryRepository.findOneWithNotFoundDetection(cashFlowCategoryId);
        final String name = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.nameParamName, element);
        final String description = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.descriptionParamName, element);
        final Boolean isQuantifierNeeded = this.fromApiJsonHelper.extractBooleanNamed(
                IncomeExpenseApiConstants.isQuantifierNeededParamName, element);
        final String quantifierLabel = this.fromApiJsonHelper.extractStringNamed(IncomeExpenseApiConstants.quantifierLabelParamName,
                element);
        final Boolean isCaptureMonthWiseIncome = this.fromApiJsonHelper.extractBooleanNamed(
                IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName, element);
        final Integer stabilityEnumId = this.fromApiJsonHelper.extractIntegerNamed(IncomeExpenseApiConstants.stabilityEnumIdParamName,
                element, locale);
        final BigDecimal defaultIncome = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                IncomeExpenseApiConstants.defaultIncomeParamName, element);
        final BigDecimal defaultExpense = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                IncomeExpenseApiConstants.defaultExpenseParamName, element);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(IncomeExpenseApiConstants.isActiveParamName, element);
        if (isActive == null) {
            isActive = true;
        }
        return IncomeExpense.create(cashFlowCategory, name, description, isQuantifierNeeded, quantifierLabel, isCaptureMonthWiseIncome,
                stabilityEnumId, defaultIncome, defaultExpense, isActive);
    }

    public Map<String, Object> assembleUpdateForm(final IncomeExpense incomeExpense, final JsonCommand command) {
        return incomeExpense.update(command);
    }
}