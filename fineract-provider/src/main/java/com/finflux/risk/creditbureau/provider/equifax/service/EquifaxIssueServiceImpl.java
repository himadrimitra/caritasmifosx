package com.finflux.risk.creditbureau.provider.equifax.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.creditReport.Controller;
import com.creditReport.pojo.InputParmeterDTO;
import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoan;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportFile;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.ReportFileType;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryRepository;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.equifax.EquifaxConstants;
import com.finflux.risk.creditbureau.provider.equifax.xsd.AccountDetailsType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.AccountType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.CreditReportSummaryType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ErrorType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryResponseType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ReportType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ScoreType;
import com.finflux.risk.creditbureau.provider.highmark.xsd.issue.ObjectFactory;
import com.finflux.risk.creditbureau.provider.service.ContentServiceUtil;
import com.google.gson.Gson;

@Service
public class EquifaxIssueServiceImpl implements EquifaxIssueService {

	private final static Logger logger = LoggerFactory.getLogger(EquifaxRequestServiceImpl.class);
	private final String DIRECTORY = System.getProperty("user.home") + File.separator;

	final ObjectFactory requestFactory = new ObjectFactory();
	final SimpleDateFormat ddmmYYYYFormat = new SimpleDateFormat("dd-MM-yyyy");
	private final CreditBureauEnquiryRepository creditBureauEnquiryRepository;
	private final ContentServiceUtil contentService ;
	@Autowired
	public EquifaxIssueServiceImpl(final CreditBureauEnquiryRepository creditBureauEnquiryRepository,
			final ContentServiceUtil contentService) {
		this.creditBureauEnquiryRepository = creditBureauEnquiryRepository;
		this.contentService = contentService ;
	}

	@Override
	public CreditBureauResponse sendEquifaxIssue(LoanEnquiryReferenceData loanEnquiryReferenceData) {
		final CreditBureauEnquiry enquiry = this.creditBureauEnquiryRepository
				.findOne(loanEnquiryReferenceData.getEnquiryId());
		final String request = contentService.getContent(enquiry.getRequestLocation()) ;
                final String response =  contentService.getContent(enquiry.getResponseLocation()) ;
		System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
		try {
			return parseResponse(request, response, loanEnquiryReferenceData);
		} catch (JAXBException e) {
			logger.error(e.getMessage());
			final String errorsJson = null ;
			EnquiryResponse enquiryResponse = new EnquiryResponse(null, request, response,
					null, null, CreditBureauEnquiryStatus.ERROR, null, errorsJson);
			return new CreditBureauResponse(enquiryResponse, null, null, null);
		} finally {
			System.getProperties().remove("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize");
		}
	}

