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
package org.apache.fineract.portfolio.loanproduct;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.loanproduct.domain.ClientProfileType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductApplicableForLoanType;

public interface LoanProductConstants {

    public static final String useBorrowerCycleParameterName = "useBorrowerCycle";

    public static final String principalVariationsForBorrowerCycleParameterName = "principalVariationsForBorrowerCycle";
    public static final String interestRateVariationsForBorrowerCycleParameterName = "interestRateVariationsForBorrowerCycle";
    public static final String numberOfRepaymentVariationsForBorrowerCycleParameterName = "numberOfRepaymentVariationsForBorrowerCycle";

    public static final String defaultValueParameterName = "defaultValue";
    public static final String minValueParameterName = "minValue";
    public static final String maxValueParameterName = "maxValue";
    public static final String valueConditionTypeParamName = "valueConditionType";
    public static final String borrowerCycleNumberParamName = "borrowerCycleNumber";
    public static final String borrowerCycleIdParameterName = "id";
    public static final String interestRatesListPerCycleParameterName = "interestRatesListPerCycle";

    public static final String principalPerCycleParameterName = "principalPerCycle";
    public static final String minPrincipalPerCycleParameterName = "minPrincipalPerCycle";
    public static final String maxPrincipalPerCycleParameterName = "maxPrincipalPerCycle";
    public static final String principalValueUsageConditionParamName = "principalValueUsageCondition";
    public static final String principalCycleNumbersParamName = "principalCycleNumbers";

    public static final String numberOfRepaymentsPerCycleParameterName = "numberOfRepaymentsPerCycle";
    public static final String minNumberOfRepaymentsPerCycleParameterName = "minNumberOfRepaymentsPerCycle";
    public static final String maxNumberOfRepaymentsPerCycleParameterName = "maxNumberOfRepaymentsPerCycle";
    public static final String repaymentValueUsageConditionParamName = "repaymentValueUsageCondition";
    public static final String repaymentCycleNumberParamName = "repaymentCycleNumber";

    public static final String interestRatePerPeriodPerCycleParameterName = "interestRatePerPeriodPerCycle";
    public static final String minInterestRatePerPeriodPerCycleParameterName = "minInterestRatePerPeriodPerCycle";
    public static final String maxInterestRatePerPeriodPerCycleParameterName = "maxInterestRatePerPeriodPerCycle";
    public static final String interestRateValueUsageConditionParamName = "interestRateValueUsageCondition";
    public static final String interestRateCycleNumberParamName = "interestRateCycleNumber";

    public static final String principal = "principal";
    public static final String minPrincipal = "minPrincipal";
    public static final String maxPrincipal = "maxPrincipalValue";

    public static final String interestRatePerPeriod = "interestRatePerPeriod";
    public static final String minInterestRatePerPeriod = "minInterestRatePerPeriod";
    public static final String maxInterestRatePerPeriod = "maxInterestRatePerPeriod";
    public static final String interestRatesListPerPeriod = "interestRatesListPerPeriod";

    public static final String numberOfRepayments = "numberOfRepayments";
    public static final String minNumberOfRepayments = "minNumberOfRepayments";
    public static final String maxNumberOfRepayments = "maxNumberOfRepayments";

    public static final String VALUE_CONDITION_END_WITH_ERROR = "condition.type.must.end.with.greterthan";
    public static final String VALUE_CONDITION_START_WITH_ERROR = "condition.type.must.start.with.equal";
    public static final String shortName = "shortName";

    public static final String multiDisburseLoanParameterName = "multiDisburseLoan";
    public static final String maxTrancheCountParameterName = "maxTrancheCount";
    public static final String outstandingLoanBalanceParameterName = "outstandingLoanBalance";

