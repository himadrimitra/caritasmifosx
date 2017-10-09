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
package org.apache.fineract.portfolio.savings.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobExecuter;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.JobRunner;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDpDetailsData;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavingsSchedularServiceImpl implements SavingsSchedularService {

    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final SavingsAccountReadPlatformService savingAccountReadPlatformService;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final JobExecuter jobExecuter;
    private final static Logger logger = LoggerFactory.getLogger(SavingsSchedularServiceImpl.class);

    @Autowired
    public SavingsSchedularServiceImpl(final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final SavingsAccountReadPlatformService savingAccountReadPlatformService,
            final CalendarInstanceRepository calendarInstanceRepository, final JobExecuter jobExecuter) {
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingAccountReadPlatformService = savingAccountReadPlatformService;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.jobExecuter = jobExecuter;
    }

    @CronTarget(jobName = JobName.POST_INTEREST_FOR_SAVINGS)
    @Override
    public void postInterestForAccounts() throws JobExecutionException {
        List<Long> savingsIds = this.savingAccountReadPlatformService.retrieveAllActiveSavingsIdsForActiveClients();
        logger.info("Post Interest to Savings job Start : "+savingsIds.size());
        JobRunner<List<Long>> runner = new PostInterestForAccountsJobRunner();
        final String errors = this.jobExecuter.executeJob(savingsIds, runner);
        logger.info("Post Interest to Savings job End: "+savingsIds.size());
        if (errors.length() > 0) { throw new JobExecutionException(errors); }

    }

    private void postInterestForAccounts(StringBuilder sb, List<Long> savingsIds) {
        final String errorMessage = "Post interest for Savings  failed for account:";
        for (Long savingsId : savingsIds) {
            try {
                boolean postInterestAsOn = false;
                LocalDate transactionDate = null;
                this.savingsAccountWritePlatformService.postInterest(savingsId, postInterestAsOn, transactionDate);
            } catch (Exception e) {
                ExceptionHelper.handleExceptions(e, sb, errorMessage, savingsId, logger);
            }
        }
    }
    
    private class PostInterestForAccountsJobRunner implements JobRunner<List<Long>> {

        @Override
        public void runJob(final List<Long> savingIds, final StringBuilder sb) {
            postInterestForAccounts(sb, savingIds);

        }

    }

    @CronTarget(jobName = JobName.UPDATE_SAVINGS_DORMANT_ACCOUNTS)
    @Override
    public void updateSavingsDormancyStatus() throws JobExecutionException {
    	final LocalDate tenantLocalDate = DateUtils.getLocalDateOfTenant();

    	final List<Long> savingsPendingInactive = this.savingAccountReadPlatformService
    													.retrieveSavingsIdsPendingInactive(tenantLocalDate);
    	if(null != savingsPendingInactive && savingsPendingInactive.size() > 0){
    		for(Long savingsId : savingsPendingInactive){
    			this.savingsAccountWritePlatformService.setSubStatusInactive(savingsId);
    		}
    	}

    	final List<Long> savingsPendingDormant = this.savingAccountReadPlatformService
				.retrieveSavingsIdsPendingDormant(tenantLocalDate);
		if(null != savingsPendingDormant && savingsPendingDormant.size() > 0){
			for(Long savingsId : savingsPendingDormant){
				this.savingsAccountWritePlatformService.setSubStatusDormant(savingsId);
			}
		}

    	final List<Long> savingsPendingEscheat = this.savingAccountReadPlatformService
				.retrieveSavingsIdsPendingEscheat(tenantLocalDate);
		if(null != savingsPendingEscheat && savingsPendingEscheat.size() > 0){
			for(Long savingsId : savingsPendingEscheat){
				this.savingsAccountWritePlatformService.escheat(savingsId);
			}
		}
    }

    @CronTarget(jobName = JobName.REDUCE_DP_LIMIT_FOR_SAVINGS)
    @Override
    public void reduceDpLimitForAccounts() throws JobExecutionException {
        final LocalDate today = DateUtils.getLocalDateOfTenant();
        final Collection<SavingsAccountDpDetailsData> savingsAccountDpDetailsDatas = this.savingAccountReadPlatformService
                .retriveSavingsAccountDpDetailsDatas();
        for (final SavingsAccountDpDetailsData savingsAccountDpDetailsData : savingsAccountDpDetailsDatas) {
            final CalendarInstance calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(
                    savingsAccountDpDetailsData.getId(), CalendarEntityType.SAVINGS_DP_DETAILS.getValue());
            final String recurringRule = calendarInstance.getCalendar().getRecurrence();
            final LocalDate seedDate = calendarInstance.getCalendar().getStartDateLocalDate();
            final LocalDate periodStartDate = new LocalDate(savingsAccountDpDetailsData.getSavingsActivatedonDate());
            final LocalDate periodEndDate = today;
            final Integer duration = savingsAccountDpDetailsData.getDuration();
            final boolean isSkippMeetingOnFirstDay = false;
            final Integer numberOfDays = null;
            final Collection<LocalDate> localDates = CalendarUtils.getRecurringDates(recurringRule, seedDate, periodStartDate,
                    periodEndDate, duration, isSkippMeetingOnFirstDay, numberOfDays);
            final int periodNumber = localDates.size();
            if (periodNumber > 0) {
                final BigDecimal dpAmount = savingsAccountDpDetailsData.getDpAmount();
                BigDecimal dpLimitAmount = BigDecimal.ZERO;
                if (isPeriodNumberFallsInDuration(periodNumber, duration)) {
                    BigDecimal amount = savingsAccountDpDetailsData.getAmount();
                    dpLimitAmount = dpAmount.subtract(BigDecimal.valueOf(periodNumber).multiply(amount));
                    if (dpLimitAmount.compareTo(BigDecimal.ZERO) == -1) {
                        dpLimitAmount = BigDecimal.ZERO;
                    }
                }
                this.savingAccountReadPlatformService.updateSavingsAccountDpLimit(dpLimitAmount,
                        savingsAccountDpDetailsData.getSavingsAccountId());
            }
        }
    }

    private boolean isPeriodNumberFallsInDuration(int periodNumber, Integer duration) {
        boolean isNumberOfPeriodFallsInDuring = false;
        if (periodNumber < duration.intValue()) {
            isNumberOfPeriodFallsInDuring = true;
        }
        return isNumberOfPeriodFallsInDuring;
    }
}
