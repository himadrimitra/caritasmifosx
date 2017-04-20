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

import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffAccountSummaryCollectionData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.domain.GroupingTypeStatus;
import org.apache.fineract.portfolio.loanaccount.data.AccountSummaryDataMapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BulkLoansReadPlatformServiceImpl implements BulkLoansReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public BulkLoansReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public StaffAccountSummaryCollectionData retrieveLoanOfficerAccountSummary(final Long loanOfficerId) {

        this.context.authenticatedUser();

        final AccountSummaryDataMapper.StaffClientMapper staffClientMapper = new AccountSummaryDataMapper.StaffClientMapper();

        final String clientSql = staffClientMapper.schemaForReassign();
        final Collection<ClientData> clientSummaryList = this.jdbcTemplate.query(clientSql, staffClientMapper,
                new Object[] { loanOfficerId, ClientStatus.ACTIVE.getValue() });

        final AccountSummaryDataMapper.StaffGroupMapper staffGroupMapper = new AccountSummaryDataMapper.StaffGroupMapper();
        final String groupSql = staffGroupMapper.schemaForReassign();

        final Collection<GroupGeneralData> groupSummaryList = this.jdbcTemplate.query(groupSql, staffGroupMapper,
                new Object[] { GroupingTypeStatus.ACTIVE.getValue(), loanOfficerId });

        final AccountSummaryDataMapper.StaffAccountSummaryCollectionDataMapper staffAccountSummaryCollectionDataMapper = new AccountSummaryDataMapper.StaffAccountSummaryCollectionDataMapper();
        final String dataSql = staffAccountSummaryCollectionDataMapper.schemaForReassign();

        Collection<CenterData> staffAccountSummaryCollectionData = this.jdbcTemplate.query(dataSql, staffAccountSummaryCollectionDataMapper,
                new Object[] { ClientStatus.ACTIVE.getValue(), GroupingTypeStatus.ACTIVE.getValue(), loanOfficerId,
                        LoanStatus.ACTIVE.getValue(), });

        return new StaffAccountSummaryCollectionData(clientSummaryList, groupSummaryList, staffAccountSummaryCollectionData);
    }

}