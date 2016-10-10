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
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;
import com.finflux.risk.existingloans.data.ExistingLoanData;
import com.finflux.risk.existingloans.data.ExistingLoanTimelineData;
import com.finflux.risk.existingloans.domain.ExistingLoanRepositoryWrapper;

@Service
public class ExistingLoanReadPlatformServiceImp implements ExistingLoanReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final LoanDropdownReadPlatformService loanDropdownReadPlatformService;
    private final ExistingLoanStatusDropDownReadPlatformService existingLoanStatusDropDownReadPlatformService;
    private final ClientRepositoryWrapper clientRepository;
    private final ExistingLoanRepositoryWrapper existingLoanRepository;

    @Autowired
    public ExistingLoanReadPlatformServiceImp(final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final LoanDropdownReadPlatformService loanDropdownReadPlatformService,
            final ExistingLoanStatusDropDownReadPlatformService existingLoanStatusDropDownReadPlatformService,
            final ClientRepositoryWrapper clientRepository, final ExistingLoanRepositoryWrapper existingLoanRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.loanDropdownReadPlatformService = loanDropdownReadPlatformService;
        this.existingLoanStatusDropDownReadPlatformService = existingLoanStatusDropDownReadPlatformService;
        this.clientRepository = clientRepository;
        this.existingLoanRepository = existingLoanRepository;
    }

    @Override
    public ExistingLoanData retriveTemplate() {

        final List<CodeValueData> sourceCvOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.Source_Cv_Option));

        final List<CodeValueData> BureauCvOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.Bureau_Cv_Option));

        final List<CodeValueData> LenderCvOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.Lender_Cv_Option));

        final List<CodeValueData> LoanTypeCvOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.LoanType_Cv_Option));

        final List<CodeValueData> externalLoanPurposeOption = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ExistingLoanApiConstants.ExternalLoan_Purpose_Option));

        final Collection<EnumOptionData> loanTenaureType = this.loanDropdownReadPlatformService.retrieveLoanTermFrequencyTypeOptions();

        final Collection<EnumOptionData> repaymentFrequency = this.loanDropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions();

        final Collection<LoanStatusEnumData> loanStatusOption = this.existingLoanStatusDropDownReadPlatformService.statusTypeOptions();
        return ExistingLoanData.ExistingLoanDataTemplate(sourceCvOption, BureauCvOption, LenderCvOption, LoanTypeCvOption,
                externalLoanPurposeOption, loanTenaureType, repaymentFrequency, loanStatusOption);
    }

    @Override
    public ExistingLoanData retrieveOne(Long clientId, Long existingloanId) {
        this.clientRepository.findOneWithNotFoundDetection(clientId);
        this.existingLoanRepository.findOneWithNotFoundDetection(existingloanId);
        final ExistingLoanMapper rm = new ExistingLoanMapper();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ");
        sqlBuilder.append(rm.schema());
        sqlBuilder.append("where el.client_id= ?");
        sqlBuilder.append(" and  el.id= ?");
        return this.jdbcTemplate.queryForObject(sqlBuilder.toString(), rm, new Object[] { clientId, existingloanId });

    }

    @Override
    public List<ExistingLoanData> retriveAll(Long clientId) {
        this.clientRepository.findOneWithNotFoundDetection(clientId);
        final ExistingLoanMapper rm = new ExistingLoanMapper();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ");
        sqlBuilder.append(rm.schema());
        sqlBuilder.append("where el.client_id= ?");
        return this.jdbcTemplate.query(sqlBuilder.toString(), rm, new Object[] { clientId });
    }

    private static final class ExistingLoanMapper implements RowMapper<ExistingLoanData> {

        private final String schema;

        public ExistingLoanMapper() {
            final StringBuilder builder = new StringBuilder(400);
            builder.append("el.id as id,el.client_id as clientId,el.loan_application_id ");
            builder.append("as loanApplicationId,el.loan_id as loanId ,el.source_cv_id as sourceCvId,cv.code_value as SourceName, ");
            builder.append(" el.bureau_cv_id as bureauCvId,bcv.code_value as bureauName,el.bureau_enq_ref_id as bureauenqrefid , ");
            builder.append("el.lender_cv_id as lendercvid,lcv.code_value as lenderCvName,el.lender_not_listed as lendernotlisted,  ");
            builder.append("el.loanType_cv_id as loanTypecvid,ltc.code_value as loanTypes,el.amount_borrowed as amountBorrowed, ");
            builder.append("el.current_outstanding as currentOutstanding,el.amt_overdue as amtOverdue, ");
            builder.append("el.written_off_amount as writtenOffAmount,el.loan_tenure as loantenure, ");
            builder.append("el.loan_tenure_period_type as loanTenurePeriodType,el.repayment_frequency as repaymentfrequency, ");
            builder.append(" el.repayment_frequency_multiple_of as repaymentfrequencymultipleof,el.installment_amount as installmentAmount, ");
            builder.append("el.external_loan_purpose_cv_id as externalloanpurposecvid ,melp.code_value as externalLoanPurpose, el.loan_status_id as loanStatus, ");
            builder.append("el.disbursed_date as disbursedDate,el.maturity_date as maturityDate,el.gt_0_dpd_3_mths as gt0dpd3mths, ");
            builder.append("el.30_dpd_12_mths as dpd30mths12,el.30_dpd_24_mths as dpd30mths24,el.60_dpd_24_mths as dpd60mths24, ");
            builder.append("el.remark as remark,el.archive as archive,el.createdby_id as createdbyid ,el.lastmodifiedby_id as lastmodifiedbyid, ");
            builder.append("mau .firstname as cretaedbyName,maul.firstname as modifiedByName ");
            builder.append("from f_existing_loan el left join m_code_value cv on cv.id = el.source_cv_id  ");
            builder.append("left join m_code_value bcv on bcv.id =el.bureau_cv_id  ");
            builder.append("left join m_code_value lcv on lcv.id =el.lender_cv_id  ");
            builder.append(" left join m_code_value ltc on ltc.id = el.loanType_cv_id ");
            builder.append("left join m_code_value  melp on melp.id =el.external_loan_purpose_cv_id ");
            builder.append("left join m_appuser  mau on mau.id = el.createdby_id  ");
            builder.append(" left join m_appuser maul on maul.id =el.lastmodifiedby_id ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ExistingLoanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final Long loanId = JdbcSupport.getLong(rs, "loanId");
            final Long loanApplicationId = JdbcSupport.getLong(rs, "loanApplicationId");
            final Long sourceId = JdbcSupport.getLong(rs, "sourceCvId");
            final String sourceName = rs.getString("sourceName");
            final Long bureauId = JdbcSupport.getLong(rs, "bureauCvId");
            final String bureauName = rs.getString("bureauName");
            final Long bureauenqrefid = JdbcSupport.getLong(rs, "bureauenqrefid");
            final Long lendercvid = JdbcSupport.getLong(rs, "lendercvid");
            final String lenderName = rs.getString("lenderCvName");
            final String lenderNotListed = rs.getString("lendernotlisted");
            final Long loanTypeid = JdbcSupport.getLong(rs, "loanTypecvid");
            final String LoanType = rs.getString("loanTypes");
            final BigDecimal amountBorrowed = rs.getBigDecimal("amountBorrowed");
            final BigDecimal amountOutstanding = rs.getBigDecimal("currentOutstanding");
            final BigDecimal writtenOffAmount = rs.getBigDecimal("writtenOffAmount");
            final BigDecimal amtOverdue = rs.getBigDecimal("amtOverdue");
            final Integer loantenurePeriodTypeId = JdbcSupport.getInteger(rs, "loantenure");
            // to be discuss
            final Integer loantenure = JdbcSupport.getInteger(rs, "loanTenurePeriodType");
            EnumOptionData loanTenurePeriodType = null;
            if (loantenurePeriodTypeId != null) {
                loanTenurePeriodType = LoanEnumerations.termFrequencyType(loantenurePeriodTypeId);
            }
            final Integer repaymentFrequencyTypeInt = JdbcSupport.getInteger(rs, "repaymentfrequency");
            EnumOptionData repaymentFrequency = null;
            if (repaymentFrequencyTypeInt != null) {
                repaymentFrequency = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeInt);
            }
            final Integer repaymentFrequencyMultipleOf = JdbcSupport.getInteger(rs, "repaymentfrequencymultipleof");

            final BigDecimal installAmount = rs.getBigDecimal("installmentAmount");
            final Integer externalloanpurposecvid = JdbcSupport.getInteger(rs, "externalloanpurposecvid");
            final String externalLoanPurposeCvName = rs.getString("externalLoanPurpose");

            final Integer loanstatusId = JdbcSupport.getInteger(rs, "loanStatus");
            LoanStatusEnumData status = null;
            if (loanstatusId != null) {
                status = LoanEnumerations.status(loanstatusId);
            }
            final LocalDate disbursedDate = JdbcSupport.getLocalDate(rs, "disbursedDate");
            final LocalDate maturityDate = JdbcSupport.getLocalDate(rs, "maturityDate");
            final String createdByName = rs.getString("cretaedbyName");
            final String modifiedByName = rs.getString("modifiedByName");
            final ExistingLoanTimelineData timeline = new ExistingLoanTimelineData(disbursedDate, maturityDate, createdByName,
                    modifiedByName);

            final Integer gt0Dpd3Mths = JdbcSupport.getInteger(rs, "gt0dpd3mths");
            final Integer dpd30Mths12 = JdbcSupport.getInteger(rs, "dpd30mths12");
            final Integer dpd30Mths24 = JdbcSupport.getInteger(rs, "dpd30mths24");
            final Integer dpd60Mths24 = JdbcSupport.getInteger(rs, "dpd60mths24");

            final Integer archive = JdbcSupport.getInteger(rs, "archive");
            final String remark = rs.getString("remark");

            return ExistingLoanData.ExistingLoanDataDetails(id, clientId, loanApplicationId, loanId, status, sourceId, sourceName,
                    bureauId, bureauName, bureauenqrefid, lendercvid, lenderName, lenderNotListed, loanTypeid, LoanType, amountBorrowed,
                    amountOutstanding, amtOverdue, writtenOffAmount, loanTenurePeriodType, loantenure, repaymentFrequencyMultipleOf,
                    repaymentFrequency, installAmount, externalloanpurposecvid, externalLoanPurposeCvName, gt0Dpd3Mths, dpd30Mths12,
                    dpd30Mths24, dpd60Mths24, remark, archive, timeline);

        }
    }

}
