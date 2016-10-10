package com.finflux.risk.creditbureau.provider.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMapping;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMappingRepository;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.provider.data.CreditBureauFileContentData;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportData;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.OtherInstituteLoansSummaryData;
import com.finflux.risk.creditbureau.provider.data.ReportFileType;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiryMapping;

@Service
public class CreditBureauCheckServiceImpl implements CreditBureauCheckService {

    final JdbcTemplate jdbcTemplate;
    private CreditBureauProviderFactory creditBureauProviderFactory;
    private CreditBureauEnquiryReadService creditBureauEnquiryReadService;
    private CreditBureauEnquiryWritePlatformService creditBureauEnquiryWritePlatformService;
    private final CreditBureauLoanProductMappingRepository creditBureauLpMappingRepository;
    private final CodeValueRepository codeValueRepository;

    @Autowired
    public CreditBureauCheckServiceImpl(final RoutingDataSource dataSource, CreditBureauProviderFactory creditBureauProviderFactory,
            CreditBureauEnquiryReadService creditBureauEnquiryReadService,
            CreditBureauEnquiryWritePlatformService creditBureauEnquiryWritePlatformService,
            CreditBureauLoanProductMappingRepository creditBureauLpMappingRepository, final CodeValueRepository codeValueRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.creditBureauProviderFactory = creditBureauProviderFactory;
        this.creditBureauEnquiryReadService = creditBureauEnquiryReadService;
        this.creditBureauEnquiryWritePlatformService = creditBureauEnquiryWritePlatformService;
        this.creditBureauLpMappingRepository = creditBureauLpMappingRepository;
        this.codeValueRepository = codeValueRepository;
    }

    @SuppressWarnings("unused")
    @Override
    public CreditBureauReportData getCreditBureauDataForLoan(Long loanId) {
        return null;
    }

    @Override
    public OtherInstituteLoansSummaryData getCreditBureauDataForLoanApplication(final Long loanApplicationId) {
        final String type = "SINGLE";
        final LoanEnquiryData enquiryData = this.creditBureauEnquiryReadService.getEnquiryRequestDataForLoanApplication(loanApplicationId);
        final CreditBureauLoanProductMapping creditBureauLpMapping = creditBureauLpMappingRepository.findWithLoanProductId(enquiryData
                .getLoanProductId());
        final CreditBureauProduct creditBureauProduct = creditBureauLpMapping.getCreditBureauProduct();
        final Integer stalePeriod = creditBureauLpMapping.getStalePeriod();

        String implementationKey = creditBureauProduct.getImplementationKey();
        CreditBureauProvider creditBureauProvider = creditBureauProviderFactory.getCreditBureauProvider(implementationKey);

        // checkwhether report is available or stale

        LoanEnquiryReferenceData loanEnquiryReferenceData = creditBureauEnquiryReadService
                .getLatestCreditBureauEnquiryForLoanApplicationReference(loanApplicationId, creditBureauProduct.getId());

        // checkwhether report is available or stale

        if (loanEnquiryReferenceData == null || loanEnquiryReferenceData.getStatus().isInValid()
                || loanEnquiryReferenceData.isCBReportGeneratedDaysGreaterThanStalePeriod(stalePeriod)) {
            // fire new request
            this.creditBureauEnquiryReadService.inActivePreviousLoanApplicationCreditbureauEnquiries(loanApplicationId);
            List<Long> loanApplicationIds = new ArrayList<>();
            loanApplicationIds.add(loanApplicationId);
            EnquiryReferenceData enquiryReferenceData = createCreditBureauEnquiry(creditBureauProduct, type, null, loanApplicationIds);
            CreditBureauResponse creditBureauResponseEnquire = creditBureauProvider.enquireCreditBureau(enquiryReferenceData);
            creditBureauEnquiryWritePlatformService.saveEnquiryResponseDetails(enquiryReferenceData, creditBureauResponseEnquire);

            // fetch report
            loanEnquiryReferenceData = creditBureauEnquiryReadService.getLatestCreditBureauEnquiryForLoanApplicationReference(
                    loanApplicationId, creditBureauProduct.getId());
            final CreditBureauResponse creditBureauResponse = creditBureauProvider.fetchCreditBureauReport(loanEnquiryReferenceData);
            creditBureauEnquiryWritePlatformService.saveReportResponseDetails(loanEnquiryReferenceData, creditBureauResponse);

        }
        final CodeValue source = this.codeValueRepository.findByCodeNameAndLabel("ExistingLoanSource", "Credit Bureau");
        return getOtherInstituteLoansSummary(loanApplicationId, source.getId());
    }