    public static final String graceOnArrearsAgeingParameterName = "graceOnArrearsAgeing";
    public static final String overdueDaysForNPAParameterName = "overdueDaysForNPA";
    public static final String minimumDaysBetweenDisbursalAndFirstRepayment = "minimumDaysBetweenDisbursalAndFirstRepayment";
    public static final String minimumPeriodsBetweenDisbursalAndFirstRepayment = "minimumPeriodsBetweenDisbursalAndFirstRepayment";
    public static final String isMinDurationApplicableForAllDisbursementsParamName = "isMinDurationApplicableForAllDisbursements";
    public static final String accountMovesOutOfNPAOnlyOnArrearsCompletionParamName = "accountMovesOutOfNPAOnlyOnArrearsCompletion";
    public static final String stopLoanProcessingOnNpa = "stopLoanProcessingOnNpa";

    // Interest recalculation related
    public static final String isInterestRecalculationEnabledParameterName = "isInterestRecalculationEnabled";
    public static final String daysInYearTypeParameterName = "daysInYearType";
    public static final String daysInMonthTypeParameterName = "daysInMonthType";
    public static final String interestRecalculationCompoundingMethodParameterName = "interestRecalculationCompoundingMethod";
    public static final String rescheduleStrategyMethodParameterName = "rescheduleStrategyMethod";
    public static final String recalculationRestFrequencyTypeParameterName = "recalculationRestFrequencyType";
    public static final String recalculationRestFrequencyIntervalParameterName = "recalculationRestFrequencyInterval";
    public static final String recalculationRestFrequencyWeekdayParamName = "recalculationRestFrequencyDayOfWeekType";
    public static final String recalculationRestFrequencyNthDayParamName = "recalculationRestFrequencyNthDayType";
    public static final String recalculationRestFrequencyOnDayParamName = "recalculationRestFrequencyOnDayType";
    public static final String isArrearsBasedOnOriginalScheduleParamName = "isArrearsBasedOnOriginalSchedule";
    public static final String preClosureInterestCalculationStrategyParamName = "preClosureInterestCalculationStrategy";
    public static final String recalculationCompoundingFrequencyTypeParameterName = "recalculationCompoundingFrequencyType";
    public static final String recalculationCompoundingFrequencyIntervalParameterName = "recalculationCompoundingFrequencyInterval";
    public static final String recalculationCompoundingFrequencyWeekdayParamName = "recalculationCompoundingFrequencyDayOfWeekType";
    public static final String recalculationCompoundingFrequencyNthDayParamName = "recalculationCompoundingFrequencyNthDayType";
    public static final String recalculationCompoundingFrequencyOnDayParamName = "recalculationCompoundingFrequencyOnDayType";
    public static final String isCompoundingToBePostedAsTransactionParamName = "isCompoundingToBePostedAsTransaction";

    // Guarantee related
    public static final String holdGuaranteeFundsParamName = "holdGuaranteeFunds";
    public static final String mandatoryGuaranteeParamName = "mandatoryGuarantee";
    public static final String minimumGuaranteeFromOwnFundsParamName = "minimumGuaranteeFromOwnFunds";
    public static final String minimumGuaranteeFromGuarantorParamName = "minimumGuaranteeFromGuarantor";

    public static final String principalThresholdForLastInstallmentParamName = "principalThresholdForLastInstallment";
    public static final BigDecimal DEFAULT_PRINCIPAL_THRESHOLD_FOR_MULTI_DISBURSE_LOAN = BigDecimal.valueOf(50);
    public static final BigDecimal DEFAULT_PRINCIPAL_THRESHOLD_FOR_SINGLE_DISBURSE_LOAN = BigDecimal.valueOf(0);
    // Fixed installment configuration related
    public static final String canDefineEmiAmountParamName = "canDefineInstallmentAmount";
    public static final String installmentAmountInMultiplesOfParamName = "installmentAmountInMultiplesOf";
    public static final String adjustedInstallmentInMultiplesOfParamName = "adjustedInstallmentInMultiplesOf";
    public static final String adjustFirstEMIAmountParamName = "adjustFirstEMIAmount";
    public static final String adjustInterestForRoundingParamName = "adjustInterestForRounding";
    