	@SuppressWarnings("unused")
	private CreditBureauResponse parseResponse(final String requstXml, final String responseXml,
			final LoanEnquiryReferenceData loanEnquiryReferenceData) throws JAXBException {
		// Response
		JAXBContext jc = JAXBContext.newInstance(InquiryResponseType.class);
		EnquiryResponse enquiryResponse = null;
		StringReader responseReader = new StringReader(responseXml);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		int numberOfAccounts = 0;
		int numberOfPasDueAccounts = 0;
		BigDecimal totalPastDue = java.math.BigDecimal.ZERO;
		BigDecimal totalBalanceAmount = java.math.BigDecimal.ZERO;
		BigDecimal totalWrittenOfAmount = java.math.BigDecimal.ZERO;
		String scoreValue = "";
		String errorsJson = null ;
		InquiryResponseType creditReport = (InquiryResponseType) unmarshaller.unmarshal(responseReader);
		ReportType reportData = creditReport.getReportData();
		List<CreditBureauExistingLoan> loanList = new ArrayList<>();
		CreditBureauEnquiryStatus status = CreditBureauEnquiryStatus.SUCCESS;
		if (reportData.getError().size() > 0) {
			status = getStatusForErros(reportData.getError());
			errorsJson = constructErrorJson(reportData.getError()) ;
		} else {
			CreditReportSummaryType reportSummary = reportData.getAccountSummary();
			if (reportSummary.getNoOfActiveAccounts() != null) {
				numberOfAccounts = reportSummary.getNoOfActiveAccounts();
			}

			if (reportSummary.getNoOfPastDueAccounts() != null) {
				numberOfPasDueAccounts = reportSummary.getNoOfPastDueAccounts();
			}

			if (reportSummary.getTotalPastDue() != null) {
				totalPastDue = reportSummary.getTotalPastDue();
			}

			if (reportSummary.getTotalBalanceAmount() != null) {
				totalBalanceAmount = reportSummary.getTotalBalanceAmount();
			}

			if (reportSummary.getTotalWrittenOffAmount() != null) {
				totalWrittenOfAmount = reportSummary.getTotalWrittenOffAmount();
			}

			if (reportData.getScore() != null) {
				ScoreType score = reportData.getScore();
				scoreValue = score.getValue();
			}
			// final CreditScore creditScore = new CreditScore();
			AccountDetailsType accountDetails = reportData.getAccountDetails();
			List<AccountType> accounts = accountDetails.getAccount();

			for (AccountType account : accounts) {
				final CreditBureauExistingLoan existingLoan = new CreditBureauExistingLoan(
						loanEnquiryReferenceData.getClientId(), loanEnquiryReferenceData.getLoanApplicationId(),
						loanEnquiryReferenceData.getLoanId(), loanEnquiryReferenceData.getCbProductId(),
						loanEnquiryReferenceData.getLoanEnquiryId());

				if(account.getAccountType() != null) {
				    existingLoan.setLoanType(account.getAccountType());    
				}
				if(account.getInstitution() != null) {
				    existingLoan.setLenderName(account.getInstitution());    
				}
				if(account.getDisbursedAmount() != null) {
				    existingLoan.setAmountDisbursed(account.getDisbursedAmount().doubleValue());    
				}else {
				    existingLoan.setAmountDisbursed(0.0);
				}
				
				if(account.getCurrentBalance() != null) {
				    existingLoan.setCurrentOutstanding(account.getCurrentBalance().doubleValue());    
				}else {
				    existingLoan.setCurrentOutstanding(0.0);
				}
				
				existingLoan.setAmountOverdue(0.0);
				if(account.getWriteOffAmount() != null) {
					existingLoan.setWrittenOffAmount(account.getWriteOffAmount().doubleValue());
				}else {
				    existingLoan.setWrittenOffAmount(0.0);
				}
				if(account.getInstallmentAmount() != null) {
				    existingLoan.setInstallmentAmount(account.getInstallmentAmount().doubleValue());
				}else {
				    existingLoan.setInstallmentAmount(0.0);
				}
				
				if (account.getDateSanctioned() != null) {
					existingLoan.setDisbursedDate(account.getDateSanctioned().toGregorianCalendar().getTime());
				}
				if (account.getDateClosed() != null) {
					existingLoan.setClosedDate(account.getDateClosed().toGregorianCalendar().getTime());
				}
				if (account.getPastDueAmount() != null) {
					existingLoan.setAmountOverdue(account.getPastDueAmount().doubleValue());
				}
				existingLoan.setLoanStatus(convertToLoanStatus(account.getAccountStatus()));
				existingLoan.setReceivedLoanStatus(account.getAccountStatus());
				existingLoan.setRepaymentFrequency(convertToLoanTenureType(account.getTermFrequency()));
				loanList.add(existingLoan);
			}
		}
		byte[] reportBytes = null;
		if(responseXml != null) {
			reportBytes = generateReportFile(responseXml);	
		}
		enquiryResponse = new EnquiryResponse(loanEnquiryReferenceData.getAcknowledgementNumber(), requstXml,
				responseXml, null, null, status, loanEnquiryReferenceData.getCbReportId(), errorsJson);
		CreditBureauReportFile reportFile = new CreditBureauReportFile("ReportFile", reportBytes, ReportFileType.HTML);
		CreditBureauResponse creditBureauResponse = new CreditBureauResponse(enquiryResponse, null, loanList,
				reportFile);
		return creditBureauResponse;

	}
	
    public CreditBureauEnquiryStatus getStatusForErros(List<ErrorType> errors){
	    if(errors.size()==1 && errors.get(0).getErrorCode().equalsIgnoreCase("00")){
	        return CreditBureauEnquiryStatus.SUCCESS;
	    }
	    return CreditBureauEnquiryStatus.ERROR;
    }

