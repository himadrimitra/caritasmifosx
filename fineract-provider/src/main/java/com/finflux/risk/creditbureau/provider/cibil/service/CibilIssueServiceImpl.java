package com.finflux.risk.creditbureau.provider.cibil.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.configuration.data.CibilCredentialsData;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.cibil.CibilConstants;
import com.finflux.risk.creditbureau.provider.cibil.response.CibilResponse;
import com.finflux.risk.creditbureau.provider.cibil.response.data.OwnAccountData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.ScoreData;
import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoan;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportFile;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.CreditScore;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.ReportFileType;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryRepository;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.service.ContentServiceUtil;

@Service
public class CibilIssueServiceImpl implements CibilIssueService {

    private final static Logger logger = LoggerFactory.getLogger(CibilIssueServiceImpl.class);
    private final CreditBureauEnquiryRepository creditBureauEnquiryRepository;
    private final ContentServiceUtil contentService ;
    
    @Autowired
    public CibilIssueServiceImpl(final CreditBureauEnquiryRepository creditBureauEnquiryRepository,
    		final ContentServiceUtil contentService) {
        this.creditBureauEnquiryRepository = creditBureauEnquiryRepository;
        this.contentService = contentService ;
    }

    @Override
    public CreditBureauResponse sendEquifaxIssue(LoanEnquiryReferenceData loanEnquiryReferenceData,
            final CibilCredentialsData cibilCredentials) {
        final CreditBureauEnquiry enquiry = this.creditBureauEnquiryRepository.findOne(loanEnquiryReferenceData.getEnquiryId());
        final String request = contentService.getContent(enquiry.getRequestLocation()) ;
        final String response =  contentService.getContent(enquiry.getResponseLocation()) ;
        try {
            return parseResponse(request, response, loanEnquiryReferenceData, cibilCredentials);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return createErrorResponse(request, response);
        }
    }
    
