package com.finflux.loanapplicationreference.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.accountdetails.service.AccountEnumerations;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.data.LoanApplicationChargeData;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceData;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceStatus;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceTemplateData;
import com.finflux.loanapplicationreference.data.LoanApplicationSanctionData;
import com.finflux.loanapplicationreference.data.LoanApplicationSanctionTrancheData;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepositoryWrapper;

@Service
public class LoanApplicationReferenceReadPlatformServiceImpl implements LoanApplicationReferenceReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanApplicationReferenceDataMapper dataMapper;
    private final LoanApplicationChargeDataMapper chargeDataMapper;
    private final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;

    @Autowired
    public LoanApplicationReferenceReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.dataMapper = new LoanApplicationReferenceDataMapper();
        this.chargeDataMapper = new LoanApplicationChargeDataMapper();
        this.loanApplicationReferenceRepository = loanApplicationReferenceRepository;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
    }

    @Override
    public LoanApplicationReferenceTemplateData templateData(final boolean onlyActive) {
        final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup(onlyActive);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        return LoanApplicationReferenceTemplateData.template(productOptions, paymentOptions);
    }

    @Override
    public Collection<LoanApplicationReferenceData> retrieveAll(final Long clientId) {
        try {
            String sql = "SELECT " + this.dataMapper.schema();
            if (clientId != null) {
                sql += " WHERE lar.client_id = " + clientId;
            }
            return this.jdbcTemplate.query(sql, this.dataMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public LoanApplicationReferenceData retrieveOne(final Long loanApplicationReferenceId) {
        try {
            final String sql = "SELECT " + this.dataMapper.schema() + " WHERE lar.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { loanApplicationReferenceId });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<LoanApplicationChargeData> retrieveChargesByLoanAppRefId(final Long loanApplicationReferenceId) {
        try {
            final String sql = "SELECT " + this.chargeDataMapper.schema() + " WHERE lac.loan_app_ref_id = ? ";
            return this.jdbcTemplate.query(sql, this.chargeDataMapper, new Object[] { loanApplicationReferenceId });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class LoanApplicationReferenceDataMapper implements RowMapper<LoanApplicationReferenceData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public LoanApplicationReferenceDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("lar.id AS loanApplicationReferenceId ");
            sqlBuilder.append(",lar.loan_application_reference_no AS loanApplicationReferenceNo ");
            sqlBuilder.append(",lar.external_id_one AS externalIdOne ");
            sqlBuilder.append(",lar.external_id_two AS externalIdTwo ");
            sqlBuilder.append(",lar.loan_id AS loanId ");
            sqlBuilder.append(",lar.client_id AS clientId ");
            sqlBuilder.append(",lar.loan_officer_id AS loanOfficerId ");
            sqlBuilder.append(",lar.group_id AS groupId ");
            sqlBuilder.append(",sf.display_name AS loanOfficerName ");
            sqlBuilder.append(",lar.status_enum AS statusEnum ");
            sqlBuilder.append(",lar.account_type_enum AS accountTypeEnum ");
            sqlBuilder.append(",lar.loan_product_id AS loanProductId ");
            sqlBuilder.append(",lar.loan_purpose_id AS loanPurposeId ");
            sqlBuilder.append(",lp.name AS loanProductName ");
            sqlBuilder.append(",loanPurpose.name AS loanPurposeName ");
            sqlBuilder.append(",lar.loan_amount_requested AS loanAmountRequested ");
            sqlBuilder.append(",lar.number_of_repayments AS numberOfRepayments ");
            sqlBuilder.append(",lar.repayment_period_frequency_enum AS repaymentPeriodFrequencyEnum ");
            sqlBuilder.append(",lar.repay_every AS repayEvery ");
            sqlBuilder.append(",lar.term_period_frequency_enum AS termPeriodFrequencyEnum ");
            sqlBuilder.append(",lar.term_frequency AS termFrequency ");
            sqlBuilder.append(",lar.fixed_emi_amount AS fixedEmiAmount ");
            sqlBuilder.append(",lar.no_of_tranche AS noOfTranche ");
            sqlBuilder.append(",lar.submittedon_date AS submittedOnDate ");
            sqlBuilder.append(",lar.expected_disbursal_payment_type_id as expectedDisbursalPaymentTypeId, pt_disburse.value as disbursementPaymentTypeName ");
            sqlBuilder.append(",lar.expected_repayment_payment_type_id as expectedRepaymentPaymentTypeId, pt_repayment.value as repaymenPaymentTypeName ");
            sqlBuilder.append("FROM f_loan_application_reference lar ");
            sqlBuilder.append("INNER JOIN m_product_loan lp ON lp.id = lar.loan_product_id ");
            sqlBuilder.append("LEFT JOIN m_staff sf ON sf.id = lar.loan_officer_id ");
            sqlBuilder.append("LEFT JOIN f_loan_purpose loanPurpose ON loanPurpose.id = lar.loan_purpose_id ");
            sqlBuilder.append("LEFT JOIN m_payment_type pt_disburse ON pt_disburse.id = lar.expected_disbursal_payment_type_id ");
            sqlBuilder.append(" LEFT JOIN m_payment_type pt_repayment ON pt_repayment.id = lar.expected_repayment_payment_type_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public LoanApplicationReferenceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long loanApplicationReferenceId = JdbcSupport.getLongActualValue(rs, "loanApplicationReferenceId");
            final String loanApplicationReferenceNo = rs.getString("loanApplicationReferenceNo");
            final String externalIdOne = rs.getString("externalIdOne");
            final String externalIdTwo = rs.getString("externalIdTwo");
            final Long loanId = JdbcSupport.getLongActualValue(rs, "loanId");
            final Long clientId = JdbcSupport.getLongActualValue(rs, "clientId");
            final Long loanOfficerId = JdbcSupport.getLongActualValue(rs, "loanOfficerId");
            final String loanOfficerName = rs.getString("loanOfficerName");
            final Long groupId = JdbcSupport.getLongActualValue(rs, "groupId");
            final Integer statusEnum = JdbcSupport.getIntegeActualValue(rs, "statusEnum");
            final Integer accountTypeEnum = JdbcSupport.getIntegeActualValue(rs, "accountTypeEnum");
            final EnumOptionData status = LoanApplicationReferenceStatus.loanApplicationReferenceStatus(statusEnum);
            final EnumOptionData accountType = AccountEnumerations.loanType(accountTypeEnum);
            final Long loanProductId = JdbcSupport.getLongActualValue(rs, "loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final Long loanPurposeId = JdbcSupport.getLongActualValue(rs, "loanPurposeId");
            final String loanPurposeName = rs.getString("loanPurposeName");
            final CodeValueData loanPurpose = CodeValueData.instance(loanPurposeId, loanPurposeName);
            final BigDecimal loanAmountRequested = rs.getBigDecimal("loanAmountRequested");
            final Integer numberOfRepayments = JdbcSupport.getIntegeActualValue(rs, "numberOfRepayments");
            final Integer repaymentPeriodFrequencyEnum = JdbcSupport.getIntegeActualValue(rs, "repaymentPeriodFrequencyEnum");
            final EnumOptionData repaymentPeriodFrequency = PeriodFrequencyType.periodFrequencyType(repaymentPeriodFrequencyEnum);
            final Integer repayEvery = JdbcSupport.getIntegeActualValue(rs, "repayEvery");
            final Integer termPeriodFrequencyEnum = JdbcSupport.getIntegeActualValue(rs, "termPeriodFrequencyEnum");
            final EnumOptionData termPeriodFrequency = PeriodFrequencyType.periodFrequencyType(termPeriodFrequencyEnum);
            final Integer termFrequency = JdbcSupport.getIntegeActualValue(rs, "termFrequency");
            final BigDecimal fixedEmiAmount = rs.getBigDecimal("fixedEmiAmount");
            final Integer noOfTranche = JdbcSupport.getIntegeActualValue(rs, "noOfTranche");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
			PaymentTypeData expectedDisbursalPaymentType = null;
			final Integer expectedDisbursalPaymentTypeId = JdbcSupport.getInteger(rs,"expectedDisbursalPaymentTypeId");
			if (expectedDisbursalPaymentTypeId != null) {
				final String disbursementPaymentTypeName = rs.getString("disbursementPaymentTypeName");
				expectedDisbursalPaymentType = PaymentTypeData.instance(expectedDisbursalPaymentTypeId.longValue(),
						disbursementPaymentTypeName);
			}
			PaymentTypeData expectedRepaymentPaymentType = null;
			final Integer expectedRepaymentPaymentTypeId = JdbcSupport.getInteger(rs,"expectedRepaymentPaymentTypeId");
			if (expectedRepaymentPaymentTypeId != null) {
				final String repaymenPaymentTypeName = rs.getString("repaymenPaymentTypeName");
				expectedRepaymentPaymentType = PaymentTypeData.instance(expectedRepaymentPaymentTypeId.longValue(),
						repaymenPaymentTypeName);
			} 

            return LoanApplicationReferenceData.instance(loanApplicationReferenceId, loanApplicationReferenceNo, externalIdOne,
                    externalIdTwo, loanId, clientId, loanOfficerId, loanOfficerName, groupId, status, accountType, loanProductId,
                    loanProductName, loanPurposeId, loanPurpose, loanAmountRequested, numberOfRepayments, repaymentPeriodFrequency,
                    repayEvery, termPeriodFrequency, termFrequency, fixedEmiAmount, noOfTranche, submittedOnDate, 
                    expectedDisbursalPaymentType, expectedRepaymentPaymentType);
        }
    }

    private static final class LoanApplicationChargeDataMapper implements RowMapper<LoanApplicationChargeData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public LoanApplicationChargeDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("lac.id AS loanAppChargeId ");
            sqlBuilder.append(",lac.loan_app_ref_id AS loanApplicationReferenceId ");
            sqlBuilder.append(",lac.charge_id AS chargeId ");
            sqlBuilder.append(",lac.due_for_collection_as_of_date AS dueDate ");
            sqlBuilder.append(",lac.charge_amount_or_percentage AS amount ");
            sqlBuilder.append(",lac.is_mandatory AS isMandatory ");
            sqlBuilder.append("FROM f_loan_application_charge lac ");
            sqlBuilder.append("INNER JOIN f_loan_application_reference lar ON lar.id = lac.loan_app_ref_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public LoanApplicationChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long loanAppChargeId = JdbcSupport.getLongActualValue(rs, "loanAppChargeId");
            final Long loanApplicationReferenceId = JdbcSupport.getLongActualValue(rs, "loanApplicationReferenceId");
            final Long chargeId = JdbcSupport.getLongActualValue(rs, "chargeId");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Boolean isMandatory = rs.getBoolean("isMandatory");
            return LoanApplicationChargeData.instance(loanAppChargeId, loanApplicationReferenceId, chargeId, dueDate, amount, isMandatory);
        }
    }

    @Override
    public LoanApplicationSanctionData retrieveSanctionDataByLoanAppRefId(final Long loanApplicationReferenceId) {
        try {
            final LoanApplicationReference loanApplicationReference = this.loanApplicationReferenceRepository
                    .findOneWithNotFoundDetection(loanApplicationReferenceId);
            Collection<LoanApplicationSanctionTrancheData> loanApplicationSanctionTrancheDatas = null;
            if (loanApplicationReference.getNoOfTranche() != null && loanApplicationReference.getNoOfTranche() > 0) {
                final LoanApplicationSanctionTrancheDataMapper sanctionTrancheDataMapper = new LoanApplicationSanctionTrancheDataMapper();
                final String sanctionTrancheSql = "SELECT " + sanctionTrancheDataMapper.schema() + " WHERE lar.id = "
                        + loanApplicationReferenceId;
                loanApplicationSanctionTrancheDatas = this.jdbcTemplate.query(sanctionTrancheSql, sanctionTrancheDataMapper);
            }
            final LoanApplicationSanctionDataMapper sanctionDataMapper = new LoanApplicationSanctionDataMapper(
                    loanApplicationSanctionTrancheDatas);

            final String sanctionSql = "SELECT " + sanctionDataMapper.schema() + " WHERE lar.id = " + loanApplicationReferenceId;

            return this.jdbcTemplate.queryForObject(sanctionSql, sanctionDataMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class LoanApplicationSanctionDataMapper implements RowMapper<LoanApplicationSanctionData> {

        private final String schemaSql;
        private final Collection<LoanApplicationSanctionTrancheData> loanApplicationSanctionTrancheDatas;

        public String schema() {
            return this.schemaSql;
        }

        public LoanApplicationSanctionDataMapper(final Collection<LoanApplicationSanctionTrancheData> loanApplicationSanctionTrancheDatas) {
            this.loanApplicationSanctionTrancheDatas = loanApplicationSanctionTrancheDatas;
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("las.id AS loanAppSanctionId ");
            sqlBuilder.append(",las.loan_app_ref_id AS loanApplicationReferenceId ");
            sqlBuilder.append(",las.loan_amount_approved AS loanAmountApproved ");
            sqlBuilder.append(",las.approvedon_date AS approvedOnDate ");
            sqlBuilder.append(",las.expected_disbursement_date AS expectedDisbursementDate ");
            sqlBuilder.append(",las.repayments_starting_from_date AS repaymentsStartingFromDate ");
            sqlBuilder.append(",las.number_of_repayments AS numberOfRepayments ");
            sqlBuilder.append(",las.repayment_period_frequency_enum AS repaymentPeriodFrequencyEnum ");
            sqlBuilder.append(",las.repay_every AS repayEvery ");
            sqlBuilder.append(",las.term_period_frequency_enum AS termPeriodFrequencyEnum ");
            sqlBuilder.append(",las.term_frequency AS termFrequency ");
            sqlBuilder.append(",las.fixed_emi_amount AS fixedEmiAmount ");
            sqlBuilder.append(",las.max_outstanding_loan_balance AS maxOutstandingLoanBalance ");
            sqlBuilder.append("FROM f_loan_application_sanction las ");
            sqlBuilder.append("INNER JOIN f_loan_application_reference lar ON lar.id = las.loan_app_ref_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public LoanApplicationSanctionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long loanAppSanctionId = JdbcSupport.getLongActualValue(rs, "loanAppSanctionId");
            final Long loanApplicationReferenceId = JdbcSupport.getLongActualValue(rs, "loanApplicationReferenceId");
            final BigDecimal loanAmountApproved = rs.getBigDecimal("loanAmountApproved");
            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedOnDate");
            final LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedDisbursementDate");
            final LocalDate repaymentsStartingFromDate = JdbcSupport.getLocalDate(rs, "repaymentsStartingFromDate");
            final Integer numberOfRepayments = JdbcSupport.getIntegeActualValue(rs, "numberOfRepayments");
            final Integer repaymentPeriodFrequencyEnum = JdbcSupport.getIntegeActualValue(rs, "repaymentPeriodFrequencyEnum");
            final EnumOptionData repaymentPeriodFrequency = PeriodFrequencyType.periodFrequencyType(repaymentPeriodFrequencyEnum);
            final Integer repayEvery = JdbcSupport.getIntegeActualValue(rs, "repayEvery");
            final Integer termPeriodFrequencyEnum = JdbcSupport.getIntegeActualValue(rs, "termPeriodFrequencyEnum");
            final EnumOptionData termPeriodFrequency = PeriodFrequencyType.periodFrequencyType(termPeriodFrequencyEnum);
            final Integer termFrequency = JdbcSupport.getIntegeActualValue(rs, "termFrequency");
            final BigDecimal fixedEmiAmount = rs.getBigDecimal("fixedEmiAmount");
            final BigDecimal maxOutstandingLoanBalance = rs.getBigDecimal("maxOutstandingLoanBalance");
            return LoanApplicationSanctionData
                    .instance(loanAppSanctionId, loanApplicationReferenceId, loanAmountApproved, approvedOnDate, expectedDisbursementDate,
                            repaymentsStartingFromDate, numberOfRepayments, repaymentPeriodFrequency, repayEvery, termPeriodFrequency,
                            termFrequency, fixedEmiAmount, maxOutstandingLoanBalance, this.loanApplicationSanctionTrancheDatas);
        }
    }

    private static final class LoanApplicationSanctionTrancheDataMapper implements RowMapper<LoanApplicationSanctionTrancheData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public LoanApplicationSanctionTrancheDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("last.id AS loanAppSanctionTrancheId ");
            sqlBuilder.append(",last.loan_app_sanction_id AS loanAppSanctionId ");
            sqlBuilder.append(",last.tranche_amount AS trancheAmount ");
            sqlBuilder.append(",last.fixed_emi_amount AS fixedEmiAmount ");
            sqlBuilder.append(",last.expected_tranche_disbursement_date AS expectedTrancheDisbursementDate ");
            sqlBuilder.append("FROM f_loan_app_sanction_tranche last ");
            sqlBuilder.append("INNER JOIN f_loan_application_sanction las ON las.id = last.loan_app_sanction_id ");
            sqlBuilder.append("INNER JOIN f_loan_application_reference lar ON lar.id = las.loan_app_ref_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public LoanApplicationSanctionTrancheData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Long loanAppSanctionTrancheId = JdbcSupport.getLongActualValue(rs, "loanAppSanctionTrancheId");
            final Long loanAppSanctionId = JdbcSupport.getLongActualValue(rs, "loanAppSanctionId");
            final BigDecimal trancheAmount = rs.getBigDecimal("trancheAmount");
            final BigDecimal fixedEmiAmount = rs.getBigDecimal("fixedEmiAmount");
            final LocalDate expectedTrancheDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedTrancheDisbursementDate");
            return LoanApplicationSanctionTrancheData.instance(loanAppSanctionTrancheId, loanAppSanctionId, trancheAmount, fixedEmiAmount,
                    expectedTrancheDisbursementDate);
        }
    }
}
