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
package org.apache.fineract.accounting.accrual.service;

import static org.apache.fineract.accounting.accrual.api.AccrualAccountingConstants.PERIODIC_ACCRUAL_ACCOUNTING_EXECUTION_ERROR_CODE;
import static org.apache.fineract.accounting.accrual.api.AccrualAccountingConstants.PERIODIC_ACCRUAL_ACCOUNTING_RESOURCE_NAME;
import static org.apache.fineract.accounting.accrual.api.AccrualAccountingConstants.accrueTillParamName;
import static org.apache.fineract.accounting.accrual.api.AccrualAccountingConstants.loanListParamName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.accrual.serialization.AccrualAccountingDataValidator;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.SchedularWritePlatformService;
import org.apache.fineract.infrastructure.jobs.service.SchedulerJobRunnerReadService;
import org.apache.fineract.portfolio.loanaccount.service.LoanAccrualPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccrualAccountingWritePlatformServiceImpl implements AccrualAccountingWritePlatformService {

    private final LoanAccrualPlatformService loanAccrualPlatformService;
    private final AccrualAccountingDataValidator accountingDataValidator;
    private final SchedularWritePlatformService schedularWritePlatformService;
    private final SchedulerJobRunnerReadService schedulerJobRunnerReadService;

    @Autowired
    public AccrualAccountingWritePlatformServiceImpl(final LoanAccrualPlatformService loanAccrualPlatformService,
            final AccrualAccountingDataValidator accountingDataValidator, final SchedularWritePlatformService schedularWritePlatformService,
            final SchedulerJobRunnerReadService schedulerJobRunnerReadService) {
        this.loanAccrualPlatformService = loanAccrualPlatformService;
        this.accountingDataValidator = accountingDataValidator;
        this.schedularWritePlatformService = schedularWritePlatformService;
        this.schedulerJobRunnerReadService = schedulerJobRunnerReadService;
    }

    @Override
    public CommandProcessingResult executeLoansPeriodicAccrual(JsonCommand command) {
        this.accountingDataValidator.validateLoanPeriodicAccrualData(command.json());
        LocalDate tilldate = command.localDateValueOfParameterNamed(accrueTillParamName);
        String[] loanList = command.arrayValueOfParameterNamed(loanListParamName);   
        List<Long> list = new ArrayList<Long>();
        if(loanList != null){
	        for (int i=0; i< loanList.length; i++) {
	            list.add( new Long(loanList[i]));
	        }
        }
        boolean updated = this.schedularWritePlatformService.updateCurrentlyRunningStatus(JobName.ADD_PERIODIC_ACCRUAL_ENTRIES.toString(),
                true);
        // if the current running status updated which means system is not
        // running this job so we will start the job
        if (updated) {
            Map<String,String> jobParams = this.schedulerJobRunnerReadService.getJobParams(JobName.ADD_PERIODIC_ACCRUAL_ENTRIES.toString());
            ThreadLocalContextUtil.setJobParams(jobParams);
            String errorlog = this.loanAccrualPlatformService.addPeriodicAccruals(tilldate, list);
            this.schedularWritePlatformService.updateCurrentlyRunningStatus(JobName.ADD_PERIODIC_ACCRUAL_ENTRIES.toString(), false);

            if (errorlog.length() > 0) {
                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(PERIODIC_ACCRUAL_ACCOUNTING_RESOURCE_NAME);
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(PERIODIC_ACCRUAL_ACCOUNTING_EXECUTION_ERROR_CODE,
                        errorlog);
                throw new PlatformApiDataValidationException(dataValidationErrors);
            }
        }
        return CommandProcessingResult.empty();
    }

}
