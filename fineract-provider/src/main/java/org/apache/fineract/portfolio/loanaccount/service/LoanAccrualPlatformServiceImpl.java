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
package org.apache.fineract.portfolio.loanaccount.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobExecuter;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.JobRunner;
import org.apache.fineract.infrastructure.jobs.service.SchedulerServiceConstants;
import org.apache.fineract.portfolio.loanaccount.data.LoanScheduleAccrualData;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanAccrualPlatformServiceImpl implements LoanAccrualPlatformService {

    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanAccrualWritePlatformService loanAccrualWritePlatformService;
    private final JobExecuter jobExecuter;
    private final static Logger logger = LoggerFactory.getLogger(LoanAccrualPlatformServiceImpl.class);

    @Autowired
    public LoanAccrualPlatformServiceImpl(final LoanReadPlatformService loanReadPlatformService,
            final LoanAccrualWritePlatformService loanAccrualWritePlatformService, final JobExecuter jobExecuter) {
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanAccrualWritePlatformService = loanAccrualWritePlatformService;
        this.jobExecuter = jobExecuter;
    }

    @Override
    @CronTarget(jobName = JobName.ADD_DUE_DATE_ACCRUAL_ENTRIES)
    public void addAccrualAccounting() throws JobExecutionException {
        Collection<LoanScheduleAccrualData> loanScheduleAccrualDatas = this.loanReadPlatformService.retriveScheduleAccrualData();
       
        Map<Long, Collection<LoanScheduleAccrualData>> loanDataMap = new HashMap<>();
        for (final LoanScheduleAccrualData accrualData : loanScheduleAccrualDatas) {
            if (loanDataMap.containsKey(accrualData.getLoanId())) {
                loanDataMap.get(accrualData.getLoanId()).add(accrualData);
            } else {
                Collection<LoanScheduleAccrualData> accrualDatas = new ArrayList<>();
                accrualDatas.add(accrualData);
                loanDataMap.put(accrualData.getLoanId(), accrualDatas);
            }
        }
        JobRunner<Map<Long, Collection<LoanScheduleAccrualData>>> runner = new AddAccrualAccountingJobRunner();
        String errors = this.jobExecuter.executeJob(loanDataMap, runner);
        if (errors.length() > 0) { throw new JobExecutionException(errors); }
    }
    
    private class AddAccrualAccountingJobRunner implements JobRunner<Map<Long, Collection<LoanScheduleAccrualData>>> {

        @Override
        public void runJob(Map<Long, Collection<LoanScheduleAccrualData>> jobDetails, StringBuilder sb) {
            addAccrualAccounting(jobDetails, sb);
        }
    }

    private void addAccrualAccounting(Map<Long, Collection<LoanScheduleAccrualData>> loanDataMap, StringBuilder sb) {
        for (Map.Entry<Long, Collection<LoanScheduleAccrualData>> mapEntry : loanDataMap.entrySet()) {
            try {
                this.loanAccrualWritePlatformService.addAccrualAccounting(mapEntry.getKey(), mapEntry.getValue());
            } catch (Exception e) {
                String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                sb.append("failed to add accural transaction for loan " + mapEntry.getKey() + " with message " + rootCause);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @CronTarget(jobName = JobName.ADD_PERIODIC_ACCRUAL_ENTRIES)
    public void addPeriodicAccruals() throws JobExecutionException {
        List<Long> loanList =  (List<Long>) ThreadLocalContextUtil.getJobParams().get("loanList");
        LocalDate tillDate = (LocalDate) ThreadLocalContextUtil.getJobParams().get(SchedulerServiceConstants.EXECUTE_AS_ON_DATE);
        if(tillDate == null){
            tillDate = DateUtils.getLocalDateOfTenant();
        }
        String errors = addPeriodicAccruals(tillDate, loanList);
        if (errors.length() > 0) { throw new JobExecutionException(errors); }
    }

    @Override
    public String addPeriodicAccruals(final LocalDate tilldate, List<Long> loanList) {
        Collection<LoanScheduleAccrualData> loanScheduleAccrualDatas = this.loanReadPlatformService.retrivePeriodicAccrualData(tilldate, loanList);
        final boolean accrueAllInstallments = false;
        return addPeriodicAccruals(tilldate, loanScheduleAccrualDatas, accrueAllInstallments);
    }

    @Override
    public String addPeriodicAccruals(final LocalDate tilldate, Collection<LoanScheduleAccrualData> loanScheduleAccrualDatas, final boolean accrueAllInstallments) {
        Map<Long, Collection<LoanScheduleAccrualData>> loanDataMap = new HashMap<>();
        for (final LoanScheduleAccrualData accrualData : loanScheduleAccrualDatas) {
            if (loanDataMap.containsKey(accrualData.getLoanId())) {
                loanDataMap.get(accrualData.getLoanId()).add(accrualData);
            } else {
                Collection<LoanScheduleAccrualData> accrualDatas = new ArrayList<>();
                accrualDatas.add(accrualData);
                loanDataMap.put(accrualData.getLoanId(), accrualDatas);
            }
        }

        JobRunner<Map<Long, Collection<LoanScheduleAccrualData>>> runner = new AddPeriodicAccrualAccountingJobRunner(tilldate,accrueAllInstallments);
        return this.jobExecuter.executeJob(loanDataMap, runner);

    }
    
    private class AddPeriodicAccrualAccountingJobRunner implements JobRunner<Map<Long, Collection<LoanScheduleAccrualData>>> {

        final LocalDate tilldate;
        final boolean accrueAllInstallments;

        public AddPeriodicAccrualAccountingJobRunner(final LocalDate tilldate, final boolean accrueAllInstallments) {
            this.tilldate = tilldate;
            this.accrueAllInstallments = accrueAllInstallments;
        }

        @Override
        public void runJob(Map<Long, Collection<LoanScheduleAccrualData>> jobDetails, StringBuilder sb) {
            addPeriodicAccruals(this.tilldate, this.accrueAllInstallments, sb, jobDetails);
        }
    }

    private void addPeriodicAccruals(final LocalDate tilldate, final boolean accrueAllInstallments, StringBuilder sb,
            Map<Long, Collection<LoanScheduleAccrualData>> loanDataMap) {
        for (Map.Entry<Long, Collection<LoanScheduleAccrualData>> mapEntry : loanDataMap.entrySet()) {
            try {
            	logger.debug("Add Periodic Accrual, LoanId[" + mapEntry.getKey() + "]");
                this.loanAccrualWritePlatformService.addPeriodicAccruals(tilldate, mapEntry.getKey(), mapEntry.getValue(), accrueAllInstallments);
            } catch (Exception e) {
                String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                sb.append("failed to add accural transaction for loan " + mapEntry.getKey() + " with message " + rootCause);
            }
        }
    }
    
    

    @Override
    @CronTarget(jobName = JobName.ADD_PERIODIC_ACCRUAL_ENTRIES_FOR_LOANS_WITH_INCOME_POSTED_AS_TRANSACTIONS)
    public void addPeriodicAccrualsForLoansWithIncomePostedAsTransactions() throws JobExecutionException {
        Collection<Long> loanIds = this.loanReadPlatformService.retrieveLoanIdsWithPendingIncomePostingTransactions();
        if(loanIds != null && loanIds.size() > 0){
            StringBuilder sb = new StringBuilder();
            for (Long loanId : loanIds) {
                try {
                    this.loanAccrualWritePlatformService.addIncomeAndAccrualTransactions(loanId);
                } catch (Exception e) {
                    String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                    sb.append("failed to add income and accrual transaction for loan " + loanId + " with message " + rootCause);
                }
            }
            if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
        }
    }
}
