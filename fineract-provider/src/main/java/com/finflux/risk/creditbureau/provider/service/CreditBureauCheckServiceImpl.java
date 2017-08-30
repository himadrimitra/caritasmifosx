package com.finflux.risk.creditbureau.provider.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepository;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMapping;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductOfficeMapping;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductOfficeMappingRepository;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.provider.data.CreditBureauEntityType;
import com.finflux.risk.creditbureau.provider.data.CreditBureauFileContentData;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.OtherInstituteLoansSummaryData;
import com.finflux.risk.creditbureau.provider.data.ReportFileType;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiryRepository;
import com.google.gson.Gson;

@Service
public class CreditBureauCheckServiceImpl implements CreditBureauCheckService {

    final JdbcTemplate jdbcTemplate;
    private CreditBureauProviderFactory creditBureauProviderFactory;
    private CreditBureauEnquiryReadService creditBureauEnquiryReadService;
    private CreditBureauEnquiryWritePlatformService creditBureauEnquiryWritePlatformService;
    private final CreditBureauLoanProductOfficeMappingRepository creditBureauLoanProductOfficeMappingRepository;
    private final LoanReadPlatformServiceImpl loanReadPlatformServiceImpl;
    private final LoanApplicationReferenceRepository loanApplicationReferenceRepository;
    private final LoanCreditBureauEnquiryRepository loanCreditBureauEnquiryRepository;
    private final ContentServiceUtil contentService ;
    @Autowired
    public CreditBureauCheckServiceImpl(final RoutingDataSource dataSource, CreditBureauProviderFactory creditBureauProviderFactory,
            CreditBureauEnquiryReadService creditBureauEnquiryReadService,
            CreditBureauEnquiryWritePlatformService creditBureauEnquiryWritePlatformService,
            final LoanReadPlatformServiceImpl loanReadPlatformServiceImpl,
            final LoanApplicationReferenceRepository loanApplicationReferenceRepository,
            final LoanCreditBureauEnquiryRepository loanCreditBureauEnquiryRepository,
            final CreditBureauLoanProductOfficeMappingRepository creditBureauLoanProductOfficeMappingRepository,
            final ContentServiceUtil contentService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.creditBureauProviderFactory = creditBureauProviderFactory;
        this.creditBureauEnquiryReadService = creditBureauEnquiryReadService;
        this.creditBureauEnquiryWritePlatformService = creditBureauEnquiryWritePlatformService;
        this.loanReadPlatformServiceImpl = loanReadPlatformServiceImpl;
        this.loanApplicationReferenceRepository = loanApplicationReferenceRepository;
        this.loanCreditBureauEnquiryRepository = loanCreditBureauEnquiryRepository;
        this.creditBureauLoanProductOfficeMappingRepository = creditBureauLoanProductOfficeMappingRepository;
        this.contentService = contentService ;
    }