    private CreditBureauResponse parseResponse(final String requestString, final String responseString,
            final LoanEnquiryReferenceData loanEnquiryReferenceData, final CibilCredentialsData cibilCredentials) {
        CreditBureauResponse creditBureauResponse = null;
        try {
            final CibilResponse cibilResponse = new CibilResponse(responseString.getBytes());
            if (cibilResponse.isError()) {
                creditBureauResponse = createErrorResponse(requestString, responseString);
            } else {
                List<CreditBureauExistingLoan> loanList = new ArrayList<>();
                List<OwnAccountData> accounts = cibilResponse.getOwnAccountsList();
                for (OwnAccountData account : accounts) {
                    final CreditBureauExistingLoan existingLoan = new CreditBureauExistingLoan(loanEnquiryReferenceData.getClientId(),
                            loanEnquiryReferenceData.getLoanApplicationId(), loanEnquiryReferenceData.getLoanId(),
                            loanEnquiryReferenceData.getCbProductId(), loanEnquiryReferenceData.getLoanEnquiryId());

                    existingLoan.setLoanType(account.getAccountType());
                    existingLoan.setLenderName(account.getMemberShortName());
                    if (account.getSanctionedAmount() != null) {
                        existingLoan.setAmountDisbursed(new BigDecimal(account.getSanctionedAmount()).doubleValue());
                    } else {
                        existingLoan.setAmountDisbursed(BigDecimal.ZERO.doubleValue());
                    }
                    if (account.getCurrentBalance() != null) {
                        existingLoan.setCurrentOutstanding(new BigDecimal(account.getCurrentBalance()).doubleValue());
                    } else {
                        existingLoan.setAmountDisbursed(BigDecimal.ZERO.doubleValue());
                    }
                    if (account.getOverdueAmount() != null) {
                        existingLoan.setAmountOverdue(new BigDecimal(account.getOverdueAmount()).doubleValue());
                    } else {
                        existingLoan.setAmountOverdue(BigDecimal.ZERO.doubleValue());
                    }

                    if (account.getTotalWrittenOfAmount() != null) {
                        existingLoan.setWrittenOffAmount(new BigDecimal(account.getTotalWrittenOfAmount()).doubleValue());
                    }
                    if (account.getEmiAmount() != null) {
                        existingLoan.setInstallmentAmount(new BigDecimal(account.getEmiAmount()).doubleValue());
                    }

                    if (account.getDateOpened() != null) {
                        existingLoan.setDisbursedDate(CibilResponse.dateFormat_DDMMYYYY.parse(account.getDateOpened()));
                    }
                    if (account.getClosedDate() != null) {
                        existingLoan.setClosedDate(CibilResponse.dateFormat_DDMMYYYY.parse(account.getClosedDate()));
                    }
                    if (account.getOverdueAmount() != null) {
                        existingLoan.setAmountOverdue(new BigDecimal(account.getOverdueAmount()).doubleValue());
                    }
                    if (account.getClosedDate() != null) {
                        existingLoan.setLoanStatus(convertToLoanStatus(CibilConstants.CLOSED));
                        existingLoan.setReceivedLoanStatus(CibilConstants.CLOSED);
                    } else {
                        existingLoan.setLoanStatus(convertToLoanStatus(CibilConstants.ACTIVE));
                        existingLoan.setReceivedLoanStatus(CibilConstants.ACTIVE);
                    }
                    existingLoan.setRepaymentFrequency(convertToLoanTenureType(account.getPaymentFrequency()));
                    loanList.add(existingLoan);
                }
                List<ScoreData> creditScores = cibilResponse.getCreditScore();
                List<CreditScore> scores = null;
                if (creditScores != null && creditScores.size() > 0) {
                    scores = new ArrayList<>();
                    for (ScoreData data : creditScores) {
                        scores.add(data.toCreditScore());
                    }
                }
                final String errorsJson = null;
                EnquiryResponse enquiryResponse = new EnquiryResponse(loanEnquiryReferenceData.getAcknowledgementNumber(), requestString,
                        responseString, null, null, CreditBureauEnquiryStatus.SUCCESS, loanEnquiryReferenceData.getCbReportId(),
                        errorsJson);
                final String reportRequest = getReportRequest(requestString);
                byte[] reportData = CibilConnector.getConsumerCreditReport(reportRequest, cibilCredentials);
                CreditBureauReportFile reportFile = new CreditBureauReportFile("ReportFile", reportData, ReportFileType.XML);
                creditBureauResponse = new CreditBureauResponse(enquiryResponse, scores, loanList, reportFile);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            creditBureauResponse = createErrorResponse(requestString, responseString);
        }
        return creditBureauResponse;
    }

    private CreditBureauResponse createErrorResponse(final String requestString, final String responseString) {
        CibilResponse response = null;
        if (responseString != null) {
            response = new CibilResponse(responseString.getBytes());
        }
        return createErrorResponse(requestString, response);
    }

    private CreditBureauResponse createErrorResponse(final String requestString, final CibilResponse response) {
        List<CreditBureauExistingLoan> loanList = null;
        CreditBureauReportFile reportFile = null;
        String acknowledgementNumber = null;
        Date reportGeneratedTime = null;
        String fileName = null;
        String reportId = null;
        List<CreditScore> creditScore = null;
        String errorsJson = null;
        String responseString = null;
        if (response != null) {
            errorsJson = response.getErrorSegment().getErrorsAsJson();
            responseString = response.getResponseAsString();
        }
        EnquiryResponse enquiryResponse = new EnquiryResponse(acknowledgementNumber, requestString, responseString, reportGeneratedTime,
                fileName, CreditBureauEnquiryStatus.ERROR, reportId, errorsJson);
        return new CreditBureauResponse(enquiryResponse, creditScore, loanList, reportFile);
    }

    private String getReportRequest(final String request) {
        char[] requestChars = request.toCharArray();
        requestChars[93] = '0';
        requestChars[94] = '0';
        requestChars[109] = '0';
        requestChars[110] = '2';
        return new String(requestChars);
    }

    private CalendarFrequencyType convertToLoanTenureType(final String freq) {
        /*
         * 01 = Weekly 02 = Fortnightly 03 = Monthly 04 = Quarterly
         */
        if (freq == null) {
            return CalendarFrequencyType.INVALID;
        } else if ("01".equalsIgnoreCase(freq)) {
            return CalendarFrequencyType.WEEKLY;
        } else if ("02".equalsIgnoreCase(freq)) {
            return CalendarFrequencyType.WEEKLY;
        } else if ("03".equalsIgnoreCase(freq)) {
            return CalendarFrequencyType.MONTHLY;
        } else if ("04".equalsIgnoreCase(freq)) { return CalendarFrequencyType.MONTHLY; }
        return CalendarFrequencyType.INVALID;
    }

    private LoanStatus convertToLoanStatus(final String status) {
        LoanStatus loanStatus = LoanStatus.INVALID;
        switch (status) {
            case CibilConstants.ACTIVE:
                loanStatus = LoanStatus.ACTIVE;
            break;
            case CibilConstants.CLOSED:
                loanStatus = LoanStatus.CLOSED_OBLIGATIONS_MET;
        }
        return loanStatus;
    }
}
