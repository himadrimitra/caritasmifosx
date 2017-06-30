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
package org.apache.fineract.integrationtests.common.loans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoanProductTestBuilder {

    private static final String LOCALE = "en_GB";
    private static final String USD = "USD";
    private static final String DAYS = "0";
    private static final String WEEK = "1";
    private static final String MONTHS = "2";
    private static final String YEARS = "3";
    private static final String CALCULATION_PERIOD_SAME_AS_REPAYMENT_PERIOD = "1";
    private static final String EQUAL_PRINCIPAL_PAYMENTS = "0";
    private static final String EQUAL_INSTALLMENTS = "1";
    private static final String DECLINING_BALANCE = "0";
    private static final String FLAT_BALANCE = "1";
    public static final String DEFAULT_STRATEGY = "1";
    // private static final String HEAVENS_FAMILY_STRATEGY ="2";
    // private static final String CREO_CORE_STRATEGY ="3";
    public static final String RBI_INDIA_STRATEGY = "4";
    public static final String PRINCIPAL_INTEREST_CHARGE_STRATEGY = "5";

    public static final String RECALCULATION_FREQUENCY_TYPE_SAME_AS_REPAYMENT_PERIOD = "1";
    public static final String RECALCULATION_FREQUENCY_TYPE_DAILY = "2";
    public static final String RECALCULATION_FREQUENCY_TYPE_WEEKLY = "3";
    public static final String RECALCULATION_FREQUENCY_TYPE_MONTHLY = "4";

    public static final String RECALCULATION_STRATEGY_RESCHEDULE_NEXT_REPAYMENTS = "1";
    public static final String RECALCULATION_STRATEGY_REDUCE_NUMBER_OF_INSTALLMENTS = "2";
    public static final String RECALCULATION_STRATEGY_REDUCE_EMI_AMOUN = "3";

    public static final String RECALCULATION_COMPOUNDING_METHOD_NONE = "0";
    public static final String RECALCULATION_COMPOUNDING_METHOD_INTEREST = "1";
    public static final String RECALCULATION_COMPOUNDING_METHOD_FEE = "2";
    public static final String RECALCULATION_COMPOUNDING_METHOD_INTEREST_AND_FEE = "3";

    private static final String NONE = "1";
    private static final String CASH_BASED = "2";
    private static final String ACCRUAL_PERIODIC = "3";
    private static final String ACCRUAL_UPFRONT = "4";

    public static final String INTEREST_APPLICABLE_STRATEGY_REST_DATE = "2";
    public static final String INTEREST_APPLICABLE_STRATEGY_ON_PRE_CLOSE_DATE = "1";

    private String digitsAfterDecimal = "2";
    private String inMultiplesOf = "0";

    private String nameOfLoanProduct = Utils.randomNameGenerator("LOAN_PRODUCT_", 6);
    private final String shortName = Utils.randomNameGenerator("", 4);
    private String principal = "10000.00";
    private String numberOfRepayments = "5";
    private String repaymentFrequency = MONTHS;
    private String repaymentPeriod = "1";
    private String interestRatePerPeriod = "2";
    private String interestRateFrequencyType = MONTHS;
    private String interestType = FLAT_BALANCE;
    private String overdueDaysForNPA = "5";
    private String interestCalculationPeriodType = CALCULATION_PERIOD_SAME_AS_REPAYMENT_PERIOD;
    private String inArrearsTolerance = "0";
    private String transactionProcessingStrategy = DEFAULT_STRATEGY;
    private String accountingRule = NONE;
    private final String currencyCode = USD;
    private String amortizationType = EQUAL_INSTALLMENTS;
    private String minPrincipal = "1000.00";
    private String maxPrincipal = "10000000.00";
    private Account[] accountList = null;
    List<HashMap<String, Object>> interestRateVariationsForBorrowerCycle = null;

    private Boolean multiDisburseLoan = false;
    private final String outstandingLoanBalance = "35000";
    private final String maxTrancheCount = "3";

    private Boolean isInterestRecalculationEnabled = false;
    private String daysInYearType = "1";
    private String daysInMonthType = "1";
    private String interestRecalculationCompoundingMethod = "0";
    private String preCloseInterestCalculationStrategy = INTEREST_APPLICABLE_STRATEGY_ON_PRE_CLOSE_DATE;
    private String rescheduleStrategyMethod = "1";
    private String recalculationRestFrequencyType = "1";
    private String recalculationRestFrequencyInterval = "0";
    private String recalculationCompoundingFrequencyType = null;
    private String recalculationCompoundingFrequencyInterval = null;
    private String minimumDaysBetweenDisbursalAndFirstRepayment = null;
    private Boolean holdGuaranteeFunds = null;
    private String mandatoryGuarantee = null;
    private String minimumGuaranteeFromOwnFunds = null;
    private String minimumGuaranteeFromGuarantor = null;
    private String isArrearsBasedOnOriginalSchedule = null;
    private String graceOnPrincipalPayment = "1";
    private String graceOnInterestPayment = "1";
    private JsonObject allowAttributeOverrides = null;
    private Boolean allowPartialPeriodInterestCalcualtion = false;

    private Boolean allowVariableInstallments = Boolean.FALSE;
    private Integer minimumGap;
    private Integer maximumGap;
    private Integer recalculationCompoundingFrequencyOnDayType = null;
    private Integer recalculationRestFrequencyOnDayType = null;
    private Integer recalculationCompoundingFrequencyDayOfWeekType = null;
    private Integer recalculationRestFrequencyDayOfWeekType = null;
    private Boolean isSubsidyApplicable = null;
    private Integer installmentAmountInMultiplesOf = null;
    private Boolean isAdjustFirstEMIAmount = Boolean.FALSE;
    private Integer adjustedInstallmentInMultiplesOf = null;
    private Boolean canDefineInstallmentAmount = Boolean.FALSE;
    private Boolean closeLoanOnOverpayment = Boolean.FALSE;
    private Boolean syncExpectedWithDisbursementDate = Boolean.FALSE;
    private Integer minimumPeriodsBetweenDisbursalAndFirstRepayment;
    private Integer maxLoanTerm = null;
    private Integer minLoanTerm = null;
    private Integer minNumberOfRepayments = null;
    private Integer maxNumberOfRepayments = null;
    private Integer loanTenureFrequencyType = null;
    private boolean adjustInterestForRounding = false;
    private boolean isEmiBasedOnDisbursements = false;
    private Integer installmentCalculationPeriodType = null;
    private Integer applicableForLoanType = 1;
    private Boolean isEnableRestrictionForClientProfile = false;
    private Integer profileType = null;
    private Integer selectedProfileTypeValues[] = null;

    public String build(final String chargeId) {
        final HashMap<String, Object> map = new HashMap<>();

        if (chargeId != null) {
            List<HashMap<String, Object>> charges = new ArrayList<>();
            HashMap<String, Object> chargeMap = new HashMap<>();
            chargeMap.put("id", chargeId);
            chargeMap.put("isMandatory", true);
            charges.add(chargeMap);
            map.put("charges", charges);
        }
        map.put("name", this.nameOfLoanProduct);
        map.put("shortName", this.shortName);
        map.put("currencyCode", this.currencyCode);
        map.put("applicableForLoanType", this.applicableForLoanType);
        map.put("isEnableRestrictionForClientProfile", this.isEnableRestrictionForClientProfile);
        if (this.isEnableRestrictionForClientProfile) {
            map.put("profileType", this.profileType);
            map.put("selectedProfileTypeValues", this.selectedProfileTypeValues);
        }
        map.put("locale", LOCALE);
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("digitsAfterDecimal", digitsAfterDecimal);
        map.put("inMultiplesOf", inMultiplesOf);
        map.put("principal", this.principal);
        map.put("numberOfRepayments", this.numberOfRepayments);
        map.put("repaymentEvery", this.repaymentPeriod);
        map.put("repaymentFrequencyType", this.repaymentFrequency);
        map.put("interestRatePerPeriod", this.interestRatePerPeriod);
        map.put("interestRateFrequencyType", this.interestRateFrequencyType);
        map.put("amortizationType", this.amortizationType);
        map.put("interestType", this.interestType);
        map.put("interestCalculationPeriodType", this.interestCalculationPeriodType);
        map.put("inArrearsTolerance", this.inArrearsTolerance);
        map.put("transactionProcessingStrategyId", this.transactionProcessingStrategy);
        map.put("accountingRule", this.accountingRule);
        map.put("minPrincipal", this.minPrincipal);
        map.put("maxPrincipal", this.maxPrincipal);
        map.put("overdueDaysForNPA", this.overdueDaysForNPA);
        if (this.minimumPeriodsBetweenDisbursalAndFirstRepayment != null) {
            map.put("minimumPeriodsBetweenDisbursalAndFirstRepayment", this.minimumPeriodsBetweenDisbursalAndFirstRepayment);
        }
        if (this.minimumDaysBetweenDisbursalAndFirstRepayment != null) {
            map.put("minimumDaysBetweenDisbursalAndFirstRepayment", this.minimumDaysBetweenDisbursalAndFirstRepayment);
        }
        if (multiDisburseLoan) {
            map.put("multiDisburseLoan", this.multiDisburseLoan);
            map.put("maxTrancheCount", this.maxTrancheCount);
            map.put("outstandingLoanBalance", this.outstandingLoanBalance);
        }

        if (this.accountingRule.equals(ACCRUAL_UPFRONT) || this.accountingRule.equals(ACCRUAL_PERIODIC)) {
            map.putAll(getAccountMappingForAccrualBased());
        } else if (this.accountingRule.equals(CASH_BASED)) {
            map.putAll(getAccountMappingForCashBased());
        }
        map.put("daysInMonthType", this.daysInMonthType);
        map.put("daysInYearType", this.daysInYearType);
        map.put("isInterestRecalculationEnabled", this.isInterestRecalculationEnabled);
        if (this.isInterestRecalculationEnabled) {
            map.put("interestRecalculationCompoundingMethod", this.interestRecalculationCompoundingMethod);
            map.put("rescheduleStrategyMethod", this.rescheduleStrategyMethod);
            map.put("recalculationRestFrequencyType", recalculationRestFrequencyType);
            map.put("recalculationRestFrequencyInterval", recalculationRestFrequencyInterval);
            if (!RECALCULATION_COMPOUNDING_METHOD_NONE.equals(this.interestRecalculationCompoundingMethod)) {
                map.put("recalculationCompoundingFrequencyType", recalculationCompoundingFrequencyType);
                map.put("recalculationCompoundingFrequencyInterval", recalculationCompoundingFrequencyInterval);
            }
            map.put("preClosureInterestCalculationStrategy", preCloseInterestCalculationStrategy);
            if (isArrearsBasedOnOriginalSchedule != null) {
                map.put("isArrearsBasedOnOriginalSchedule", isArrearsBasedOnOriginalSchedule);
            }
            map.put("recalculationCompoundingFrequencyOnDayType", this.recalculationCompoundingFrequencyOnDayType);
            map.put("recalculationCompoundingFrequencyDayOfWeekType", this.recalculationCompoundingFrequencyDayOfWeekType);
            map.put("recalculationRestFrequencyOnDayType", this.recalculationRestFrequencyOnDayType);
            map.put("recalculationRestFrequencyDayOfWeekType", this.recalculationRestFrequencyDayOfWeekType);
        }
        if (holdGuaranteeFunds != null) {
            map.put("holdGuaranteeFunds", this.holdGuaranteeFunds);
            if (this.holdGuaranteeFunds) {
                map.put("mandatoryGuarantee", this.mandatoryGuarantee);
                map.put("minimumGuaranteeFromGuarantor", this.minimumGuaranteeFromGuarantor);
                map.put("minimumGuaranteeFromOwnFunds", this.minimumGuaranteeFromOwnFunds);
            }
        }
        map.put("graceOnPrincipalPayment", graceOnPrincipalPayment);
        map.put("graceOnInterestPayment", graceOnInterestPayment);
        if (allowAttributeOverrides != null) {
            map.put("allowAttributeOverrides", this.allowAttributeOverrides);
        }
        map.put("allowPartialPeriodInterestCalcualtion", this.allowPartialPeriodInterestCalcualtion);
        map.put("allowVariableInstallments", allowVariableInstallments);
        map.put("adjustInterestForRounding", this.adjustInterestForRounding);
        if (allowVariableInstallments) {
            map.put("minimumGap", minimumGap);
            map.put("maximumGap", maximumGap);
        }

        if (isSubsidyApplicable != null) {
            map.put("isSubsidyApplicable", this.isSubsidyApplicable);
        }

        if (isAdjustFirstEMIAmount) {
            map.put("adjustFirstEMIAmount", this.isAdjustFirstEMIAmount);
        }

        if (adjustedInstallmentInMultiplesOf != null) {
            map.put("adjustedInstallmentInMultiplesOf", this.adjustedInstallmentInMultiplesOf);
        }
        if (installmentAmountInMultiplesOf != null) {
            map.put("installmentAmountInMultiplesOf", this.installmentAmountInMultiplesOf);

        }
        if (canDefineInstallmentAmount) {
            map.put("canDefineInstallmentAmount", this.canDefineInstallmentAmount);
            map.put("principalThresholdForLastInstallment", 50);
        }

        if (closeLoanOnOverpayment) {
            map.put("closeLoanOnOverpayment", closeLoanOnOverpayment);
        }

        if (isEmiBasedOnDisbursements) {
            map.put("isEmiBasedOnDisbursements", isEmiBasedOnDisbursements);
        }

        if (installmentCalculationPeriodType != null) {
            map.put("installmentCalculationPeriodType", installmentCalculationPeriodType);
        }

        if (syncExpectedWithDisbursementDate) {
            map.put("syncExpectedWithDisbursementDate", this.syncExpectedWithDisbursementDate);
        }
        map.put("maxLoanTerm", this.maxLoanTerm);
        map.put("minLoanTerm", this.minLoanTerm);
        map.put("canDefineInstallmentAmount", this.canDefineInstallmentAmount);
        map.put("minNumberOfRepayments", this.minNumberOfRepayments);
        map.put("maxNumberOfRepayments", this.maxNumberOfRepayments);
        map.put("loanTenureFrequencyType", this.loanTenureFrequencyType);

        return new Gson().toJson(map);
    }

    public LoanProductTestBuilder withMinPrincipal(final String minPrincipal) {
        this.minPrincipal = minPrincipal;
        return this;
    }

    public LoanProductTestBuilder withMaxPrincipal(final String maxPrincipal) {
        this.maxPrincipal = maxPrincipal;
        return this;
    }

    public LoanProductTestBuilder withLoanName(final String loanName) {
        this.nameOfLoanProduct = loanName;
        return this;
    }

    public LoanProductTestBuilder withPrincipal(final String principal) {
        this.principal = principal;
        return this;
    }

    public LoanProductTestBuilder withNumberOfRepayments(final String numberOfRepayment) {
        this.numberOfRepayments = numberOfRepayment;
        return this;
    }

    public LoanProductTestBuilder withRepaymentTypeAsMonth() {
        this.repaymentFrequency = MONTHS;
        return this;
    }

    public LoanProductTestBuilder withRepaymentTypeAsWeek() {
        this.repaymentFrequency = WEEK;
        return this;
    }

    public LoanProductTestBuilder withRepaymentTypeAsDays() {
        this.repaymentFrequency = DAYS;
        return this;
    }

    public LoanProductTestBuilder withRepaymentAfterEvery(final String repaymentAfterEvery) {
        this.repaymentPeriod = repaymentAfterEvery;
        return this;
    }

    public LoanProductTestBuilder withInterestRateFrequencyTypeAsMonths() {
        this.interestRateFrequencyType = MONTHS;
        return this;
    }

    public LoanProductTestBuilder withInterestRateFrequencyTypeAsYear() {
        this.interestRateFrequencyType = YEARS;
        return this;
    }

    public LoanProductTestBuilder withinterestRatePerPeriod(final String interestRatePerPeriod) {
        this.interestRatePerPeriod = interestRatePerPeriod;
        return this;
    }

    public LoanProductTestBuilder withAmortizationTypeAsEqualPrincipalPayment() {
        this.amortizationType = EQUAL_PRINCIPAL_PAYMENTS;
        return this;
    }

    public LoanProductTestBuilder withAmortizationTypeAsEqualInstallments() {
        this.amortizationType = EQUAL_INSTALLMENTS;
        return this;
    }

    public LoanProductTestBuilder withInterestTypeAsFlat() {
        this.interestType = FLAT_BALANCE;
        return this;
    }

    public LoanProductTestBuilder withInterestTypeAsDecliningBalance() {
        this.interestType = DECLINING_BALANCE;
        return this;
    }

    public LoanProductTestBuilder withOverdueDaysForNPA(String days) {
        this.overdueDaysForNPA = days;
        return this;
    }

    public LoanProductTestBuilder withInterestCalculationPeriodTypeAsDays() {
        this.interestCalculationPeriodType = DAYS;
        return this;
    }

    public LoanProductTestBuilder withInterestCalculationPeriodTypeAsRepaymentPeriod(final Boolean allowPartialPeriodInterestCalcualtion) {
        this.interestCalculationPeriodType = CALCULATION_PERIOD_SAME_AS_REPAYMENT_PERIOD;
        this.allowPartialPeriodInterestCalcualtion = allowPartialPeriodInterestCalcualtion;
        return this;
    }

    public LoanProductTestBuilder withAdjustInterestForRounding(final boolean adjustInterestForRounding) {
        this.adjustInterestForRounding = adjustInterestForRounding;
        return this;
    }

    public LoanProductTestBuilder withInArrearsTolerance(final String amountCanBeWaved) {
        this.inArrearsTolerance = amountCanBeWaved;
        return this;
    }

    public LoanProductTestBuilder withAccountingRuleAsNone() {
        this.accountingRule = NONE;
        return this;
    }

    public LoanProductTestBuilder withAccountingRuleAsCashBased(final Account[] account_list) {
        this.accountingRule = CASH_BASED;
        this.accountList = account_list;
        return this;
    }

    public LoanProductTestBuilder withAccountingRuleUpfrontAccrual(final Account[] account_list) {
        this.accountingRule = ACCRUAL_UPFRONT;
        this.accountList = account_list;
        return this;
    }

    public LoanProductTestBuilder withAccountingRulePeriodicAccrual(final Account[] account_list) {
        this.accountingRule = ACCRUAL_PERIODIC;
        this.accountList = account_list;
        return this;
    }

    public LoanProductTestBuilder withTranches(boolean multiDisburseLoan) {
        this.multiDisburseLoan = multiDisburseLoan;
        return this;
    }

    public LoanProductTestBuilder withInterestRecalculation(boolean interestRecalculation) {
        this.isInterestRecalculationEnabled = interestRecalculation;
        return this;
    }

    private Map<String, String> getAccountMappingForCashBased() {
        final Map<String, String> map = new HashMap<>();
        for (int i = 0; i < this.accountList.length; i++) {
            if (this.accountList[i].getAccountType().equals(Account.AccountType.ASSET)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("fundSourceAccountId", ID);
                map.put("loanPortfolioAccountId", ID);
                map.put("transfersInSuspenseAccountId", ID);
                if (this.isSubsidyApplicable != null && this.isSubsidyApplicable) {
                    map.put("subsidyAccountId", ID);
                }
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.INCOME)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("interestOnLoanAccountId", ID);
                map.put("incomeFromFeeAccountId", ID);
                map.put("incomeFromPenaltyAccountId", ID);
                map.put("incomeFromRecoveryAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.EXPENSE)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("writeOffAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.LIABILITY)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("overpaymentLiabilityAccountId", ID);
                if (this.isSubsidyApplicable != null && this.isSubsidyApplicable) {
                    map.put("subsidyFundSourceId", ID);
                }
            }
        }
        return map;
    }

    private Map<String, String> getAccountMappingForAccrualBased() {
        final Map<String, String> map = new HashMap<>();
        for (int i = 0; i < this.accountList.length; i++) {
            if (this.accountList[i].getAccountType().equals(Account.AccountType.ASSET)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("fundSourceAccountId", ID);
                map.put("loanPortfolioAccountId", ID);
                map.put("transfersInSuspenseAccountId", ID);
                map.put("receivableInterestAccountId", ID);
                map.put("receivableFeeAccountId", ID);
                map.put("receivablePenaltyAccountId", ID);
                if (this.isSubsidyApplicable != null && this.isSubsidyApplicable) {
                    map.put("subsidyAccountId", ID);
                }
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.INCOME)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("interestOnLoanAccountId", ID);
                map.put("incomeFromFeeAccountId", ID);
                map.put("incomeFromPenaltyAccountId", ID);
                map.put("incomeFromRecoveryAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.EXPENSE)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("writeOffAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.LIABILITY)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("overpaymentLiabilityAccountId", ID);
                map.put("npaInterestSuspenseAccountId", ID);
                map.put("npaFeeSuspenseAccountId", ID);
                map.put("npaPenaltySuspenseAccountId", ID);
                if (this.isSubsidyApplicable != null && this.isSubsidyApplicable) {
                    map.put("subsidyFundSourceId", ID);
                }
            }
        }

        return map;
    }

    public LoanProductTestBuilder withAccounting(final String accountingRule, final Account[] account_list) {
        this.accountingRule = accountingRule;
        this.accountList = account_list;
        return this;
    }

    public LoanProductTestBuilder currencyDetails(final String digitsAfterDecimal, final String inMultiplesOf) {
        this.digitsAfterDecimal = digitsAfterDecimal;
        this.inMultiplesOf = inMultiplesOf;
        return this;
    }

    public LoanProductTestBuilder withRepaymentStrategy(final String transactionProcessingStrategy) {
        this.transactionProcessingStrategy = transactionProcessingStrategy;
        return this;
    }

    public LoanProductTestBuilder withDaysInMonth(final String daysInMonthType) {
        this.daysInMonthType = daysInMonthType;
        return this;
    }

    public LoanProductTestBuilder withDaysInYear(final String daysInYearType) {
        this.daysInYearType = daysInYearType;
        return this;
    }

    public LoanProductTestBuilder withInterestRecalculationDetails(final String interestRecalculationCompoundingMethod,
            final String rescheduleStrategyMethod, String preCloseInterestCalculationStrategy) {
        this.isInterestRecalculationEnabled = true;
        this.interestRecalculationCompoundingMethod = interestRecalculationCompoundingMethod;
        this.rescheduleStrategyMethod = rescheduleStrategyMethod;
        this.preCloseInterestCalculationStrategy = preCloseInterestCalculationStrategy;
        return this;
    }

    public LoanProductTestBuilder withInterestRecalculationRestFrequencyDetails(final String recalculationRestFrequencyType,
            final String recalculationRestFrequencyInterval, final Integer recalculationRestFrequencyOnDayType,
            final Integer recalculationRestFrequencyDayOfWeekType) {
        this.isInterestRecalculationEnabled = true;
        this.recalculationRestFrequencyType = recalculationRestFrequencyType;
        this.recalculationRestFrequencyInterval = recalculationRestFrequencyInterval;
        this.recalculationRestFrequencyOnDayType = recalculationRestFrequencyOnDayType;
        this.recalculationRestFrequencyDayOfWeekType = recalculationRestFrequencyDayOfWeekType;
        return this;
    }

    public LoanProductTestBuilder withInterestRecalculationCompoundingFrequencyDetails(final String recalculationCompoundingFrequencyType,
            final String recalculationCompoundingFrequencyInterval, final Integer recalculationCompoundingFrequencyOnDayType,
            final Integer recalculationCompoundingFrequencyDayOfWeekType) {
        this.isInterestRecalculationEnabled = true;
        this.recalculationCompoundingFrequencyType = recalculationCompoundingFrequencyType;
        this.recalculationCompoundingFrequencyInterval = recalculationCompoundingFrequencyInterval;
        this.recalculationCompoundingFrequencyOnDayType = recalculationCompoundingFrequencyOnDayType;
        this.recalculationCompoundingFrequencyDayOfWeekType = recalculationCompoundingFrequencyDayOfWeekType;
        return this;
    }

    public LoanProductTestBuilder withMinimumDaysBetweenDisbursalAndFirstRepayment(final String minimumDaysBetweenDisbursalAndFirstRepayment) {
        this.minimumDaysBetweenDisbursalAndFirstRepayment = minimumDaysBetweenDisbursalAndFirstRepayment;
        return this;
    }

    public LoanProductTestBuilder withMinimumPeriodsBetweenDisbursalAndFirstRepayment(
            final Integer minimumPeriodsBetweenDisbursalAndFirstRepayment) {
        this.minimumPeriodsBetweenDisbursalAndFirstRepayment = minimumPeriodsBetweenDisbursalAndFirstRepayment;
        return this;
    }

    public LoanProductTestBuilder withArrearsConfiguration() {
        this.isArrearsBasedOnOriginalSchedule = "true";
        return this;
    }

    public LoanProductTestBuilder withOnHoldFundDetails(final String mandatoryGuarantee, final String minimumGuaranteeFromGuarantor,
            final String minimumGuaranteeFromOwnFunds) {
        this.holdGuaranteeFunds = true;
        this.mandatoryGuarantee = mandatoryGuarantee;
        this.minimumGuaranteeFromGuarantor = minimumGuaranteeFromGuarantor;
        this.minimumGuaranteeFromOwnFunds = minimumGuaranteeFromOwnFunds;
        return this;
    }

    public LoanProductTestBuilder withMoratorium(String principal, String interest) {
        this.graceOnPrincipalPayment = principal;
        this.graceOnInterestPayment = interest;
        return this;
    }

    public LoanProductTestBuilder withLoanProductConfiguration(JsonObject loanProductConfigurableAttributes) {
        this.allowAttributeOverrides = loanProductConfigurableAttributes;
        return this;
    }

    public LoanProductTestBuilder withVariableInstallmentsConfig(Boolean allowVariableInstallments, Integer minimumGap, Integer maximumGap) {
        this.allowVariableInstallments = allowVariableInstallments;
        this.minimumGap = minimumGap;
        this.maximumGap = maximumGap;
        return this;
    }

    public LoanProductTestBuilder withIsSubsidyApplicable(Boolean isSubsidyApplicable) {
        this.isSubsidyApplicable = isSubsidyApplicable;
        return this;
    }

    public LoanProductTestBuilder withinstallmentAmountInMultiplesOfType(Integer installmentAmountInMultiplesOf) {
        this.installmentAmountInMultiplesOf = installmentAmountInMultiplesOf;
        return this;
    }

    public LoanProductTestBuilder withadjustedInstallmentInMultiplesOf(Integer adjustedInstallmentInMultiplesOf) {
        this.adjustedInstallmentInMultiplesOf = adjustedInstallmentInMultiplesOf;
        return this;
    }

    public LoanProductTestBuilder withAdjustFirstEMi(Boolean isAdjustFirstEMIAmount) {
        this.isAdjustFirstEMIAmount = isAdjustFirstEMIAmount;
        return this;
    }

    public LoanProductTestBuilder withcanDefineInstallmentAmount(Boolean canDefineInstallmentAmount) {
        this.canDefineInstallmentAmount = canDefineInstallmentAmount;
        return this;
    }

    public LoanProductTestBuilder withonOverPaymentCloseLoan(Boolean closeLoanOnOverpayment) {
        this.closeLoanOnOverpayment = closeLoanOnOverpayment;
        return this;
    }

    public LoanProductTestBuilder withSyncExpectedWithDisbursementDate(Boolean syncExpectedWithDisbursementDate) {
        this.syncExpectedWithDisbursementDate = syncExpectedWithDisbursementDate;
        return this;
    }

    public LoanProductTestBuilder withLoanTerms(final Integer minLoanTerm, final Integer maxLoanTerm) {
        this.minLoanTerm = minLoanTerm;
        this.maxLoanTerm = maxLoanTerm;
        return this;
    }

    public LoanProductTestBuilder withCanDefineInstallmentAmount(Boolean canDefineInstallmentAmount) {
        this.canDefineInstallmentAmount = canDefineInstallmentAmount;
        return this;
    }

    public LoanProductTestBuilder withMininmumAndMaximumNumberOfRepayments(final Integer minNumberOfRepayments,
            final Integer maxNumberOfRepayments) {
        this.minNumberOfRepayments = minNumberOfRepayments;
        this.maxNumberOfRepayments = maxNumberOfRepayments;
        return this;
    }

    public LoanProductTestBuilder withLoanTenureFrequencyType(final Integer loanTenureFrequencyType) {
        this.loanTenureFrequencyType = loanTenureFrequencyType;
        return this;
    }

    public LoanProductTestBuilder withEmiBasedOnDisbursements(boolean isEmiBasedOnDisbursements) {
        this.isEmiBasedOnDisbursements = isEmiBasedOnDisbursements;
        return this;
    }

    public LoanProductTestBuilder withInstallmentCalculationPeriodType(Integer installmentCalculationPeriodType) {
        this.installmentCalculationPeriodType = installmentCalculationPeriodType;
        return this;
    }

    public LoanProductTestBuilder withApplicableForAllClients() {
        this.applicableForLoanType = 2;
        this.isEnableRestrictionForClientProfile = false;
        return this;
    }

    public LoanProductTestBuilder withApplicableForClientProfileTypeLegalFormPerson() {
        this.applicableForLoanType = 2;
        this.isEnableRestrictionForClientProfile = true;
        this.profileType = 1;
        this.selectedProfileTypeValues = new Integer[1];
        this.selectedProfileTypeValues[0] = 1; // LegalForm.PERSON
        return this;
    }

    public LoanProductTestBuilder withApplicableForClientProfileTypeLegalFormEntity() {
        this.applicableForLoanType = 2;
        this.isEnableRestrictionForClientProfile = true;
        this.profileType = 1;
        this.selectedProfileTypeValues = new Integer[1];
        this.selectedProfileTypeValues[0] = 2; // LegalForm.ENTITY
        return this;
    }

    public LoanProductTestBuilder withApplicableForClientProfileTypeLegalFormPersonOrEntity() {
        this.applicableForLoanType = 2;
        this.isEnableRestrictionForClientProfile = true;
        this.profileType = 1;
        this.selectedProfileTypeValues = new Integer[2];
        this.selectedProfileTypeValues[0] = 1; // LegalForm.PERSON
        this.selectedProfileTypeValues[1] = 2; // LegalForm.ENTITY
        return this;
    }

    public LoanProductTestBuilder withApplicableForClientProfileTypeAllClientTypes(final List<Integer> clientTypeValues) {
        this.applicableForLoanType = 2;
        this.isEnableRestrictionForClientProfile = true;
        this.profileType = 2;
        this.selectedProfileTypeValues = clientTypeValues.stream().toArray(v -> new Integer[v]);
        return this;
    }

    public LoanProductTestBuilder withSelectedProfileTypeValues(final List<Integer> values) {
        this.selectedProfileTypeValues = values.stream().toArray(v -> new Integer[v]);
        return this;
    }
    
    public LoanProductTestBuilder withinterestRateVariationsForBorrowerCycle() {
        List<HashMap<String, Object>> interestRateVariationsForBorrowerCycle = new ArrayList<>();
        HashMap<String, Object> interestRateVariationsForBorrowerCycleMap = new HashMap<>();
        interestRateVariationsForBorrowerCycleMap.put("valueConditionType", 2);
        interestRateVariationsForBorrowerCycleMap.put("borrowerCycleNumber", 1);
        interestRateVariationsForBorrowerCycleMap.put("interestRatesListPerCycle", Arrays.asList(2.6, 2.7));
        interestRateVariationsForBorrowerCycleMap.put("defaultValue", 2.5);

        HashMap<String, Object> interestRateVariationsForBorrowerCycleMap1 = new HashMap<>();
        interestRateVariationsForBorrowerCycleMap1.put("valueConditionType", 2);
        interestRateVariationsForBorrowerCycleMap1.put("borrowerCycleNumber", 2);
        interestRateVariationsForBorrowerCycleMap1.put("interestRatesListPerCycle", Arrays.asList(5.6));
        interestRateVariationsForBorrowerCycleMap1.put("defaultValue", 5.6);

        HashMap<String, Object> interestRateVariationsForBorrowerCycleMap2 = new HashMap<>();
        interestRateVariationsForBorrowerCycleMap2.put("valueConditionType", 3);
        interestRateVariationsForBorrowerCycleMap2.put("borrowerCycleNumber", 2);
        interestRateVariationsForBorrowerCycleMap2.put("interestRatesListPerCycle", Arrays.asList(6.9, 3.5));
        interestRateVariationsForBorrowerCycleMap2.put("defaultValue", 3.5);

        interestRateVariationsForBorrowerCycle.add(interestRateVariationsForBorrowerCycleMap);
        interestRateVariationsForBorrowerCycle.add(interestRateVariationsForBorrowerCycleMap1);
        interestRateVariationsForBorrowerCycle.add(interestRateVariationsForBorrowerCycleMap2);
        this.interestRateVariationsForBorrowerCycle = interestRateVariationsForBorrowerCycle;
        return this;
    }
    
    public String buildLoanProductWithBorrowerCycle() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.nameOfLoanProduct);
        map.put("shortName", this.shortName);
        map.put("currencyCode", this.currencyCode);
        map.put("locale", LOCALE);
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("digitsAfterDecimal", digitsAfterDecimal);
        map.put("inMultiplesOf", inMultiplesOf);
        map.put("principal", this.principal);
        map.put("numberOfRepayments", this.numberOfRepayments);
        map.put("repaymentEvery", this.repaymentPeriod);
        map.put("repaymentFrequencyType", this.repaymentFrequency);
        map.put("interestRatePerPeriod", this.interestRatePerPeriod);
        map.put("interestRatesListPerPeriod",Arrays.asList(2.6, 2.5, 2.0));
        map.put("interestRateFrequencyType", this.interestRateFrequencyType);
        map.put("amortizationType", this.amortizationType);
        map.put("interestType", this.interestType);
        map.put("interestCalculationPeriodType", this.interestCalculationPeriodType);
        map.put("inArrearsTolerance", this.inArrearsTolerance);
        map.put("transactionProcessingStrategyId", this.transactionProcessingStrategy);
        map.put("accountingRule", this.accountingRule);
        map.put("minPrincipal", this.minPrincipal);
        map.put("maxPrincipal", this.maxPrincipal);
        map.put("overdueDaysForNPA", this.overdueDaysForNPA);
        map.put("useBorrowerCycle",true);
        map.put("weeksInYearType","1");
        map.put("multiDisburseLoan", this.multiDisburseLoan);
        map.put("isLinkedToFloatingInterestRates", false);
        map.put("includeInBorrowerCycle", true);
        if (this.interestRateVariationsForBorrowerCycle != null) {
            map.put("interestRateVariationsForBorrowerCycle", this.interestRateVariationsForBorrowerCycle);
        } else {
            HashMap<String, Object> interestRateVariationsForBorrowerCycleMap = new HashMap<>();
            interestRateVariationsForBorrowerCycleMap.put("valueConditionType", 2);
            interestRateVariationsForBorrowerCycleMap.put("borrowerCycleNumber", 1);
            interestRateVariationsForBorrowerCycleMap.put("interestRatesListPerCycle", Arrays.asList(2.6, 2.7,2.5));
            interestRateVariationsForBorrowerCycleMap.put("defaultValue", 2.5);

            HashMap<String, Object> interestRateVariationsForBorrowerCycleMap1 = new HashMap<>();
            interestRateVariationsForBorrowerCycleMap1.put("valueConditionType", 2);
            interestRateVariationsForBorrowerCycleMap1.put("borrowerCycleNumber", 2);
            interestRateVariationsForBorrowerCycleMap1.put("interestRatesListPerCycle", Arrays.asList(5.6));
            interestRateVariationsForBorrowerCycleMap1.put("defaultValue", 5.6);

            HashMap<String, Object> interestRateVariationsForBorrowerCycleMap2 = new HashMap<>();
            interestRateVariationsForBorrowerCycleMap2.put("valueConditionType", 3);
            interestRateVariationsForBorrowerCycleMap2.put("borrowerCycleNumber", 2);
            interestRateVariationsForBorrowerCycleMap2.put("interestRatesListPerCycle", Arrays.asList(6.9, 3.5));
            interestRateVariationsForBorrowerCycleMap2.put("defaultValue", 3.5);

            this.interestRateVariationsForBorrowerCycle= new ArrayList<>();
            this.interestRateVariationsForBorrowerCycle.add(interestRateVariationsForBorrowerCycleMap);
            this.interestRateVariationsForBorrowerCycle.add(interestRateVariationsForBorrowerCycleMap1);
            this.interestRateVariationsForBorrowerCycle.add(interestRateVariationsForBorrowerCycleMap2);
            map.put("interestRateVariationsForBorrowerCycle", this.interestRateVariationsForBorrowerCycle);
        }
        map.put("considerFutureDisbursementsInSchedule", false);
        map.put("considerAllDisbursementsInSchedule", false);
        map.put("accountingRule", NONE);
        map.put("preClosureInterestCalculationStrategy", NONE);
        map.put("allowVariableInstallments", allowVariableInstallments) ;
        map.put("maxLoanTerm", this.maxLoanTerm);
        map.put("minLoanTerm", this.minLoanTerm);
        map.put("canDefineInstallmentAmount", this.canDefineInstallmentAmount);
        map.put("minNumberOfRepayments", this.minNumberOfRepayments);
        map.put("maxNumberOfRepayments", this.maxNumberOfRepayments);
        map.put("loanTenureFrequencyType", this.loanTenureFrequencyType);
        map.put("daysInYearType",1);
        map.put("daysInMonthType", 1);
        map.put("isInterestRecalculationEnabled", false);
        map.put("applicableForLoanType", this.applicableForLoanType);
        
        return new Gson().toJson(map);
    }
    

    public LoanProductTestBuilder withApplicableForClientProfileTypeAllClientClassification(final List<Integer> clientClassificationValues) {
        this.applicableForLoanType = 2;
        this.isEnableRestrictionForClientProfile = true;
        this.profileType = 3;
        this.selectedProfileTypeValues = clientClassificationValues.stream().toArray(v -> new Integer[v]);
        return this;
    }

    public LoanProductTestBuilder withApplicableForGroups() {
        this.applicableForLoanType = 3;
        this.isEnableRestrictionForClientProfile = false;
        return this;
    }

    public LoanProductTestBuilder withApplicableForLoanType(final Integer applicableForLoanType) {
        this.applicableForLoanType = applicableForLoanType;
        return this;
    }

}
