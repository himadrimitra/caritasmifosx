package com.finflux.portfolio.loanemipacks.service;

import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;
import com.finflux.portfolio.loanemipacks.exception.LoanEMIPackNotFoundException;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
public class LoanEMIPacksReadPlatformServiceImpl implements LoanEMIPacksReadPlatformService {

        private final PlatformSecurityContext context;
        private final JdbcTemplate jdbcTemplate;
        private final FineractEntityAccessUtil fineractEntityAccessUtil;
        private final LoanProductReadPlatformService loanProductReadPlatformService;
        private final LoanDropdownReadPlatformService dropdownReadPlatformService;

        @Autowired
        public LoanEMIPacksReadPlatformServiceImpl(final PlatformSecurityContext context,
                final RoutingDataSource dataSource,
                final FineractEntityAccessUtil fineractEntityAccessUtil,
                final LoanProductReadPlatformService loanProductReadPlatformService,
                final LoanDropdownReadPlatformService dropdownReadPlatformService){

                this.context = context;
                this.jdbcTemplate = new JdbcTemplate(dataSource);
                this.fineractEntityAccessUtil = fineractEntityAccessUtil;
                this.loanProductReadPlatformService = loanProductReadPlatformService;
                this.dropdownReadPlatformService = dropdownReadPlatformService;
        }

        @Override
        public Collection<LoanEMIPackData> retrieveActiveLoanProductsWithoutEMIPacks() {
                LoanProductMapper mapper = new LoanProductMapper();
                String sql = mapper.schemaActiveLoanProductsWithoutEMIPacks();
                String inClause = this.fineractEntityAccessUtil
                        .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
                if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
                        sql += " and id in ( " + inClause + " )";
                }

