package com.finflux.risk.creditbureau.provider.equifax.service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryResponseType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ReportType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ScoreType;
import com.finflux.risk.creditbureau.provider.highmark.xsd.issue.ObjectFactory;

@Service
public class EquifaxIssueServiceImpl implements EquifaxIssueService {
	 private final static Logger logger = LoggerFactory.getLogger(EquifaxRequestServiceImpl.class);
	final ObjectFactory requestFactory = new ObjectFactory();
	final SimpleDateFormat ddmmYYYYFormat = new SimpleDateFormat("dd-MM-yyyy");
	private final CreditBureauEnquiryRepository creditBureauEnquiryRepository;

	@Autowired
	public EquifaxIssueServiceImpl(final CreditBureauEnquiryRepository creditBureauEnquiryRepository) {
		this.creditBureauEnquiryRepository = creditBureauEnquiryRepository;
	}

	@Override
	public CreditBureauResponse sendEquifaxIssue(LoanEnquiryReferenceData loanEnquiryReferenceData) {
		final CreditBureauEnquiry enquiry = this.creditBureauEnquiryRepository
				.findOne(loanEnquiryReferenceData.getEnquiryId());
		try {
			return parseResponse(enquiry.getRequest(), enquiry.getResponse(), loanEnquiryReferenceData);
		} catch (JAXBException e) {
			logger.error(e.getMessage());
			 EnquiryResponse enquiryResponse = new EnquiryResponse(null, enquiry.getRequest(), enquiry.getResponse(), null, null,
	                    CreditBureauEnquiryStatus.ERROR, null);
	            return new CreditBureauResponse(enquiryResponse, null, null, null);
		}
	}

	@SuppressWarnings("unused")
	private CreditBureauResponse parseResponse(final String requstXml, final String responseXml,
			final LoanEnquiryReferenceData loanEnquiryReferenceData) throws JAXBException {
		// Response
		JAXBContext jc = JAXBContext.newInstance(InquiryResponseType.class);
		EnquiryResponse enquiryResponse = null;
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringReader responseReader = new StringReader(responseXml);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		int numberOfAccounts = 0;
		int numberOfPasDueAccounts = 0;
		BigDecimal totalPastDue = java.math.BigDecimal.ZERO;
		BigDecimal totalBalanceAmount = java.math.BigDecimal.ZERO;
		BigDecimal totalWrittenOfAmount = java.math.BigDecimal.ZERO;
		String scoreValue = "";

		InquiryResponseType creditReport = (InquiryResponseType) unmarshaller.unmarshal(responseReader);
		ReportType reportData = creditReport.getReportData();
		List<CreditBureauExistingLoan> loanList = new ArrayList<>();
		CreditBureauEnquiryStatus status = CreditBureauEnquiryStatus.SUCCESS;
		if (reportData.getError().size() > 0) {
			status = CreditBureauEnquiryStatus.ERROR;
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

				existingLoan.setLoanType(account.getAccountType());
				existingLoan.setLenderName(account.getInstitution());
				existingLoan.setAmountDisbursed(account.getDisbursedAmount().doubleValue());
				existingLoan.setCurrentOutstanding(account.getCurrentBalance().doubleValue());
				existingLoan.setAmountOverdue(0.0);
				if (account.getWriteOffAmount() != null) {
					existingLoan.setWrittenOffAmount(account.getWriteOffAmount().doubleValue());
				}
				existingLoan.setInstallmentAmount(account.getInstallmentAmount().doubleValue());
				if (account.getDateSanctioned() != null) {
					existingLoan.setDisbursedDate(account.getDateSanctioned().toGregorianCalendar().getTime());
				}
				if (account.getDateClosed() != null) {
					existingLoan.setClosedDate(account.getDateSanctioned().toGregorianCalendar().getTime());
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

		enquiryResponse = new EnquiryResponse(loanEnquiryReferenceData.getAcknowledgementNumber(), requstXml,
				responseXml, null, null, status, loanEnquiryReferenceData.getCbReportId());
		CreditBureauReportFile reportFile = new CreditBureauReportFile("ReportFile", responseXml.getBytes(),
				ReportFileType.XML);
		CreditBureauResponse creditBureauResponse = new CreditBureauResponse(enquiryResponse, null, loanList,
				reportFile);
		return creditBureauResponse;

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
