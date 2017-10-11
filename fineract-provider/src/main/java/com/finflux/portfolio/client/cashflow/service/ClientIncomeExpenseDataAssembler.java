package com.finflux.portfolio.client.cashflow.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.domain.FamilyDetail;
import com.finflux.familydetail.domain.FamilyDetailsRepository;
import com.finflux.portfolio.cashflow.domain.IncomeExpense;
import com.finflux.portfolio.cashflow.domain.IncomeExpenseRepositoryWrapper;
import com.finflux.portfolio.client.cashflow.api.ClientIncomeExpenseApiConstants;
import com.finflux.portfolio.client.cashflow.domain.ClientIncomeExpense;
import com.finflux.portfolio.client.cashflow.domain.ClientMonthWiseIncomeExpense;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ClientIncomeExpenseDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final FamilyDetailsRepository familyDetailsRepository;
    private final IncomeExpenseRepositoryWrapper incomeExpenseRepository;

    @Autowired
    public ClientIncomeExpenseDataAssembler(final FromJsonHelper fromApiJsonHelper, final FamilyDetailsRepository familyDetailsRepository,
            final IncomeExpenseRepositoryWrapper incomeExpenseRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.familyDetailsRepository = familyDetailsRepository;
        this.incomeExpenseRepository = incomeExpenseRepository;
    }

    public ClientIncomeExpense assembleCreateForm(final Client client, final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        FamilyDetail familyDetail = null;
        final Long familyDetailsId = this.fromApiJsonHelper.extractLongNamed(ClientIncomeExpenseApiConstants.familyDetailsIdParamName,
                element);
        if (familyDetailsId != null) {
            familyDetail = this.familyDetailsRepository.findOne(familyDetailsId);
        }
        final Long incomeExpenseId = this.fromApiJsonHelper.extractLongNamed(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName,
                element);
        IncomeExpense incomeExpense = null;
        if (incomeExpenseId != null) {
            incomeExpense = this.incomeExpenseRepository.findOneWithNotFoundDetection(incomeExpenseId);
        }
        final BigDecimal quintity = this.fromApiJsonHelper.extractBigDecimalNamed(ClientIncomeExpenseApiConstants.quintityParamName,
                element, locale);
        final BigDecimal totalIncome = this.fromApiJsonHelper.extractBigDecimalNamed(ClientIncomeExpenseApiConstants.totalIncomeParamName,
                element, locale);
        final BigDecimal totalExpense = this.fromApiJsonHelper.extractBigDecimalNamed(
                ClientIncomeExpenseApiConstants.totalExpenseParamName, element, locale);
        final Boolean isMonthWiseIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName, element);
        final Boolean isPrimaryIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName, element);
        final Boolean isRemmitanceIncome = this.fromApiJsonHelper.extractBooleanNamed(
                ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName, element);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ClientIncomeExpenseApiConstants.isActiveParamName, element);
        if (isActive == null) {
            isActive = true;
        }
        final ClientIncomeExpense clientIncomeExpense = ClientIncomeExpense.create(client, familyDetail, incomeExpense, quintity,
                totalIncome, totalExpense, isMonthWiseIncome, isPrimaryIncome, isActive,isRemmitanceIncome);
        final List<ClientMonthWiseIncomeExpense> clientMonthWiseIncomeExpense = assembleClientMonthWiseIncomeExpense(clientIncomeExpense,
                element);
        if (clientIncomeExpense != null && clientMonthWiseIncomeExpense != null && clientMonthWiseIncomeExpense.size() > 0) {
            clientIncomeExpense.addAllClientMonthWiseIncomeExpense(clientMonthWiseIncomeExpense);
        }
        return clientIncomeExpense;
    }

    @SuppressWarnings("unused")
    private List<ClientMonthWiseIncomeExpense> assembleClientMonthWiseIncomeExpense(final ClientIncomeExpense clientIncomeExpense,
            final JsonElement element) {
        final List<ClientMonthWiseIncomeExpense> clientMonthWiseIncomeExpense = new ArrayList<ClientMonthWiseIncomeExpense>();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final JsonArray clientMonthWiseIncomeExpenseArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                ClientIncomeExpenseApiConstants.clientMonthWiseIncomeExpenseParamName, element);
        if (clientMonthWiseIncomeExpenseArray != null && clientMonthWiseIncomeExpenseArray.size() > 0) {
            for (int i = 0; i < clientMonthWiseIncomeExpenseArray.size(); i++) {
                final JsonObject object = clientMonthWiseIncomeExpenseArray.get(i).getAsJsonObject();
                final Integer month = this.fromApiJsonHelper.extractIntegerNamed(ClientIncomeExpenseApiConstants.monthParamName, object,
                        locale);
                final Integer year = this.fromApiJsonHelper.extractIntegerNamed(ClientIncomeExpenseApiConstants.yearParamName, object,
                        locale);
                final BigDecimal incomeAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                        ClientIncomeExpenseApiConstants.incomeAmountParamName, object, locale);
                final BigDecimal expenseAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                        ClientIncomeExpenseApiConstants.expenseAmountParamName, object, locale);
                Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ClientIncomeExpenseApiConstants.isActiveParamName, object);
                if (isActive == null) {
                    isActive = true;
                }
                final ClientMonthWiseIncomeExpense clientMonthWiseIncomeExpenseObject = ClientMonthWiseIncomeExpense.create(
                        clientIncomeExpense, month, year, incomeAmount, expenseAmount, isActive);
                clientMonthWiseIncomeExpense.add(clientMonthWiseIncomeExpenseObject);
            }
        }
        return clientMonthWiseIncomeExpense;
    }

    public Map<String, Object> assembleUpdateForm(final ClientIncomeExpense clientIncomeExpense, final JsonCommand command) {
        final Map<String, Object> changes = clientIncomeExpense.update(command);
        final List<ClientMonthWiseIncomeExpense> clientMonthWiseIncomeExpense = assembleClientMonthWiseIncomeExpense(clientIncomeExpense,
                command.parsedJson());
        clientIncomeExpense.addAllClientMonthWiseIncomeExpense(clientMonthWiseIncomeExpense);
        if (changes.containsKey(ClientIncomeExpenseApiConstants.familyDetailsIdParamName)) {
            final Long familyDetailsId = (Long) changes.get(ClientIncomeExpenseApiConstants.familyDetailsIdParamName);
            final FamilyDetail familyDetail = this.familyDetailsRepository.findOne(familyDetailsId);
            clientIncomeExpense.updateFamilyDetail(familyDetail);
        }
        if (changes.containsKey(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName)) {
            final Long incomeExpenseId = (Long) changes.get(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName);
            final IncomeExpense incomeExpense = this.incomeExpenseRepository.findOneWithNotFoundDetection(incomeExpenseId);
            clientIncomeExpense.updateIncomeExpense(incomeExpense);
        }
        return changes;
    }
}