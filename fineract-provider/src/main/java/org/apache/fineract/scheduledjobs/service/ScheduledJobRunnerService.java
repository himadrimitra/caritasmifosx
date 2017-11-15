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
package org.apache.fineract.scheduledjobs.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;

public interface ScheduledJobRunnerService {

    void updateLoanSummaryDetails();

    void updateLoanPaidInAdvance();

    void applyAnnualFeeForSavings();

    void applyDueChargesForSavings() throws JobExecutionException;

    void updateNPA() throws JobExecutionException;

    void updateMaturityDetailsOfDepositAccounts();

    void generateRDSchedule();

    void updateClientSubStatus();

    void doAppySavingLateFeeCharge() throws JobExecutionException;

    // the following method will call if any user manually wanted to run the job
    // by passing some parameter from front end
    CommandProcessingResult doInvestmentTracker(JsonCommand json);

    void distributeInvestmentEarning();

    void postDividends() throws JobExecutionException;

    void applyClientRecurringCharge() throws JobExecutionException;

    void highmarkEnquiry();

    void generateNextRecurringDate();

    @CronTarget(jobName = JobName.INITIATE_BANK_TRANSACTION)
    void initiateBankTransactions() throws JobExecutionException;

    @CronTarget(jobName = JobName.UPDATE_BANK_TRANSACTION_STATUS)
    void updateBankTransactionsStatus() throws JobExecutionException;

    void applyHolidays() throws JobExecutionException;
}