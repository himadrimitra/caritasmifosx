package com.finflux.loanapplicationreference.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.finflux.loanapplicationreference.data.*;
import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.accountdetails.service.AccountEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.fingerprint.data.FingerPrintData;
import com.finflux.fingerprint.services.FingerPrintReadPlatformServices;
import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;
import com.finflux.infrastructure.external.authentication.service.ExternalAuthenticationServicesReadPlatformService;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepositoryWrapper;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticaionTransactionTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationReadPlatformService;

@Service
public class LoanApplicationReferenceReadPlatformServiceImpl implements LoanApplicationReferenceReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanApplicationReferenceDataMapper dataMapper;
    private final LoanApplicationChargeDataMapper chargeDataMapper;
    private final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService;
    private final FingerPrintReadPlatformServices fingerPrintReadPlatformServices;
    private final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final LoanApplicationReferenceDataForLookUpMapper dataLookUpMapper;

    @Autowired
    public LoanApplicationReferenceReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService,
            final FingerPrintReadPlatformServices fingerPrintReadPlatformServices,
            final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.dataMapper = new LoanApplicationReferenceDataMapper();
        this.chargeDataMapper = new LoanApplicationChargeDataMapper();
        this.loanApplicationReferenceRepository = loanApplicationReferenceRepository;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.transactionAuthenticationReadPlatformService = transactionAuthenticationReadPlatformService;
        this.fingerPrintReadPlatformServices = fingerPrintReadPlatformServices;
        this.externalAuthenticationServicesReadPlatformService = externalAuthenticationServicesReadPlatformService;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.dataLookUpMapper = new LoanApplicationReferenceDataForLookUpMapper();
    }

    @Override
    public LoanApplicationReferenceTemplateData templateData(final boolean onlyActive, final Integer productApplicableForLoanType,
            final Integer entityType, final Long entityId) {
        final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup(onlyActive,
                productApplicableForLoanType, entityType, entityId);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        return LoanApplicationReferenceTemplateData.template(productOptions, paymentOptions);
    }

    @Override
    public LoanApplicationReferenceTemplateData templateData(final boolean onlyActive, final Long loanApplicationReferenceId,
            final Integer productApplicableForLoanType, final Integer entityType, final Long entityId) {
        final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup(onlyActive,
                productApplicableForLoanType, entityType, entityId);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        Map<String, Object> data = retrieveLoanProductIdApprovedAmountClientId(loanApplicationReferenceId);
        Long productId = (Long) data.get("productId");
        BigDecimal approvedPrincipal = (BigDecimal) data.get("approvedAmount");
        Long clientId = (Long) data.get("clientId");
        Collection<FingerPrintData> fingerPrintData = null;
        Collection<TransactionAuthenticationData> transactionAuthenticationOptions = null;
        if (clientId != null) {
            transactionAuthenticationOptions = this.transactionAuthenticationReadPlatformService
                    .retiveTransactionAuthenticationDetailsForTemplate(SupportedAuthenticationPortfolioTypes.LOANS.getValue(),
                            SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue(), approvedPrincipal, loanApplicationReferenceId,
                            productId);
            final Collection<ExternalAuthenticationServiceData> externalServices = this.externalAuthenticationServicesReadPlatformService
                    .getOnlyActiveExternalAuthenticationServices();
            if (externalServices.size() > 0 && !externalServices.isEmpty()) {
                for (ExternalAuthenticationServiceData services : externalServices) {
                    if (services.getName().contains("Fingerprint Auth")) {
                        if (services.isActive()) {
                            fingerPrintData = this.fingerPrintReadPlatformServices.retriveFingerPrintData(clientId);
                        }
                    }
                }
            }
        }
        return LoanApplicationReferenceTemplateData.template(productOptions, paymentOptions, transactionAuthenticationOptions,
                fingerPrintData);
    }
    
    @Override
    public Collection<LoanApplicationReferenceData> retrieveAll(final Long clientId) {
        try {
            StringBuilder sql = new StringBuilder();

            if (clientId != null) {
                sql.append("SELECT IF(ISNULL(coapp.client_id), false, true) as isCoApplicant, " + this.dataLookUpMapper.schema());
                sql.append(
                        " LEFT JOIN f_loan_coapplicants_mapping coapp ON coapp.loan_application_reference_id = lar.id AND lar.client_id <> :clientId");
                sql.append(" WHERE lar.client_id = :clientId  OR coapp.client_id = :clientId");

            } else {
                sql.append("SELECT false as isCoApplicant, " + this.dataLookUpMapper.schema());
            }
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("clientId", clientId);

            return this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, this.dataLookUpMapper);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public LoanApplicationReferenceData retrieveOne(final Long loanApplicationReferenceId) {
        try {
            final String sql = "SELECT false as isCoApplicant, " + this.dataMapper.schema() + " WHERE lar.id = ? ";
			
            return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { loanApplicationReferenceId });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    };

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
            sqlBuilder.append(",cl.display_name AS clientName ");
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
            sqlBuilder.append(",lep.id as lepid, lep.loan_product_id as leploanProductId, ");
            sqlBuilder.append("lep.repay_every as leprepaymentEvery, lep.repayment_period_frequency_enum as leprepaymentFrequencyTypeEnum, ");
            sqlBuilder.append("lep.number_of_repayments as lepnumberOfRepayments, lep.sanction_amount as lepsanctionAmount, ");
            sqlBuilder.append("lep.fixed_emi as lepfixedEmi, lep.disbursal_1_amount as lepdisbursalAmount1, ");
            sqlBuilder.append("lep.disbursal_2_amount as lepdisbursalAmount2, lep.disbursal_3_amount as lepdisbursalAmount3, ");
            sqlBuilder.append("lep.disbursal_4_amount as lepdisbursalAmount4, lep.disbursal_2_emi as lepdisbursalEmi2, ");
            sqlBuilder.append("lep.disbursal_3_emi as lepdisbursalEmi3, lep.disbursal_4_emi as lepdisbursalEmi4 ");
            sqlBuilder.append(", if(cblpm.stale_period is null,0,cblpm.stale_period) as stalePeriod ");
            sqlBuilder.append(", cbe.created_date as initiatedDate ");
            sqlBuilder.append(", if(cblpom.loan_product_id is null, false , if(cblpm.is_active is null, false,cblpm.is_active)) as isCreditBureauProduct ");
            sqlBuilder.append("FROM f_loan_application_reference lar ");
            sqlBuilder.append("INNER JOIN m_product_loan lp ON lp.id = lar.loan_product_id ");
            sqlBuilder.append("LEFT JOIN m_client cl ON cl.id = lar.client_id ");
            sqlBuilder.append("LEFT JOIN m_staff sf ON sf.id = lar.loan_officer_id ");
            sqlBuilder.append("LEFT JOIN f_loan_purpose loanPurpose ON loanPurpose.id = lar.loan_purpose_id ");
            sqlBuilder.append("LEFT JOIN m_payment_type pt_disburse ON pt_disburse.id = lar.expected_disbursal_payment_type_id ");
            sqlBuilder.append(" LEFT JOIN m_payment_type pt_repayment ON pt_repayment.id = lar.expected_repayment_payment_type_id ");
            sqlBuilder.append(" LEFT JOIN f_loan_emi_packs lep ON lar.loan_emi_pack_id = lep.id ");
            sqlBuilder.append(" LEFT join f_creditbureau_loanproduct_office_mapping cblpom on cblpom.loan_product_id= lp.id ");
			sqlBuilder.append(
					" and cblpom.id = case when cl.office_id = (select m.office_id from f_creditbureau_loanproduct_office_mapping m where m.loan_product_id = lp.id and m.office_id = cl.office_id) then (select m.id from f_creditbureau_loanproduct_office_mapping m where m.loan_product_id = lp.id and m.office_id = cl.office_id) else (select m.id from f_creditbureau_loanproduct_office_mapping m where m.loan_product_id = lp.id and m.office_id is null) end ");
            sqlBuilder.append(" LEFT JOIN f_creditbureau_loanproduct_mapping cblpm ON cblpm.id = cblpom.credit_bureau_loan_product_mapping_id ");
            sqlBuilder.append(" LEFT JOIN f_loan_creditbureau_enquiry lcbe ON lcbe.loan_application_id = lar.id ");
            sqlBuilder.append(" LEFT JOIN f_creditbureau_enquiry cbe ON cbe.id = lcbe.creditbureau_enquiry_id ");
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
            final String clientName = rs.getString("clientName");
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
            final Boolean isCoApplicant = rs.getBoolean("isCoApplicant");
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

            LoanEMIPackData loanEMIPackData = null;
            final Long lepid = JdbcSupport.getLong(rs, "lepid");
            if(lepid != null){
                final Long leploanProductId = JdbcSupport.getLong(rs, "leploanProductId");
                final Integer leprepaymentEvery = JdbcSupport.getInteger(rs, "leprepaymentEvery");
                final Integer leprepaymentFrequencyTypeEnum = JdbcSupport.getInteger(rs, "leprepaymentFrequencyTypeEnum");
                final EnumOptionData leprepaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(leprepaymentFrequencyTypeEnum);
                final Integer lepnumberOfRepayments = JdbcSupport.getInteger(rs, "lepnumberOfRepayments");
                final BigDecimal lepsanctionAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepsanctionAmount");
                final BigDecimal lepfixedEmi = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepfixedEmi");
                final BigDecimal lepdisbursalAmount1  = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount1");
                final BigDecimal lepdisbursalAmount2 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount2");
                final BigDecimal lepdisbursalAmount3 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount3");
                final BigDecimal lepdisbursalAmount4 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount4");
                final Integer lepdisbursalEmi2 = JdbcSupport.getInteger(rs, "lepdisbursalEmi2");
                final Integer lepdisbursalEmi3 = JdbcSupport.getInteger(rs, "lepdisbursalEmi3");
                final Integer lepdisbursalEmi4 = JdbcSupport.getInteger(rs, "lepdisbursalEmi4");
                final String leploanProductName = null;

                loanEMIPackData = LoanEMIPackData.loanEMIPackData(lepid,
                        leploanProductId,
                        leprepaymentEvery,
                        leprepaymentFrequencyType,
                        lepnumberOfRepayments,
                        lepsanctionAmount,
                        lepfixedEmi,
                        lepdisbursalAmount1,
                        lepdisbursalAmount2,
                        lepdisbursalAmount3,
                        lepdisbursalAmount4,
                        lepdisbursalEmi2,
                        lepdisbursalEmi3,
                        lepdisbursalEmi4,
                        leploanProductName);
            }
            final Integer stalePeriod = JdbcSupport.getIntegeActualValue(rs, "stalePeriod");
            final LocalDate initiatedDate = JdbcSupport.getLocalDate(rs, "initiatedDate");
            boolean isStalePeriodExceeded = false;
            if(initiatedDate != null){
                isStalePeriodExceeded = initiatedDate.plusDays(stalePeriod).isBefore(DateUtils.getLocalDateOfTenant());
            }      
            final Boolean isCreditBureauProduct = rs.getBoolean("isCreditBureauProduct");
            return LoanApplicationReferenceData.instance(loanApplicationReferenceId, loanApplicationReferenceNo, externalIdOne,
                    externalIdTwo, loanId, clientId, loanOfficerId, loanOfficerName, groupId, status, accountType, loanProductId,
                    loanProductName, loanPurposeId, loanPurpose, loanAmountRequested, numberOfRepayments, repaymentPeriodFrequency,
                    repayEvery, termPeriodFrequency, termFrequency, fixedEmiAmount, noOfTranche, submittedOnDate, 
                    expectedDisbursalPaymentType, expectedRepaymentPaymentType, loanEMIPackData, isCoApplicant, clientName, isStalePeriodExceeded, isCreditBureauProduct);
        }
    }
    
    private static final class LoanApplicationReferenceDataForLookUpMapper implements RowMapper<LoanApplicationReferenceData> {


        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public LoanApplicationReferenceDataForLookUpMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("lar.id AS loanApplicationReferenceId, ");
            sqlBuilder.append("lar.external_id_one AS externalIdOne, ");
            sqlBuilder.append("lar.loan_id AS loanId, ");
            sqlBuilder.append("lar.loan_application_reference_no AS loanApplicationReferenceNo, ");
            sqlBuilder.append("lar.status_enum AS statusEnum, ");
            sqlBuilder.append("lar.account_type_enum AS accountTypeEnum, ");
            sqlBuilder.append("lar.loan_product_id AS loanProductId, ");
            sqlBuilder.append("lp.name AS loanProductName, ");
            sqlBuilder.append("(select  (if(cblpom.loan_product_id is null, false , if(cblpm.is_active is null, false,cblpm.is_active))) from f_creditbureau_loanproduct_office_mapping cblpom  LEFT join f_creditbureau_loanproduct_mapping cblpm on cblpm.id=cblpom.credit_bureau_loan_product_mapping_id where cblpom.loan_product_id = lp.id and cblpom.id = case when cl.office_id = (select m.office_id from f_creditbureau_loanproduct_office_mapping m where m.loan_product_id = lp.id and m.office_id = cl.office_id) then (select m.id from f_creditbureau_loanproduct_office_mapping m where m.loan_product_id = lp.id and m.office_id = cl.office_id) else (select m.id from f_creditbureau_loanproduct_office_mapping m where m.loan_product_id = lp.id and m.office_id is null) end )  as isCreditBureauProduct, ");
            sqlBuilder.append("lar.loan_amount_requested AS loanAmountRequested ");
            sqlBuilder.append("FROM f_loan_application_reference lar ");
            sqlBuilder.append("INNER JOIN m_product_loan lp ON lp.id = lar.loan_product_id ");
            sqlBuilder.append(" LEFT JOIN m_client cl ON cl.id = lar.client_id ");
          
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public LoanApplicationReferenceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long loanApplicationReferenceId = JdbcSupport.getLongActualValue(rs, "loanApplicationReferenceId");
            final String loanApplicationReferenceNo = rs.getString("loanApplicationReferenceNo");
            final String externalIdOne = rs.getString("externalIdOne");
            final Long loanId = JdbcSupport.getLongActualValue(rs, "loanId");
            
            final Integer statusEnum = JdbcSupport.getIntegeActualValue(rs, "statusEnum");
            final Integer accountTypeEnum = JdbcSupport.getIntegeActualValue(rs, "accountTypeEnum");
            final EnumOptionData status = LoanApplicationReferenceStatus.loanApplicationReferenceStatus(statusEnum);
            final EnumOptionData accountType = AccountEnumerations.loanType(accountTypeEnum);
            final Long loanProductId = JdbcSupport.getLongActualValue(rs, "loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final BigDecimal loanAmountRequested = rs.getBigDecimal("loanAmountRequested");
            final Boolean isCoApplicant = rs.getBoolean("isCoApplicant");
            final Boolean isCreditBureauProduct = rs.getBoolean("isCreditBureauProduct");
            
            return LoanApplicationReferenceData.forLookUp(loanApplicationReferenceId, loanApplicationReferenceNo, 
                    externalIdOne, loanId, accountType, status, loanProductId, loanProductName, loanAmountRequested, isCoApplicant, isCreditBureauProduct);
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
            sqlBuilder.append(",lep.id as lepid, lep.loan_product_id as leploanProductId, ");
            sqlBuilder.append("lep.repay_every as leprepaymentEvery, lep.repayment_period_frequency_enum as leprepaymentFrequencyTypeEnum, ");
            sqlBuilder.append("lep.number_of_repayments as lepnumberOfRepayments, lep.sanction_amount as lepsanctionAmount, ");
            sqlBuilder.append("lep.fixed_emi as lepfixedEmi, lep.disbursal_1_amount as lepdisbursalAmount1, ");
            sqlBuilder.append("lep.disbursal_2_amount as lepdisbursalAmount2, lep.disbursal_3_amount as lepdisbursalAmount3, ");
            sqlBuilder.append("lep.disbursal_4_amount as lepdisbursalAmount4, lep.disbursal_2_emi as lepdisbursalEmi2, ");
            sqlBuilder.append("lep.disbursal_3_emi as lepdisbursalEmi3, lep.disbursal_4_emi as lepdisbursalEmi4 ");
            sqlBuilder.append("FROM f_loan_application_sanction las ");
            sqlBuilder.append("INNER JOIN f_loan_application_reference lar ON lar.id = las.loan_app_ref_id ");
            sqlBuilder.append(" LEFT JOIN f_loan_emi_packs lep ON las.loan_emi_pack_id = lep.id ");
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

            LoanEMIPackData loanEMIPackData = null;
            final Long lepid = JdbcSupport.getLong(rs, "lepid");
            if(lepid != null){
                final Long leploanProductId = JdbcSupport.getLong(rs, "leploanProductId");
                final Integer leprepaymentEvery = JdbcSupport.getInteger(rs, "leprepaymentEvery");
                final Integer leprepaymentFrequencyTypeEnum = JdbcSupport.getInteger(rs, "leprepaymentFrequencyTypeEnum");
                final EnumOptionData leprepaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(leprepaymentFrequencyTypeEnum);
                final Integer lepnumberOfRepayments = JdbcSupport.getInteger(rs, "lepnumberOfRepayments");
                final BigDecimal lepsanctionAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepsanctionAmount");
                final BigDecimal lepfixedEmi = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepfixedEmi");
                final BigDecimal lepdisbursalAmount1  = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount1");
                final BigDecimal lepdisbursalAmount2 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount2");
                final BigDecimal lepdisbursalAmount3 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount3");
                final BigDecimal lepdisbursalAmount4 = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "lepdisbursalAmount4");
                final Integer lepdisbursalEmi2 = JdbcSupport.getInteger(rs, "lepdisbursalEmi2");
                final Integer lepdisbursalEmi3 = JdbcSupport.getInteger(rs, "lepdisbursalEmi3");
                final Integer lepdisbursalEmi4 = JdbcSupport.getInteger(rs, "lepdisbursalEmi4");
                final String leploanProductName = null;

                loanEMIPackData = LoanEMIPackData.loanEMIPackData(lepid,
                        leploanProductId,
                        leprepaymentEvery,
                        leprepaymentFrequencyType,
                        lepnumberOfRepayments,
                        lepsanctionAmount,
                        lepfixedEmi,
                        lepdisbursalAmount1,
                        lepdisbursalAmount2,
                        lepdisbursalAmount3,
                        lepdisbursalAmount4,
                        lepdisbursalEmi2,
                        lepdisbursalEmi3,
                        lepdisbursalEmi4,
                        leploanProductName);
            }


            return LoanApplicationSanctionData
                    .instance(loanAppSanctionId, loanApplicationReferenceId, loanAmountApproved, approvedOnDate, expectedDisbursementDate,
                            repaymentsStartingFromDate, numberOfRepayments, repaymentPeriodFrequency, repayEvery, termPeriodFrequency,
                            termFrequency, fixedEmiAmount, maxOutstandingLoanBalance, this.loanApplicationSanctionTrancheDatas, loanEMIPackData);
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
    
	@Override
	public Map<String, Object> retrieveLoanProductIdApprovedAmountClientId(Long loanApplicationReferenceId) {
		final StringBuilder sql = new StringBuilder(200);
		sql.append(
				"Select lpar.loan_product_id as productId, flas.loan_amount_approved as approvedAmount, lpar.client_id as clientId ");
		sql.append(" from f_loan_application_reference lpar ");
		sql.append(
				"Join f_loan_application_sanction flas on lpar.id = flas.loan_app_ref_id and lpar.id = :loanApplicationReferenceId");
		Map<String, Object> paramMap = new HashMap<>(1);
		paramMap.put("loanApplicationReferenceId", loanApplicationReferenceId);

		return this.namedParameterJdbcTemplate.queryForMap(sql.toString(), paramMap );
	}

    @Override
    public Collection<CoApplicantData> retrieveCoApplicants(final Long loanApplicationReferenceId) {
        CoApplicantMapper mapper = new CoApplicantMapper();
        return this.jdbcTemplate.query(mapper.schema(), mapper,  new Object[] { loanApplicationReferenceId });
    }

    @Override
    public CoApplicantData retrieveOneCoApplicant(final Long loanApplicationReferenceId,  final Long coApplicantId) {
        CoApplicantMapper mapper = new CoApplicantMapper();
        return this.jdbcTemplate.queryForObject(mapper.schema()+" and coapp.id = ?", mapper,  new Object[] { loanApplicationReferenceId,coApplicantId });
    }

    private static final class CoApplicantMapper implements RowMapper<CoApplicantData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public CoApplicantMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select coapp.id as id, ");
            sqlBuilder.append("coapp.client_id as clientId, ");
            sqlBuilder.append("cl.display_name as displayName, ");
            sqlBuilder.append("cl.status_enum as statusEnum, ");
            sqlBuilder.append("cl.account_no as accountNo ");
            sqlBuilder.append("from f_loan_coapplicants_mapping as coapp ");
            sqlBuilder.append("left join m_client as cl on coapp.client_id = cl.id ");
            sqlBuilder.append("where coapp.loan_application_reference_id = ?");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public CoApplicantData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Long id = JdbcSupport.getLongActualValue(rs, "id");
            final Long clientId = JdbcSupport.getLongActualValue(rs, "clientId");
            final String displayName = rs.getString("displayName");
            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final EnumOptionData status = ClientEnumerations.status(statusEnum);
            final String accountNo = rs.getString("accountNo");
            return new CoApplicantData(id, clientId, displayName, status, accountNo);
        }
    }
}