                return this.jdbcTemplate.query(sql, mapper);
        }

        @Override
        public Collection<LoanEMIPackData> retrieveActiveLoanProductsWithEMIPacks() {
                LoanProductMapper mapper = new LoanProductMapper();
                String sql = mapper.schemaActiveLoanProductsWithEMIPacks();
                String inClause = this.fineractEntityAccessUtil
                        .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
                if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
                        sql += " and lp.id in ( " + inClause + " )";
                }

                return this.jdbcTemplate.query(sql, mapper);
        }

        @Override
        public LoanEMIPackData retrieveEMIPackTemplate(Long loanProductId) {
                LoanProductData loanProductData = this.loanProductReadPlatformService.retrieveLoanProduct(loanProductId);

                return LoanEMIPackData.template(loanProductData.getId(),
                        loanProductData.getName(),
                        loanProductData.getMultiDisburseLoan(),
                        loanProductData.getMaxTrancheCount(),
                        this.dropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions());
        }

        @Override
        public Collection<LoanEMIPackData> retrieveEMIPackDetails(Long loanProductId) {
                LoanEMIPackMapper mapper = new LoanEMIPackMapper();
                return this.jdbcTemplate.query(mapper.schemaList(), mapper,  new Object[] { loanProductId });
        }

        @Override public LoanEMIPackData retrieveEMIPackDetails(Long loanProductId, Long loanEMIPackId) {
                try{
                        LoanEMIPackMapper mapper = new LoanEMIPackMapper();
                        return this.jdbcTemplate.queryForObject(mapper.schemaSingle(),mapper, new Object[]{loanProductId, loanEMIPackId});
                }catch(EmptyResultDataAccessException e){
                        throw new LoanEMIPackNotFoundException(loanEMIPackId);
                }
        }

        private static final class LoanProductMapper implements RowMapper<LoanEMIPackData> {

                public String schemaActiveLoanProductsWithoutEMIPacks() {
                        return "select lp.id as loanProductId, lp.name as loanProductName from m_product_loan as lp "
                                + "where (close_date is null or close_date >= CURDATE()) "
                                + "and lp.id not in (select distinct lep.loan_product_id from f_loan_emi_packs as lep) ";
                }

                public String schemaActiveLoanProductsWithEMIPacks() {
                        return "select distinct lep.loan_product_id as loanProductId, lp.name as loanProductName "
                                + "from f_loan_emi_packs as lep left join m_product_loan as lp on lep.loan_product_id = lp.id "
                                + "where (close_date is null or close_date >= CURDATE()) ";
                }

                @Override
                public LoanEMIPackData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

                        final Long loanProductId = rs.getLong("loanProductId");
                        final String loanProductName = rs.getString("loanProductName");

                        return LoanEMIPackData.loanData(loanProductId, loanProductName);
                }
        }

        private static final class LoanEMIPackMapper implements RowMapper<LoanEMIPackData> {

                public String schemaList() {
                        return "select lep.id as id, lep.loan_product_id as loanProductId, "
                                + "lep.repay_every as repaymentEvery, lep.repayment_period_frequency_enum as repaymentFrequencyTypeEnum, "
                                + "lep.number_of_repayments as numberOfRepayments, lep.sanction_amount as sanctionAmount, "
                                + "lep.fixed_emi as fixedEmi, lep.disbursal_1_amount as disbursalAmount1, "
                                + "lep.disbursal_2_amount as disbursalAmount2, lep.disbursal_3_amount as disbursalAmount3, "
                                + "lep.disbursal_4_amount as disbursalAmount4, lep.disbursal_2_emi as disbursalEmi2, "
                                + "lep.disbursal_3_emi as disbursalEmi3, lep.disbursal_4_emi as disbursalEmi4, "
                                + "lp.name as loanProductName from f_loan_emi_packs as lep "
                                + "left join m_product_loan as lp on lep.loan_product_id = lp.id where lep.loan_product_id = ? "
                                + "order by lep.repayment_period_frequency_enum, lep.repay_every, lep.sanction_amount ";
                }

                public String schemaSingle() {
                        return "select lep.id as id, lep.loan_product_id as loanProductId, "
                                + "lep.repay_every as repaymentEvery, lep.repayment_period_frequency_enum as repaymentFrequencyTypeEnum, "
                                + "lep.number_of_repayments as numberOfRepayments, lep.sanction_amount as sanctionAmount, "
                                + "lep.fixed_emi as fixedEmi, lep.disbursal_1_amount as disbursalAmount1, "
                                + "lep.disbursal_2_amount as disbursalAmount2, lep.disbursal_3_amount as disbursalAmount3, "
                                + "lep.disbursal_4_amount as disbursalAmount4, lep.disbursal_2_emi as disbursalEmi2, "
                                + "lep.disbursal_3_emi as disbursalEmi3, lep.disbursal_4_emi as disbursalEmi4, "
                                + "lp.name as loanProductName from f_loan_emi_packs as lep "
                                + "left join m_product_loan as lp on lep.loan_product_id = lp.id "
                                + "where lep.loan_product_id = ? and lep.id = ? ";
                }

                @Override
                public LoanEMIPackData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

                        final Long id = JdbcSupport.getLong(rs, "id");
                        final Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");
                        final Integer repaymentEvery = JdbcSupport.getInteger(rs, "repaymentEvery");
                        final Integer repaymentFrequencyTypeEnum = JdbcSupport.getInteger(rs, "repaymentFrequencyTypeEnum");
                        final EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeEnum);
                        final Integer numberOfRepayments = JdbcSupport.getInteger(rs, "numberOfRepayments");
                        final BigDecimal sanctionAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "sanctionAmount");
                        final BigDecimal fixedEmi = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "fixedEmi");
                        final BigDecimal disbursalAmount1  = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "disbursalAmount1");
                        final BigDecimal disbursalAmount2 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "disbursalAmount2");
                        final BigDecimal disbursalAmount3 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "disbursalAmount3");
                        final BigDecimal disbursalAmount4 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "disbursalAmount4");
                        final Integer disbursalEmi2 = JdbcSupport.getInteger(rs, "disbursalEmi2");
                        final Integer disbursalEmi3 = JdbcSupport.getInteger(rs, "disbursalEmi3");
                        final Integer disbursalEmi4 = JdbcSupport.getInteger(rs, "disbursalEmi4");
                        final String loanProductName = rs.getString("loanProductName");

                        return LoanEMIPackData.loanEMIPackData(id,
                                loanProductId,
                                repaymentEvery,
                                repaymentFrequencyType,
                                numberOfRepayments,
                                sanctionAmount,
                                fixedEmi,
                                disbursalAmount1,
                                disbursalAmount2,
                                disbursalAmount3,
                                disbursalAmount4,
                                disbursalEmi2,
                                disbursalEmi3,
                                disbursalEmi4,
                                loanProductName);
                }
        }
}