    @Override
    public OtherInstituteLoansSummaryData getOtherInstituteLoansSummary(final Long loanApplicationReferenceId, final Long sourceId) {
        try {
            final StringBuilder sb = new StringBuilder(200);
            sb.append("SELECT lcbe.status AS lcbenquiryStatus,lcbe.response AS lcbeResponse,cbe.response AS cbeResponse,lcbe.file_type AS reportFileTypeId ");
            sb.append(",IFNULL(SUM(el.amount_borrowed),0.0) AS totalAmountBorrowed, IFNULL(SUM(el.current_outstanding) ,0.0) AS totalCurrentOutstanding ");
            sb.append(",IFNULL(SUM(el.amt_overdue),0.0) AS totalAmtOverdue, IFNULL(SUM(el.installment_amount),0.0) AS totalInstallmentAmount ");
            sb.append(",cbe.created_date AS cbInitiatedDateTime ");
            sb.append("FROM f_loan_creditbureau_enquiry lcbe ");
            sb.append("INNER JOIN f_creditbureau_enquiry cbe ON cbe.id = lcbe.creditbureau_enquiry_id ");
            sb.append("LEFT JOIN f_existing_loan el ON el.loan_creditbureau_enquiry_id = lcbe.id AND el.loan_status_id = 300 ");
            if (sourceId != null) {
                sb.append("AND el.source_id = ").append(sourceId.toString()).append(" ");
            }
            sb.append("WHERE lcbe.loan_application_id = ? AND lcbe.is_active = 1 GROUP BY el.loan_application_id ");
            return this.jdbcTemplate.queryForObject(sb.toString(), new OtherInstituteLoansSummaryDataExtractor(),
                    new Object[] { loanApplicationReferenceId });
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
            byte[] cbResponse = null;
            byte[] cbLoanEnqResponse = null;

            if (cbStatus != null && !cbStatus.getValue().equalsIgnoreCase("SUCCESS")) {
                final String cbResponseStr = rs.getString("cbeResponse");
                if (cbResponseStr != null && cbResponseStr.trim().length() > 0) {
                    cbResponse = cbResponseStr.getBytes();
                }

                final String cbLoanEnqResponseStr = rs.getString("lcbeResponse");
                if (cbLoanEnqResponseStr != null && cbLoanEnqResponseStr.trim().length() > 0) {
                    cbLoanEnqResponse = cbLoanEnqResponseStr.getBytes();
                }
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
            String cbInitiatedDateTime = null;
            final Date date = rs.getTimestamp("cbInitiatedDateTime");
            if (date != null) {
                cbInitiatedDateTime = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss").format(date);
            }
            return new OtherInstituteLoansSummaryData(cbStatus, cbResponse, cbLoanEnqResponse, totalAmountBorrowed,
                    totalCurrentOutstanding, totalAmtOverdue, totalInstallmentAmount, reportFileType, cbInitiatedDateTime);
        }
    }

    private EnquiryReferenceData createCreditBureauEnquiry(CreditBureauProduct creditBureauProduct, String type, List<Long> loanIds,
            List<Long> loanApplicationIds) {
        CreditBureauEnquiry creditBureauEnquiry = new CreditBureauEnquiry(creditBureauProduct, type,
                CreditBureauEnquiryStatus.INITIATED.getValue(), null);

        List<LoanCreditBureauEnquiryMapping> loanMappings = new ArrayList<>();
        if (loanIds != null && !loanIds.isEmpty()) {

        }
        if (loanApplicationIds != null && !loanApplicationIds.isEmpty()) {
            for (final Long loanApplicationId : loanApplicationIds) {
                final LoanEnquiryData enquiryData = this.creditBureauEnquiryReadService
                        .getEnquiryRequestDataForLoanApplication(loanApplicationId);
                String refNumber = generateRandomEnquiryNumberByLoanApplicationId(type, creditBureauProduct.getImplementationKey(),
                        loanApplicationId);
                final LoanCreditBureauEnquiryMapping loanMapping = new LoanCreditBureauEnquiryMapping(null, refNumber,
                        enquiryData.getClientId(), null, loanApplicationId, CreditBureauEnquiryStatus.INITIATED.getValue());
                loanMapping.setCreditBureauEnquiry(creditBureauEnquiry);
                loanMappings.add(loanMapping);
            }
        }
        creditBureauEnquiry.setLoanCreditBureauEnquiryMapping(loanMappings);
        CreditBureauEnquiry newEnquiry = creditBureauEnquiryWritePlatformService.createNewEnquiry(creditBureauEnquiry);

        EnquiryReferenceData enquiryReferenceData = new EnquiryReferenceData(newEnquiry.getId(), null,
                CreditBureauEnquiryStatus.fromInt(newEnquiry.getStatus()), newEnquiry.getType(), new Date(), creditBureauProduct.getId());
        List<LoanEnquiryReferenceData> loanEnquiryReferenceDataList = new ArrayList<>();
        for (LoanCreditBureauEnquiryMapping loanEnquiryMap : newEnquiry.getLoanCreditBureauEnquiryMapping()) {
            LoanEnquiryData loanEnquiryData = null;
            LoanEnquiryReferenceData loanEnquiryReferenceData = new LoanEnquiryReferenceData(loanEnquiryMap.getId(),
                    creditBureauEnquiry.getId(), loanEnquiryMap.getReferenceNum(), loanEnquiryMap.getClientId(),
                    loanEnquiryMap.getLoanId(), loanEnquiryMap.getLoanApplicationId(), loanEnquiryMap.getCbReportId(),
                    newEnquiry.getAcknowledgementNumber(), CreditBureauEnquiryStatus.fromInt(loanEnquiryMap.getStatus()), newEnquiry
                            .getCreditBureauProduct().getId(), newEnquiry.getCreatedDate().toDate());
            if (loanEnquiryMap.getLoanApplicationId() != null) {
                loanEnquiryData = this.creditBureauEnquiryReadService.getEnquiryRequestDataForLoanApplication(loanEnquiryMap
                        .getLoanApplicationId());
            }
            if (loanEnquiryData != null) {
                loanEnquiryData.setAddressList(creditBureauEnquiryReadService.getClientAddressData(loanEnquiryData.getClientId()));
                loanEnquiryData.setDocumentList(creditBureauEnquiryReadService.getClientDocumentData(loanEnquiryData.getClientId()));
                loanEnquiryReferenceData.setEnquiryData(loanEnquiryData);
                loanEnquiryReferenceDataList.add(loanEnquiryReferenceData);
            }
        }
        enquiryReferenceData.setLoansReferenceData(loanEnquiryReferenceDataList);

        return enquiryReferenceData;
    }

    private String generateRandomEnquiryNumberByLoanApplicationId(final String type, final String implementationKey,
            final Long loanApplicationId) {
        return implementationKey + "_" + type + "_" + System.currentTimeMillis() + "_" + loanApplicationId;
    }

    @Override
    public CreditBureauFileContentData getCreditBureauReportFileContent(final Long loanApplicationReferenceId) {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("SELECT lcbe.file_type AS reportFileTypeId, lcbe.file_content AS fileContent ");
        sb.append("FROM f_loan_creditbureau_enquiry lcbe ");
        sb.append("WHERE lcbe.loan_application_id = ? AND lcbe.is_active = 1 ");
        return this.jdbcTemplate.queryForObject(sb.toString(), new CreditBureauFileContantDataExtractor(),
                new Object[] { loanApplicationReferenceId });
    }

    private static final class CreditBureauFileContantDataExtractor implements RowMapper<CreditBureauFileContentData> {

        @Override
        public CreditBureauFileContentData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Integer fileTypeId = JdbcSupport.getIntegeActualValue(rs, "reportFileTypeId");
            EnumOptionData reportFileType = null;
            if (fileTypeId != null) {
                reportFileType = ReportFileType.reportFileType(fileTypeId);
            }
            byte[] fileContent = rs.getBytes("fileContent");
            return new CreditBureauFileContentData(reportFileType, fileContent);
        }
    }
}