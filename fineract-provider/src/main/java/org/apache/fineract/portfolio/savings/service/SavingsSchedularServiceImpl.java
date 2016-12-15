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
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDpDetailsData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountStatusType;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavingsSchedularServiceImpl implements SavingsSchedularService {

    private final SavingsAccountAssembler savingAccountAssembler;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final SavingsAccountRepository savingAccountRepository;
    private final SavingsAccountReadPlatformService savingAccountReadPlatformService;

    @Autowired
    public SavingsSchedularServiceImpl(final SavingsAccountAssembler savingAccountAssembler,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final SavingsAccountRepository savingAccountRepository,
            final SavingsAccountReadPlatformService savingAccountReadPlatformService) {
        this.savingAccountAssembler = savingAccountAssembler;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingAccountRepository = savingAccountRepository;
        this.savingAccountReadPlatformService = savingAccountReadPlatformService;
    }

    @CronTarget(jobName = JobName.POST_INTEREST_FOR_SAVINGS)
    @Override
    public void postInterestForAccounts() throws JobExecutionException {
        final List<SavingsAccount> savingsAccounts = this.savingAccountRepository.findSavingAccountByStatus(SavingsAccountStatusType.ACTIVE
                .getValue());
        StringBuffer sb = new StringBuffer();
        for (final SavingsAccount savingsAccount : savingsAccounts) {
            try {
                this.savingAccountAssembler.assignSavingAccountHelpers(savingsAccount);
                boolean postInterestAsOn = false;
                LocalDate transactionDate = null;
                this.savingsAccountWritePlatformService.postInterest(savingsAccount, postInterestAsOn, transactionDate);
            } catch (Exception e) {
                Throwable realCause = e;
                if (e.getCause() != null) {
                    realCause = e.getCause();
                }
                sb.append("failed to post interest for Savings with id " + savingsAccount.getId() + " with message "
                        + realCause.getMessage());
            }
        }
        
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
        Collection<SavingsAccountDpDetailsData>  savingsAccountDpDetailsDatas = this.savingAccountReadPlatformService.retriveSavingsAccountDpDetailsDatas();
        
        for (SavingsAccountDpDetailsData savingsAccountDpDetailsData : savingsAccountDpDetailsDatas) {
            int periodNumber = calculatePeriodsBetweenDates(new LocalDate(savingsAccountDpDetailsData.getsavingsActivatedonDate()), today, savingsAccountDpDetailsData);
            if (periodNumber > 0) {
                BigDecimal dpAmount = savingsAccountDpDetailsData.getDpAmount();
                BigDecimal amount = savingsAccountDpDetailsData.getAmount();
                Integer duration = savingsAccountDpDetailsData.getDuration();
                BigDecimal dpLimitAmount = BigDecimal.ZERO;
                if (isPeriodNumberFallsInDuration(periodNumber, duration)) {
                    if (!isLastPeriod(periodNumber, duration)) {
                        dpLimitAmount = dpAmount.subtract(BigDecimal.valueOf(periodNumber).multiply(amount));
                        if (dpLimitAmount.compareTo(BigDecimal.ZERO) == -1) {
                            dpLimitAmount = BigDecimal.ZERO;
                        }
                    } 
                } 
                this.savingAccountReadPlatformService.updateSavingsAccountDpLimit(dpLimitAmount, savingsAccountDpDetailsData.getSavingsAccountId());
            }
        }
        
        
    }
    
    private boolean isLastPeriod(int periodNumber, Integer duration) {
        boolean isLastPeriod = false;
        if (periodNumber == duration.intValue()) {
            isLastPeriod = true;
        }
        return isLastPeriod;
    }

    private boolean isPeriodNumberFallsInDuration(int periodNumber, Integer duration) {
        boolean isNumberOfPeriodFallsInDuring = false;
        if (periodNumber <= duration.intValue()) {
            isNumberOfPeriodFallsInDuring = true;
        }
        return isNumberOfPeriodFallsInDuring;
    }

    public int calculatePeriodsBetweenDates(final LocalDate startDate, LocalDate endDate, SavingsAccountDpDetailsData savingsAccountDpDetailsData) {
        int periodNumber = 0;
        switch(savingsAccountDpDetailsData.getFrequencyType()) {
            case DAYS:
                break;
            case MONTHS:
                int reduceEvery = savingsAccountDpDetailsData.getDpReductionEvery();
                periodNumber = Months.monthsBetween(startDate, endDate).getMonths();
                if (reduceEvery > 1) {
                    periodNumber = periodNumber / reduceEvery;
                }
                break;
            case YEARS:
                break;
            default:
                break;
                
        }
        return periodNumber;
        
    }
}
