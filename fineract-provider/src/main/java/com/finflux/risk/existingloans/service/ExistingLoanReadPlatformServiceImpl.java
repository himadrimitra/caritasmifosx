package com.finflux.risk.existingloans.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;
import com.finflux.risk.creditbureau.configuration.data.CreditBureauProductData;
import com.finflux.risk.creditbureau.configuration.service.CreditBureauProductReadPlatformService;
import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;
import com.finflux.risk.existingloans.data.ExistingLoanData;
import com.finflux.risk.existingloans.data.ExistingLoanTemplateData;
import com.finflux.risk.existingloans.exception.ExistingLoanNotFoundException;

@Service
public class ExistingLoanReadPlatformServiceImpl implements ExistingLoanReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final LoanDropdownReadPlatformService loanDropdownReadPlatformService;
    private final ExistingLoanStatusDropDownReadPlatformService existingLoanStatusDropDownReadPlatformService;
    private final CreditBureauProductReadPlatformService creditBureauProductReadPlatformService;

    @Autowired
    public ExistingLoanReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final LoanDropdownReadPlatformService loanDropdownReadPlatformService,
            final ExistingLoanStatusDropDownReadPlatformService existingLoanStatusDropDownReadPlatformService,
            final CreditBureauProductReadPlatformService creditBureauProductReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.loanDropdownReadPlatformService = loanDropdownReadPlatformService;
        this.existingLoanStatusDropDownReadPlatformService = existingLoanStatusDropDownReadPlatformService;
        this.creditBureauProductReadPlatformService = creditBureauProductReadPlatformService;
    }

    @Override
    public ExistingLoanTemplateData retriveTemplate() {
        final List<CodeValueData> existingLoanSourceOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.existingLoanSource));
        final Collection<CreditBureauData> creditBureauProductsOption = this.creditBureauProductReadPlatformService.retrieveCreditBureaus();
        final List<CodeValueData> lenderOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.lenderOption));
        final List<CodeValueData> loanTypeOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.loanType));
        final List<CodeValueData> externalLoanPurposeOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.externalLoanPurpose));
        final Collection<EnumOptionData> loanTenaureOption = this.loanDropdownReadPlatformService.retrieveLoanTermFrequencyTypeOptions();
        final Collection<EnumOptionData> termPeriodFrequencyType = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyTypeOptions();
        final Collection<LoanStatusEnumData> loanStatusOption = this.existingLoanStatusDropDownReadPlatformService.statusTypeOptions();
        return ExistingLoanTemplateData.template(existingLoanSourceOption, creditBureauProductsOption, lenderOption, loanTypeOption,
                externalLoanPurposeOption, loanTenaureOption, termPeriodFrequencyType, loanStatusOption);
    }

    @Override
    public List<ExistingLoanData> retriveAll(final Long clientId, final Long loanApplicationId, final Long loanId,
            final Long trancheDisbursalId) {
        final ExistingLoanMapper rm = new ExistingLoanMapper();
        String sql = rm.schema() + " WHERE el.client_id= ? ";
        if (loanApplicationId != null) {
            sql += " AND el.loan_application_id = ? ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { clientId, loanApplicationId });
        } else if (loanId != null && trancheDisbursalId == null) {
            sql += " AND el.loan_id = ? ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { clientId, loanId });
        } else if (loanId != null && trancheDisbursalId != null) {
            sql += " AND el.loan_id = ? AND el.tranche_disbursal_id = ? ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { clientId, loanId, trancheDisbursalId });
        }
        return this.jdbcTemplate.query(sql, rm, new Object[] { clientId });
    }

    @Override
    public ExistingLoanData retrieveOne(Long clientId, Long existingloanId) {
        try {
            final ExistingLoanMapper rm = new ExistingLoanMapper();
            final String sql = rm.schema() + " WHERE el.client_id= ? AND el.id= ? ";
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { clientId, existingloanId });
        } catch (EmptyResultDataAccessException e) {
            throw new ExistingLoanNotFoundException(existingloanId);
        }
    }

    private static final class ExistingLoanMapper implements RowMapper<ExistingLoanData> {

        private final String schema;

        public ExistingLoanMapper() {
            final StringBuilder builder = new StringBuilder(400);
            builder.append("SELECT el.id AS id,el.client_id AS clientId,el.loan_application_id AS loanApplicationId,el.loan_id AS loanId ");
            builder.append(",source.id AS sourceId, source.code_value AS sourceName,source.is_active AS sourceIsActive ");
            builder.append(",cbp.id AS cbpId, cbp.name AS cbpName, cbp.implementation_key AS cbpImplementationKey, cbp.is_active AS cbpIsActive ");
            builder.append(",el.loan_creditbureau_enquiry_id AS loanCreditBureauEnquiryId ");
            builder.append(",lender.id AS lenderId, lender.code_value AS cvlenderName,lender.is_active AS lenderIsActive ");
            builder.append(",el.lender_name AS lenderName ");
            builder.append(",loanType.id AS loanTypeId, loanType.code_value AS loanTypeName,loanType.is_active AS loanTypeIsActive ");
            builder.append(",el.amount_borrowed AS amountBorrowed ,el.current_outstanding AS currentOutstanding ,el.amt_overdue AS amtOverdue ");
            builder.append(",el.written_off_amount AS writtenOffAmount ,el.loan_tenure AS loanTenure ,el.loan_tenure_period_type AS loanTenurePeriodTypeId ");
            builder.append(",el.repayment_frequency AS repaymentFrequencyId ,el.repayment_frequency_multiple_of AS repaymentFrequencyMultipleOf ,el.installment_amount AS installmentAmount ");
            builder.append(",externalLoanPurpose.id AS externalLoanPurposeId, externalLoanPurpose.code_value AS externalLoanPurposeName,externalLoanPurpose.is_active AS externalLoanPurposeIsActive ");
            builder.append(",el.loan_status_id AS loanStatusId,el.disbursed_date AS disbursedDate,el.maturity_date as maturityDate,el.closed_date as closedDate ");
            builder.append(",el.gt_0_dpd_3_mths AS gt0dpd3mths, el.30_dpd_12_mths AS dpd30mths12, el.30_dpd_24_mths AS dpd30mths24, el.60_dpd_24_mths AS dpd60mths24 ");
            builder.append(",el.remark AS remark, el.archive AS archive ");
            builder.append("FROM f_existing_loan el ");
            builder.append("JOIN m_client client ON client.id = el.client_id ");
            builder.append("LEFT JOIN m_code_value source ON source.id = el.source_id  ");
            builder.append("LEFT JOIN f_creditbureau_product cbp ON cbp.id = el.creditbureau_product_id  ");
            builder.append("LEFT JOIN m_code_value lender ON lender.id = el.lender_cv_id  ");
            builder.append("LEFT JOIN m_code_value loanType ON loanType.id = el.loantype_cv_id  ");
            builder.append("LEFT JOIN m_code_value externalLoanPurpose ON externalLoanPurpose.id = el.external_loan_purpose_cv_id ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ExistingLoanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final Long loanApplicationId = JdbcSupport.getLong(rs, "loanApplicationId");
            final Long loanId = JdbcSupport.getLong(rs, "loanId");
            final Long sourceId = JdbcSupport.getLong(rs, "sourceId");
            final String sourceName = rs.getString("sourceName");
            final Boolean sourceIsActive = rs.getBoolean("sourceIsActive");
            CodeValueData source = null;
            if (sourceId != null) {
                source = CodeValueData.instance(sourceId, sourceName, sourceIsActive);
            }
            final Long cbpId = JdbcSupport.getLong(rs, "cbpId");
            final String cbpName = rs.getString("cbpName");
            final String cbpImplementationKey = rs.getString("cbpImplementationKey");
            final Boolean cbpIsActive = rs.getBoolean("cbpIsActive");
            CreditBureauProductData creditBureauProductData = null;
            if (cbpId != null) {
                creditBureauProductData = CreditBureauProductData.instance(cbpId, cbpName, cbpImplementationKey, cbpIsActive);
            }
            final Long loanCreditBureauEnquiryId = JdbcSupport.getLong(rs, "loanCreditBureauEnquiryId");
            final Long lenderId = JdbcSupport.getLong(rs, "lenderId");
            final String cvlenderName = rs.getString("cvlenderName");
            final Boolean lenderIsActive = rs.getBoolean("lenderIsActive");
            CodeValueData lender = null;
            if (lenderId != null) {
                lender = CodeValueData.instance(lenderId, cvlenderName, lenderIsActive);
            }
            final String lenderName = rs.getString("lenderName");
            final Long loanTypeId = JdbcSupport.getLong(rs, "loanTypeId");
            final String loanTypeName = rs.getString("loanTypeName");
            final Boolean loanTypeIsActive = rs.getBoolean("loanTypeIsActive");
            CodeValueData loanType = null;
            if (loanTypeId != null) {
                loanType = CodeValueData.instance(loanTypeId, loanTypeName, loanTypeIsActive);
            }

            final BigDecimal amountBorrowed = rs.getBigDecimal("amountBorrowed");
            final BigDecimal currentOutstanding = rs.getBigDecimal("currentOutstanding");
            final BigDecimal amtOverdue = rs.getBigDecimal("amtOverdue");
            final BigDecimal writtenOffAmount = rs.getBigDecimal("writtenOffAmount");
            final Integer loanTenure = JdbcSupport.getInteger(rs, "loanTenure");
            final Integer loanTenurePeriodTypeId = JdbcSupport.getInteger(rs, "loanTenurePeriodTypeId");
            EnumOptionData loanTenurePeriodType = null;
            if (loanTenurePeriodTypeId != null) {
                loanTenurePeriodType = LoanEnumerations.termFrequencyType(loanTenurePeriodTypeId);
            }
            final Integer repaymentFrequencyId = JdbcSupport.getInteger(rs, "repaymentFrequencyId");
            EnumOptionData repaymentFrequency = null;
            if (repaymentFrequencyId != null) {
                repaymentFrequency = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyId);
            }
            final Integer repaymentFrequencyMultipleOf = JdbcSupport.getInteger(rs, "repaymentFrequencyMultipleOf");
            final BigDecimal installmentAmount = rs.getBigDecimal("installmentAmount");

            final Long externalLoanPurposeId = JdbcSupport.getLong(rs, "externalLoanPurposeId");
            final String externalLoanPurposeName = rs.getString("externalLoanPurposeName");
            final Boolean externalLoanPurposeIsActive = rs.getBoolean("externalLoanPurposeIsActive");
            CodeValueData externalLoanPurpose = null;
            if (externalLoanPurposeId != null) {
                externalLoanPurpose = CodeValueData.instance(externalLoanPurposeId, externalLoanPurposeName, externalLoanPurposeIsActive);
            }
            final Integer loanStatusId = JdbcSupport.getInteger(rs, "loanStatusId");
            LoanStatusEnumData loanStatus = null;
            if (loanStatusId != null) {
                loanStatus = LoanEnumerations.status(loanStatusId);
            }
            final LocalDate disbursedDate = JdbcSupport.getLocalDate(rs, "disbursedDate");
            final LocalDate maturityDate = JdbcSupport.getLocalDate(rs, "maturityDate");
            final LocalDate closedDate = JdbcSupport.getLocalDate(rs, "closedDate");
            final Integer gt0Dpd3Mths = JdbcSupport.getInteger(rs, "gt0dpd3mths");
            final Integer dpd30Mths12 = JdbcSupport.getInteger(rs, "dpd30mths12");
            final Integer dpd30Mths24 = JdbcSupport.getInteger(rs, "dpd30mths24");
            final Integer dpd60Mths24 = JdbcSupport.getInteger(rs, "dpd60mths24");
            final Integer archive = JdbcSupport.getInteger(rs, "archive");
            final String remark = rs.getString("remark");

            return ExistingLoanData.instance(id, clientId, loanApplicationId, loanId, source, creditBureauProductData,
                    loanCreditBureauEnquiryId, lender, lenderName, loanType, amountBorrowed, currentOutstanding, amtOverdue,
                    writtenOffAmount, loanTenure, loanTenurePeriodType, repaymentFrequency, repaymentFrequencyMultipleOf,
                    installmentAmount, externalLoanPurpose, loanStatus, disbursedDate, maturityDate, closedDate, gt0Dpd3Mths, dpd30Mths12,
                    dpd30Mths24, dpd60Mths24, remark, archive);

        }
    }

}
