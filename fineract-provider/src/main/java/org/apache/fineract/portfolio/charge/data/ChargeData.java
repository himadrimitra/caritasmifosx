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
package org.apache.fineract.portfolio.charge.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountChargeData;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountChargeData;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

/**
 * Immutable data object for charge data.
 */
public class ChargeData implements Comparable<ChargeData>, Serializable {

    private final Long id;
    private final String name;
    private final boolean active;
    private final boolean penalty;
    private final CurrencyData currency;
    private final BigDecimal amount;
    private final EnumOptionData chargeTimeType;
    private final EnumOptionData chargeAppliesTo;
    private final EnumOptionData chargeCalculationType;
    private final EnumOptionData chargePaymentMode;
    private final MonthDay feeOnMonthDay;
    private final Integer feeInterval;
    private final BigDecimal minCap;
    private final BigDecimal maxCap;
    private final EnumOptionData feeFrequency;
    private final GLAccountData incomeOrLiabilityAccount;
    private final TaxGroupData taxGroup;
    private final EnumOptionData percentageType;
    private final EnumOptionData percentagePeriodType;
    private final ChargeOverdueData chargeOverdueData;
    private final ChargeInvestmentData chargeInvestmentData;

    private final Collection<CurrencyData> currencyOptions;
    private final List<EnumOptionData> chargeCalculationTypeOptions;//
    private final List<EnumOptionData> chargeAppliesToOptions;//
    private final List<EnumOptionData> chargeTimeTypeOptions;//
    private final List<EnumOptionData> chargePaymetModeOptions;//

    private final List<EnumOptionData> loanChargeCalculationTypeOptions;
    private final List<EnumOptionData> loanChargeTimeTypeOptions;
    private final List<EnumOptionData> savingsChargeCalculationTypeOptions;
    private final List<EnumOptionData> savingsChargeTimeTypeOptions;
    private final List<EnumOptionData> clientChargeCalculationTypeOptions;
    private final List<EnumOptionData> clientChargeTimeTypeOptions;
    private final List<EnumOptionData> shareChargeCalculationTypeOptions;
    private final List<EnumOptionData> shareChargeTimeTypeOptions;

    private final Collection<EnumOptionData> loanFeeFrequencyOptions;

    private final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions;
    private final Collection<TaxGroupData> taxGroupOptions;
    private final boolean emiRoundingGoalSeek;
    private final boolean isGlimCharge;
    private final EnumOptionData glimChargeCalculation;
    private final List<EnumOptionData> glimChargeCalculationTypeOptions;
    private List<ChargeSlabData> slabs;
    private final boolean isCapitalized;
    private final List<EnumOptionData> slabChargeTypeOptions;

    private final Collection<EnumOptionData> percentageTypeOptions;
    private final Collection<EnumOptionData> percentagePeriodTypeOptions;
    private final Collection<EnumOptionData> penaltyGraceTypeOptions;
    
    private final List<EnumOptionData> investmentChargeCalculationTypeOptions;
    private final List<EnumOptionData> investmentChargeTimeTypeOptions;

