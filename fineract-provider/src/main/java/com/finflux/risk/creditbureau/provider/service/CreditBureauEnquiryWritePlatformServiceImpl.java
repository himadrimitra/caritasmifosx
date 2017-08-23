package com.finflux.risk.creditbureau.provider.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProductRepositoryWrapper;
import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoan;
import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoanPaymentDetail;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportFile;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.CreditScore;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryRepository;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauScore;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiryRepository;
import com.finflux.risk.existingloans.domain.CreditBureauPaymentDetails;
import com.finflux.risk.existingloans.domain.ExistingLoan;
import com.finflux.risk.existingloans.domain.ExistingLoanRepositoryWrapper;
import com.google.gson.Gson;

@Service
public class CreditBureauEnquiryWritePlatformServiceImpl implements CreditBureauEnquiryWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CreditBureauEnquiryWritePlatformServiceImpl.class);
    private final CreditBureauEnquiryRepository creditBureauEnquiryRepository;
    private final LoanCreditBureauEnquiryRepository loanCreditBureauEnquiryRepository;
    private final ExistingLoanRepositoryWrapper existingLoanRepository;
    private final CodeValueRepository codeValueRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final CreditBureauProductRepositoryWrapper creditBureauProductRepository;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final Gson gson;

    @Autowired
    public CreditBureauEnquiryWritePlatformServiceImpl(final CreditBureauEnquiryRepository creditBureauEnquiryRepository,
            final LoanCreditBureauEnquiryRepository creditBureauRequestDetailsRepository,
            final ExistingLoanRepositoryWrapper existingLoanRepository, final CodeValueRepository codeValueRepository,
            final ClientRepositoryWrapper clientRepository, final CreditBureauProductRepositoryWrapper creditBureauProductRepository,
            final ContentRepositoryFactory contentRepositoryFactory) {
        this.creditBureauEnquiryRepository = creditBureauEnquiryRepository;
        this.loanCreditBureauEnquiryRepository = creditBureauRequestDetailsRepository;
        this.existingLoanRepository = existingLoanRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientRepository = clientRepository;
        this.creditBureauProductRepository = creditBureauProductRepository;
        this.contentRepositoryFactory = contentRepositoryFactory ;
        this.gson = new Gson();
    }

    @Transactional
    @Override
    public void saveEnquiryResponseDetails(final EnquiryReferenceData enquiryReferenceData, final CreditBureauResponse responseData) {
        final EnquiryResponse response = responseData.getEnquiryResponse();
        final CreditBureauEnquiry creditBureauEnquiry = this.creditBureauEnquiryRepository.findOne(enquiryReferenceData.getEnquiryId());
        creditBureauEnquiry.setAcknowledgementNumber(response.getAcknowledgementNumber());
        enquiryReferenceData.setAcknowledgementNumber(response.getAcknowledgementNumber());
        creditBureauEnquiry.setStatus(response.getStatus().getValue());
        creditBureauEnquiry.setErrorsJosnString(response.getErrorsJson()); 
        if(response.getRequest() != null) {
        	creditBureauEnquiry.setRequestLocation(saveContent(response.getRequest().getBytes(), creditBureauEnquiry.getId()));	
        }
        if(response.getResponse() != null) {
        	creditBureauEnquiry.setResponseLocation(saveContent(response.getResponse().getBytes(), creditBureauEnquiry.getId()));
        }
        final List<LoanCreditBureauEnquiry> creditBureauLoanEnquiries = creditBureauEnquiry.getLoanCreditBureauEnquiryMapping();
        if (creditBureauLoanEnquiries != null && !creditBureauLoanEnquiries.isEmpty()) {
            final LoanCreditBureauEnquiry loanEnquiry = creditBureauLoanEnquiries.get(0);
            loanEnquiry.setStatus(response.getStatus().getValue());
            loanEnquiry.setCbReportId(response.getReportId());
            if (enquiryReferenceData.getLoansReferenceData() != null && !enquiryReferenceData.getLoansReferenceData().isEmpty()) {
                for (final LoanEnquiryReferenceData loanEnquiryReferenceData : enquiryReferenceData.getLoansReferenceData()) {
                    loanEnquiryReferenceData.setAcknowledgementNumber(creditBureauEnquiry.getAcknowledgementNumber());
                    loanEnquiryReferenceData.setCbReportId(response.getReportId());
                    loanEnquiryReferenceData.setStatus(response.getStatus());
                }
            }
        }
        this.creditBureauEnquiryRepository.save(creditBureauEnquiry);
    }

	private String saveContent(final byte[] data, final Long parentId) {
		final String entityType = "CREDIT_BUREAU" ;
		final ByteArrayInputStream contentInputStream = new ByteArrayInputStream(data);
		final String fileName = RandomStringUtils.randomAlphanumeric(15) ;
		final DocumentCommand documentCommand = new DocumentCommand(entityType, parentId, fileName, new Long(data.length)) ;
		final String fileLocation = this.contentRepositoryFactory.getRepository().saveFile(contentInputStream, documentCommand) ;
		return fileLocation ;
	}
	
    @Transactional
    @Override
    public void saveReportResponseDetails(LoanEnquiryReferenceData loanEnquiryReferenceData, CreditBureauResponse responseData) {
        try {
            EnquiryResponse response = responseData.getEnquiryResponse();
            CreditBureauEnquiry creditBureauEnquiry = this.creditBureauEnquiryRepository.findOne(loanEnquiryReferenceData.getEnquiryId());
            creditBureauEnquiry.setStatus(response.getStatus().getValue());
            if(response.getErrorsJson() != null) creditBureauEnquiry.setErrorsJosnString(response.getErrorsJson()); 
            this.creditBureauEnquiryRepository.save(creditBureauEnquiry);
            List<LoanCreditBureauEnquiry> creditBureauLoanEnquiries = creditBureauEnquiry.getLoanCreditBureauEnquiryMapping();
            if (creditBureauLoanEnquiries != null && !creditBureauLoanEnquiries.isEmpty()) {
                LoanCreditBureauEnquiry loanEnquiry = creditBureauLoanEnquiries.get(0);
                loanEnquiry.setStatus(response.getStatus().getValue());
                
                if(response.getRequest() != null) {
                	loanEnquiry.setRequestLocation(saveContent(response.getRequest().getBytes(), loanEnquiry.getId())); 
                }
                if(response.getResponse() != null) {
                	loanEnquiry.setResponseLocation(saveContent(response.getResponse().getBytes(), loanEnquiry.getId()));
                }
                if (responseData.getCreditBureauReportFile() != null) {
                    CreditBureauReportFile reportFile = responseData.getCreditBureauReportFile();
                    loanEnquiry.setFileName(reportFile.getFileName());
                    loanEnquiry.setFileType(reportFile.getFileType().getValue());
                    loanEnquiry.setReportLocation(saveContent(reportFile.getFileContent(), loanEnquiry.getId())); 
                }
                List<CreditScore> creditScores = responseData.getCreditScore() ;
                if(creditScores != null && creditScores.size() > 0) {
                	for(CreditScore score: creditScores) {
                		loanEnquiry.addCreditScore(new CreditBureauScore(loanEnquiry, score)); 
                	}
                }
                this.loanCreditBureauEnquiryRepository.save(loanEnquiry);
            }
        } catch (Exception e) {
            // logger.error(this.gson.toJson(responseData));
        }
    }

    @Transactional
    @Override
    public void saveCreditBureauExistingLoans(final Long loanApplicationId, final List<CreditBureauExistingLoan> creditBureauExistingLoans,
            final Long loanId, final Long trancheDisbursalId) {
        try {
            final CreditBureauExistingLoan cbel = creditBureauExistingLoans.get(0);
            final CreditBureauProduct cbProduct = this.creditBureauProductRepository
                    .findOneWithNotFoundDetection(cbel.getCreditBureauProductId());
            final CodeValue source = this.codeValueRepository.findByCodeNameAndLabel("ExistingLoanSource", "Credit Bureau");
            List<ExistingLoan> existingLoans = null;
            if (loanId != null && trancheDisbursalId != null) {
                final LoanCreditBureauEnquiry loanCreditBureauEnquiry = this.loanCreditBureauEnquiryRepository
                        .findOneByLoanApplicationIdAndLoanIdAndTrancheDisbursalId(loanApplicationId, loanId, trancheDisbursalId);
                if (loanCreditBureauEnquiry != null) {
                    existingLoans = this.existingLoanRepository
                            .findByLoanApplicationIdAndLoanIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(loanApplicationId,
                                    loanId, source, cbProduct, loanCreditBureauEnquiry.getId());
                }
            } else if (loanId == null && trancheDisbursalId == null) {
                existingLoans = this.existingLoanRepository
                        .findByLoanApplicationIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(loanApplicationId, source,
                                cbProduct, cbel.getLoanEnquiryId());
            }
            if (existingLoans != null && !existingLoans.isEmpty()) {
                this.existingLoanRepository.delete(existingLoans);
            }
            final List<ExistingLoan> newExistingLoans = new ArrayList<>();
            Client client = null;
            CodeValue lender = null;
            CodeValue loanType = null;
            for (final CreditBureauExistingLoan creditBureauExistingLoan : creditBureauExistingLoans) {
                try {
                    final Long clientId = creditBureauExistingLoan.getClientId();
                    /**
                     * Same Client then no need to fetch again client object
                     */
                    if (client == null || client.getId() != clientId) {
                        client = this.clientRepository.findOneWithNotFoundDetection(clientId);
                    }
                    final Long creditBureauProductId = creditBureauExistingLoan.getCreditBureauProductId();
                    final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository
                            .findOneWithNotFoundDetection(creditBureauProductId);
                    final Long loanEnquiryId = creditBureauExistingLoan.getLoanEnquiryId();
                    final LoanCreditBureauEnquiry loanCreditBureauEnquiry = this.loanCreditBureauEnquiryRepository.findOne(loanEnquiryId);
                    final String lenderName = creditBureauExistingLoan.getLenderName();
                    if (lenderName != null && (lender == null || !lender.label().equalsIgnoreCase(lenderName))) {
                        lender = this.codeValueRepository.findByCodeNameAndLabel("LenderOption", lenderName);
                    }
                    final String loanTypeName = creditBureauExistingLoan.getLoanType();
                    if (loanTypeName != null && (loanType == null || !loanType.label().equalsIgnoreCase(loanTypeName))) {
                        loanType = this.codeValueRepository.findByCodeNameAndLabel("LoanType", loanTypeName);
                    }
                    BigDecimal amountBorrowed = null;
                    if (creditBureauExistingLoan.getAmountDisbursed() != null) {
                        amountBorrowed = new BigDecimal(creditBureauExistingLoan.getAmountDisbursed());
                    }
                    BigDecimal currentOutstanding = null;
                    if (creditBureauExistingLoan.getCurrentOutstanding() != null) {
                        currentOutstanding = new BigDecimal(creditBureauExistingLoan.getCurrentOutstanding());
                    }
                    BigDecimal amtOverdue = null;
                    if (creditBureauExistingLoan.getAmountOverdue() != null) {
                        amtOverdue = new BigDecimal(creditBureauExistingLoan.getAmountOverdue());
                    }
                    BigDecimal writtenoffamount = null;
                    if (creditBureauExistingLoan.getWrittenOffAmount() != null) {
                        writtenoffamount = new BigDecimal(creditBureauExistingLoan.getWrittenOffAmount());
                    }
                    BigDecimal installmentAmount = null;
                    if (creditBureauExistingLoan.getInstallmentAmount() != null) {
                        installmentAmount = new BigDecimal(creditBureauExistingLoan.getInstallmentAmount());
                    }

                    final CodeValue externalLoanPurpose = null;
                    Integer status = 0;
                    if (creditBureauExistingLoan.getLoanStatus() != null) {
                        status = creditBureauExistingLoan.getLoanStatus().getValue();
                    }
                    String receivedLoanStatus = creditBureauExistingLoan.getReceivedLoanStatus();
                    
                    LocalDate disbursedDate = null;
                    if (creditBureauExistingLoan.getDisbursedDate() != null) {
                        disbursedDate = new LocalDate(creditBureauExistingLoan.getDisbursedDate());
                    }
                    final Integer loanTenure = null;
                    Integer loanTenurePeriodType = null;
                    if (creditBureauExistingLoan.getRepaymentFrequency() != null) {
                        loanTenurePeriodType = CalendarFrequencyType.from(creditBureauExistingLoan.getRepaymentFrequency()).getValue();
                    }
                    Integer repaymentFrequencyMultipleOf = null;
                    Integer repaymentFrequency = null;
                    if (creditBureauExistingLoan.getRepaymentFrequency() != null) {
                        repaymentFrequency = CalendarFrequencyType.from(creditBureauExistingLoan.getRepaymentFrequency()).getValue();
                    }

                    if (creditBureauExistingLoan.getRepaymentMultiple() != null) {
                        repaymentFrequencyMultipleOf = creditBureauExistingLoan.getRepaymentMultiple();
                    }

                    final LocalDate maturityDate = null;
                    LocalDate closedDate = null;
                    if (creditBureauExistingLoan.getClosedDate() != null) {
                        closedDate = new LocalDate(creditBureauExistingLoan.getClosedDate());
                    }
                    final Integer gt0dpd3mths = null;
                    final Integer dpd30mths12 = null;
                    final Integer dpd30mths24 = null;
                    final Integer dpd60mths24 = null;
                    final String remark = null;
                    final Integer archive = null;

                    final ExistingLoan existingLoan = ExistingLoan.saveExistingLoan(client, loanApplicationId, loanId, source,
                            creditBureauProduct, loanEnquiryId, lender, lenderName, loanType, amountBorrowed, currentOutstanding,
                            amtOverdue, writtenoffamount, loanTenure, loanTenurePeriodType, repaymentFrequency,
                            repaymentFrequencyMultipleOf, installmentAmount, externalLoanPurpose, status, receivedLoanStatus, disbursedDate, maturityDate,
                            closedDate, gt0dpd3mths, dpd30mths12, dpd30mths24, dpd60mths24, remark, archive);
                    existingLoan.setTrancheDisbursalId(loanCreditBureauEnquiry.getTrancheDisbursalId());
                    if (creditBureauExistingLoan.getCreditBureauExistingLoanPaymentDetails() != null) {
                        for (CreditBureauExistingLoanPaymentDetail paymentDetail : creditBureauExistingLoan
                                .getCreditBureauExistingLoanPaymentDetails()) {
                            existingLoan.addPaymentdetail(
                                    CreditBureauPaymentDetails.create(existingLoan, paymentDetail.getDate(), paymentDetail.getDpd()));
                        }

                    }
                    newExistingLoans.add(existingLoan);
                } catch (Exception e) {
                    logger.error(this.gson.toJson(creditBureauExistingLoan));
                }
            }
            if (!newExistingLoans.isEmpty()) {
                this.existingLoanRepository.save(newExistingLoans);
            }
        } catch (Exception e) {
            logger.error(this.gson.toJson(creditBureauExistingLoans));
        }
    }

    @Transactional
    @Override
    public CreditBureauEnquiry createNewEnquiry(final CreditBureauEnquiry creditBureauEnquiry) {
        return creditBureauEnquiryRepository.save(creditBureauEnquiry);
    }
}
