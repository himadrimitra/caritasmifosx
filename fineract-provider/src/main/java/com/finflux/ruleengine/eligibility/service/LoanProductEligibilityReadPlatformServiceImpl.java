package com.finflux.ruleengine.eligibility.service;

import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityCriteriaData;
import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityData;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibility;
import com.finflux.ruleengine.lib.data.ExpressionNode;
import com.google.gson.reflect.TypeToken;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class LoanProductEligibilityReadPlatformServiceImpl implements LoanProductEligibilityReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final FromJsonHelper fromJsonHelper;
    private final LoanProductEligibilityDataMapper eligibilityDataMapper = new LoanProductEligibilityDataMapper();
    private final LoanProductEligibilityCriteriaDataMapper eligibilityCriteriaDataMapper = new LoanProductEligibilityCriteriaDataMapper();

    @Autowired
    public LoanProductEligibilityReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
                                                         final CodeValueReadPlatformService codeValueReadPlatformService,
                                                         final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.fromJsonHelper = fromJsonHelper;
    }

    @Override
    public List<LoanProductEligibilityData> getAllLoanProductEligibility() {
        LoanProductEligibilityData loanProductEligibilityData = null;
        final String sql = "select " + eligibilityDataMapper.schema();
        try {
            List<LoanProductEligibilityData> eligibilityDatas =  this.jdbcTemplate.query(sql, eligibilityDataMapper);
            return eligibilityDatas;
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public LoanProductEligibilityData retrieveOneLoanProductEligibility(Long loanProductId) {
        LoanProductEligibilityData loanProductEligibilityData = null;
        final String sql = "select " + eligibilityDataMapper.schema()+ " where lpe.loan_product_id = ? ";
        try {
            List<LoanProductEligibilityData> eligibilityDatas =  this.jdbcTemplate.query(sql, eligibilityDataMapper, loanProductId);
            if(eligibilityDatas!=null && eligibilityDatas.size()==1) {
                loanProductEligibilityData = eligibilityDatas.get(0);
            }
        } catch (final EmptyResultDataAccessException e) {
        }

        if(loanProductEligibilityData!=null){
            final String sql2 = "select " + eligibilityCriteriaDataMapper.schema()+ " where lpec.loan_product_eligibility_id = ? ";
            try {
                List<LoanProductEligibilityCriteriaData> eligibilityCriterias =  this.jdbcTemplate.query(sql2, eligibilityCriteriaDataMapper, loanProductEligibilityData.getId());
                loanProductEligibilityData.setCriterias(eligibilityCriterias);
            } catch (final EmptyResultDataAccessException e) {
            }
        }
        return loanProductEligibilityData;
    }

    private static final class LoanProductEligibilityDataMapper implements RowMapper<LoanProductEligibilityData> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public LoanProductEligibilityDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("lpe.id as id, ")
                    .append("lpe.loan_product_id as loanProductId, ")
                    .append("lp.name as loanProductName, ")
                    .append("lpe.is_active as isActive ")
                    .append("from f_loan_product_eligibility as lpe ")
                    .append("left join m_product_loan lp on lpe.loan_product_id= lp.id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanProductEligibilityData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long loanProductId = rs.getLong("loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final Boolean isActive = rs.getBoolean("isActive");
            return new LoanProductEligibilityData(id, loanProductId, loanProductName, isActive);
        }
    }

    private static final class LoanProductEligibilityCriteriaDataMapper implements RowMapper<LoanProductEligibilityCriteriaData> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public LoanProductEligibilityCriteriaDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("lpec.id as id, ")
                    .append("lpec.min_amount as minAmount, ")
                    .append("lpec.max_amount as maxAmount, ")
                    .append("lpec.risk_criteria_id as riskCriteriaId, ")
                    .append("rr.name as riskCriteriaName, ")
                    .append("lpec.approval_logic as approvalLogic, ")
                    .append("lpec.rejection_logic as rejectionLogic ")
                    .append("from f_loan_product_eligibility_criteria as lpec ")
                    .append("left join f_risk_rule rr on lpec.risk_criteria_id= rr.id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanProductEligibilityCriteriaData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Double minAmount = rs.getDouble("minAmount");
            final Double maxAmount = rs.getDouble("maxAmount");
            final Long riskCriteriaId = rs.getLong("riskCriteriaId");
            final String riskCriteriaName = rs.getString("riskCriteriaName");
            final String approvalLogicStr = rs.getString("approvalLogic");
            final String rejectionLogicStr = rs.getString("rejectionLogic");
            ExpressionNode approvalLogic = null;
            ExpressionNode rejectionLogic = null;
            Type type = new TypeToken<ExpressionNode>() {
            }.getType();
            if (approvalLogicStr != null) {
                approvalLogic = jsonHelper.getGsonConverter().fromJson(approvalLogicStr, type);
            }
            if (rejectionLogicStr != null) {
                rejectionLogic = jsonHelper.getGsonConverter().fromJson(rejectionLogicStr, type);
            }
            return new LoanProductEligibilityCriteriaData(minAmount, maxAmount, riskCriteriaId, riskCriteriaName,
                    approvalLogic, rejectionLogic);
        }
    }

}