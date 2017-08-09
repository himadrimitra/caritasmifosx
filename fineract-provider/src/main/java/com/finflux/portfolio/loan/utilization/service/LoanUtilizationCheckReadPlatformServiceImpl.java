package com.finflux.portfolio.loan.utilization.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.accountdetails.service.AccountEnumerations;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;
import com.finflux.portfolio.loan.purpose.service.LoanPurposeGroupReadPlatformService;
import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckData;
import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckDetailData;
import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckTemplateData;
import com.finflux.portfolio.loan.utilization.data.UtilizationDetailsData;
import com.finflux.portfolio.loan.utilization.exception.LoanUtilizationCheckNotFoundException;

@Service
public class LoanUtilizationCheckReadPlatformServiceImpl implements LoanUtilizationCheckReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final LoanUtilizationCheckDataExtractor loanUtilizationCheckDataExtractor = new LoanUtilizationCheckDataExtractor();
    private final LoanPurposeGroupReadPlatformService loanPurposeGroupReadPlatformService;

    @Autowired
    public LoanUtilizationCheckReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final LoanPurposeGroupReadPlatformService loanPurposeGroupReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.loanPurposeGroupReadPlatformService = loanPurposeGroupReadPlatformService;
    }

    @Override
    public Collection<LoanUtilizationCheckTemplateData> retrieveGroupUtilizationchecksTemplate(final Long centerId) {
        final Collection<LoanPurposeData> loanPurposeDatas = this.loanPurposeGroupReadPlatformService.retrieveAllLoanPurposes(null, null,
                true);
        final LoanUtilizationCheckTemplateDataMapper utilizationChecksDataMapper = new LoanUtilizationCheckTemplateDataMapper(
                loanPurposeDatas);
        final String sql = "SELECT " + utilizationChecksDataMapper.schema() + " WHERE g.id = ? ";
        return this.jdbcTemplate.query(sql, utilizationChecksDataMapper, new Object[] { centerId });

    }

    @Override
    public Collection<LoanUtilizationCheckTemplateData> retrieveCenterUtilizationchecksTemplate(final Long centerId) {
        final Collection<LoanPurposeData> loanPurposeDatas = this.loanPurposeGroupReadPlatformService.retrieveAllLoanPurposes(null, null,
                true);
        final LoanUtilizationCheckTemplateDataMapper utilizationChecksDataMapper = new LoanUtilizationCheckTemplateDataMapper(
                loanPurposeDatas);
        final String sql = "SELECT " + utilizationChecksDataMapper.schema() + " WHERE g.parent_id = ? ";
        return this.jdbcTemplate.query(sql, utilizationChecksDataMapper, new Object[] { centerId });

    }

    private static final class LoanUtilizationCheckTemplateDataMapper implements RowMapper<LoanUtilizationCheckTemplateData> {

        private final String schema;
        final Collection<LoanPurposeData> loanPurposeDatas;

        public LoanUtilizationCheckTemplateDataMapper(final Collection<LoanPurposeData> loanPurposeDatas) {
            this.loanPurposeDatas = loanPurposeDatas;
            final StringBuilder sql = new StringBuilder(200);
            sql.append("g.id AS groupId,g.display_name AS groupName,c.id AS clientId, c.display_name AS clientName ");
            sql.append(",l.id AS loanId, l.loan_status_id AS loanStatusId, l.loan_type_enum AS loanTypeId ");
            sql.append(",lp.id AS loanPurposeId, lp.name AS loanPurposeName, l.principal_amount AS principalAmount ");
            sql.append("FROM m_group g ");
            sql.append("JOIN m_loan l ON l.group_id = g.id ");
            sql.append("JOIN m_client c ON c.id = l.client_id ");
            sql.append("JOIN f_loan_purpose lp ON lp.id = l.loan_purpose_id ");
            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LoanUtilizationCheckTemplateData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Long groupId = rs.getLong("groupId");
            final String groupName = rs.getString("groupName");
            final Long clientId = rs.getLong("clientId");
            final String clientName = rs.getString("clientName");
            final Long loanId = rs.getLong("loanId");
            final Integer loanStatusId = rs.getInt("loanStatusId");
            LoanStatusEnumData loanStatus = null;
            if (loanStatusId != null) {
                loanStatus = LoanEnumerations.status(loanStatusId);
            }
            final Integer loanTypeId = rs.getInt("loanTypeId");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);
            final Long loanPurposeId = rs.getLong("loanPurposeId");
            final String loanPurposeName = rs.getString("loanPurposeName");
            final BigDecimal principalAmount = rs.getBigDecimal("principalAmount");
            return LoanUtilizationCheckTemplateData.template(groupId, groupName, clientId, clientName, loanId, loanStatus, loanType,
                    loanPurposeId, loanPurposeName, principalAmount, this.loanPurposeDatas);
        }
    }

    @Override
    public Collection<LoanUtilizationCheckData> retrieveAll(final Long loanId) {
        this.context.authenticatedUser();
        final String sql = "SELECT " + this.loanUtilizationCheckDataExtractor.schema() + " WHERE lucd.loan_id = ? ";
        return this.jdbcTemplate.query(sql, this.loanUtilizationCheckDataExtractor, new Object[] { loanId });
    }

    @Override
    public LoanUtilizationCheckData retrieveOne(final Long loanId, final Long utilizationCheckId) {
        this.context.authenticatedUser();
        final String sql = "SELECT " + this.loanUtilizationCheckDataExtractor.schema() + " WHERE luc.id = ? ";
        Collection<LoanUtilizationCheckData> loanUtilizationCheckDatas = this.jdbcTemplate.query(sql,
                this.loanUtilizationCheckDataExtractor, new Object[] { utilizationCheckId });
        if (loanUtilizationCheckDatas == null || loanUtilizationCheckDatas.isEmpty()) { throw new LoanUtilizationCheckNotFoundException(
                loanId, utilizationCheckId); }

        return loanUtilizationCheckDatas.iterator().next();
    }

    @Override
    public Collection<LoanUtilizationCheckData> retrieveCenterLoanUtilizationchecks(final Long centerId) {
        this.context.authenticatedUser();
        String sql = "SELECT " + this.loanUtilizationCheckDataExtractor.schema() + "WHERE gp.parent_id = ? ";
        return this.jdbcTemplate.query(sql, this.loanUtilizationCheckDataExtractor, new Object[] { centerId });
    }

    @Override
    public Collection<LoanUtilizationCheckData> retrieveGroupLoanUtilizationchecks(final Long groupId) {
        this.context.authenticatedUser();
        String sql = "SELECT " + this.loanUtilizationCheckDataExtractor.schema() + "WHERE gp.id = ? ";
        return this.jdbcTemplate.query(sql, this.loanUtilizationCheckDataExtractor, new Object[] { groupId });
    }

    private static final class LoanUtilizationCheckDataMapper implements RowMapper<LoanUtilizationCheckData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        private LoanUtilizationCheckDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("luc.id AS lucId, luc.audit_done_by AS auditDoneById, staff.display_name AS auditDoneByName ");
            sqlBuilder.append(",luc.audit_done_on AS auditDoneOn,lucd.id AS lucdId,luc.loan_id AS loanId ");
            sqlBuilder.append(",lucd.is_same_as_oroginal_purpose AS isSameAsOroginalPurpose ");
            sqlBuilder.append(",lucd.amount AS amount,lucd.comment AS comment ");
            sqlBuilder.append(",lp.id AS lpId,lp.name AS lpName,lp.is_active AS lpIsActive ");
            sqlBuilder.append(",gp.id AS groupId,gp.display_name AS groupName,c.id AS clientId,c.display_name AS clientName ");
            sqlBuilder.append(",l.loan_status_id AS loanStatusId, l.loan_type_enum AS loanTypeId,l.principal_amount AS principalAmount ");
            sqlBuilder.append("FROM f_loan_utilization_check luc ");
            sqlBuilder.append("LEFT JOIN m_staff staff ON staff.id = luc.audit_done_by ");
            sqlBuilder.append("JOIN f_loan_utilization_check_detail lucd ON lucd.loan_utilization_check_id = luc.id ");
            sqlBuilder.append("JOIN m_loan l ON l.id = luc.loan_id ");
            sqlBuilder.append("JOIN m_client c ON c.id = l.client_id ");
            sqlBuilder.append("LEFT JOIN m_group gp ON gp.id = l.group_id ");
            sqlBuilder.append("LEFT JOIN f_loan_purpose lp ON lp.id = lucd.loan_purpose_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public LoanUtilizationCheckData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "lucId");
            if (id == null) { return null; }
            final Long loanId = rs.getLong("loanId");
            final Long auditDoneById = rs.getLong("auditDoneById");
            final String auditDoneByName = rs.getString("auditDoneByName");
            final LocalDate auditDoneOn = JdbcSupport.getLocalDate(rs, "auditDoneOn");
            return LoanUtilizationCheckData.instance(id, loanId, auditDoneById, auditDoneByName, auditDoneOn);
        }

    }

    private static final class LoanUtilizationCheckDataExtractor implements ResultSetExtractor<Collection<LoanUtilizationCheckData>> {

        LoanUtilizationCheckDataMapper loanUtilizationCheckDataMapper = new LoanUtilizationCheckDataMapper();
        LoanUtilizationCheckDetailDataMapper loanUtilizationCheckDetailDataMapper = new LoanUtilizationCheckDetailDataMapper();
        UtilizationDetailsDataMapper utilizationDetailsDataMapper = new UtilizationDetailsDataMapper();

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        private LoanUtilizationCheckDataExtractor() {
            this.schemaSql = loanUtilizationCheckDataMapper.schema();
        }

        @Override
        public Collection<LoanUtilizationCheckData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            final List<LoanUtilizationCheckData> loanUtilizationCheckDataList = new ArrayList<>();
            LoanUtilizationCheckData loanUtilizationCheckData = null;
            LoanUtilizationCheckDetailData loanUtilizationCheckDetailData = null;
            int lucIndex = 0;// Loan utilization check index
            int lucdIndex = 0;// Loan utilization check details index
            int udIndex = 0;// utilization details index
            while (rs.next()) {
                loanUtilizationCheckData = this.loanUtilizationCheckDataMapper.mapRow(rs, lucIndex++);
                if (loanUtilizationCheckData != null) {
                    loanUtilizationCheckDetailData = this.loanUtilizationCheckDetailDataMapper.mapRow(rs, lucdIndex++);
                    if (loanUtilizationCheckDetailData != null) {
                        loanUtilizationCheckData.addLoanUtilizationCheckDetailData(loanUtilizationCheckDetailData);
                    }
                    if (loanUtilizationCheckDetailData != null) {
                        final UtilizationDetailsData utilizationDetailsData = this.utilizationDetailsDataMapper.mapRow(rs, udIndex++);
                        loanUtilizationCheckDetailData.setUtilizationDetailsData(utilizationDetailsData);
                    }
                    loanUtilizationCheckDataList.add(loanUtilizationCheckData);
                }
            }
            return loanUtilizationCheckDataList;
        }
    }

    private static final class LoanUtilizationCheckDetailDataMapper implements RowMapper<LoanUtilizationCheckDetailData> {

        @Override
        public LoanUtilizationCheckDetailData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "lucdId");
            if (id == null) { return null; }
            final Long groupId = JdbcSupport.getLongDefaultToNullIfZero(rs, "groupId");
            final String groupName = rs.getString("groupName");
            final Long clientId = JdbcSupport.getLongDefaultToNullIfZero(rs, "clientId");
            final String clientName = rs.getString("clientName");
            final Integer loanStatusId = rs.getInt("loanStatusId");
            LoanStatusEnumData loanStatus = null;
            if (loanStatusId != null) {
                loanStatus = LoanEnumerations.status(loanStatusId);
            }
            final Integer loanTypeId = rs.getInt("loanTypeId");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);
            final BigDecimal principalAmount = rs.getBigDecimal("principalAmount");
            return LoanUtilizationCheckDetailData.instance(id, groupId, groupName, clientId, clientName, loanStatus, loanType,
                    principalAmount);
        }
    }

    private static final class UtilizationDetailsDataMapper implements RowMapper<UtilizationDetailsData> {

        @Override
        public UtilizationDetailsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            LoanPurposeData loanPurposeData = null;
            final Long lpId = JdbcSupport.getLongDefaultToNullIfZero(rs, "lpId");
            if (lpId != null) {
                final String lpName = rs.getString("lpName");
                final Boolean isActive = rs.getBoolean("lpIsActive");
                loanPurposeData = LoanPurposeData.instance(lpId, lpName, null, null, isActive);
            }
            final Boolean isSameAsOroginalPurpose = rs.getBoolean("isSameAsOroginalPurpose");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final String comment = rs.getString("comment");
            return UtilizationDetailsData.instance(loanPurposeData, isSameAsOroginalPurpose, amount, comment);
        }
    }

    @Override
    public BigDecimal retrieveUtilityAmountByLoanId(final Long loanId) {
        String sql = "SELECT IFNULL(SUM(IFNULL(lucd.amount, 0)),0) from f_loan_utilization_check_detail lucd "
                + "join f_loan_utilization_check luc on luc.id = lucd.loan_utilization_check_id" + " WHERE luc.loan_id = ? ";
        return this.jdbcTemplate.queryForObject(sql, new Object[] { loanId }, BigDecimal.class);
    }
}