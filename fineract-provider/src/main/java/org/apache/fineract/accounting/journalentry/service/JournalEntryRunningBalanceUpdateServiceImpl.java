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
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.api.JournalEntryJsonInputParams;
import org.apache.fineract.accounting.journalentry.data.JournalEntryDataValidator;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.exception.OfficeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JournalEntryRunningBalanceUpdateServiceImpl implements JournalEntryRunningBalanceUpdateService {

    private final static Logger logger = LoggerFactory.getLogger(JournalEntryRunningBalanceUpdateServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final OfficeRepository officeRepository;

    private final JournalEntryDataValidator dataValidator;

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public JournalEntryRunningBalanceUpdateServiceImpl(final RoutingDataSource dataSource, final OfficeRepository officeRepository,
            final JournalEntryDataValidator dataValidator, final FromJsonHelper fromApiJsonHelper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeRepository = officeRepository;
        this.dataValidator = dataValidator;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    @CronTarget(jobName = JobName.ACCOUNTING_RUNNING_BALANCE_UPDATE)
    public void updateRunningBalance() {
        logger.info(ThreadLocalContextUtil.getTenant().getName() + ":Calling UpdateAccountingRunningBalances : ");
        this.jdbcTemplate.execute("CALL UpdateAccountingRunningBalances");
        logger.info(ThreadLocalContextUtil.getTenant().getName() + ":executed UpdateAccountingRunningBalances : ");
    }

    @Override
    public CommandProcessingResult updateOfficeRunningBalance(JsonCommand command) {
        this.dataValidator.validateForUpdateRunningbalance(command);
        final Long officeId = this.fromApiJsonHelper.extractLongNamed(JournalEntryJsonInputParams.OFFICE_ID.getValue(),
                command.parsedJson());
        final Office office = this.officeRepository.findOne(officeId);
        if (office == null) { throw new OfficeNotFoundException(officeId); }
        CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder().withCommandId(command
                .commandId());
        if (officeId == null) {
            updateRunningBalance();
        } else {
            logger.info(ThreadLocalContextUtil.getTenant().getName() + ":Calling UpdateAccountingRunningBalancesByOffice with office id "
                    + officeId);
            this.jdbcTemplate.execute("CALL UpdateAccountingRunningBalancesByOffice(" + officeId + ")");
            logger.info(ThreadLocalContextUtil.getTenant().getName() + ":executed UpdateAccountingRunningBalancesByOffice with office id "
                    + officeId);
        }
        return commandProcessingResultBuilder.build();
    }

}
