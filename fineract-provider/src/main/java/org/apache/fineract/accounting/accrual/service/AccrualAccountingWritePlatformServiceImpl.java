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

import static org.apache.fineract.accounting.accrual.api.AccrualAccountingConstants.accrueTillParamName;
import static org.apache.fineract.accounting.accrual.api.AccrualAccountingConstants.loanListParamName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.accrual.serialization.AccrualAccountingDataValidator;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.JobRegisterService;
import org.apache.fineract.infrastructure.jobs.service.SchedulerServiceConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccrualAccountingWritePlatformServiceImpl implements AccrualAccountingWritePlatformService {

    private final AccrualAccountingDataValidator accountingDataValidator;
    private final JobRegisterService jobRegisterService;

    @Autowired
    public AccrualAccountingWritePlatformServiceImpl(final AccrualAccountingDataValidator accountingDataValidator,
            final JobRegisterService jobRegisterService) {
        this.accountingDataValidator = accountingDataValidator;
        this.jobRegisterService = jobRegisterService;
    }

    @Override
    public CommandProcessingResult executeLoansPeriodicAccrual(JsonCommand command) {
        this.accountingDataValidator.validateLoanPeriodicAccrualData(command.json());
        LocalDate tilldate = command.localDateValueOfParameterNamed(accrueTillParamName);
        String[] loanList = command.arrayValueOfParameterNamed(loanListParamName);   
        List<Long> list = new ArrayList<>();
        if (loanList != null) {
            for (int i = 0; i < loanList.length; i++) {
                list.add(new Long(loanList[i]));
            }
        }
        Map<String,Object> jobParams = new HashMap<>(2);
        jobParams.put("loanList",list);
        jobParams.put(SchedulerServiceConstants.EXECUTE_AS_ON_DATE,tilldate);
        this.jobRegisterService.executeJob(JobName.ADD_PERIODIC_ACCRUAL_ENTRIES.toString(),
                jobParams);
        return CommandProcessingResult.empty();
    }

}