    public static ChargeData template(final Collection<CurrencyData> currencyOptions,
            final List<EnumOptionData> chargeCalculationTypeOptions, final List<EnumOptionData> chargeAppliesToOptions,
            final List<EnumOptionData> chargeTimeTypeOptions, final List<EnumOptionData> chargePaymentModeOptions,
            final List<EnumOptionData> loansChargeCalculationTypeOptions, final List<EnumOptionData> loansChargeTimeTypeOptions,
            final List<EnumOptionData> savingsChargeCalculationTypeOptions, final List<EnumOptionData> savingsChargeTimeTypeOptions,
            final List<EnumOptionData> clientChargeCalculationTypeOptions, final List<EnumOptionData> clientChargeTimeTypeOptions,
            final Collection<EnumOptionData> loanFeeFrequencyOptions,
            final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions, final Collection<TaxGroupData> taxGroupOptions,
            final List<EnumOptionData> shareChargeCalculationTypeOptions, final List<EnumOptionData> shareChargeTimeTypeOptions,
            final List<EnumOptionData> glimChargeCalculationTypeOptions, final List<EnumOptionData> slabChargeTypeOptions,
            final Collection<EnumOptionData> percentageTypeOptions, final Collection<EnumOptionData> percentagePeriodTypeOptions,
            final Collection<EnumOptionData> penaltyGraceTypeOptions, List<EnumOptionData> investmentChargeCalculationTypeOptions,
            final List<EnumOptionData> investmentChargeTimeTypeOptions) {
        final GLAccountData account = null;
        final TaxGroupData taxGroupData = null;
        final boolean emiRoundingGoalSeek = false;
        final boolean isGlimCharge = false;
        final EnumOptionData glimChargeCalculationType = null;
        final boolean isCapitalized = false;
        final List<ChargeSlabData> slabs = null;
        final EnumOptionData percentageType = null;
        final EnumOptionData percentagePeriodType = null;
        final ChargeOverdueData chargeOverdueData = null;
        final ChargeInvestmentData chargeInvestmentData = null;
        return new ChargeData(null, null, null, null, null, null, null, null, false, false, taxGroupData, currencyOptions,
                chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions, chargePaymentModeOptions,
                loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions, savingsChargeCalculationTypeOptions,
                savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions, clientChargeTimeTypeOptions, null, null, null, null, null,
                loanFeeFrequencyOptions, account, incomeOrLiabilityAccountOptions, taxGroupOptions, shareChargeCalculationTypeOptions,
                shareChargeTimeTypeOptions, emiRoundingGoalSeek, isGlimCharge, glimChargeCalculationType, glimChargeCalculationTypeOptions,
                isCapitalized, slabs, slabChargeTypeOptions, percentageType, percentagePeriodType, chargeOverdueData, percentageTypeOptions,
                percentagePeriodTypeOptions, penaltyGraceTypeOptions, investmentChargeCalculationTypeOptions, investmentChargeTimeTypeOptions,
                chargeInvestmentData);
    }

    public static ChargeData withTemplate(final ChargeData charge, final ChargeData template) {
        return new ChargeData(charge.id, charge.name, charge.amount, charge.currency, charge.chargeTimeType, charge.chargeAppliesTo,
                charge.chargeCalculationType, charge.chargePaymentMode, charge.penalty, charge.active, charge.taxGroup,
                template.currencyOptions, template.chargeCalculationTypeOptions, template.chargeAppliesToOptions,
                template.chargeTimeTypeOptions, template.chargePaymetModeOptions, template.loanChargeCalculationTypeOptions,
                template.loanChargeTimeTypeOptions, template.savingsChargeCalculationTypeOptions, template.savingsChargeTimeTypeOptions,
                template.clientChargeCalculationTypeOptions, template.clientChargeTimeTypeOptions, charge.feeOnMonthDay, charge.feeInterval,
                charge.minCap, charge.maxCap, charge.feeFrequency, template.loanFeeFrequencyOptions, charge.incomeOrLiabilityAccount,
                template.incomeOrLiabilityAccountOptions, template.taxGroupOptions, template.shareChargeCalculationTypeOptions,
                template.shareChargeTimeTypeOptions, charge.emiRoundingGoalSeek, charge.isGlimCharge, charge.glimChargeCalculation,
                template.glimChargeCalculationTypeOptions, charge.isCapitalized, charge.getChargeSlabs(), template.slabChargeTypeOptions,
                charge.percentageType, charge.percentagePeriodType, charge.chargeOverdueData, template.percentageTypeOptions,
                template.percentagePeriodTypeOptions, template.penaltyGraceTypeOptions, template.getInvestmentChargeCalculationTypeOptions(),
                template.getInvestmentChargeTimeTypeOptions(), charge.getChargeInvestmentData());
    }

