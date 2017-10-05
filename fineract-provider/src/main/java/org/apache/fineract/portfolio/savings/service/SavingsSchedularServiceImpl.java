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

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDpDetailsData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SavingsSchedularServiceImpl implements SavingsSchedularService {

    private final SavingsAccountAssembler savingAccountAssembler;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final SavingsAccountRepositoryWrapper savingAccountRepository;
    private final SavingsAccountReadPlatformService savingAccountReadPlatformService;
    private final CalendarInstanceRepository calendarInstanceRepository;

    @Autowired
    public SavingsSchedularServiceImpl(final SavingsAccountAssembler savingAccountAssembler,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final SavingsAccountRepositoryWrapper savingAccountRepository,
            final SavingsAccountReadPlatformService savingAccountReadPlatformService,
            final CalendarInstanceRepository calendarInstanceRepository) {
        this.savingAccountAssembler = savingAccountAssembler;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingAccountRepository = savingAccountRepository;
        this.savingAccountReadPlatformService = savingAccountReadPlatformService;
        this.calendarInstanceRepository = calendarInstanceRepository;
    }

    @CronTarget(jobName = JobName.POST_INTEREST_FOR_SAVINGS)
    @Override
    public void postInterestForAccounts() throws JobExecutionException {
        int offSet = 0;
        Integer limt = 500;
        Integer numberOfFilteredRecords = 0;
        int numberOfRecordsProcessed = 0;
        StringBuffer sb = new StringBuffer();

        do {
            Page<Long> savingsIds = this.savingAccountReadPlatformService.retrieveAllActiveSavingsIdsForActiveClients(limt, offSet);
            for (Long savingsId : savingsIds.getPageItems()) {
                try {
                    boolean postInterestAsOn = false;
                    LocalDate transactionDate = null;
                    this.savingsAccountWritePlatformService.postInterest(savingsId, postInterestAsOn, transactionDate);
                } catch (Exception e) {
                    Throwable realCause = e;
                    if (e.getCause() != null) {
                        realCause = e.getCause();
                    }
                    sb.append("failed to post interest for Savings with id " + savingsId + " with message " + realCause.getMessage());
                }
                numberOfRecordsProcessed++;
            }

            offSet++;
            numberOfFilteredRecords = savingsIds.getTotalFilteredRecords();
        } while (numberOfRecordsProcessed < numberOfFilteredRecords);

        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }

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
