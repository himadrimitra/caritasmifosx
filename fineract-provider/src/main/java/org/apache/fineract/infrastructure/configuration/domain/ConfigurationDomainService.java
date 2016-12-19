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
package org.apache.fineract.infrastructure.configuration.domain;

import java.util.Date;

import org.apache.fineract.infrastructure.cache.domain.CacheType;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;

public interface ConfigurationDomainService {

    boolean isMakerCheckerEnabledForTask(String taskPermissionCode);

    boolean isAmazonS3Enabled();

    boolean isRescheduleFutureRepaymentsEnabled();

    boolean isRescheduleRepaymentsOnHolidaysEnabled();

    boolean allowTransactionsOnHolidayEnabled();

    boolean allowTransactionsOnNonWorkingDayEnabled();

    boolean isConstraintApproachEnabledForDatatables();

    boolean isEhcacheEnabled();

    void updateCache(CacheType cacheType);

    Long retrievePenaltyWaitPeriod();

    boolean isPasswordForcedResetEnable();

    Long retrievePasswordLiveTime();

    Long retrieveGraceOnPenaltyPostingPeriod();

    Long retrieveOpeningBalancesContraAccount();

    boolean isSavingsInterestPostingAtCurrentPeriodEnd();

    Integer retrieveFinancialYearBeginningMonth();

    public Integer retrieveMinAllowedClientsInGroup();

    public Integer retrieveMaxAllowedClientsInGroup();

    boolean isMeetingMandatoryForJLGLoans();

    int getRoundingMode();

    boolean isBackdatePenaltiesEnabled();
    
    boolean isOrganisationstartDateEnabled();
    
    Date retrieveOrganisationStartDate();
    
    boolean isPaymnetypeApplicableforDisbursementCharge();

    boolean isInterestChargedFromDateSameAsDisbursementDate();

    boolean isSkippingMeetingOnFirstDayOfMonthEnabled();
    
    Long retreivePeroidInNumberOfDaysForSkipMeetingDate();
    
    boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled();
    
    boolean isDailyTPTLimitEnabled();
    
    Long getDailyTPTLimit();

    int getAdjustedAmountRoundingMode();
    
    boolean isDefaultCurrencyEnabled();
    
    String retreiveDefaultCurrency();

    boolean isForceLoanRepaymentFrequencyMatchWithMeetingFrequencyEnabled();

    boolean isShowLoanDetailsInCenterPageEnabled();

    boolean isSavingAccountsInculdedInCollectionSheet();
    
    boolean isWithDrawForSavingsIncludedInCollectionSheet();
    
    boolean isSearchIncludeGroupInfo();

	GlobalConfigurationPropertyData getGlobalConfigurationPropertyData(
			String propertyName);
    
    boolean isCustomerDeDuplicationEnabled();

    boolean isJlgLoansIncludedInIndividualCollectionSheet();
    
    boolean isGlimLoanInClientProfileShown();

    boolean isCgtEnabled();
    
    boolean isMaxCgtDaysEnabled();
    
    boolean isMinCgtDaysEnabled();
    
    Long getMinCgtDays();
    
    Long getMaxCgtDays();
    
    boolean isLoanOfficerToCenterHierarchyEnabled();
}