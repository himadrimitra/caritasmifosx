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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepositoryWrapper;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.OverdueLoanScheduleData;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class LoanSchedularServiceImpl implements LoanSchedularService {

    private final static Logger logger = LoggerFactory.getLogger(LoanSchedularServiceImpl.class);
    private final ConfigurationDomainService configurationDomainService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanWritePlatformService loanWritePlatformService;
    private final LoanUtilService loanUtilService;
    private final HolidayRepositoryWrapper holidayRepository;

    @Autowired
    public LoanSchedularServiceImpl(final ConfigurationDomainService configurationDomainService,
            final LoanReadPlatformService loanReadPlatformService, final LoanWritePlatformService loanWritePlatformService,
            final LoanUtilService loanUtilService, final HolidayRepositoryWrapper holidayRepository) {
        this.configurationDomainService = configurationDomainService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanWritePlatformService = loanWritePlatformService;
        this.loanUtilService = loanUtilService;
        this.holidayRepository = holidayRepository;
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

            for (final Long loanId : overdueScheduleData.keySet()) {
                try {
                    this.loanWritePlatformService.applyOverdueChargesForLoan(loanId, overdueScheduleData.get(loanId));

                } catch (final PlatformApiDataValidationException e) {
                    final List<ApiParameterError> errors = e.getErrors();
                    for (final ApiParameterError error : errors) {
                        logger.error("Apply Charges due for overdue loans failed for account:" + loanId + " with message "
                                + error.getDeveloperMessage());
                        sb.append("Apply Charges due for overdue loans failed for account:").append(loanId).append(" with message ")
                                .append(error.getDeveloperMessage());
                    }
                } catch (final AbstractPlatformDomainRuleException ex) {
                    logger.error("Apply Charges due for overdue loans failed for account:" + loanId + " with message "
                            + ex.getDefaultUserMessage());
                    sb.append("Apply Charges due for overdue loans failed for account:").append(loanId).append(" with message ")
                            .append(ex.getDefaultUserMessage());
                } catch (Exception e) {
                    String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                    logger.error("Apply Charges due for overdue loans failed for account:" + loanId + " with message "
                            + rootCause);
                    sb.append("Apply Charges due for overdue loans failed for account:").append(loanId).append(" with message ")
                            .append(rootCause);
                }
            }
            if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
        }
    }

    @Override
    @CronTarget(jobName = JobName.RECALCULATE_INTEREST_FOR_LOAN)
    public void recalculateInterest() throws JobExecutionException {
        Map<String, String> jobParams = ThreadLocalContextUtil.getJobParams();
        int numberOfThreads = 1;
        if (jobParams.containsKey("number-of-threads")) {
            numberOfThreads = Integer.parseInt(jobParams.get("number-of-threads"));
            if (numberOfThreads == 0) {
                numberOfThreads = 1;
            }
        }
        List<Long> loanIds = this.loanReadPlatformService.fetchLoansForInterestRecalculation();

        if (!loanIds.isEmpty()) {
            int loanSize = loanIds.size();
            final StringBuilder sb = new StringBuilder();
            if (numberOfThreads <= 1 || numberOfThreads > loanSize) {
                Integer maxNumberOfRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxRetriesOnDeadlock();
                Integer maxIntervalBetweenRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxIntervalBetweenRetries();
                recalculateInterest(sb, maxNumberOfRetries, maxIntervalBetweenRetries, loanIds);
            } else {
                int subListSize = loanSize / numberOfThreads;
                List<StringBuilder> bufferes = new ArrayList<>(numberOfThreads);
                try {
                    List<Thread> threads = new ArrayList<>();
                    for (int threadNumber = 0; threadNumber < numberOfThreads; threadNumber++) {
                        final StringBuilder threadsb = new StringBuilder();
                        bufferes.add(threadsb);
                        boolean completeList = false;
                        if (threadNumber == numberOfThreads - 1) {
                            completeList = true;
                        }
                        List<Long> subList = subList(loanIds, threadNumber, subListSize, completeList);
                        if (subList.size() > 0) {
                            Thread thread = new Thread(new RecalculateInterestThread(subList, threadsb));
                            thread.start();
                            threads.add(thread);
                        }
                        if (loanSize <= (threadNumber + 1) * subListSize) {
                            break;
                        }
                    }
                    for (Thread thread : threads) {
                        thread.join();
                    }
                    for (StringBuilder threadsb : bufferes) {
                        sb.append(threadsb.toString());
                    }
                } catch (InterruptedException e) {
                    sb.append("Interest recalculation for loans failed " + e.getMessage());
                }
            }

            if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
        }

    }
    
    private class RecalculateInterestThread implements Runnable {

        Integer maxNumberOfRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxRetriesOnDeadlock();
        Integer maxIntervalBetweenRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxIntervalBetweenRetries();
        final List<Long> loanIds;
        final FineractPlatformTenant tenant;
        final StringBuilder sb;

        public RecalculateInterestThread(final List<Long> loanIds, final StringBuilder sb) {
            this.loanIds = loanIds;
            this.tenant = ThreadLocalContextUtil.getTenant();
            this.sb = sb;
        }

        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(tenant);
            recalculateInterest(sb, maxNumberOfRetries, maxIntervalBetweenRetries, loanIds);

        }

    }

    private StringBuilder recalculateInterest(final StringBuilder sb, Integer maxNumberOfRetries, Integer maxIntervalBetweenRetries,
            List<Long> loanIds) {
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
                    String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                    logger.error("Interest recalculation for loans failed for account:" + loanId + " with message " + rootCause);
                    sb.append(" Interest recalculation for loans failed for account:").append(loanId).append(" with message ")
                            .append(rootCause);
                    numberOfRetries = maxNumberOfRetries + 1;
                }
            }
        }
        return sb;
    }

    private List<Long> subList(List<Long> list, int counter, int sublistSize, boolean completeList) {
        int fromIndex = counter * sublistSize;
        int toIndex = fromIndex + sublistSize;
        if (completeList || toIndex > list.size()) {
            toIndex = list.size();
        }
        return list.subList(fromIndex, toIndex);
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_HOLIDAYS_TO_LOANS)
    public void applyHolidaysToLoans() throws JobExecutionException {

        final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();

        if (!isHolidayEnabled) { return; }

        final Collection<Integer> loanStatuses = new ArrayList<>(Arrays.asList(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(),
                LoanStatus.APPROVED.getValue(), LoanStatus.ACTIVE.getValue()));
        // Get all Holidays which are active/deleted and not processed
        final List<Holiday> holidays = this.holidayRepository.findUnprocessed();

        // Loop through all holidays
        final Map<Long, List<Holiday>> officeIds = new HashMap<>();
        for (final Holiday holiday : holidays) {
            // All offices to which holiday is applied
            final Set<Office> offices = holiday.getOffices();
            for (final Office office : offices) {
                if (officeIds.containsKey(office.getId())) {
                    officeIds.get(office.getId()).add(holiday);
                } else {
                    List<Holiday> holidaylist = new ArrayList<>();
                    holidaylist.add(holiday);
                    officeIds.put(office.getId(), holidaylist);
                }
            }

        }
        StringBuilder sb = new StringBuilder();
        Set<Long> failedForOffices = new HashSet<>();
        for (Map.Entry<Long, List<Holiday>> entry : officeIds.entrySet()) {
            try {
                LocalDate recalculateFrom = null;
                for(final Holiday holiday : entry.getValue()){
                    if(recalculateFrom == null || recalculateFrom.isAfter(holiday.getFromDateLocalDate())){
                        recalculateFrom = holiday.getFromDateLocalDate();
                    }
                }
                Collection<Long> loansForProcess = this.loanReadPlatformService.retrieveLoansByOfficesAndHoliday(entry.getKey(),
                        entry.getValue(), loanStatuses, recalculateFrom);
                final List<Holiday> holidaysList = null;
                HolidayDetailDTO holidayDetailDTO = this.loanUtilService.constructHolidayDTO(holidaysList);
                for (Long loanId : loansForProcess) {
                    try {
                        this.loanWritePlatformService.updateScheduleDates(loanId, holidayDetailDTO, recalculateFrom);
                    } catch (final PlatformApiDataValidationException e) {
                        final List<ApiParameterError> errors = e.getErrors();
                        for (final ApiParameterError error : errors) {
                            logger.error("Apply Holidays for loans failed for account:" + loanId + " with message "
                                    + error.getDeveloperMessage());
                            sb.append("Apply Holidays for loans failed for account:").append(loanId).append(" with message ")
                                    .append(error.getDeveloperMessage());
                        }
                        failedForOffices.add(entry.getKey());
                    } catch (final AbstractPlatformDomainRuleException ex) {
                        logger.error("Apply Holidays for loans failed for account:" + loanId + " with message "
                                + ex.getDefaultUserMessage());
                        sb.append("Apply Holidays for loans failed for account:").append(loanId).append(" with message ")
                                .append(ex.getDefaultUserMessage());
                        failedForOffices.add(entry.getKey());
                    } catch (Exception e) {
                        String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                        logger.error("Apply Holidays for loans failed for account:" + loanId + " with message " + rootCause);
                        sb.append("Apply Holidays for loans failed for account:").append(loanId).append(" with message ")
                                .append(rootCause);
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
        boolean holidayStatusChanged = false;
        for (final Holiday holiday : holidays) {
            if (failedForOffices.isEmpty()) {
                holiday.processed();
                holidayStatusChanged = true;
            } else {
                final Set<Office> offices = holiday.getOffices();
                boolean updateProcessedStatus = true;
                for (final Office office : offices) {
                    if (failedForOffices.contains(office.getId())) {
                        updateProcessedStatus = false;
                    }
                }
                if (updateProcessedStatus) {
                    holiday.processed();
                    holidayStatusChanged = true;
                }
            }
        }
        if (holidayStatusChanged) {
            this.holidayRepository.save(holidays);
        }
        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
    }

}
