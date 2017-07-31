/** 
 * Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.group.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.data.SavingsAccountSummaryData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.savings.data.SavingsAccountStatusEnumData;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CenterGroupMemberAccountReadPlatformServiceImpl implements CenterGroupMemberAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public CenterGroupMemberAccountReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final ConfigurationDomainService configurationDomainService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public Collection<GroupGeneralData> retrieveAssociatedMembersByCenterId(Long centerId) {
        final GroupGeneralDataExtractor groupGeneralDataExtractor = new GroupGeneralDataExtractor(this.configurationDomainService);
        final String sql = "select " + groupGeneralDataExtractor.schema() + " WHERE g.parent_id = ? ";
        Collection<GroupGeneralData> groupGeneralDatas = this.jdbcTemplate.query(sql, groupGeneralDataExtractor,
                new Object[] { centerId });
        return groupGeneralDatas;
    }

    private static final class GroupGeneralDataExtractor implements ResultSetExtractor<Collection<GroupGeneralData>> {

        GroupGeneralDataMapper groupGeneralDataMapper = new GroupGeneralDataMapper();
        ClientDataMapper clientDataExtractor = new ClientDataMapper();
        LoanAccountSummaryDataMapper loanAccountSummaryDataMapper = new LoanAccountSummaryDataMapper();
        private final boolean isShowLoanDetailsInCenterPageEnabled;

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public GroupGeneralDataExtractor(ConfigurationDomainService configurationDomainService) {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            this.isShowLoanDetailsInCenterPageEnabled = configurationDomainService.isShowLoanDetailsInCenterPageEnabled();

            sqlBuilder.append("g.id AS groupId, g.account_no AS groupAccountNo, g.display_name AS groupName, g.status_enum AS groupStatusEnum, g.external_id AS groupExternalId ");
            sqlBuilder.append(", c.id AS clientId, c.account_no AS clientAccountNo, c.display_name AS clientFullName, c.status_enum AS clientStatusEnum ");
            if (this.isShowLoanDetailsInCenterPageEnabled) {
                sqlBuilder.append(", l.id AS loanId, l.account_no AS loanAccountNo, l.loan_status_id AS loanStatusEnum, lp.name AS loanProductName ");
                sqlBuilder.append(", lp.short_name as loanShortName, IF(l.principal_disbursed_derived IS NULL, 0, l.principal_disbursed_derived) AS originalLoanAmount ");
                sqlBuilder.append(", IF(l.total_outstanding_derived IS NULL, 0, l.total_outstanding_derived) AS loanBalance ");
                sqlBuilder.append(", IF(l.total_repayment_derived IS NULL, 0, l.total_repayment_derived) AS loanAmountPaid ");
                sqlBuilder.append(", s.id AS savingsId, s.account_no AS savingsAccountNo, sp.name AS savingsProductName, s.status_enum AS savingsStatusEnum ");
                sqlBuilder.append(", IF(s.account_balance_derived IS NULL, 0, s.account_balance_derived) AS savingsBalance, ");
                sqlBuilder.append(" IF(la.principal_overdue_derived IS NULL,false,true) AS inArrears ");
            }
            sqlBuilder.append("FROM m_group g ");
            sqlBuilder.append("JOIN m_office go ON go.id = g.office_id ");
            sqlBuilder.append("JOIN m_group pg ON pg.id = g.parent_id ");
            sqlBuilder.append("LEFT JOIN m_group_client gc ON gc.group_id = g.id ");
            sqlBuilder.append("LEFT JOIN m_client c ON c.id = gc.client_id AND c.status_enum = 300 ");
            if (this.isShowLoanDetailsInCenterPageEnabled) {
                sqlBuilder.append("LEFT JOIN m_loan l ON l.client_id = c.id AND l.loan_status_id = 300 ");
                sqlBuilder.append(" LEFT JOIN m_loan_arrears_aging la ON la.loan_id = l.id ");
                sqlBuilder.append("LEFT JOIN m_product_loan AS lp ON lp.id = l.product_id ");
                sqlBuilder.append("LEFT JOIN m_savings_account s ON s.client_id = c.id ");
                sqlBuilder.append("LEFT JOIN m_savings_product AS sp ON sp.id = s.product_id ");
            }

            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public Collection<GroupGeneralData> extractData(ResultSet rs) throws SQLException, DataAccessException {

            Collection<GroupGeneralData> groupGeneralDataList = new ArrayList<>();

            GroupGeneralData groupGeneralData = null;
            ClientData clientData = null;
            LoanAccountSummaryData loanAccountSummaryData = null;
            Long groupId = null;
            Long clientId = null;
            int rowIndex = 0;

            while (rs.next()) {
                Long tempGroupId = rs.getLong("groupId");
                Long tempClientId = rs.getLong("clientId");
                rowIndex = rowIndex++;
                if (groupGeneralData == null || (groupId != null && !groupId.equals(tempGroupId))) {
                    groupId = tempGroupId;
                    groupGeneralData = groupGeneralDataMapper.mapRow(rs, rowIndex);
                    groupGeneralData = GroupGeneralData.withConfig(groupGeneralData, this.isShowLoanDetailsInCenterPageEnabled);
                    if (clientData == null || (clientId != null && !clientId.equals(tempClientId))) {
                        clientId = tempClientId;
                        clientData = clientDataExtractor.mapRow(rs, rowIndex);
                        if (clientData != null) {
                            if (this.isShowLoanDetailsInCenterPageEnabled) {
                                loanAccountSummaryData = loanAccountSummaryDataMapper.mapRow(rs, rowIndex);
                                if (loanAccountSummaryData != null) {
                                    clientData.addLoanAccountSummaryData(loanAccountSummaryData);
                                }
                            }
                            groupGeneralData.addActiveClientMember(clientData);
                        }
                    } else if (clientId != null && clientId.equals(tempClientId)) {
                        if (this.isShowLoanDetailsInCenterPageEnabled) {
                            loanAccountSummaryData = loanAccountSummaryDataMapper.mapRow(rs, rowIndex);
                            if (loanAccountSummaryData != null) {
                                clientData.addLoanAccountSummaryData(loanAccountSummaryData);
                            }
                        }
                    }
                    groupGeneralDataList.add(groupGeneralData);
                } else if (groupId != null && groupId.equals(tempGroupId)) {
                    if (clientData == null || (clientId != null && !clientId.equals(tempClientId))) {
                        clientId = tempClientId;
                        clientData = clientDataExtractor.mapRow(rs, rowIndex);
                        if (clientData != null) {
                            if (this.isShowLoanDetailsInCenterPageEnabled) {
                                loanAccountSummaryData = loanAccountSummaryDataMapper.mapRow(rs, rowIndex);
                                if (loanAccountSummaryData != null) {
                                    clientData.addLoanAccountSummaryData(loanAccountSummaryData);
                                }
                            }
                            groupGeneralData.addActiveClientMember(clientData);
                        }
                    } else if (clientId != null && clientId.equals(tempClientId) && this.isShowLoanDetailsInCenterPageEnabled) {
                        loanAccountSummaryData = loanAccountSummaryDataMapper.mapRow(rs, rowIndex);
                        if (loanAccountSummaryData != null) {
                            clientData.addLoanAccountSummaryData(loanAccountSummaryData);
                        }
                    }
                }
            }
            return groupGeneralDataList;
        }

    }

    public static final class GroupGeneralDataMapper implements RowMapper<GroupGeneralData> {

        @Override
        public GroupGeneralData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long groupId = rs.getLong("groupId");
            final String groupAccountNo = rs.getString("groupAccountNo");
            final String groupName = rs.getString("groupName");
            final Integer groupStatus = JdbcSupport.getInteger(rs, "groupStatusEnum");
            final EnumOptionData status = ClientEnumerations.status(groupStatus);
            final String groupExternalId = rs.getString("groupExternalId");
            return GroupGeneralData.lookup(groupId, groupAccountNo, groupName, status, groupExternalId);
        }
    }

    private static final class ClientDataMapper implements RowMapper<ClientData> {

        @Override
        public ClientData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            if (clientId == null) { return null; }
            final String clientAccountNumber = rs.getString("clientAccountNo");
            final String clientFullName = rs.getString("clientFullName");
            final Integer clientStatus = JdbcSupport.getInteger(rs, "clientStatusEnum");
            EnumOptionData status = null;
            if (clientStatus != null) {
                status = ClientEnumerations.status(clientStatus);
            }
 
            return ClientData.instance(clientId, clientAccountNumber, clientFullName, status);
        }
    }

    private static final class LoanAccountSummaryDataMapper implements RowMapper<LoanAccountSummaryData> {

        @Override
        public LoanAccountSummaryData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long loanId = JdbcSupport.getLong(rs, "loanId");
            if (loanId == null) { return null; }
            final String loanAccountNo = rs.getString("loanAccountNo");
            final String loanProductName = rs.getString("loanProductName");
            final String loanShortName = rs.getString("loanShortName");
            final Integer loanStatusId = JdbcSupport.getInteger(rs, "loanStatusEnum");

            LoanStatusEnumData loanStatus = null;
            if (loanStatusId != null) {
                loanStatus = LoanEnumerations.status(loanStatusId);
            }

            BigDecimal originalLoanAmount = rs.getBigDecimal("originalLoanAmount");
            BigDecimal loanBalance = rs.getBigDecimal("loanBalance");
            BigDecimal amountPaid = rs.getBigDecimal("loanAmountPaid");
            final Boolean inArrears = rs.getBoolean("inArrears");
            

            return LoanAccountSummaryData.instance(loanId, loanAccountNo, loanProductName, loanShortName, loanStatus, originalLoanAmount,
                    loanBalance, amountPaid,inArrears);
        }
    }

    @SuppressWarnings("unused")
    private static final class SavingsAccountSummaryDataMapper implements RowMapper<SavingsAccountSummaryData> {

        @Override
        public SavingsAccountSummaryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long savingsId = rs.getLong("savingsId");

            final String savingsAccountNo = rs.getString("savingsAccountNo");
            final String savingsProductName = rs.getString("savingsProductName");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "savingsStatusEnum");
            final SavingsAccountStatusEnumData status = SavingsEnumerations.status(statusEnum);

            BigDecimal savingsBalance = rs.getBigDecimal("savingsBalance");

            return SavingsAccountSummaryData.instance(savingsId, savingsAccountNo, savingsProductName, status, savingsBalance);
        }
    }

}
