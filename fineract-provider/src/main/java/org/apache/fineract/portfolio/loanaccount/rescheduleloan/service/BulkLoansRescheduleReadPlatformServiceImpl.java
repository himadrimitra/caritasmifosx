/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.loanaccount.rescheduleloan.service;

import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
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
public class BulkLoansRescheduleReadPlatformServiceImpl implements BulkLoanRescheduleService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BulkLoansRescheduleReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.fineract.portfolio.loanaccount.service.
     * BulkLoanRescheduleService#retrieveLoanOfficerAccountSummary(java
     * .lang.Long, java.util.Date) retrieveLoanOfficerAccountSummary() returns
     * all loans mapped under particular loan officer identified by
     * 
     * @Param- loanOfficerId and their installment date
     * 
     * @Param - mapping to one of the installment re-payment schedule date
     * 
     * @return - it returns StaffStaffAccountSummaryCollectionData object
     * consisting of loan details of Centers (Center,Groups,Clients, Group
     * loan), Groups (that are not part of any centers), Clients (which are
     * mapped directly to office)
     */
    @Override
    public StaffAccountSummaryCollectionData retrieveLoanOfficerAccountSummary(final Long loanOfficerId, final Date dueDate) {

        /**
         * First fetch details of all Client who are not linked to Groups and
         * Centers and have a loan which has a re-payment on the given date
         */

        final AccountSummaryDataMapper.StaffClientMapper staffClientMapper = new AccountSummaryDataMapper.StaffClientMapper();
        final String clientSql = staffClientMapper.schemaForreschedule();

        final Collection<ClientData> clientSummaryList = this.jdbcTemplate.query(clientSql, staffClientMapper,
                new Object[] { loanOfficerId, dueDate, ClientStatus.ACTIVE.getValue() });

        final AccountSummaryDataMapper.StaffGroupMapper staffGroupMapper = new AccountSummaryDataMapper.StaffGroupMapper();
        final String groupSql = staffGroupMapper.schemaForreschedule();

        final Collection<GroupGeneralData> groupSummaryList = this.jdbcTemplate.query(groupSql, staffGroupMapper,
                new Object[] { GroupingTypeStatus.ACTIVE.getValue(), loanOfficerId, dueDate });

        final AccountSummaryDataMapper.StaffAccountSummaryCollectionDataMapper staffAccountSummaryCollectionDataMapper = new AccountSummaryDataMapper.StaffAccountSummaryCollectionDataMapper();
        final String dataSql = staffAccountSummaryCollectionDataMapper.schemaForReschedule();

        Collection<CenterData> staffStaffStaffAccountSummaryCollectionData = this.jdbcTemplate.query(dataSql,
                staffAccountSummaryCollectionDataMapper, new Object[] { ClientStatus.ACTIVE.getValue(),
                        GroupingTypeStatus.ACTIVE.getValue(), loanOfficerId, LoanStatus.ACTIVE.getValue(), dueDate, });

        return new StaffAccountSummaryCollectionData(clientSummaryList, groupSummaryList, staffStaffStaffAccountSummaryCollectionData);

    }
}