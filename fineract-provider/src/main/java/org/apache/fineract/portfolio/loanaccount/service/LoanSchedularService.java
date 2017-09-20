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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;

public interface LoanSchedularService {

    void applyChargeForOverdueLoans() throws JobExecutionException;

    void recalculateInterest() throws JobExecutionException;

    void applyHolidaysToLoans(final HolidayDetailDTO holidayDetailDTO, final Map<Long, List<Holiday>> officeIds,
            final Set<Long> failedForOffices, final StringBuilder sb) throws JobExecutionException;

    void updateNPAForNonAccrualBasedProducts(StringBuilder sb);

    void updateNPAForAccrualBasedProducts(StringBuilder sb);

    void applyChargeForOverdueLoansWithBrokenPeriodDate() throws JobExecutionException;

    CommandProcessingResult executeJobForLoans(JsonCommand command, JobName jobName);
    
}