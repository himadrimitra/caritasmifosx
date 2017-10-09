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
package org.apache.fineract.portfolio.savings.domain;

import static org.apache.fineract.portfolio.interestratechart.InterestRateChartApiConstants.INTERESTRATE_CHART_RESOURCE_NAME;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.allowOverdraftParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.chargesParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.currencyCodeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.daysToDormancyParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.daysToEscheatParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.daysToInactiveParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.descriptionParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.digitsAfterDecimalParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.effectiveFromDateParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.enforceMinRequiredBalanceParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.externalIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.floatingInterestRateChartParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.idParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.inMultiplesOfParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestCalculationDaysInYearTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestCalculationTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestCompoundingPeriodTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestPostingPeriodTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.interestRateParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.isDeletedParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.isDormancyTrackingActiveParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.lockinPeriodFrequencyParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.lockinPeriodFrequencyTypeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.minBalanceForInterestCalculationParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.minOverdraftForInterestCalculationParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.minRequiredBalanceParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.minRequiredOpeningBalanceParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.nameParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.nominalAnnualInterestRateOverdraftParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.nominalAnnualInterestRateParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.overdraftLimitParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.shortNameParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.taxGroupIdParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.withHoldTaxParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.withdrawalFeeForTransfersParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.chartPeriodOverlapped;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.isInterestCalculationFromProductChartParamName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.exception.ChargeCannotBeAppliedToException;
import org.apache.fineract.portfolio.interestratechart.domain.FloatingInterestRateChart;
import org.apache.fineract.portfolio.interestratechart.exception.InterestRateChartNotFoundException;
import org.apache.fineract.portfolio.loanproduct.exception.InvalidCurrencyException;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationType;
import org.apache.fineract.portfolio.savings.SavingsPeriodFrequencyType;
import org.apache.fineract.portfolio.savings.SavingsPostingInterestPeriodType;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;
import org.apache.fineract.portfolio.tax.domain.TaxGroupRepositoryWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class SavingsProductAssembler {

    private final ChargeRepositoryWrapper chargeRepository;
    private final TaxGroupRepositoryWrapper taxGroupRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SavingsProductAssembler(final ChargeRepositoryWrapper chargeRepository, final TaxGroupRepositoryWrapper taxGroupRepository,
            final FromJsonHelper fromApiJsonHelper) {
        this.chargeRepository = chargeRepository;
        this.taxGroupRepository = taxGroupRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public SavingsProduct createAssemble(final JsonCommand command) {

        final String name = command.stringValueOfParameterNamed(nameParamName);
        final String shortName = command.stringValueOfParameterNamed(shortNameParamName);
        final String description = command.stringValueOfParameterNamed(descriptionParamName);
        final String externalId = command.stringValueOfParameterNamed(externalIdParamName);

        final String currencyCode = command.stringValueOfParameterNamed(currencyCodeParamName);
        final Integer digitsAfterDecimal = command.integerValueOfParameterNamed(digitsAfterDecimalParamName);
        final Integer inMultiplesOf = command.integerValueOfParameterNamed(inMultiplesOfParamName);
        final MonetaryCurrency currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal, inMultiplesOf);

        final BigDecimal interestRate = command.bigDecimalValueOfParameterNamed(nominalAnnualInterestRateParamName);

        SavingsCompoundingInterestPeriodType interestCompoundingPeriodType = null;
        final Integer interestPeriodTypeValue = command.integerValueOfParameterNamed(interestCompoundingPeriodTypeParamName);
        if (interestPeriodTypeValue != null) {
            interestCompoundingPeriodType = SavingsCompoundingInterestPeriodType.fromInt(interestPeriodTypeValue);
        }

        SavingsPostingInterestPeriodType interestPostingPeriodType = null;
        final Integer interestPostingPeriodTypeValue = command.integerValueOfParameterNamed(interestPostingPeriodTypeParamName);
        if (interestPostingPeriodTypeValue != null) {
            interestPostingPeriodType = SavingsPostingInterestPeriodType.fromInt(interestPostingPeriodTypeValue);
        }

        SavingsInterestCalculationType interestCalculationType = null;
        final Integer interestCalculationTypeValue = command.integerValueOfParameterNamed(interestCalculationTypeParamName);
        if (interestCalculationTypeValue != null) {
            interestCalculationType = SavingsInterestCalculationType.fromInt(interestCalculationTypeValue);
        }

        SavingsInterestCalculationDaysInYearType interestCalculationDaysInYearType = null;
        final Integer interestCalculationDaysInYearTypeValue = command
                .integerValueOfParameterNamed(interestCalculationDaysInYearTypeParamName);
        if (interestCalculationDaysInYearTypeValue != null) {
            interestCalculationDaysInYearType = SavingsInterestCalculationDaysInYearType.fromInt(interestCalculationDaysInYearTypeValue);
        }

        final BigDecimal minRequiredOpeningBalance = command
                .bigDecimalValueOfParameterNamedDefaultToNullIfZero(minRequiredOpeningBalanceParamName);

        final Integer lockinPeriodFrequency = command.integerValueOfParameterNamedDefaultToNullIfZero(lockinPeriodFrequencyParamName);
        SavingsPeriodFrequencyType lockinPeriodFrequencyType = null;
        final Integer lockinPeriodFrequencyTypeValue = command.integerValueOfParameterNamed(lockinPeriodFrequencyTypeParamName);
        if (lockinPeriodFrequencyTypeValue != null) {
            lockinPeriodFrequencyType = SavingsPeriodFrequencyType.fromInt(lockinPeriodFrequencyTypeValue);
        }

        boolean iswithdrawalFeeApplicableForTransfer = false;
        if (command.parameterExists(withdrawalFeeForTransfersParamName)) {
            iswithdrawalFeeApplicableForTransfer = command.booleanPrimitiveValueOfParameterNamed(withdrawalFeeForTransfersParamName);
        }

        final AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(command.integerValueOfParameterNamed("accountingRule"));

        // Savings product charges
        final Set<Charge> charges = assembleListOfSavingsProductCharges(command, currencyCode);

        boolean allowOverdraft = false;
        if (command.parameterExists(allowOverdraftParamName)) {
            allowOverdraft = command.booleanPrimitiveValueOfParameterNamed(allowOverdraftParamName);
        }

        BigDecimal overdraftLimit = BigDecimal.ZERO;
        if (command.parameterExists(overdraftLimitParamName)) {
            overdraftLimit = command.bigDecimalValueOfParameterNamed(overdraftLimitParamName);
        }

        BigDecimal nominalAnnualInterestRateOverdraft = BigDecimal.ZERO;
        if (command.parameterExists(nominalAnnualInterestRateOverdraftParamName)) {
            nominalAnnualInterestRateOverdraft = command.bigDecimalValueOfParameterNamed(nominalAnnualInterestRateOverdraftParamName);
        }

        BigDecimal minOverdraftForInterestCalculation = BigDecimal.ZERO;
        if (command.parameterExists(minOverdraftForInterestCalculationParamName)) {
            minOverdraftForInterestCalculation = command.bigDecimalValueOfParameterNamed(minOverdraftForInterestCalculationParamName);
        }

        boolean enforceMinRequiredBalance = false;
        if (command.parameterExists(enforceMinRequiredBalanceParamName)) {
            enforceMinRequiredBalance = command.booleanPrimitiveValueOfParameterNamed(enforceMinRequiredBalanceParamName);
        }

        BigDecimal minRequiredBalance = BigDecimal.ZERO;
        if (command.parameterExists(minRequiredBalanceParamName)) {
            minRequiredBalance = command.bigDecimalValueOfParameterNamed(minRequiredBalanceParamName);
        }
        final BigDecimal minBalanceForInterestCalculation = command
                .bigDecimalValueOfParameterNamedDefaultToNullIfZero(minBalanceForInterestCalculationParamName);

        boolean withHoldTax = command.booleanPrimitiveValueOfParameterNamed(withHoldTaxParamName);
        final TaxGroup taxGroup = assembleTaxGroup(command);
        
        final Boolean isDormancyTrackingActive = command.booleanObjectValueOfParameterNamed(isDormancyTrackingActiveParamName);
        final Long daysToInactive = command.longValueOfParameterNamed(daysToInactiveParamName);
        final Long daysToDormancy = command.longValueOfParameterNamed(daysToDormancyParamName);
        final Long daysToEscheat = command.longValueOfParameterNamed(daysToEscheatParamName);
        final boolean isInterestCalculationFromProduct = command.booleanPrimitiveValueOfParameterNamed(isInterestCalculationFromProductChartParamName);

        final SavingsProduct savingsProduct = SavingsProduct.createNew(name, shortName, description, currency, interestRate,
                interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType, iswithdrawalFeeApplicableForTransfer,
                accountingRuleType, charges, allowOverdraft, overdraftLimit, enforceMinRequiredBalance, minRequiredBalance,
                minBalanceForInterestCalculation, nominalAnnualInterestRateOverdraft, minOverdraftForInterestCalculation, withHoldTax,
                taxGroup, isDormancyTrackingActive, daysToInactive, daysToDormancy, daysToEscheat, externalId, isInterestCalculationFromProduct);
        
        /**
         * Construct Savings Product Drawing Power Details
         */
        final SavingsProductDrawingPowerDetails savingsProductDrawingPowerDetails = constructSavingsProductDrawingPowerDetailsObject(
                savingsProduct, allowOverdraft, command);
        savingsProduct.updateSavingsProductDrawingPowerDetails(savingsProductDrawingPowerDetails);
        final List<FloatingInterestRateChart> floatingInterestRateChart = constructFloatingInterestRateChart(command.json());
        savingsProduct.updateFloatingInterestRateChart(floatingInterestRateChart);
        return savingsProduct;
    }

    private SavingsProductDrawingPowerDetails constructSavingsProductDrawingPowerDetailsObject(final SavingsProduct savingsProduct,
            final boolean allowOverdraft, final JsonCommand command) {
        SavingsProductDrawingPowerDetails savingsProductDrawingPowerDetails = null;
        if (allowOverdraft) {
            if (command.parameterExists(SavingsApiConstants.allowDpLimitParamName)) {
                final boolean allowDpLimit = command.booleanPrimitiveValueOfParameterNamed(SavingsApiConstants.allowDpLimitParamName);
                if (allowDpLimit) {
                    final Integer frequencyType = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyTypeParamName);
                    final Integer frequencyInterval = command
                            .integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyIntervalParamName);
                    final Integer frequencyNthDay = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyNthDayParamName);
                    final Integer frequencyDayOfWeekType = command
                            .integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyDayOfWeekTypeParamName);
                    final Integer frequencyOnDay = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyOnDayParamName);
                    savingsProductDrawingPowerDetails = SavingsProductDrawingPowerDetails.create(savingsProduct, frequencyType,
                            frequencyInterval, frequencyNthDay, frequencyDayOfWeekType, frequencyOnDay);
                }
            }
        }
        return savingsProductDrawingPowerDetails;
    }

    public Set<Charge> assembleListOfSavingsProductCharges(final JsonCommand command, final String savingsProductCurrencyCode) {

        final Set<Charge> charges = new HashSet<>();

        if (command.parameterExists(chargesParamName)) {
            final JsonArray chargesArray = command.arrayOfParameterNamed(chargesParamName);
            if (chargesArray != null) {
                for (int i = 0; i < chargesArray.size(); i++) {

                    final JsonObject jsonObject = chargesArray.get(i).getAsJsonObject();
                    if (jsonObject.has(idParamName)) {
                        final Long id = jsonObject.get(idParamName).getAsLong();

                        final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(id);

                        if (!charge.isSavingsCharge()) {
                            final String errorMessage = "Charge with identifier " + charge.getId()
                                    + " cannot be applied to Savings product.";
                            throw new ChargeCannotBeAppliedToException("savings.product", errorMessage, charge.getId());
                        }

                        if (!savingsProductCurrencyCode.equals(charge.getCurrencyCode())) {
                            final String errorMessage = "Charge and Savings Product must have the same currency.";
                            throw new InvalidCurrencyException("charge", "attach.to.savings.product", errorMessage);
                        }
                        charges.add(charge);
                    }
                }
            }
        }

        return charges;
    }

    public TaxGroup assembleTaxGroup(final JsonCommand command) {
        final Long taxGroupId = command.longValueOfParameterNamed(taxGroupIdParamName);
        TaxGroup taxGroup = null;
        if (taxGroupId != null) {
            taxGroup = this.taxGroupRepository.findOneWithNotFoundDetection(taxGroupId);
        }
        return taxGroup;
    }
    
    private List<FloatingInterestRateChart> constructFloatingInterestRateChart(String json) {
        List<FloatingInterestRateChart> floatingInterestRateCharts = new ArrayList<FloatingInterestRateChart>();
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
        if (element.isJsonObject()) {
            if (topLevelJsonElement.has(floatingInterestRateChartParamName)) {
                final JsonArray array = topLevelJsonElement.get(floatingInterestRateChartParamName).getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject interestrateChartElement = array.get(i).getAsJsonObject();
                    FloatingInterestRateChart floatingInterestRateChart = createFloatingInterestRateChart(interestrateChartElement, locale,
                            dateFormat);
                    floatingInterestRateCharts.add(floatingInterestRateChart);
                }
            }
        }
        if(floatingInterestRateCharts.size() > 0){
            validateFloatingInterestRateChart(floatingInterestRateCharts);
        }
        return floatingInterestRateCharts;
    }

    private FloatingInterestRateChart createFloatingInterestRateChart(final JsonObject interestrateChartElement, final Locale locale,
            final String dateFormat) {
        final LocalDate effectiveFromDate = this.fromApiJsonHelper.extractLocalDateNamed(effectiveFromDateParamName,
                interestrateChartElement, dateFormat, locale);
        final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalNamed(interestRateParamName,
                interestrateChartElement, locale);
        return new FloatingInterestRateChart(effectiveFromDate, interestRate);

    }
    
    public List<FloatingInterestRateChart> updateFloatingInterestRateChart(final SavingsProduct product, Map<String, Object> changes,
            final String json) {
        List<FloatingInterestRateChart> existingFloatingInterestRateCharts = product.getFloatingInterestRateChart();
        Map<Long, FloatingInterestRateChart> existigFloatingInterestRateChartMap = new HashMap<>();
        if (existingFloatingInterestRateCharts.size() > 0) {
            for (FloatingInterestRateChart floatingInterestRateChart : existingFloatingInterestRateCharts) {
                existigFloatingInterestRateChartMap.put(floatingInterestRateChart.getId(), floatingInterestRateChart);
            }
        }
        final List<Map<String, Object>> floatingInterestRateChartChangesList = new ArrayList<>();
        changes.put(floatingInterestRateChartParamName, floatingInterestRateChartChangesList);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
        if (element.isJsonObject()) {
            if (topLevelJsonElement.has(floatingInterestRateChartParamName)) {
                final JsonArray array = topLevelJsonElement.get(floatingInterestRateChartParamName).getAsJsonArray();

                for (int i = 0; i < array.size(); i++) {
                    final JsonObject interestrateChartElement = array.get(i).getAsJsonObject();
                    final Long id = this.fromApiJsonHelper.extractLongNamed(idParamName, interestrateChartElement);
                    final Boolean isDeleted = this.fromApiJsonHelper.extractBooleanNamed(isDeletedParamName, interestrateChartElement);
                    final Map<String, Object> changeDetails = new HashMap<>(2);
                    if (id == null) {
                        FloatingInterestRateChart floatingInterestRateChart = createFloatingInterestRateChart(interestrateChartElement,
                                locale, dateFormat);
                        existingFloatingInterestRateCharts.add(floatingInterestRateChart);
                        changeDetails.put(interestRateParamName, floatingInterestRateChart.getInterestRate());
                        changeDetails.put(effectiveFromDateParamName, floatingInterestRateChart.getEffectiveFromAsLocalDate());
                    } else {
                        FloatingInterestRateChart floatingInterestRateChart = existigFloatingInterestRateChartMap.get(id);
                        if (floatingInterestRateChart == null) { throw new InterestRateChartNotFoundException(id); }
                        if (isDeleted != null && isDeleted) {
                            validateForDelete(floatingInterestRateChart);
                            existingFloatingInterestRateCharts.remove(floatingInterestRateChart);
                            changeDetails.put("deleted", true);
                            changeDetails.put(idParamName, id);

                        }
                    }
                    floatingInterestRateChartChangesList.add(changeDetails);
                }
            }
        }
        if (existingFloatingInterestRateCharts.size() > 0) {
            validateFloatingInterestRateChart(existingFloatingInterestRateCharts);
        }
        return existingFloatingInterestRateCharts;
    }
    
    private void validateFloatingInterestRateChart(List<FloatingInterestRateChart> floatingInterestRateCharts) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(INTERESTRATE_CHART_RESOURCE_NAME);
        List<LocalDate> effectiveFromDateList = new ArrayList<>();
        for (FloatingInterestRateChart chart : floatingInterestRateCharts) {
            if (effectiveFromDateList.contains(chart.getEffectiveFromAsLocalDate())) {
                baseDataValidator.failWithCodeNoParameterAddedToErrorCode(chartPeriodOverlapped);
                throw new PlatformApiDataValidationException(dataValidationErrors);
            }
            effectiveFromDateList.add(chart.getEffectiveFromAsLocalDate());
        }

    }
    
    private void validateForDelete(FloatingInterestRateChart floatingInterestRateChart) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(INTERESTRATE_CHART_RESOURCE_NAME);
        final LocalDate effectiveFromDate = floatingInterestRateChart.getEffectiveFromAsLocalDate();
        baseDataValidator.reset().parameter(effectiveFromDateParamName).value(effectiveFromDate)
                .validateDateAfter(DateUtils.getLocalDateOfTenant());
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }
    
    
    
}