package com.finflux.portfolio.cashflow.service;

import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.api.CashFlowCategoryApiConstants;
import com.finflux.portfolio.cashflow.domain.CashFlowCategory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class CashFlowCategoryDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CashFlowCategoryDataAssembler(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public CashFlowCategory assembleCreateForm(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final String name = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.nameParamName, element);
        final String shortName = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.shortNameParamName, element);
        final String description = this.fromApiJsonHelper.extractStringNamed(CashFlowCategoryApiConstants.descriptionParamName, element);
        final Integer categoryEnumId = this.fromApiJsonHelper.extractIntegerNamed(CashFlowCategoryApiConstants.categoryEnumIdParamName, element,
                locale);
        final Integer typeEnumId = this.fromApiJsonHelper.extractIntegerNamed(CashFlowCategoryApiConstants.typeEnumIdParamName, element, locale);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(CashFlowCategoryApiConstants.isActiveParamName, element);
        if (isActive == null) {
            isActive = true;
        }
        return CashFlowCategory.create(name, shortName, description, categoryEnumId, typeEnumId, isActive);
    }

    public Map<String, Object> assembleUpdateForm(final CashFlowCategory cashFlowCategory, final JsonCommand command) {
        return cashFlowCategory.update(command);
    }
}