    public static ChargeData instance(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final EnumOptionData chargePaymentMode, final MonthDay feeOnMonthDay, final Integer feeInterval, final boolean penalty,
            final boolean active, final BigDecimal minCap, final BigDecimal maxCap, final EnumOptionData feeFrequency,
            final GLAccountData accountData, final TaxGroupData taxGroupData, final boolean emiRoundingGoalSeek, final boolean isGlimCharge,
            final EnumOptionData glimChargeCalculationType, final boolean isCapitalized, final EnumOptionData percentageType,
            final EnumOptionData percentagePeriodType, final ChargeOverdueData chargeOverdueData,
            final ChargeInvestmentData chargeInvestmentData) {

        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> chargeCalculationTypeOptions = null;
        final List<EnumOptionData> chargeAppliesToOptions = null;
        final List<EnumOptionData> chargeTimeTypeOptions = null;
        final List<EnumOptionData> chargePaymentModeOptions = null;
        final List<EnumOptionData> loansChargeCalculationTypeOptions = null;
        final List<EnumOptionData> loansChargeTimeTypeOptions = null;
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = null;
        final List<EnumOptionData> savingsChargeTimeTypeOptions = null;
        final Collection<EnumOptionData> loanFeeFrequencyOptions = null;
        final List<EnumOptionData> clientChargeCalculationTypeOptions = null;
        final List<EnumOptionData> clientChargeTimeTypeOptions = null;
        final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions = null;
        final List<EnumOptionData> shareChargeCalculationTypeOptions = null;
        final List<EnumOptionData> shareChargeTimeTypeOptions = null;
        final Collection<TaxGroupData> taxGroupOptions = null;
        final List<EnumOptionData> glimChargeCalculationTypeOptions = null;
        final List<ChargeSlabData> chargeSlabs = null;
        final List<EnumOptionData> slabChargeTypeOptions = null;
        final Collection<EnumOptionData> percentageTypeOptions = null;
        final Collection<EnumOptionData> percentagePeriodTypeOptions = null;
        final Collection<EnumOptionData> penaltyGraceTypeOptions = null;
        final List<EnumOptionData> investmentChargeCalculationTypeOptions = null;
        final List<EnumOptionData> investmentChargeTimeTypeOptions = null;
        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, chargePaymentMode,
                penalty, active, taxGroupData, currencyOptions, chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions,
                chargePaymentModeOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions,
                clientChargeTimeTypeOptions, feeOnMonthDay, feeInterval, minCap, maxCap, feeFrequency, loanFeeFrequencyOptions, accountData,
                incomeOrLiabilityAccountOptions, taxGroupOptions, shareChargeCalculationTypeOptions, shareChargeTimeTypeOptions,
                emiRoundingGoalSeek, isGlimCharge, glimChargeCalculationType, glimChargeCalculationTypeOptions, isCapitalized, chargeSlabs,
                slabChargeTypeOptions, percentageType, percentagePeriodType, chargeOverdueData, percentageTypeOptions,
                percentagePeriodTypeOptions, penaltyGraceTypeOptions, investmentChargeCalculationTypeOptions, investmentChargeTimeTypeOptions,
                chargeInvestmentData);
    }

    public static ChargeData instance(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final EnumOptionData chargePaymentMode, final MonthDay feeOnMonthDay, final Integer feeInterval, final boolean penalty,
            final boolean active, final BigDecimal minCap, final BigDecimal maxCap, final EnumOptionData feeFrequency,
            final GLAccountData accountData) {

        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> chargeCalculationTypeOptions = null;
        final List<EnumOptionData> chargeAppliesToOptions = null;
        final List<EnumOptionData> chargeTimeTypeOptions = null;
        final List<EnumOptionData> chargePaymentModeOptions = null;
        final List<EnumOptionData> loansChargeCalculationTypeOptions = null;
        final List<EnumOptionData> loansChargeTimeTypeOptions = null;
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = null;
        final List<EnumOptionData> savingsChargeTimeTypeOptions = null;
        final Collection<EnumOptionData> loanFeeFrequencyOptions = null;
        final List<EnumOptionData> clientChargeCalculationTypeOptions = null;
        final List<EnumOptionData> clientChargeTimeTypeOptions = null;
        final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions = null;
        final Collection<TaxGroupData> taxGroupOptions = null;
        final TaxGroupData taxGroupData = null;
        final List<EnumOptionData> shareChargeCalculationTypeOptions = null;
        final List<EnumOptionData> shareChargeTimeTypeOptions = null;
        final boolean emiRoundingGoalSeek = false;
        final boolean isGlimCharge = false;
        final List<EnumOptionData> glimChargeCalculationTypeOptions = null;
        final EnumOptionData glimChargeCalculationType = null;
        final boolean isCapitalized = false;
        final List<ChargeSlabData> chargeSlabs = null;
        final List<EnumOptionData> slabChargeTypeOptions = null;
        final Collection<EnumOptionData> percentageTypeOptions = null;
        final Collection<EnumOptionData> percentagePeriodTypeOptions = null;
        final Collection<EnumOptionData> penaltyGraceTypeOptions = null;
        final EnumOptionData percentageType = null;
        final EnumOptionData percentagePeriodType = null;
        final ChargeOverdueData chargeOverdueData = null;
        final List<EnumOptionData> investmentChargeCalculationTypeOptions = null;
        final List<EnumOptionData> investmentChargeTimeTypeOptions = null;
        final ChargeInvestmentData chargeInvestmentData = null;

        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, chargePaymentMode,
                penalty, active, taxGroupData, currencyOptions, chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions,
                chargePaymentModeOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions,
                clientChargeTimeTypeOptions, feeOnMonthDay, feeInterval, minCap, maxCap, feeFrequency, loanFeeFrequencyOptions, accountData,
                incomeOrLiabilityAccountOptions, taxGroupOptions, shareChargeCalculationTypeOptions, shareChargeTimeTypeOptions,
                emiRoundingGoalSeek, isGlimCharge, glimChargeCalculationType, glimChargeCalculationTypeOptions, isCapitalized, chargeSlabs,
                slabChargeTypeOptions, percentageType, percentagePeriodType, chargeOverdueData, percentageTypeOptions,
                percentagePeriodTypeOptions, penaltyGraceTypeOptions, investmentChargeCalculationTypeOptions, investmentChargeTimeTypeOptions,
                chargeInvestmentData);
    }

    public static ChargeData lookup(final Long id, final String name, final boolean isPenalty) {
        final BigDecimal amount = null;
        final CurrencyData currency = null;
        final EnumOptionData chargeTimeType = null;
        final EnumOptionData chargeAppliesTo = null;
        final EnumOptionData chargeCalculationType = null;
        final EnumOptionData chargePaymentMode = null;
        final MonthDay feeOnMonthDay = null;
        final Integer feeInterval = null;
        final Boolean penalty = isPenalty;
        final Boolean active = false;
        final BigDecimal minCap = null;
        final BigDecimal maxCap = null;
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> chargeCalculationTypeOptions = null;
        final List<EnumOptionData> chargeAppliesToOptions = null;
        final List<EnumOptionData> chargeTimeTypeOptions = null;
        final List<EnumOptionData> chargePaymentModeOptions = null;
        final List<EnumOptionData> loansChargeCalculationTypeOptions = null;
        final List<EnumOptionData> loansChargeTimeTypeOptions = null;
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = null;
        final List<EnumOptionData> savingsChargeTimeTypeOptions = null;
        final List<EnumOptionData> clientChargeCalculationTypeOptions = null;
        final List<EnumOptionData> clientChargeTimeTypeOptions = null;
        final EnumOptionData feeFrequency = null;
        final Collection<EnumOptionData> loanFeeFrequencyOptions = null;
        final GLAccountData account = null;
        final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions = null;
        final List<EnumOptionData> shareChargeCalculationTypeOptions = null;
        final List<EnumOptionData> shareChargeTimeTypeOptions = null;
        final TaxGroupData taxGroupData = null;
        final Collection<TaxGroupData> taxGroupOptions = null;
        final boolean emiRoundingGoalSeek = false;
        final boolean isGlimCharge = false;
        final List<EnumOptionData> glimChargeCalculationTypeOptions = null;
        final EnumOptionData glimChargeCalculationType = null;
        final boolean isCapitalized = false;
        final List<ChargeSlabData> chargeSlabs = null;
        final List<EnumOptionData> slabChargeTypeOptions = null;
        final Collection<EnumOptionData> percentageTypeOptions = null;
        final Collection<EnumOptionData> percentagePeriodTypeOptions = null;
        final Collection<EnumOptionData> penaltyGraceTypeOptions = null;
        final EnumOptionData percentageType = null;
        final EnumOptionData percentagePeriodType = null;
        final ChargeOverdueData chargeOverdueData = null;
        final List<EnumOptionData> investmentChargeCalculationTypeOptions = null;
        final List<EnumOptionData> investmentChargeTimeTypeOptions = null;
        final ChargeInvestmentData chargeInvestmentData = null;
        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, chargePaymentMode,
                penalty, active, taxGroupData, currencyOptions, chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions,
                chargePaymentModeOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions,
                clientChargeTimeTypeOptions, feeOnMonthDay, feeInterval, minCap, maxCap, feeFrequency, loanFeeFrequencyOptions, account,
                incomeOrLiabilityAccountOptions, taxGroupOptions, shareChargeCalculationTypeOptions, shareChargeTimeTypeOptions,
                emiRoundingGoalSeek, isGlimCharge, glimChargeCalculationType, glimChargeCalculationTypeOptions, isCapitalized, chargeSlabs,
                slabChargeTypeOptions, percentageType, percentagePeriodType, chargeOverdueData, percentageTypeOptions,
                percentagePeriodTypeOptions, penaltyGraceTypeOptions, investmentChargeCalculationTypeOptions, investmentChargeTimeTypeOptions,
                chargeInvestmentData);
    }

    private ChargeData(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final EnumOptionData chargePaymentMode, final boolean penalty, final boolean active, final TaxGroupData taxGroupData,
            final Collection<CurrencyData> currencyOptions, final List<EnumOptionData> chargeCalculationTypeOptions,
            final List<EnumOptionData> chargeAppliesToOptions, final List<EnumOptionData> chargeTimeTypeOptions,
            final List<EnumOptionData> chargePaymentModeOptions, final List<EnumOptionData> loansChargeCalculationTypeOptions,
            final List<EnumOptionData> loansChargeTimeTypeOptions, final List<EnumOptionData> savingsChargeCalculationTypeOptions,
            final List<EnumOptionData> savingsChargeTimeTypeOptions, final List<EnumOptionData> clientChargeCalculationTypeOptions,
            final List<EnumOptionData> clientChargeTimeTypeOptions, final MonthDay feeOnMonthDay, final Integer feeInterval,
            final BigDecimal minCap, final BigDecimal maxCap, final EnumOptionData feeFrequency,
            final Collection<EnumOptionData> loanFeeFrequencyOptions, final GLAccountData account,
            final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions, final Collection<TaxGroupData> taxGroupOptions,
            final List<EnumOptionData> shareChargeCalculationTypeOptions, final List<EnumOptionData> shareChargeTimeTypeOptions,
            final boolean emiRoundingGoalSeek, final boolean isGlimCharge, final EnumOptionData glimChargeCalculationType,
            final List<EnumOptionData> glimChargeCalculationTypeOptions, final boolean isCapitalized,
            final List<ChargeSlabData> chargeSlabs, final List<EnumOptionData> slabChargeTypeOptions, final EnumOptionData percentageType,
            final EnumOptionData percentagePeriodType, final ChargeOverdueData chargeOverdueData,
            final Collection<EnumOptionData> percentageTypeOptions, final Collection<EnumOptionData> percentagePeriodTypeOptions,
            final Collection<EnumOptionData> penaltyGraceTypeOptions, final List<EnumOptionData> investmentChargeCalculationTypeOptions,
            final List<EnumOptionData> investmentChargeTimeTypeOptions, final ChargeInvestmentData chargeInvestmentData) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.chargeTimeType = chargeTimeType;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeCalculationType = chargeCalculationType;
        this.chargePaymentMode = chargePaymentMode;
        this.feeInterval = feeInterval;
        this.feeOnMonthDay = feeOnMonthDay;
        this.penalty = penalty;
        this.active = active;
        this.minCap = minCap;
        this.maxCap = maxCap;
        this.currencyOptions = currencyOptions;
        this.chargeCalculationTypeOptions = chargeCalculationTypeOptions;
        this.chargeAppliesToOptions = chargeAppliesToOptions;
        this.chargeTimeTypeOptions = chargeTimeTypeOptions;
        this.chargePaymetModeOptions = chargePaymentModeOptions;
        this.savingsChargeCalculationTypeOptions = savingsChargeCalculationTypeOptions;
        this.savingsChargeTimeTypeOptions = savingsChargeTimeTypeOptions;
        this.clientChargeCalculationTypeOptions = clientChargeCalculationTypeOptions;
        this.clientChargeTimeTypeOptions = clientChargeTimeTypeOptions;
        this.loanChargeCalculationTypeOptions = loansChargeCalculationTypeOptions;
        this.loanChargeTimeTypeOptions = loansChargeTimeTypeOptions;
        this.feeFrequency = feeFrequency;
        this.loanFeeFrequencyOptions = loanFeeFrequencyOptions;
        this.incomeOrLiabilityAccount = account;
        this.incomeOrLiabilityAccountOptions = incomeOrLiabilityAccountOptions;
        this.taxGroup = taxGroupData;
        this.taxGroupOptions = taxGroupOptions;
        this.shareChargeCalculationTypeOptions = shareChargeCalculationTypeOptions;
        this.shareChargeTimeTypeOptions = shareChargeTimeTypeOptions;
        this.emiRoundingGoalSeek = emiRoundingGoalSeek;
        this.isGlimCharge = isGlimCharge;
        this.glimChargeCalculation = glimChargeCalculationType;
        this.glimChargeCalculationTypeOptions = glimChargeCalculationTypeOptions;
        this.isCapitalized = isCapitalized;
        this.slabs = chargeSlabs;
        this.slabChargeTypeOptions = slabChargeTypeOptions;
        this.percentageType = percentageType;
        this.percentagePeriodType = percentagePeriodType;
        this.chargeOverdueData = chargeOverdueData;
        this.percentageTypeOptions = percentageTypeOptions;
        this.percentagePeriodTypeOptions = percentagePeriodTypeOptions;
        this.penaltyGraceTypeOptions = penaltyGraceTypeOptions;
        this.investmentChargeCalculationTypeOptions = investmentChargeCalculationTypeOptions;
        this.investmentChargeTimeTypeOptions = investmentChargeTimeTypeOptions;
        this.chargeInvestmentData = chargeInvestmentData;
    }

    @Override
    public boolean equals(final Object obj) {
        final ChargeData chargeData = (ChargeData) obj;
        return this.id.equals(chargeData.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public int compareTo(final ChargeData obj) {
        if (obj == null) { return -1; }

        return obj.id.compareTo(this.id);
    }

    public LoanChargeData toLoanChargeData() {

        BigDecimal percentage = null;
        if (this.chargeCalculationType.getId() == 2) {
            percentage = this.amount;
        }
        Long taxGroupId = null;
        if (this.taxGroup != null) {
            taxGroupId = this.taxGroup.getId();
        }
        final boolean isMandatory = false;
        return LoanChargeData.newLoanChargeDetails(this.id, this.name, this.currency, this.amount, percentage, this.chargeTimeType,
                this.chargeCalculationType, this.penalty, this.chargePaymentMode, this.minCap, this.maxCap, this.isGlimCharge,
                this.isCapitalized, taxGroupId, isMandatory);
    }

    public SavingsAccountChargeData toSavingsAccountChargeData() {

        final Long savingsChargeId = null;
        final Long savingsAccountId = null;
        final BigDecimal amountPaid = BigDecimal.ZERO;
        final BigDecimal amountWaived = BigDecimal.ZERO;
        final BigDecimal amountWrittenOff = BigDecimal.ZERO;
        final BigDecimal amountOutstanding = BigDecimal.ZERO;
        final BigDecimal percentage = BigDecimal.ZERO;
        final BigDecimal amountPercentageAppliedTo = BigDecimal.ZERO;
        final Collection<ChargeData> chargeOptions = null;
        final LocalDate dueAsOfDate = null;
        final Boolean isActive = null;
        final LocalDate inactivationDate = null;

        return SavingsAccountChargeData.instance(savingsChargeId, this.id, savingsAccountId, this.name, this.currency, this.amount,
                amountPaid, amountWaived, amountWrittenOff, amountOutstanding, this.chargeTimeType, dueAsOfDate, this.chargeCalculationType,
                percentage, amountPercentageAppliedTo, chargeOptions, this.penalty, this.feeOnMonthDay, this.feeInterval, isActive,
                inactivationDate);
    }

    public ShareAccountChargeData toShareAccountChargeData() {

        final Long shareChargeId = null;
        final Long shareAccountId = null;
        final BigDecimal amountPaid = BigDecimal.ZERO;
        final BigDecimal amountWaived = BigDecimal.ZERO;
        final BigDecimal amountWrittenOff = BigDecimal.ZERO;
        final BigDecimal amountOutstanding = BigDecimal.ZERO;
        final BigDecimal percentage = BigDecimal.ZERO;
        final BigDecimal amountPercentageAppliedTo = BigDecimal.ZERO;
        final Collection<ChargeData> chargeOptions = null;
        final Boolean isActive = null;
        final BigDecimal chargeAmountOrPercentage = BigDecimal.ZERO;

        return new ShareAccountChargeData(shareChargeId, this.id, shareAccountId, this.name, this.currency, this.amount, amountPaid,
                amountWaived, amountWrittenOff, amountOutstanding, this.chargeTimeType, this.chargeCalculationType, percentage,
                amountPercentageAppliedTo, chargeOptions, isActive, chargeAmountOrPercentage);
    }

    public boolean isPenalty() {
        return this.penalty;
    }

    public boolean isOverdueInstallmentCharge() {
        boolean isOverdueInstallmentCharge = false;
        if (this.chargeTimeType != null) {
            isOverdueInstallmentCharge = ChargeTimeType.fromInt(this.chargeTimeType.getId().intValue()).isOverdueInstallment();
        }
        return isOverdueInstallmentCharge;
    }

    public boolean isActive() {
        return this.active;
    }

    public Integer getFeeInterval() {
        return this.feeInterval;
    }

    public EnumOptionData getFeeFrequency() {
        return this.feeFrequency;
    }

    public boolean isSlabBasedCharge() {
        boolean isSlabBasedCharge = false;
        if (ChargeCalculationType.fromInt(this.chargeCalculationType.getId().intValue()).isSlabBased()) {
            isSlabBasedCharge = true;
        }
        return isSlabBasedCharge;
    }

    public void updateSlabCharges(final Collection<ChargeSlabData> chargeSlabs) {
        if (chargeSlabs == null) {
            this.slabs = new ArrayList<>();
        }
        if (this.slabs != null) {
            this.slabs.addAll(chargeSlabs);
        }

    }

    public void addChargeSlabData(final ChargeSlabData chargeSlabData) {
        if (this.slabs == null) {
            this.slabs = new ArrayList<>();
        }
        this.slabs.add(chargeSlabData);
    }

    public List<ChargeSlabData> getChargeSlabs() {
        return this.slabs;
    }

    public Long getId() {
        return this.id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Integer getChargeTimeType() {
        return this.chargeTimeType.getId().intValue();
    }

    public Integer getChargeCalculationType() {
        return this.chargeCalculationType.getId().intValue();
    }

    public Integer getChargePaymentMode() {
        return this.chargePaymentMode.getId().intValue();
    }

    public Integer feeInterval() {
        return this.feeInterval;
    }

    public Integer feeFrequency() {
        Integer feeFrequency = null;
        if (this.feeFrequency != null) {
            feeFrequency = this.feeFrequency.getId().intValue();
        }
        return feeFrequency;
    }

    public TaxGroupData getTaxGroup() {
        return this.taxGroup;
    }

    public Integer getPercentageType() {
        return this.percentageType.getId().intValue();
    }

    public Integer getPercentagePeriodType() {
        return this.percentagePeriodType.getId().intValue();
    }

    public ChargeOverdueData getChargeOverdueData() {
        return this.chargeOverdueData;
    }

	public List<EnumOptionData> getInvestmentChargeCalculationTypeOptions() {
		return this.investmentChargeCalculationTypeOptions;
	}

	public List<EnumOptionData> getInvestmentChargeTimeTypeOptions() {
		return this.investmentChargeTimeTypeOptions;
	}

	public ChargeInvestmentData getChargeInvestmentData() {
		return this.chargeInvestmentData;
	}
    
}