    @SuppressWarnings("null")
    @Override
    public OtherInstituteLoansSummaryData getCreditBureauEnquiryData(final String entityType, final Long entityId) {
        final String type = "SINGLE";
        if (entityType == null) { return null; }
        Long loanApplicationId = null;
        Long loanId = null;
        LoanEnquiryData enquiryData = null;
        if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOANAPPLICATION.toString())) {
            loanApplicationId = entityId;
            enquiryData = this.creditBureauEnquiryReadService.getEnquiryRequestDataForLoanApplication(loanApplicationId);
        } else if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOAN.toString())) {
            loanId = entityId;
            final LoanApplicationReference loanApplicationReference = this.loanApplicationReferenceRepository.findOneByLoanId(loanId);
            if (loanApplicationReference != null) {
                loanApplicationId = loanApplicationReference.getId();
                enquiryData = this.creditBureauEnquiryReadService.getEnquiryRequestDataForLoanApplication(loanApplicationId);
            } else {
                enquiryData = this.creditBureauEnquiryReadService.getEnquiryRequestDataForLoan(loanId);
            }
        }
        CreditBureauLoanProductOfficeMapping creditBureauLoanProductOfficeMapping = this.creditBureauLoanProductOfficeMappingRepository.retrieveCreditBureauAndLoanProductOfficeMapping(enquiryData.getLoanProductId(), enquiryData.getBranchId());

        if (creditBureauLoanProductOfficeMapping == null) {
            creditBureauLoanProductOfficeMapping = this.creditBureauLoanProductOfficeMappingRepository
                    .retrieveDefaultCreditBureauAndLoanProductOfficeMapping(enquiryData.getLoanProductId());
        }
        if (creditBureauLoanProductOfficeMapping != null) {
            final CreditBureauLoanProductMapping creditBureauLpMapping = creditBureauLoanProductOfficeMapping
                    .getCreditBureauLoanProductMapping();
            final CreditBureauProduct creditBureauProduct = creditBureauLpMapping.getCreditBureauProduct();
            final Integer stalePeriod = creditBureauLpMapping.getStalePeriod();

            String implementationKey = creditBureauProduct.getImplementationKey();
            CreditBureauProvider creditBureauProvider = creditBureauProviderFactory.getCreditBureauProvider(implementationKey);

            // check whether report is available or stale

            Long trancheDisbursalId = null;
            Map<String, Object> data = null;
            if (loanId != null) {
                enquiryData.setLoanId(loanId);
                data = this.loanReadPlatformServiceImpl.retrieveDisbursalDataMap(loanId);
                if (data.get("trancheDisbursalId") != null) {
                    trancheDisbursalId = (Long) data.get("trancheDisbursalId");
                }
                if (data.get("principal") != null) {
                    enquiryData.setLoanAmount((BigDecimal) data.get("principal"));
                }
            }
            LoanEnquiryReferenceData loanEnquiryReferenceData = this.creditBureauEnquiryReadService.getLatestCreditBureauEnquiryDetails(
                    loanApplicationId, creditBureauProduct.getId(), loanId, trancheDisbursalId);
            Boolean isCBReportExpired = false;
            if(loanEnquiryReferenceData != null && !loanEnquiryReferenceData.getStatus().isInValid()){
                isCBReportExpired = loanEnquiryReferenceData.isCBReportGeneratedDaysGreaterThanStalePeriod(stalePeriod);
            }

            if (loanEnquiryReferenceData == null || loanEnquiryReferenceData.getStatus().isInValid()
                    || isCBReportExpired) {
                // fire new request
                //this.creditBureauEnquiryReadService.inActivePreviousLoanApplicationCreditbureauEnquiries(loanApplicationId);
                final EnquiryReferenceData enquiryReferenceData = createCreditBureauEnquiry(enquiryData, creditBureauProduct, type,
                        loanApplicationId, loanId, trancheDisbursalId, isCBReportExpired);
                final CreditBureauResponse creditBureauResponseEnquire = creditBureauProvider.enquireCreditBureau(enquiryReferenceData);
                this.creditBureauEnquiryWritePlatformService.saveEnquiryResponseDetails(enquiryReferenceData, creditBureauResponseEnquire);
                
                // fetch report
                /*loanEnquiryReferenceData = this.creditBureauEnquiryReadService.getLatestCreditBureauEnquiryDetails(loanApplicationId,
                        creditBureauProduct.getId(), loanId, trancheDisbursalId);*/
                loanEnquiryReferenceData = enquiryReferenceData.getLoansReferenceData().get(0) ;
                processCreditBureauReportRespon(creditBureauProvider, loanEnquiryReferenceData, loanId, trancheDisbursalId);
            } else if (loanEnquiryReferenceData.getStatus().isPending()) {
                // fetch report
                processCreditBureauReportRespon(creditBureauProvider, loanEnquiryReferenceData, loanId, trancheDisbursalId);
            }
            return getOtherInstituteLoansSummary(entityType, entityId, trancheDisbursalId);
        }
        OtherInstituteLoansSummaryData otherInstituteLoansSummaryData = null;
        return otherInstituteLoansSummaryData;
    }

    private void processCreditBureauReportRespon(final CreditBureauProvider creditBureauProvider,
            final LoanEnquiryReferenceData loanEnquiryReferenceData, final Long loanId, final Long trancheDisbursalId) {
        final CreditBureauResponse creditBureauResponse = creditBureauProvider.fetchCreditBureauReport(loanEnquiryReferenceData);
        this.creditBureauEnquiryWritePlatformService.saveReportResponseDetails(loanEnquiryReferenceData, creditBureauResponse);
        if (creditBureauResponse.getCreditBureauExistingLoans() != null) {
            this.creditBureauEnquiryWritePlatformService.saveCreditBureauExistingLoans(loanEnquiryReferenceData.getLoanApplicationId(),
                    creditBureauResponse.getCreditBureauExistingLoans(), loanId, trancheDisbursalId);
        }
    }

    @SuppressWarnings("null")
    @Override
    public OtherInstituteLoansSummaryData getOtherInstituteLoansSummary(final String entityType, final Long entityId,
            Long trancheDisbursalId) {
        try {
            if (entityType == null) { return null; }
            Long loanApplicationId = null;
            Long loanId = null;
            if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOANAPPLICATION.toString())) {
                loanApplicationId = entityId;
            } else if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOAN.toString())) {
                loanId = entityId;
                final LoanApplicationReference loanApplicationReference = this.loanApplicationReferenceRepository.findOneByLoanId(loanId);
                if(loanApplicationReference != null){
                    loanApplicationId = loanApplicationReference.getId();
                }
                if (loanId != null && trancheDisbursalId == null) {
                    final Map<String, Object> data = this.loanReadPlatformServiceImpl.retrieveDisbursalDataMap(loanId);
                    if (data.get("trancheDisbursalId") != null) {
                        trancheDisbursalId = (Long) data.get("trancheDisbursalId");
                    }
                }
            }
            final StringBuilder sb = new StringBuilder(200);
            sb.append("SELECT lcbe.loan_id AS loanId, lcbe.status AS lcbenquiryStatus,lcbe.response AS lcbeResponse,cbe.response AS cbeResponse,lcbe.file_type AS reportFileTypeId ");
            sb.append(",IFNULL(SUM(el.amount_borrowed),0.0) AS totalAmountBorrowed, IFNULL(SUM(el.current_outstanding) ,0.0) AS totalCurrentOutstanding ");
            sb.append(",IFNULL(SUM(el.amt_overdue),0.0) AS totalAmtOverdue, IFNULL(SUM(el.installment_amount),0.0) AS totalInstallmentAmount, ");
            sb.append("cbe.errors_json as errorJson ");
            sb.append(",cbe.created_date AS cbInitiatedDateTime ");
            sb.append("FROM f_loan_creditbureau_enquiry lcbe ");
            sb.append("INNER JOIN f_creditbureau_enquiry cbe ON cbe.id = lcbe.creditbureau_enquiry_id ");
            sb.append("LEFT JOIN f_existing_loan el ON el.loan_creditbureau_enquiry_id = lcbe.id AND el.loan_status_id = 300 ");
            sb.append("WHERE lcbe.is_active = 1 ");
            if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOAN.toString())) {
                sb.append("AND lcbe.loan_id = ? AND lcbe.tranche_disbursal_id = ? ");
                sb.append("GROUP BY el.loan_id ");
            }else{
                sb.append("AND lcbe.loan_application_id = ? "); 
                sb.append("GROUP BY el.loan_application_id ");
            }
            
            if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOANAPPLICATION.toString())) {
                return this.jdbcTemplate.queryForObject(sb.toString(), new OtherInstituteLoansSummaryDataExtractor(),
                        new Object[] { loanApplicationId });
            } else if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOAN.toString())) { return this.jdbcTemplate
                    .queryForObject(sb.toString(), new OtherInstituteLoansSummaryDataExtractor(),
                            new Object[] { loanId, trancheDisbursalId }); }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class OtherInstituteLoansSummaryDataExtractor implements RowMapper<OtherInstituteLoansSummaryData> {

        @Override
        public OtherInstituteLoansSummaryData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Integer cbStatusId = JdbcSupport.getIntegeActualValue(rs, "lcbenquiryStatus");
            EnumOptionData cbStatus = null;
            if (cbStatusId != null) {
                cbStatus = CreditBureauEnquiryStatus.creditBureauEnquiryStatus(cbStatusId);
            }
            final Integer fileTypeId = JdbcSupport.getIntegeActualValue(rs, "reportFileTypeId");
            EnumOptionData reportFileType = null;
            if (fileTypeId != null) {
                reportFileType = ReportFileType.reportFileType(fileTypeId);
            }
            final BigDecimal totalAmountBorrowed = rs.getBigDecimal("totalAmountBorrowed");
            final BigDecimal totalCurrentOutstanding = rs.getBigDecimal("totalCurrentOutstanding");
            final BigDecimal totalAmtOverdue = rs.getBigDecimal("totalAmtOverdue");
            final BigDecimal totalInstallmentAmount = rs.getBigDecimal("totalInstallmentAmount");
            final String errorJson = rs.getString("errorJson") ;
            String cbInitiatedDateTime = null;
            final Date date = rs.getTimestamp("cbInitiatedDateTime");
            if (date != null) {
                cbInitiatedDateTime = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss").format(date);
            }
            final Long loanId = JdbcSupport.getLongActualValue(rs, "loanId");
            List<Map<String, String>> errors = null ;
            if(!StringUtils.isEmpty(errorJson)) {
                errors = new Gson().fromJson(errorJson, List.class) ;
            }
            return new OtherInstituteLoansSummaryData(loanId, cbStatus, totalAmountBorrowed,
                    totalCurrentOutstanding, totalAmtOverdue, totalInstallmentAmount, reportFileType, cbInitiatedDateTime, errors);
        }
    }

    private EnquiryReferenceData createCreditBureauEnquiry(final LoanEnquiryData enquiryData, final CreditBureauProduct creditBureauProduct,
            final String type, final Long loanApplicationId, final Long loanId, final Long trancheDisbursalId, final Boolean isCBReportExpired) {
        List<LoanCreditBureauEnquiry> loanMappings = new ArrayList<>();
        CreditBureauEnquiry creditBureauEnquiry = null;
        final LoanCreditBureauEnquiry loanCreditBureauEnquiry = this.loanCreditBureauEnquiryRepository
                .findOneByLoanApplicationIdAndLoanIdAndTrancheDisbursalId(loanApplicationId, loanId, trancheDisbursalId);
        if (loanCreditBureauEnquiry != null) {
            creditBureauEnquiry = loanCreditBureauEnquiry.getCreditBureauEnquiry();
            creditBureauEnquiry.setCreditBureauProduct(creditBureauProduct);
            creditBureauEnquiry.setType(type);
            creditBureauEnquiry.setStatus(CreditBureauEnquiryStatus.INITIATED.getValue());
            /**
             * If CB report expired then update created date with current date for new inquiry.
             */
            if(isCBReportExpired){
                creditBureauEnquiry.setCreatedDate(DateUtils.getLocalDateTimeOfTenant().toDateTime());
            }
            loanMappings = creditBureauEnquiry.getLoanCreditBureauEnquiryMapping();
        } else {
            creditBureauEnquiry = new CreditBureauEnquiry(creditBureauProduct, type, CreditBureauEnquiryStatus.INITIATED.getValue());
        }
        String refNumber = generateRandomEnquiryNumber(type, creditBureauProduct.getImplementationKey(), loanApplicationId, loanId,
                trancheDisbursalId);
        if (!loanMappings.isEmpty()) {
            for (final LoanCreditBureauEnquiry lce : loanMappings) {
                lce.update(refNumber, enquiryData.getClientId(), enquiryData.getLoanId(), loanApplicationId, trancheDisbursalId,
                        CreditBureauEnquiryStatus.INITIATED.getValue());
            }
        } else {
            final LoanCreditBureauEnquiry loanMapping = new LoanCreditBureauEnquiry(null, refNumber, enquiryData.getClientId(),
                    enquiryData.getLoanId(), loanApplicationId, trancheDisbursalId, CreditBureauEnquiryStatus.INITIATED.getValue());
            loanMapping.setCreditBureauEnquiry(creditBureauEnquiry);
            loanMappings.add(loanMapping);
            creditBureauEnquiry.setLoanCreditBureauEnquiries(loanMappings);
        }

        final CreditBureauEnquiry newEnquiry = this.creditBureauEnquiryWritePlatformService.createNewEnquiry(creditBureauEnquiry);
        final EnquiryReferenceData enquiryReferenceData = new EnquiryReferenceData(newEnquiry.getId(), null,
                CreditBureauEnquiryStatus.fromInt(newEnquiry.getStatus()), newEnquiry.getType(), new Date(), creditBureauProduct.getId());
        final List<LoanEnquiryReferenceData> loanEnquiryReferenceDataList = new ArrayList<>();
        for (final LoanCreditBureauEnquiry loanEnquiryMap : newEnquiry.getLoanCreditBureauEnquiryMapping()) {
            final LoanEnquiryReferenceData loanEnquiryReferenceData = new LoanEnquiryReferenceData(loanEnquiryMap.getId(),
                    creditBureauEnquiry.getId(), loanEnquiryMap.getReferenceNum(), loanEnquiryMap.getClientId(),
                    loanEnquiryMap.getLoanId(), loanEnquiryMap.getLoanApplicationId(), loanEnquiryMap.getCbReportId(),
                    newEnquiry.getAcknowledgementNumber(), CreditBureauEnquiryStatus.fromInt(loanEnquiryMap.getStatus()), newEnquiry
                            .getCreditBureauProduct().getId(), newEnquiry.getCreatedDate().toDate());
            if (enquiryData != null) {
                enquiryData.setAddressList(creditBureauEnquiryReadService.getClientAddressData(enquiryData.getClientId()));
                enquiryData.setDocumentList(creditBureauEnquiryReadService.getClientDocumentData(enquiryData.getClientId()));
                enquiryData.setRelationshipList(creditBureauEnquiryReadService.getClientRelationshipData(enquiryData.getClientId()));
                loanEnquiryReferenceData.setEnquiryData(enquiryData);
                loanEnquiryReferenceDataList.add(loanEnquiryReferenceData);
            }
        }
        enquiryReferenceData.setLoansReferenceData(loanEnquiryReferenceDataList);
        return enquiryReferenceData;
    }

    private String generateRandomEnquiryNumber(final String type, final String implementationKey, final Long loanApplicationId,
            final Long loanId, final Long trancheDisbursalId) {
        if (loanId != null && trancheDisbursalId != null) { return implementationKey + "_" + type + "_" + System.currentTimeMillis() + "_"
                + loanId + "_" + trancheDisbursalId; }
        return implementationKey + "_" + type + "_" + System.currentTimeMillis() + "_" + loanApplicationId;
    }

    @SuppressWarnings("null")
    @Override
    public CreditBureauFileContentData getCreditBureauReportFileContent(final String entityType, final Long entityId) {
        try {
            if (entityType == null) { return null; }
            Long loanApplicationId = null;
            Long loanId = null;
            Long trancheDisbursalId = null;
            if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOANAPPLICATION.toString())) {
                loanApplicationId = entityId;
            } else if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOAN.toString())) {
                loanId = entityId;
                final LoanApplicationReference loanApplicationReference = this.loanApplicationReferenceRepository.findOneByLoanId(loanId);
                if (loanApplicationReference != null) {
                    loanApplicationId = loanApplicationReference.getId();
                }
                if (loanId != null) {
                    final Map<String, Object> data = this.loanReadPlatformServiceImpl.retrieveDisbursalDataMap(loanId);
                    if (data.get("trancheDisbursalId") != null) {
                        trancheDisbursalId = (Long) data.get("trancheDisbursalId");
                    }
                }
            }
            final StringBuilder sb = new StringBuilder(100);
            sb.append("SELECT lcbe.file_type AS reportFileTypeId, lcbe.report_location reportLocation ");
            sb.append("FROM f_loan_creditbureau_enquiry lcbe ");
            sb.append("WHERE lcbe.is_active = 1 ");
            if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOANAPPLICATION.toString())) {
                sb.append("AND lcbe.loan_application_id = ? ");
                return this.jdbcTemplate.queryForObject(sb.toString(), new CreditBureauFileContantDataExtractor(),
                        new Object[] { loanApplicationId });
            } else if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.LOAN.toString())) {
                sb.append("AND lcbe.loan_id = ? AND lcbe.tranche_disbursal_id = ? ");
                return this.jdbcTemplate.queryForObject(sb.toString(), new CreditBureauFileContantDataExtractor(), new Object[] { loanId,
                        trancheDisbursalId });
            } else if (entityType != null && entityType.equalsIgnoreCase(CreditBureauEntityType.CLIENT.toString())) {
                //Get Only Latest Client CB Report
                sb.append("AND lcbe.client_id = ? ORDER BY lcbe.id DESC LIMIT 0, 1 ");
                return this.jdbcTemplate.queryForObject(sb.toString(), new CreditBureauFileContantDataExtractor(),
                        new Object[] { entityId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return new CreditBureauFileContentData();
    }

    private final class CreditBureauFileContantDataExtractor implements RowMapper<CreditBureauFileContentData> {

        @Override
        public CreditBureauFileContentData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Integer fileTypeId = JdbcSupport.getIntegeActualValue(rs, "reportFileTypeId");
            EnumOptionData reportFileType = null;
            if (fileTypeId != null) {
                reportFileType = ReportFileType.reportFileType(fileTypeId);
            }
            final String reportLocation = rs.getString("reportLocation") ;
            return new CreditBureauFileContentData(reportFileType, contentService.getContent(reportLocation).getBytes()); 
        }
    }
}