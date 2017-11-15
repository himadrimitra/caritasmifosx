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

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.cache.domain.CacheType;
import org.apache.fineract.infrastructure.cache.domain.PlatformCache;
import org.apache.fineract.infrastructure.cache.domain.PlatformCacheRepository;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyConstant;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.useradministration.domain.Permission;
import org.apache.fineract.useradministration.domain.PermissionRepository;
import org.apache.fineract.useradministration.exception.PermissionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationDomainServiceJpa implements ConfigurationDomainService {

    private final PermissionRepository permissionRepository;
    private final GlobalConfigurationRepositoryWrapper globalConfigurationRepository;
    private final PlatformCacheRepository cacheTypeRepository;

    @Autowired
    public ConfigurationDomainServiceJpa(final PermissionRepository permissionRepository,
            final GlobalConfigurationRepositoryWrapper globalConfigurationRepository, final PlatformCacheRepository cacheTypeRepository) {
        this.permissionRepository = permissionRepository;
        this.globalConfigurationRepository = globalConfigurationRepository;
        this.cacheTypeRepository = cacheTypeRepository;
    }

    @Override
    public boolean isMakerCheckerEnabledForTask(final String taskPermissionCode) {
        if (StringUtils.isBlank(taskPermissionCode)) { throw new PermissionNotFoundException(taskPermissionCode); }

        final Permission thisTask = this.permissionRepository.findOneByCode(taskPermissionCode);
        if (thisTask == null) { throw new PermissionNotFoundException(taskPermissionCode); }

        final String makerCheckerConfigurationProperty = "maker-checker";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(makerCheckerConfigurationProperty);

        return thisTask.hasMakerCheckerEnabled() && property.isEnabled();
    }

    @Override
    public boolean isAmazonS3Enabled() {
        return getGlobalConfigurationPropertyData("amazon-S3").isEnabled();
    }

    @Override
    public boolean isRescheduleFutureRepaymentsEnabled() {
        final String rescheduleRepaymentsConfigurationProperty = "reschedule-future-repayments";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(rescheduleRepaymentsConfigurationProperty);
        return property.isEnabled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.fineract.infrastructure.configuration.domain.
     * ConfigurationDomainService#isHolidaysEnabled()
     */
    @Override
    public boolean isRescheduleRepaymentsOnHolidaysEnabled() {
        final String holidaysConfigurationProperty = "reschedule-repayments-on-holidays";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(holidaysConfigurationProperty);
        return property.isEnabled();
    }

    @Override
    public boolean allowTransactionsOnHolidayEnabled() {
        final String allowTransactionsOnHolidayProperty = "allow-transactions-on-holiday";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(allowTransactionsOnHolidayProperty);
        return property.isEnabled();
    }

    @Override
    public boolean allowTransactionsOnNonWorkingDayEnabled() {
        final String propertyName = "allow-transactions-on-non_workingday";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isConstraintApproachEnabledForDatatables() {
        final String propertyName = "constraint_approach_for_datatables";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isEhcacheEnabled() {
        return this.cacheTypeRepository.findOne(Long.valueOf(1)).isEhcacheEnabled();
    }

    @Transactional
    @Override
    public void updateCache(final CacheType cacheType) {
        final PlatformCache cache = this.cacheTypeRepository.findOne(Long.valueOf(1));
        cache.update(cacheType);
        this.cacheTypeRepository.save(cache);
    }

    @Override
    public Long retrievePenaltyWaitPeriod() {
        final String propertyName = "penalty-wait-period";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public Long retrieveGraceOnPenaltyPostingPeriod() {
        final String propertyName = "grace-on-penalty-posting";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public boolean isPasswordForcedResetEnable() {
        final String propertyName = "force-password-reset-days";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public Long retrievePasswordLiveTime() {
        final String propertyName = "force-password-reset-days";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public Long retrieveOpeningBalancesContraAccount() {
        final String propertyName = "office-opening-balances-contra-account";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public boolean isSavingsInterestPostingAtCurrentPeriodEnd() {
        final String propertyName = "savings-interest-posting-current-period-end";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public Integer retrieveFinancialYearBeginningMonth() {
        final String propertyName = "financial-year-beginning-month";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) return integerValue(property.getValue());
        return 1;
    }

    @Override
    public Integer retrieveMinAllowedClientsInGroup() {
        final String propertyName = "min-clients-in-group";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) { return integerValue(property.getValue()); }
        return null;
    }

    @Override
    public Integer retrieveMaxAllowedClientsInGroup() {
        final String propertyName = "max-clients-in-group";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) { return integerValue(property.getValue()); }
        return null;
    }

    @Override
    public boolean isMeetingMandatoryForJLGLoans() {
        final String propertyName = "meetings-mandatory-for-jlg-loans";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public int getRoundingMode() {
        final String propertyName = "rounding-mode";
        int defaultValue = 6; // 6 Stands for HALF-EVEN
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) {
            int value = integerValue(property.getValue());
            if (value < 0 || value > 6) { return defaultValue; }
            return value;
        }
        return defaultValue;
    }

    @Override
    public int getAdjustedAmountRoundingMode() {
        final String propertyName = "adjusted-amount-rounding-mode";
        int defaultValue = 6; // 6 Stands for HALF-EVEN
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) {
            int value = integerValue(property.getValue());
            if (value < 0 || value > 6) { return defaultValue; }
            return value;
        }
        return defaultValue;
    }

    @Override
    public boolean isOrganisationstartDateEnabled() {
        final String propertyName = "organisation-start-date";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public Date retrieveOrganisationStartDate() {
        final String propertyName = "organisation-start-date";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.getDateValue();
    }

    @Override
    public boolean isPaymnetypeApplicableforDisbursementCharge() {
        final String propertyName = "paymenttype-applicable-for-disbursement-charges";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isSkippingMeetingOnFirstDayOfMonthEnabled() {
        return getGlobalConfigurationPropertyData("skip-repayment-on-first-day-of-month").isEnabled();
    }

    @Override
    public Long retreivePeroidInNumberOfDaysForSkipMeetingDate() {
        final String propertyName = "skip-repayment-on-first-day-of-month";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public boolean isInterestChargedFromDateSameAsDisbursementDate() {
        final String propertyName = "interest-charged-from-date-same-as-disbursal-date";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled() {
        final String propertyName = "change-emi-if-repaymentdate-same-as-disbursementdate";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isDailyTPTLimitEnabled() {
        final String propertyName = "daily-tpt-limit";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public Long getDailyTPTLimit() {
        final String propertyName = "daily-tpt-limit";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public boolean isDefaultCurrencyEnabled() {
        return getGlobalConfigurationPropertyData("default-organisation-currency").isEnabled();
    }

    @Override
    public String retreiveDefaultCurrency() {
        final String propertyName = "default-organisation-currency";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.getValue();
    }

    public Long longValue(String value) {
        if (value == null) { return null; }
        return Long.parseLong(value);
    }

    public Integer integerValue(String value) {
        if (value == null) { return null; }
        return Integer.parseInt(value);
    }

    @Override
    public boolean isForceLoanRepaymentFrequencyMatchWithMeetingFrequencyEnabled() {
        final String propertyName = "force-loan-repayment-frequency-match-with-meeting-frequency";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isShowLoanDetailsInCenterPageEnabled() {
        final String propertyName = "show-loan-details-in-center-page";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public GlobalConfigurationPropertyData getGlobalConfigurationPropertyData(final String propertyName) {
        GlobalConfigurationProperty configuration = this.globalConfigurationRepository.findOneByNameWithNotFoundDetection(propertyName);
        return configuration.toData();
    }

    @Override
    public boolean isSavingAccountsInculdedInCollectionSheet() {
        return getGlobalConfigurationPropertyData("savings_account_included_in_collection_sheet").isEnabled();
    }

    @Override
    public boolean isWithDrawForSavingsIncludedInCollectionSheet() {
        return getGlobalConfigurationPropertyData("savings_withdraw_included_in_collection_sheet").isEnabled();
    }

    @Override
    public boolean isSearchIncludeGroupInfo() {
        return getGlobalConfigurationPropertyData("show-hierarchy-details-on-search").isEnabled();
    }

    @Override
    public boolean isCustomerDeDuplicationEnabled() {
        return getGlobalConfigurationPropertyData("customer-deduplication").isEnabled();
    }

    @Override
    public boolean isWorkFlowEnabled() {
        return getGlobalConfigurationPropertyData(GlobalConfigurationPropertyConstant.WORK_FLOW).isEnabled();
    }

    @Override
    public boolean isJlgLoansIncludedInIndividualCollectionSheet() {
        return getGlobalConfigurationPropertyData("jlg_loans_included_in_individual_collection_sheet").isEnabled();
    }

    @Override
    public boolean isGlimLoanInClientProfileShown() {
        return getGlobalConfigurationPropertyData("glim-loans-in-client-profile").isEnabled();
    }

    @Override
    public boolean isCgtEnabled() {
        return getGlobalConfigurationPropertyData("enable-cgt").isEnabled();
    }

    @Override
    public boolean isMinCgtDaysEnabled() {
        return getGlobalConfigurationPropertyData("min-cgt-days").isEnabled();
    }

    @Override
    public boolean isMaxCgtDaysEnabled() {
        return getGlobalConfigurationPropertyData("max-cgt-days").isEnabled();
    }

    @Override
    public Long getMinCgtDays() {
        final String propertyName = "min-cgt-days";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public Long getMaxCgtDays() {
        final String propertyName = "max-cgt-days";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return longValue(property.getValue());
    }

    @Override
    public boolean isMaxLoginAttemptsEnable() {
        final String propertyName = "max-login-attempts";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public Integer retrieveMaxLoginAttempts() {
        final String propertyName = "max-login-attempts";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) return integerValue(property.getValue());
        return 1;
    }

    @Override
    public boolean isLoanOfficerToCenterHierarchyEnabled() {
        return getGlobalConfigurationPropertyData("apply-loan-officer-to-center-hierarchy").isEnabled();
    }

    @Override
    public String getMaskedRegex() {
        final String propertyName = "mask-regex";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) return property.getValue();
        return "\\w(?=.{4})";
    }

    @Override
    public String getMaskedCharacter() {
        final String propertyName = "mask-replacechar";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) return property.getValue();
        return "x";
    }

    @Override
    public boolean isMonthlyLoansSyncWithWeeklyMeetings() {
        final String propertyName = "sync-monthly-loans-to-weekly-meetings";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }
    
    @Override
    public boolean isGlimPaymentAsGroup() {
        final String propertyName = "glim-payment-as-group";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isOfficeSpecificProductsEnabled() {
        return getGlobalConfigurationPropertyData("office-specific-products-enabled").isEnabled();
    }
    
    @Override
    public Integer retrieveNumberOfDays() {
        final String propertyName = "number-of-days-for-ACH-failed-transactions";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) { return integerValue(property.getValue()); }
        return null;
    }

    @Override
    public boolean allowClientsInMultipleGroups() {
        final String propertyName = "allow-clients-in-multiple-groups";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public int getInstallmentAmountRoundingMode() {
        final String propertyName = "installment-amount-rounding-mode";
        int defaultValue = 6; // 6 Stands for HALF-EVEN
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) {
            int value = integerValue(property.getValue());
            if (value < 0 || value > 6) { return defaultValue; }
            return value;
        }
        return defaultValue;
    }

    @Override
    public boolean includeClientChargesInCollectionSheet() {
        final String propertyName = "include-client-charges-in-collection-sheet";
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        return property.isEnabled();
    }

    @Override
    public boolean isEnabledEncryptLoginPasswordForAuthentication() {
        return getGlobalConfigurationPropertyData(GlobalConfigurationPropertyConstant.ENCRYPT_LOGIN_PASSWORD_FOR_AUTHENTICATION)
                .isEnabled();
    }

    @Override
    public boolean isEnabledEveryUserLoginGenerateNewCryptographicKeyPair() {
        return getGlobalConfigurationPropertyData(GlobalConfigurationPropertyConstant.EVERY_USER_LOGIN_GENERATE_NEW_CRYPTOGRAPHIC_KEY_PAIR)
                .isEnabled();
    }
    
    @Override
    public boolean isAllowPaymentsOnClosedLoansEnabled(){
        return getGlobalConfigurationPropertyData(GlobalConfigurationPropertyConstant.ALLOW_PAYMANTS_ON_CLOSED_LOANS)
                .isEnabled();
    }

    @Override
    public boolean isDisplayFirstNameLastLastNameFirstForClient() {
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(GlobalConfigurationPropertyConstant.DISPLAY_FIRSTNAME_LAST_LASTNAME_FIRST_FOR_CLIENT);
        return property.isEnabled();
    }
    
    @Override
    public int getMaxAllowedFileSizeToUpload() {
        final String propertyName = "max_file_upload_size_in_mb";
        int defaultValue = ContentRepository.MAX_FILE_UPLOAD_SIZE_IN_MB;
        final GlobalConfigurationPropertyData property = getGlobalConfigurationPropertyData(propertyName);
        if (property.isEnabled()) { return integerValue(property.getValue()); }
        return defaultValue;
    }
    
}