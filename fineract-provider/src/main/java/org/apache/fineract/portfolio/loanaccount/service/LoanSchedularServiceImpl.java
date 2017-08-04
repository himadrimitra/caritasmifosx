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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobExecuter;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.JobRunner;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.OverdueLoanScheduleData;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class LoanSchedularServiceImpl implements LoanSchedularService {

    private final static Logger logger = LoggerFactory.getLogger(LoanSchedularServiceImpl.class);
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final ConfigurationDomainService configurationDomainService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanWritePlatformService loanWritePlatformService;
    private final JobExecuter jobExecuter;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoanSchedularServiceImpl(final ConfigurationDomainService configurationDomainService,
            final LoanReadPlatformService loanReadPlatformService, final LoanWritePlatformService loanWritePlatformService,
            final JobExecuter jobExecuter, final RoutingDataSource dataSource) {
        this.configurationDomainService = configurationDomainService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanWritePlatformService = loanWritePlatformService;
        this.jobExecuter = jobExecuter;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_CHARGE_TO_OVERDUE_LOAN_INSTALLMENT)
    public void applyChargeForOverdueLoans() throws JobExecutionException {

        final Long penaltyWaitPeriodValue = this.configurationDomainService.retrievePenaltyWaitPeriod();
        final Boolean backdatePenalties = this.configurationDomainService.isBackdatePenaltiesEnabled();
        final Collection<OverdueLoanScheduleData> overdueLoanScheduledInstallments = this.loanReadPlatformService
                .retrieveAllLoansWithOverdueInstallments(penaltyWaitPeriodValue, backdatePenalties);

        if (!overdueLoanScheduledInstallments.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            final Map<Long, Collection<OverdueLoanScheduleData>> overdueScheduleData = new HashMap<>();
            for (final OverdueLoanScheduleData overdueInstallment : overdueLoanScheduledInstallments) {
                if (overdueScheduleData.containsKey(overdueInstallment.getLoanId())) {
                    overdueScheduleData.get(overdueInstallment.getLoanId()).add(overdueInstallment);
                } else {
                    Collection<OverdueLoanScheduleData> loanData = new ArrayList<>();
                    loanData.add(overdueInstallment);
                    overdueScheduleData.put(overdueInstallment.getLoanId(), loanData);
                }
            }
            final String errorMessage = "Apply Charges due for overdue loans failed for account:";
            for (final Long loanId : overdueScheduleData.keySet()) {
                try {
                    this.loanWritePlatformService.applyOverdueChargesForLoan(loanId, overdueScheduleData.get(loanId));
                } catch (Exception e) {
                    ExceptionHelper.handleExceptions(e, sb, errorMessage, loanId, logger);
                }
            }
            if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
        }
    }

    @Override
    @CronTarget(jobName = JobName.RECALCULATE_INTEREST_FOR_LOAN)
    public void recalculateInterest() throws JobExecutionException {
        List<Long> loanIds = this.loanReadPlatformService.fetchLoansForInterestRecalculation();
        logger.info("Loans taken for recalculate interest:"+loanIds.toString());
        JobRunner<List<Long>> runner = new RecalculateInterestJobRunner();
        final String errors = this.jobExecuter.executeJob(loanIds, runner);
        if (errors.length() > 0) { throw new JobExecutionException(errors); }
    }

    private class RecalculateInterestJobRunner implements JobRunner<List<Long>> {

        final Integer maxNumberOfRetries;
        final Integer maxIntervalBetweenRetries;
        final Boolean ignoreOverdue;

        public RecalculateInterestJobRunner() {
            maxNumberOfRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxRetriesOnDeadlock();
            maxIntervalBetweenRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxIntervalBetweenRetries();
            ignoreOverdue = ThreadLocalContextUtil.getIgnoreOverdue();
        }

        @Override
        public void runJob(final List<Long> loanIds, final StringBuilder sb) {
            ThreadLocalContextUtil.setIgnoreOverdue(this.ignoreOverdue);
            recalculateInterest(sb, this.maxNumberOfRetries, this.maxIntervalBetweenRetries, loanIds);

        }

    }

    private StringBuilder recalculateInterest(final StringBuilder sb, Integer maxNumberOfRetries, Integer maxIntervalBetweenRetries,
            List<Long> loanIds) {
        final String errorMessage = "Interest recalculation for loans failed for account:";
        for (Long loanId : loanIds) {
            logger.info("Loan ID " + loanId);
            Integer numberOfRetries = 0;
            while (numberOfRetries <= maxNumberOfRetries) {
                try {
                    this.loanWritePlatformService.recalculateInterest(loanId);
                    numberOfRetries = maxNumberOfRetries + 1;
                } catch (CannotAcquireLockException | ObjectOptimisticLockingFailureException exception) {
                    logger.info("Recalulate interest job has been retried  " + numberOfRetries + " time(s)");
                    /***
                     * Fail if the transaction has been retired for
                     * maxNumberOfRetries
                     **/
                    if (numberOfRetries >= maxNumberOfRetries) {
                        logger.warn("Recalulate interest job has been retried for the max allowed attempts of " + numberOfRetries
                                + " and will be rolled back");
                        sb.append("Recalulate interest job has been retried for the max allowed attempts of " + numberOfRetries
                                + " and will be rolled back");
                        break;
                    }
                    /***
                     * Else sleep for a random time (between 1 to 10 seconds)
                     * and continue
                     **/
                    try {
                        Random random = new Random();
                        int randomNum = random.nextInt(maxIntervalBetweenRetries + 1);
                        Thread.sleep(1000 + (randomNum * 1000));
                        numberOfRetries = numberOfRetries + 1;
                    } catch (InterruptedException e) {
                        sb.append("Interest recalculation for loans failed " + exception.getMessage());
                        break;
                    }
                } catch (Exception e) {
                    ExceptionHelper.handleExceptions(e, sb, errorMessage, loanId, logger);
                    numberOfRetries = maxNumberOfRetries + 1;
                }
            }
        }
        return sb;
    }

    @Override
    public void applyHolidaysToLoans(final HolidayDetailDTO holidayDetailDTO, final Map<Long, List<Holiday>> officeIds,
            final Set<Long> failedForOffices, final StringBuilder sb) throws JobExecutionException {
        final Collection<Integer> loanStatuses = new ArrayList<>(Arrays.asList(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(),
                LoanStatus.APPROVED.getValue(), LoanStatus.ACTIVE.getValue()));
        final String errorMessage = "Apply Holidays for loans failed for account:";
        for (final Map.Entry<Long, List<Holiday>> entry : officeIds.entrySet()) {
            try {
                LocalDate recalculateFrom = null;
                for (final Holiday holiday : entry.getValue()) {
                    if (recalculateFrom == null || recalculateFrom.isAfter(holiday.getFromDateLocalDate())) {
                        recalculateFrom = holiday.getFromDateLocalDate();
                    }
                }
                final Collection<Long> loansForProcess = this.loanReadPlatformService.retrieveLoansByOfficesAndHoliday(entry.getKey(),
                        entry.getValue(), loanStatuses, recalculateFrom);
                for (final Long loanId : loansForProcess) {
                    try {
                        this.loanWritePlatformService.updateScheduleDates(loanId, holidayDetailDTO, recalculateFrom);
                    } catch (Exception e) {
                        ExceptionHelper.handleExceptions(e, sb, errorMessage, loanId, logger);
                        failedForOffices.add(entry.getKey());
                    }

                }
            } catch (Exception e) {
                String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                logger.error("Apply Holidays for loans failed  with message " + rootCause);
                sb.append("Apply Holidays for loans failed with message ").append(rootCause);
                failedForOffices.add(entry.getKey());
            }
        }
    }
    
    @Override
    @Transactional
    public void updateNPAForNonAccrualBasedProducts(StringBuilder sb){
        
        try{
        int result = 0;
        
        String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
        
        final StringBuilder resetNPASqlBuilder = new StringBuilder(900);
        resetNPASqlBuilder.append("update m_loan loan ");
        resetNPASqlBuilder
                        .append("left join m_loan_arrears_aging laa on laa.loan_id = loan.id ");
        resetNPASqlBuilder
                        .append("inner join m_product_loan mpl on mpl.id = loan.product_id and mpl.overdue_days_for_npa is not null and mpl.accounting_type != ? ");
        resetNPASqlBuilder.append("set loan.is_npa = 0 ");
        resetNPASqlBuilder.append("where  loan.loan_status_id = 300 and loan.is_npa = 1 and ( ");
        resetNPASqlBuilder.append("laa.overdue_since_date_derived is null or (mpl.account_moves_out_of_npa_only_on_arrears_completion = 0 and ");
        resetNPASqlBuilder.append("laa.overdue_since_date_derived >= SUBDATE(?,INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day))) ");

        result += jdbcTemplate.update(resetNPASqlBuilder.toString(),AccountingRuleType.ACCRUAL_PERIODIC.getValue(),currentdate);

        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder.append("UPDATE m_loan as ml,");
        updateSqlBuilder.append(" (select loan.id ");
        updateSqlBuilder.append("from m_loan_arrears_aging laa");
        updateSqlBuilder
                        .append(" INNER JOIN  m_loan loan on laa.loan_id = loan.id ");
        updateSqlBuilder
                        .append(" INNER JOIN m_product_loan mpl on mpl.id = loan.product_id AND mpl.overdue_days_for_npa is not null  and mpl.accounting_type != ? ");
        updateSqlBuilder.append("WHERE loan.loan_status_id = 300  and loan.is_npa = 0 and ");
        updateSqlBuilder
                        .append("laa.overdue_since_date_derived < SUBDATE(?,INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day) ");
        updateSqlBuilder.append("group by loan.id) as sl ");
        updateSqlBuilder.append("SET ml.is_npa=1 where ml.id=sl.id ");

        result += jdbcTemplate.update(updateSqlBuilder.toString(),AccountingRuleType.ACCRUAL_PERIODIC.getValue(), currentdate);
        
        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by NPA update: " + result);
        } catch (Exception exception) {
            String rootCause = ExceptionHelper.fetchExceptionMessage(exception);
            logger.error("Failed to update NPA status for non periodic accrual loans with message " + rootCause);
            sb.append("Failed to update NPA status for non periodic accrual loans with message with message ").append(rootCause);
        }

    }
    
    
    @Override
    public void updateNPAForAccrualBasedProducts(StringBuilder sb) {
        String errorMessage = "Marking loan As Non NPA failed for account:";
        Collection<Long> LoanIdsForMovingOutOfNPA = this.loanReadPlatformService.retriveLoansForMarkingAsNonNPAWithPeriodicAccounding();
        for (Long loanId : LoanIdsForMovingOutOfNPA) {
            try {
                this.loanWritePlatformService.updateLoanAsNonNPA(loanId);
            } catch (Exception exception) {
                ExceptionHelper.handleExceptions(exception, sb, errorMessage, loanId, logger);
            }
        }
        errorMessage = "Marking loan As NPA failed for account:";
        Collection<Long> LoanIdsForMovingInToNPA = this.loanReadPlatformService.retriveLoansForMarkingAsNPAWithPeriodicAccounding();
        for (Long loanId : LoanIdsForMovingInToNPA) {
            try {
                this.loanWritePlatformService.updateLoanAsNPA(loanId);
            } catch (Exception exception) {
                ExceptionHelper.handleExceptions(exception, sb, errorMessage, loanId, logger);
            }
        }

    }
    
    
   

}
