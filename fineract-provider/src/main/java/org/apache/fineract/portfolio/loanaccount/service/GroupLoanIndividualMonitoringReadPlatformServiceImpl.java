/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringRepaymentScheduleData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringReadPlatformServiceImpl implements GroupLoanIndividualMonitoringReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public GroupLoanIndividualMonitoringReadPlatformServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GroupLoanIndividualMonitoringMapper implements RowMapper<GroupLoanIndividualMonitoringData> {

        final String schema;

        private GroupLoanIndividualMonitoringMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("glim.id as id, ");
            sql.append("glim.loan_id as loanId, l.principal_amount as totalLoanAmount, ");
            sql.append("glim.client_id as clientId, c.display_name as clientName, c.external_id as clientExternalID, ");
            sql.append("glim.proposed_amount as proposedAmount, ");
            sql.append("glim.approved_amount as approvedAmount, ");
            sql.append("glim.disbursed_amount as disbursedAmount, ");
            sql.append("glim.is_client_selected as isClientSelected, ");
            sql.append("glim.installment_amount as installmentAmount, ");
            sql.append("glim.charge_amount as chargeAmount, ");
            sql.append("glim.interest_amount as interestAmount, ");
            sql.append("glim.adjusted_amount as adjustedAmount, ");
            sql.append("glim.total_payble_amount as totalPaybleAmount, ");
            sql.append("glim.paid_amount as paidAmount, ");
            sql.append("glim.interest_amount as interestAmount, ");
            sql.append("glim.paid_interest_amount as paidInterestAmount, ");
            sql.append("glim.paid_principal_amount as paidPrincipalAmount, ");
            sql.append("glim.paid_charge_amount as paidChargeAmount, ");
            sql.append("glim.waived_interest_amount as waivedInterestAmount, ");
            sql.append("glim.waived_charge_amount as waivedChargeAmount, ");
            sql.append("glim.principal_writtenoff_amount as principalWrittenOffAmount, ");
            sql.append("glim.interest_writtenoff_amount as interestWrittenOffAmount, ");
            sql.append("glim.fee_charges_writtenoff_amount as chargeWrittenOffAmount, ");
            sql.append("cv.id as loanPurposeId, cv.code_value as loanPurposeName, cv.code_description description, cv.order_position position, cv.is_active as isLoanPurposeActive,  ");
            sql.append("glim.is_active as isActive ");
            sql.append(" from m_loan_glim glim ");
            sql.append(" left join m_client c on glim.client_id = c.id ");
            sql.append(" left join m_loan l on glim.loan_id = l.id ");
            sql.append(" left join m_code_value cv on glim.loanpurpose_cv_id = cv.id ");
            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public GroupLoanIndividualMonitoringData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long loanId = JdbcSupport.getLong(rs, "loanId");
            final BigDecimal totalLoanAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalLoanAmount");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString("clientName");
            final String clientExternalID = rs.getString("clientExternalID");
            final String description = rs.getString("description");
            final Long loanPuroseId = JdbcSupport.getLong(rs, "loanPurposeId");
            final BigDecimal proposedAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "proposedAmount");
            final BigDecimal approvedAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "approvedAmount");
            final BigDecimal disbursedAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "disbursedAmount");
            final String loanPurposeName = rs.getString("loanPurposeName");
            final int position = rs.getInt("position");
            final Boolean isLoanPurposeActive = rs.getBoolean("isLoanPurposeActive");
            final Boolean isClientSelected = rs.getBoolean("isClientSelected");
            final Integer codeScore = 0;
            CodeValueData loanPurpose = CodeValueData.instance(loanPuroseId, loanPurposeName, position, description, isLoanPurposeActive,
                    codeScore, false, null);

            final BigDecimal chargeAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "chargeAmount");
            final BigDecimal adjustedAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "adjustedAmount");
            final BigDecimal installmentAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "installmentAmount");

            final BigDecimal totalPaybleAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalPaybleAmount");
            final BigDecimal paidAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "paidAmount");
            final BigDecimal paidInterestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "paidInterestAmount");
            final BigDecimal interestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestAmount");

            final BigDecimal paidPrincipalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "paidPrincipalAmount");
            final BigDecimal paidChargeAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "paidChargeAmount");
            final BigDecimal waivedInterestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "waivedInterestAmount");
            final BigDecimal waivedChargeAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "waivedChargeAmount");
            final BigDecimal principalWrittenOffAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOffAmount");
            final BigDecimal interestWrittenOffAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOffAmount");
            final BigDecimal chargeWrittenOffAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "chargeWrittenOffAmount");
            final BigDecimal penaltyAmount = BigDecimal.ZERO;
            final BigDecimal paidPenaltyAmount = BigDecimal.ZERO;
            final BigDecimal waivedPenaltyAmount = BigDecimal.ZERO;
            final BigDecimal penaltywrittenOffAmount = BigDecimal.ZERO;
            BigDecimal totalPrincipalOutstanding = BigDecimal.ZERO;
            BigDecimal totalInterestOutstanding = BigDecimal.ZERO;
            BigDecimal totalFeeChargeOutstanding = BigDecimal.ZERO;
            BigDecimal totalpenaltyChargeOutstanding = BigDecimal.ZERO;
            BigDecimal totalLoanOutstandingAmount = BigDecimal.ZERO;
            if (disbursedAmount != null) {
                totalPrincipalOutstanding = MathUtility.subtract(disbursedAmount, paidPrincipalAmount, principalWrittenOffAmount);
            } else if (approvedAmount != null) {
                totalPrincipalOutstanding = MathUtility.subtract(approvedAmount, paidPrincipalAmount, principalWrittenOffAmount);
            } else {
            	totalPrincipalOutstanding = MathUtility.subtract(proposedAmount, paidPrincipalAmount, principalWrittenOffAmount);                
            }
            totalInterestOutstanding = MathUtility.subtract(interestAmount, paidInterestAmount, waivedInterestAmount,
                    interestWrittenOffAmount);
            totalFeeChargeOutstanding = MathUtility.subtract(chargeAmount, paidChargeAmount, waivedChargeAmount, interestWrittenOffAmount);
            totalLoanOutstandingAmount = MathUtility.add(totalPrincipalOutstanding, totalInterestOutstanding, totalFeeChargeOutstanding,
                    totalpenaltyChargeOutstanding);
            final BigDecimal remainingTransactionAmount = null;
            final BigDecimal transactionAmount = null;
            final Boolean isActive = rs.getBoolean("isActive");
            return GroupLoanIndividualMonitoringData.instance(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID,
                    proposedAmount, approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount,
                    installmentAmount, totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount,
                    paidChargeAmount, waivedInterestAmount, waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount,
                    chargeWrittenOffAmount, penaltyAmount, paidPenaltyAmount, waivedPenaltyAmount, penaltywrittenOffAmount,
                    remainingTransactionAmount, transactionAmount, isActive, totalPrincipalOutstanding, totalInterestOutstanding,
                    totalFeeChargeOutstanding, totalpenaltyChargeOutstanding, totalLoanOutstandingAmount);
        }
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveAllByLoanId(Long loanId) {

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    @Override
    public GroupLoanIndividualMonitoringData retrieveByLoanAndClientId(Long loanId, Long clientId) {

        this.context.authenticatedUser();

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ? and glim.client_id = ? and glim.is_client_selected = 1";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanId, clientId });
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveAll() {

        this.context.authenticatedUser();

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema();

        return this.jdbcTemplate.query(sql, rm);
    }

    @Override
    public GroupLoanIndividualMonitoringData retrieveOne(Long id) {

        this.context.authenticatedUser();

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema() + " where glim.id = ? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
    }

    private static final class GLIMWaiveChargeMapper implements RowMapper<GroupLoanIndividualMonitoringData> {

        final String schema;

        private GLIMWaiveChargeMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("glim.id as id, ");
            sql.append("glim.client_id as clientId, c.display_name as clientName, c.external_id as clientExternalID, ");
            sql.append("glim.is_client_selected as isClientSelected, ");
            sql.append("glim.charge_amount as chargeAmount, ");
            sql.append("glim.paid_charge_amount as paidChargeAmount, ");
            sql.append("glim.waived_charge_amount as waivedChargeAmount ");
            sql.append(" from m_loan_glim glim ");
            sql.append(" left join m_client c on glim.client_id = c.id ");
            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public GroupLoanIndividualMonitoringData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString("clientName");
            final String clientExternalID = rs.getString("clientExternalID");
            final BigDecimal chargeAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "chargeAmount");
            final BigDecimal paidChargeAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "paidChargeAmount");
            final BigDecimal waivedChargeAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "waivedChargeAmount");
            Long loanId = null;
            BigDecimal totalLoanAmount = null;
            BigDecimal proposedAmount = null;
            BigDecimal approvedAmount = null;
            BigDecimal disbursedAmount = null;
            CodeValueData loanPurpose = null;
            Boolean isClientSelected = null;
            BigDecimal adjustedAmount = null;
            BigDecimal installmentAmount = null;
            BigDecimal totalPaybleAmount = null;
            BigDecimal paidInterestAmount = null;
            BigDecimal paidAmount = null;
            BigDecimal interestAmount = null;
            BigDecimal paidPrincipalAmount = null;
            BigDecimal waivedInterestAmount = null;
            BigDecimal principalWrittenOffAmount = null;
            BigDecimal interestWrittenOffAmount = null;
            BigDecimal chargeWrittenOffAmount = null;
            BigDecimal penaltyAmount = null;
            BigDecimal paidPenaltyAmount = null;
            BigDecimal waivedPenaltyAmount = null;
            BigDecimal penaltywrittenOffAmount = null;
            BigDecimal remainingTransactionAmount = null;
            BigDecimal transactionAmount = null;
            Boolean isActive = null;
            BigDecimal totalPrincipalOutstanding = null;
            BigDecimal totalInterestOutstanding = null;
            BigDecimal totalFeeChargeOutstanding = null;
            BigDecimal totalpenaltyChargeOutstanding = null;
            BigDecimal totalLoanOutstandingAmount = null;
            return GroupLoanIndividualMonitoringData.instance(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID,
                    proposedAmount, approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount,
                    installmentAmount, totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount,
                    paidChargeAmount, waivedInterestAmount, waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount,
                    chargeWrittenOffAmount, penaltyAmount, paidPenaltyAmount, waivedPenaltyAmount, penaltywrittenOffAmount,
                    remainingTransactionAmount, transactionAmount, isActive, totalPrincipalOutstanding, totalInterestOutstanding,
                    totalFeeChargeOutstanding, totalpenaltyChargeOutstanding, totalLoanOutstandingAmount);
        }
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveWaiveChargeDetails(Long loanId) {

        GLIMWaiveChargeMapper rm = new GLIMWaiveChargeMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ? and glim.is_client_selected = 1 "
                + " and (glim.waived_charge_amount = 0 or glim.waived_charge_amount is NULL)"
                + " and  (glim.fee_charges_writtenoff_amount = 0 or glim.fee_charges_writtenoff_amount is NULL )  and glim.is_active = 1 ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveSelectedClientsByLoanId(Long loanId) {

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ? and glim.is_client_selected = 1";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveWaiveInterestTemplate(Long loanId) {

        GroupLoanIndividualMonitoringWaiveInterestMapper rm = new GroupLoanIndividualMonitoringWaiveInterestMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ? AND glim.is_active = 1 AND glim.is_client_selected = 1"
                + " and (glim.waived_interest_amount is NULL OR glim.waived_interest_amount = 0 )";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    @SuppressWarnings("unused")
    private static final class GroupLoanIndividualMonitoringWaiveInterestMapper implements RowMapper<GroupLoanIndividualMonitoringData> {

        final String schema;

        private GroupLoanIndividualMonitoringWaiveInterestMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("glim.id as id, ");
            sql.append("glim.client_id as clientId, c.display_name as clientName, ");
            sql.append("glim.interest_amount as interestAmount, ");
            sql.append("glim.is_client_selected as isClientSelected, ");
            sql.append("glim.principal_writtenoff_amount as principalWrittenoffAmount, ");
            sql.append("glim.interest_writtenoff_amount as interestWrittenoffAmount, ");
            sql.append("glim.fee_charges_writtenoff_amount as chargeWrittenoffAmount, ");
            sql.append("glim.paid_interest_amount as paidInterestAmount, glim.waived_interest_amount as waivedInterestAmount ");
            sql.append(" from m_loan_glim glim ");
            sql.append(" left join m_client c on glim.client_id = c.id ");
            sql.append(" left join m_loan l on glim.loan_id = l.id ");
            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public GroupLoanIndividualMonitoringData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString("clientName");
            final BigDecimal interestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestAmount");
            final BigDecimal paidInterestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "paidInterestAmount");
            final BigDecimal waivedInterestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "waivedInterestAmount");
            final Boolean isClientSelected = rs.getBoolean("isClientSelected");

            final BigDecimal principalWrittenoffAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenoffAmount");
            final BigDecimal interestWrittenoffAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenoffAmount");
            final BigDecimal chargeWrittenoffAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "chargeWrittenoffAmount");
            final BigDecimal writeOffAmount = principalWrittenoffAmount.add(interestWrittenoffAmount).add(chargeWrittenoffAmount);
            // calculate unpaid interest
            BigDecimal remainingTransactionAmount = MathUtility.subtract(interestAmount, paidInterestAmount);
            BigDecimal transactionAmount = remainingTransactionAmount;

            return GroupLoanIndividualMonitoringData.waiveInterestDetails(id, clientId, clientName, paidInterestAmount, interestAmount,
                    remainingTransactionAmount, transactionAmount, isClientSelected);
        }
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveAllActiveGlimByLoanId(Long loanId) {

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ? " + " AND glim.is_active = 1 AND glim.is_client_selected = 1 ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    @Override
    public Collection<GroupLoanIndividualMonitoringData> retrieveRecoveryGlimByLoanId(Long loanId) {

        GroupLoanIndividualMonitoringMapper rm = new GroupLoanIndividualMonitoringMapper();

        String sql = "select " + rm.schema() + " where glim.loan_id = ? " + " AND glim.is_active = 0 AND glim.is_client_selected = 1 "
                + " and  (glim.principal_writtenoff_amount > 0) ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    private static final class GroupLoanIndividualMonitoringRepaymentScheduleMapper implements
            ResultSetExtractor<GroupLoanIndividualMonitoringRepaymentScheduleData> {

        private BigDecimal outstandingLoanPrincipalBalance;
        private final LocalDate disbursedDate;
        private final BigDecimal disbursedAmount;

        public GroupLoanIndividualMonitoringRepaymentScheduleMapper(BigDecimal disbursedAmount, LocalDate disbursedDate) {
            this.outstandingLoanPrincipalBalance = disbursedAmount;
            this.disbursedDate = disbursedDate;
            this.disbursedAmount = disbursedAmount;
        }

        public String schema() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("mlr.fromdate as fromDate, mlr.duedate as dueDate, ");
            sql.append("mlr.installment as installmentNumber, glr.principal_amount as principalAmount, glr.interest_amount as interestAmount, ");
            sql.append("glr.fee_charges_amount as feeChargesAmount, ");
            sql.append("glr.penalty_charges_amount as penaltyChargesAmount ");
            sql.append(" from f_loan_glim_repayment_schedule glr ");
            sql.append(" left join m_loan_repayment_schedule mlr on glr.loan_repayment_schedule_id = mlr.id ");

            return sql.toString();

        }

        @Override
        public GroupLoanIndividualMonitoringRepaymentScheduleData extractData(ResultSet rs) throws SQLException, DataAccessException {

            final GroupLoanIndividualMonitoringRepaymentScheduleData disbursementPeriod = GroupLoanIndividualMonitoringRepaymentScheduleData
                    .disbursementOnlyPeriod(this.disbursedDate, this.disbursedAmount, BigDecimal.ZERO, true);

            final Collection<GroupLoanIndividualMonitoringRepaymentScheduleData> periods = new ArrayList<>();

            if (this.disbursedAmount != null && this.disbursedDate != null) {
                periods.add(disbursementPeriod);
            }
            while (rs.next()) {
                final Integer installmentNumber = JdbcSupport.getInteger(rs, "installmentNumber");
                final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "duedate");
                final LocalDate fromdate = JdbcSupport.getLocalDate(rs, "fromdate");
                final BigDecimal principalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalAmount");
                final BigDecimal interestAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestAmount");
                final BigDecimal feeChargesAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesAmount");
                final BigDecimal penaltyChargesAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesAmount");

                final BigDecimal totalExpectedCostOfLoanForPeriod = interestAmount.add(feeChargesAmount).add(penaltyChargesAmount);

                final BigDecimal totalDueForPeriod = principalAmount.add(totalExpectedCostOfLoanForPeriod);

                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.subtract(principalAmount);

                final GroupLoanIndividualMonitoringRepaymentScheduleData repaymentsPeriods = GroupLoanIndividualMonitoringRepaymentScheduleData
                        .repaymentPeriods(installmentNumber, dueDate, fromdate, principalAmount, interestAmount, feeChargesAmount,
                                penaltyChargesAmount, totalDueForPeriod, this.outstandingLoanPrincipalBalance);
                periods.add(repaymentsPeriods);
            }

            return GroupLoanIndividualMonitoringRepaymentScheduleData.GlimRepaymentScheduleData(periods);
        }

    }

    @Override
    public GroupLoanIndividualMonitoringRepaymentScheduleData retriveGlimRepaymentScheduleById(final Long glimId,
            BigDecimal disbursedAmount, LocalDate disbursedDate) {

        GroupLoanIndividualMonitoringRepaymentScheduleMapper rm = new GroupLoanIndividualMonitoringRepaymentScheduleMapper(disbursedAmount,
                disbursedDate);

        String sql = "select " + rm.schema() + " where glr.glim_id = ? ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { glimId });

    }
}