    //Loan Configurable Attributes
    public static final String allowAttributeOverridesParamName = "allowAttributeOverrides";
    public static final String amortizationTypeParamName = "amortizationType";
    public static final String interestTypeParamName = "interestType";
    public static final String transactionProcessingStrategyIdParamName = "transactionProcessingStrategyId";
    public static final String interestCalculationPeriodTypeParamName = "interestCalculationPeriodType";
    public static final String inArrearsToleranceParamName = "inArrearsTolerance";
    public static final String repaymentEveryParamName = "repaymentEvery";
    public static final String graceOnPrincipalAndInterestPaymentParamName = "graceOnPrincipalAndInterestPayment";
    public static final String allowCompoundingOnEodParamName = "allowCompoundingOnEod";
    public static final String syncExpectedWithDisbursementDate = "syncExpectedWithDisbursementDate";
    
    //Variable Installments Settings
    public static final String allowVariableInstallmentsParamName = "allowVariableInstallments" ;
    public static final String minimumGapBetweenInstallments = "minimumGap" ;
    public static final String maximumGapBetweenInstallments = "maximumGap" ;
    public static final String minLoanTerm = "minLoanTerm";
    public static final String maxLoanTerm = "maxLoanTerm";
    public static final String loanTenureFrequencyType = "loanTenureFrequencyType";
    
    public static final String allowPartialPeriodInterestCalcualtionParamName = "allowPartialPeriodInterestCalcualtion";

    public static final String isEmiBasedOnDisbursements = "isEmiBasedOnDisbursements" ;
    
    public static final String installmentCalculationPeriodTypeParamName = "installmentCalculationPeriodType";
    public static final String brokenPeriodMethodTypeParamName = "brokenPeriodMethodType";
    
    public static final String canUseForTopup = "canUseForTopup";
    
    public static final String allowUpfrontCollection = "allowUpfrontCollection";
    
    //Subsidy related constants
    public static final String isSubsidyApplicableParamName = "isSubsidyApplicable";
    public static final String createSubsidyAccountMappingsParamName = "createSubsidyAccountMappings";
    
    public static final String closeLoanOnOverpayment = "closeLoanOnOverpayment";
    
    public static final String weeksInYearType = "weeksInYearType";
    public static final String isFlatInterestRateParamName = "isFlatInterestRate";
    
    public static final String allowNegativeLoanBalance = "allowNegativeLoanBalance";
    public static final String considerFutureDisbursementsInSchedule = "considerFutureDisbursementsInSchedule";
    public static final String considerAllDisbursementsInSchedule = "considerAllDisbursementsInSchedule";
    
    public static final String percentageOfDisbursementToBeTransferred = "percentageOfDisbursementToBeTransferred";

    public static final String applicableForLoanTypeParamName = "applicableForLoanType";
    public static final String isEnableRestrictionForClientProfileParamName = "isEnableRestrictionForClientProfile";
    public static final String profileTypeParamName = "profileType";
    public static final String selectedProfileTypeValuesParamName = "selectedProfileTypeValues";
    
    /**
     * {@link LoanProductApplicableForLoanType}
     */
    public static final String ALL_TYPES = "All Customers";
    public static final String INDIVIDUAL_CLIENT = "Individual Client";
    public static final String GROUP = "Group";

    /**
     * {@link ClientProfileType}
     */
    public static final String LEGAL_FORM = "Legal Form";
    public static final String CLIENT_TYPE = "Client Type";
    public static final String CODE_CLIENT_TYPE = "ClientType";
    public static final String CLIENT_CLASSIFICATION = "Client Classification";
    public static final String CODE_CLIENT_CLASSIFICATION = "ClientClassification";
    public static final String CLIENT_TYPE_CODE = "ClientType";
    public static final String CLIENT_CLASSIFICATION_CODE = "ClientClassification";

    public static final String localeParamName = "locale";

    public static final String ERROR_CODE_EMPTY_LIST_CLIENT_TYPE = "not.supported.for.client.type";
    public static final String ERROR_CODE_EMPTY_LIST_CLIENT_CLASSIFICATION = "not.supported.for.client.classification";
    public static final String ERROR_CODE_SELECTED_PROFILE_TYPE_NOT_BELONGS_TO_CLIENT_TYPE = "not.belongs.to.client.type";
    public static final String ERROR_CODE_SELECTED_PROFILE_TYPE_NOT_BELONGS_TO_CLIENT_CLASSIFICATION = "not.belongs.to.client.classification";
}