    private String constructErrorJson(final List<ErrorType> errors) {
        String errorsJson = null ;
        List<Map<String, String>> list = new ArrayList<>() ;
        for(ErrorType error: errors) {
            Map<String, String> errorsMap = new HashMap<>() ;
            errorsMap.put("code", error.getErrorCode()) ;
            errorsMap.put("description", error.getErrorMsg()) ;
            list.add(errorsMap) ;
        }
        errorsJson = new Gson().toJson(list) ;
        return errorsJson ;
    }
	private byte[] generateReportFile(final String responseXml) {
		JAXBContext jc;
		String fileName = null ;
		try {
			jc = JAXBContext.newInstance(com.creditReport.ists.InquiryResponseType.class);
			StringReader responseReader = new StringReader(responseXml);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			com.creditReport.ists.InquiryResponseType creditReport = (com.creditReport.ists.InquiryResponseType) unmarshaller
					.unmarshal(responseReader);
			InputParmeterDTO parameterDto = new InputParmeterDTO();
			parameterDto.setStrCustType(EquifaxConstants.REPORT_CUSTOMER_TYPE);
			parameterDto.setGroup(EquifaxConstants.REPORT_GROUP_TYPE);
			parameterDto.setContents(EquifaxConstants.REPORT_REPORT_TYPE);
			parameterDto.setStrSummaryFileName("REP");
			parameterDto.setStrSummaryFilePath(DIRECTORY);
			parameterDto.setOutputfilename("REP");
			parameterDto.setStrOutputPath(DIRECTORY);
			Controller con = new Controller();
			
			con.generateIstsHtml(creditReport, parameterDto);
			String prefix = getPrefix(creditReport.getReportData().getError());
			fileName = DIRECTORY + prefix + creditReport.getInquiryResponseHeader().getReportOrderNO() + ".html" ;						
			return IOUtils.toByteArray(new FileInputStream(fileName));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fileName != null) {
				FileUtils.deleteQuietly(new File(fileName)) ;
			}
		}
		return null;
	}
	
    private String getPrefix(List<com.creditReport.ists.ErrorType> errors) {
        if (errors.size() == 1 && errors.get(0).getErrorCode().equalsIgnoreCase("00")) { return "NoHit_"; }
        return "Hit_";
    }

	private CalendarFrequencyType convertToLoanTenureType(final String freq) {
		if (freq == null) {
			return CalendarFrequencyType.INVALID;
		} else if ("DAILY".equalsIgnoreCase(freq)) {
			return CalendarFrequencyType.DAILY;
		} else if ("WEEKLY".equalsIgnoreCase(freq)) {
			return CalendarFrequencyType.WEEKLY;
		} else if ("BIWEEKLY".equalsIgnoreCase(freq)) {
			return CalendarFrequencyType.WEEKLY;
		} else if ("MONTHLY".equalsIgnoreCase(freq)) {
			return CalendarFrequencyType.MONTHLY;
		} else if ("YEARLY".equalsIgnoreCase(freq)) {
			return CalendarFrequencyType.YEARLY;
		}
		return CalendarFrequencyType.INVALID;
	}

	private LoanStatus convertToLoanStatus(final String status) {
		LoanStatus loanStatus = LoanStatus.INVALID;
		switch (status) {
		case EquifaxConstants.DOUBTFUL:
		case EquifaxConstants.ACCOUNT_INACTIVE:
		case EquifaxConstants.DUE_61_TO_90:
		case EquifaxConstants.DUE_31_TO_60:
		case EquifaxConstants.DUE_120_TO_179:
		case EquifaxConstants.DUE_90_TO_119:
		case EquifaxConstants.CURRENT_ACCOUNT:
		case EquifaxConstants.RESTRUCTURED_GV_MANDATE:
		case EquifaxConstants.DUE_30_TO_59:
		case EquifaxConstants.DUE_1_TO_30:
		case EquifaxConstants.RESTRUCTURED_NC:
		case EquifaxConstants.DUE_91_TO_120:
		case EquifaxConstants.DUE_121_TO_179:
		case EquifaxConstants.RESTRUCTURED_LOAN:
		case EquifaxConstants.DUE_60_TO_89:
		case EquifaxConstants.DUE_180_TO_MORE:
		case EquifaxConstants.SUB_STANDARD:
		case EquifaxConstants.SPECIAL_MENTION:
		case EquifaxConstants.DUE_1_TO_29:
		case EquifaxConstants.SUIT_FILED:
			loanStatus = LoanStatus.ACTIVE;
			break;
		case EquifaxConstants.NEW_ACCOUNT:
		case EquifaxConstants.SUBMITTED:
			loanStatus = LoanStatus.SUBMITTED_AND_PENDING_APPROVAL;
			break;
		case EquifaxConstants.APPROVED:
			loanStatus = LoanStatus.APPROVED;
			break;
		case EquifaxConstants.REJECTED:
			loanStatus = LoanStatus.REJECTED;
			break;
		case EquifaxConstants.WILLFUL_DEFAULT:
		case EquifaxConstants.WRITTEN_OFF:
		case EquifaxConstants.POST_WRITTEN_OFF:
			loanStatus = LoanStatus.CLOSED_WRITTEN_OFF;
			break;
		case EquifaxConstants.SETTLED:
		case EquifaxConstants.CLOSED:
			loanStatus = LoanStatus.CLOSED_OBLIGATIONS_MET;
		}
		return loanStatus;
	}
}