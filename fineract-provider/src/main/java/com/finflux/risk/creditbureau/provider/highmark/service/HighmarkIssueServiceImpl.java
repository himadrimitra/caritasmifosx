package com.finflux.risk.creditbureau.provider.highmark.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.fineract.infrastructure.configuration.data.HighmarkCredentialsData;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoan;
import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoanPaymentDetail;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportFile;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.CreditScore;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.ReportFileType;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkConstants;
import com.finflux.risk.creditbureau.provider.highmark.xsd.issue.HEADERSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.issue.INQUIRY;
import com.finflux.risk.creditbureau.provider.highmark.xsd.issue.ObjectFactory;
import com.finflux.risk.creditbureau.provider.highmark.xsd.issue.REQUESTREQUESTFILE;
import com.finflux.risk.creditbureau.provider.highmark.xsd.response.INDVREPORTFILE;
import com.finflux.risk.creditbureau.provider.highmark.xsd.response.INDVRESPONSES;
import com.finflux.risk.creditbureau.provider.highmark.xsd.response.LOANDETAILS;
import com.finflux.risk.creditbureau.provider.highmark.xsd.response.PRINTABLEREPORT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.response.RESPONSE;
import com.finflux.risk.creditbureau.provider.highmark.xsd.response.RESPONSES;

@Service
public class HighmarkIssueServiceImpl implements HighmarkIssueService {

    final ObjectFactory requestFactory = new ObjectFactory();
    final HttpSendService httpSendService;
    final SimpleDateFormat ddmmYYYYFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    public HighmarkIssueServiceImpl(HttpSendService httpSendService) {
        this.httpSendService = httpSendService;
    }

    @Override
    public CreditBureauResponse sendHighmarkIssue(LoanEnquiryReferenceData loanEnquiryReferenceData,
            HighmarkCredentialsData highmarkCredentialsData) {
        Map<String, String> headersMap = constructHeadersMap(highmarkCredentialsData);

        REQUESTREQUESTFILE requestFile = constructRequestFile(highmarkCredentialsData, loanEnquiryReferenceData);
        return sendHighmarkAT02Request(headersMap, requestFile, highmarkCredentialsData.getURL(), loanEnquiryReferenceData);
    }

    private HEADERSEGMENT getHeaderSegment(String requestType, HighmarkCredentialsData highmarkCredentialsData) {
        HEADERSEGMENT headerSegment = requestFactory.createHEADERSEGMENT();
        headerSegment.setPRODUCTTYP(highmarkCredentialsData.getPRODUCTTYP());
        headerSegment.setPRODUCTVER(highmarkCredentialsData.getPRODUCTVER());
        headerSegment.setREQMBR(highmarkCredentialsData.getREQMBR());
        headerSegment.setSUBMBRID(highmarkCredentialsData.getSUBMBRID());
        headerSegment.setINQDTTM(new SimpleDateFormat("dd-MM-yyyy").format(new Date())); // requestdate
        headerSegment.setREQVOLTYP(highmarkCredentialsData.getREQVOLTYP());
        headerSegment.setREQACTNTYP(requestType); // req type
        headerSegment.setTESTFLG(highmarkCredentialsData.getTESTFLG());
        headerSegment.setUSERID(highmarkCredentialsData.getUSERID());
        headerSegment.setPWD(highmarkCredentialsData.getPWD());
        headerSegment.setAUTHFLG(highmarkCredentialsData.getAUTHFLG());
        headerSegment.setAUTHTITLE(highmarkCredentialsData.getAUTHTITLE());
        headerSegment.setRESFRMT(highmarkCredentialsData.getRESFRMT());
        headerSegment.setMEMBERPREOVERRIDE(highmarkCredentialsData.getMEMBERPREOVERRIDE());
        headerSegment.setRESFRMTEMBD(highmarkCredentialsData.getRESFRMTEMBD());
        headerSegment.setLOSNAME(highmarkCredentialsData.getLOSNAME());
        return headerSegment;
    }

    private Map<String, String> constructHeadersMap(HighmarkCredentialsData highmarkCredentialsData) {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("userId", highmarkCredentialsData.getUSERID());
        headersMap.put("password", highmarkCredentialsData.getPWD());
        headersMap.put("mbrid", highmarkCredentialsData.getREQMBR());
        headersMap.put("productType", highmarkCredentialsData.getPRODUCTTYP());
        headersMap.put("productVersion", highmarkCredentialsData.getPRODUCTVER());
        headersMap.put("reqVolType", highmarkCredentialsData.getREQVOLTYP());
        return headersMap;
    }

    private REQUESTREQUESTFILE constructRequestFile(HighmarkCredentialsData highmarkCredentialsData,
            LoanEnquiryReferenceData loanEnquiryData) {
        INQUIRY inquiry = requestFactory.createINQUIRY();
        inquiry.setINQUIRYUNIQUEREFNO(loanEnquiryData.getRefNumber());
        inquiry.setREPORTID(loanEnquiryData.getAcknowledgementNumber());
        inquiry.setREQUESTDTTM(ddmmYYYYFormat.format(loanEnquiryData.getRequestedDate()));

        HEADERSEGMENT headerSegment = getHeaderSegment(HighmarkConstants.AT02RequestType, highmarkCredentialsData);
        REQUESTREQUESTFILE requestFile = requestFactory.createREQUESTREQUESTFILE();
        requestFile.setHEADERSEGMENT(headerSegment);
        requestFile.getINQUIRY().add(inquiry);
        return requestFile;
    }

    private CreditBureauResponse sendHighmarkAT02Request(Map<String, String> headersMap, REQUESTREQUESTFILE requestFile, String requestURL,
            LoanEnquiryReferenceData loanEnquiryReferenceData) {
        String requestString = null;
        String responseString = null;
        try {
            StringWriter sw = new StringWriter();
            JAXBContext context = JAXBContext.newInstance("com.finflux.risk.creditbureau.provider.highmark.xsd.issue");

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(requestFile, sw);
            requestString = sw.toString().trim();
            requestString = requestString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
            requestString = requestString.replace("\r\n", " ").replace("\n", " ");
            headersMap.put("requestXML", requestString);
            responseString = httpSendService.sendRequest(requestURL, headersMap, "");
            StringReader responseReader = new StringReader(responseString);
            JAXBContext reponseContext;
            reponseContext = JAXBContext.newInstance("com.finflux.risk.creditbureau.provider.highmark.xsd.response");
            Unmarshaller unmarshaller = reponseContext.createUnmarshaller();
            INDVREPORTFILE reportfile = (INDVREPORTFILE) unmarshaller.unmarshal(responseReader);
            // ResponseEntity<REPORTFILE> reportfileResponseEntity =
            // restClient.postForEntity(requestURL, requestFile,
            // REPORTFILE.class);
            // REPORTFILE reportfile = reportfileResponseEntity.getBody();

            return parseAT02Response(reportfile, requestString, responseString, loanEnquiryReferenceData);
        } catch (Exception e) {
            e.printStackTrace();
            final String errorsJson = null ;
            EnquiryResponse enquiryResponse = new EnquiryResponse(null, requestString, responseString, null, null,
                    CreditBureauEnquiryStatus.ERROR, null, errorsJson);
            return new CreditBureauResponse(enquiryResponse, null, null, null);
        }
    }

    private CreditBureauResponse parseAT02Response(INDVREPORTFILE reportfile, String requestString, String responseString,
            LoanEnquiryReferenceData loanEnquiryReferenceData) {
        List<CreditBureauExistingLoan> loanList = null;
        CreditBureauReportFile reportFile = null;
        final String errorsJson = null ;
        try {            
            EnquiryResponse enquiryResponse = null;
            CreditBureauEnquiryStatus creditBureauEnquiryStatus = CreditBureauEnquiryStatus.INVALID;
            if (reportfile != null && reportfile.getINDVREPORTS() != null && reportfile.getINDVREPORTS().getINDVREPORT() != null) {
                if(loanEnquiryReferenceData.getRefNumber().startsWith(HighmarkConstants.CONSUMER_KEY)){                    
                    loanList = updateConsumerExistingLoans(reportfile, loanEnquiryReferenceData, loanList);
                }else{
                    loanList = updateMFIExistingLoans(reportfile, loanEnquiryReferenceData, loanList);                  
                }
                if (reportfile.getINDVREPORTS().getINDVREPORT().getPRINTABLEREPORT() != null) {
                    PRINTABLEREPORT printableReport = reportfile.getINDVREPORTS().getINDVREPORT().getPRINTABLEREPORT();
                    reportFile = new CreditBureauReportFile(printableReport.getFILENAME(), printableReport.getCONTENT().getBytes(),
                            ReportFileType.HTML);
                }
                creditBureauEnquiryStatus = CreditBureauEnquiryStatus.SUCCESS;
            } else if (reportfile != null && reportfile.getINQUIRYSTATUS() != null && reportfile.getINQUIRYSTATUS().getINQUIRY() != null) {
                final com.finflux.risk.creditbureau.provider.highmark.xsd.response.INQUIRY inquiry = reportfile.getINQUIRYSTATUS()
                        .getINQUIRY();
                final String inquiryStatus = inquiry.getRESPONSETYPE();
                creditBureauEnquiryStatus = convertStatus(inquiryStatus);
            }
            
            enquiryResponse = new EnquiryResponse(loanEnquiryReferenceData.getAcknowledgementNumber(), requestString, responseString, null,
                    null, creditBureauEnquiryStatus, loanEnquiryReferenceData.getCbReportId(),errorsJson);
            CreditBureauResponse creditBureauResponse = new CreditBureauResponse(enquiryResponse, null, loanList, reportFile);
            return creditBureauResponse;
        } catch (Exception e) {
            e.printStackTrace();
            
            String acknowledgementNumber=null;
            Date reportGeneratedTime=null;
            String fileName=null;
            String reportId=null;
            List<CreditScore> creditScore = null;
            EnquiryResponse enquiryResponse = new EnquiryResponse(acknowledgementNumber, requestString, responseString, reportGeneratedTime, fileName,
                    CreditBureauEnquiryStatus.ERROR, reportId, errorsJson);
            
            return new CreditBureauResponse(enquiryResponse, creditScore, loanList, reportFile);
        }
    }

    private List<CreditBureauExistingLoan> updateMFIExistingLoans(INDVREPORTFILE reportfile,
            LoanEnquiryReferenceData loanEnquiryReferenceData, List<CreditBureauExistingLoan> loanList) throws ParseException {
        INDVRESPONSES individualResponses = reportfile.getINDVREPORTS().getINDVREPORT().getINDVRESPONSES();
        if (individualResponses != null && individualResponses.getINDVRESPONSELIST() != null && individualResponses.getINDVRESPONSELIST().getINDVRESPONSE() != null
                && !individualResponses.getINDVRESPONSELIST().getINDVRESPONSE().isEmpty()) {
            loanList = new ArrayList<>();
            for (INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE loanItem : individualResponses.getINDVRESPONSELIST()
                    .getINDVRESPONSE()) {

                if (loanItem.getMATCHEDTYPE() != null && loanItem.getMATCHEDTYPE().equalsIgnoreCase("PR")) {
                    final CreditBureauExistingLoan existingLoan = new CreditBureauExistingLoan(
                            loanEnquiryReferenceData.getClientId(), loanEnquiryReferenceData.getLoanApplicationId(),
                            loanEnquiryReferenceData.getLoanId(), loanEnquiryReferenceData.getCbProductId(),
                            loanEnquiryReferenceData.getLoanEnquiryId());

                    INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL loanDetail = loanItem.getLOANDETAIL();
                    existingLoan.setLoanType(loanDetail.getACCTTYPE());
                    existingLoan.setLenderName(loanItem.getMFI());
                    existingLoan.setAmountDisbursed(parseDouble(loanDetail.getDISBURSEDAMT()));
                    existingLoan.setCurrentOutstanding(parseDouble(loanDetail.getCURRENTBAL()));
                    existingLoan.setAmountOverdue(parseDouble(loanDetail.getOVERDUEAMT()));
                    existingLoan.setWrittenOffAmount(parseDouble(loanDetail.getWRITEOFFAMT()));
                    existingLoan.setInstallmentAmount(parseDouble(loanDetail.getINSTALLMENTAMT()));
                    try {
                        if (loanDetail.getDISBURSEDDT() != null) {
                            existingLoan.setDisbursedDate(ddmmYYYYFormat.parse(loanDetail.getDISBURSEDDT()));
                        }
                        if (loanDetail.getCLOSEDDT() != null) {
                            existingLoan.setClosedDate(ddmmYYYYFormat.parse(loanDetail.getCLOSEDDT()));
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    existingLoan.setAmountOverdue(parseDouble(loanDetail.getOVERDUEAMT()));
                    existingLoan.setLoanStatus(convertToLoanStatus(loanDetail.getSTATUS()));
                    if (loanDetail.getFREQ() != null && "BIWEEKLY".equalsIgnoreCase(loanDetail.getFREQ())) {
                        existingLoan.setRepaymentMultiple(2);
                    }

                    existingLoan.setRepaymentFrequency(convertToLoanTenureType(loanDetail.getFREQ()));
                    if (loanDetail.getCOMBINEDPAYMENTHISTORY() != null) {
                        final String[] paymentsArray = loanDetail.getCOMBINEDPAYMENTHISTORY().split("\\|");
                        for (final String paymentHistory : paymentsArray) {
                            if (paymentHistory != null && paymentHistory.trim().length() > 0) {
                                final String monthAndYear = paymentHistory.split(",")[0].trim();
                                final String dpdString = paymentHistory.split(",")[1];
                                final SimpleDateFormat formatter = new SimpleDateFormat("MMM:yyyy");
                                Date date = null;
                                date = formatter.parse(monthAndYear);
                                Integer dpd = 0;
                                dpd = calculateDpdDays(dpdString);
                                CreditBureauExistingLoanPaymentDetail creditBureauExistingLoanPaymentDetail = new CreditBureauExistingLoanPaymentDetail(
                                        date, dpd);
                                existingLoan.addCreditBureauExistingLoanPaymentDetail(creditBureauExistingLoanPaymentDetail);
                            }
                        }
                    }
                    loanList.add(existingLoan);
                }
            }
        }
        return loanList;
    }
    
    private List<CreditBureauExistingLoan> updateConsumerExistingLoans(INDVREPORTFILE reportfile,
            LoanEnquiryReferenceData loanEnquiryReferenceData, List<CreditBureauExistingLoan> loanList) throws ParseException {
        RESPONSES responses = reportfile.getINDVREPORTS().getINDVREPORT().getRESPONSES();
        
        if (responses != null && responses.getRESPONSE() != null && !responses.getRESPONSE().isEmpty()) {
            loanList = new ArrayList<>();
            for (RESPONSE  response: responses.getRESPONSE()) {
                LOANDETAILS loanItem = response.getLOANDETAILS();
                if (loanItem.getMATCHEDTYPE() != null && loanItem.getMATCHEDTYPE().equalsIgnoreCase("PRIMARY")) {
                    final CreditBureauExistingLoan existingLoan = new CreditBureauExistingLoan(
                            loanEnquiryReferenceData.getClientId(), loanEnquiryReferenceData.getLoanApplicationId(),
                            loanEnquiryReferenceData.getLoanId(), loanEnquiryReferenceData.getCbProductId(),
                            loanEnquiryReferenceData.getLoanEnquiryId());

                    //INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL loanDetail = loanItem.getLOANDETAIL();
                    existingLoan.setLoanType(loanItem.getACCTTYPE());
                    existingLoan.setLenderName(loanItem.getCREDITGUARANTOR());
                    existingLoan.setAmountDisbursed(parseDouble(loanItem.getDISBURSEDAMT().replace(",","")));
                    existingLoan.setCurrentOutstanding(parseDouble(loanItem.getCURRENTBAL()));
                    existingLoan.setAmountOverdue(parseDouble(loanItem.getOVERDUEAMT()));
                    existingLoan.setWrittenOffAmount(parseDouble(loanItem.getWRITEOFFAMT()));
                    existingLoan.setInstallmentAmount(parseDouble(loanItem.getINSTALLMENTAMT()));
                    try {
                        if (loanItem.getDISBURSEDDATE() != null) {
                            existingLoan.setDisbursedDate(ddmmYYYYFormat.parse(loanItem.getDISBURSEDDATE()));
                        }
                        if (loanItem.getCLOSEDDATE() != null) {
                            existingLoan.setClosedDate(ddmmYYYYFormat.parse(loanItem.getCLOSEDDATE()));
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    existingLoan.setAmountOverdue(parseDouble(loanItem.getOVERDUEAMT()));
                    existingLoan.setLoanStatus(convertToLoanStatus(loanItem.getACCOUNTSTATUS()));
                    if (loanItem.getFREQUENCY() != null && "BIWEEKLY".equalsIgnoreCase(loanItem.getFREQUENCY())) {
                        existingLoan.setRepaymentMultiple(2);
                    }

                    existingLoan.setRepaymentFrequency(convertToLoanTenureType(loanItem.getFREQUENCY()));
                    if (loanItem.getCOMBINEDPAYMENTHISTORY() != null) {
                        final String[] paymentsArray = loanItem.getCOMBINEDPAYMENTHISTORY().split("\\|");
                        for (final String paymentHistory : paymentsArray) {
                            if (paymentHistory != null && paymentHistory.trim().length() > 0) {
                                final String monthAndYear = paymentHistory.split(",")[0].trim();
                                final String dpdString = paymentHistory.split(",")[1];
                                final SimpleDateFormat formatter = new SimpleDateFormat("MMM:yyyy");
                                Date date = null;
                                date = formatter.parse(monthAndYear);
                                Integer dpd = 0;
                                dpd = calculateDpdDays(dpdString);
                                CreditBureauExistingLoanPaymentDetail creditBureauExistingLoanPaymentDetail = new CreditBureauExistingLoanPaymentDetail(
                                        date, dpd);
                                existingLoan.addCreditBureauExistingLoanPaymentDetail(creditBureauExistingLoanPaymentDetail);
                            }
                        }
                    }
                    loanList.add(existingLoan);
                }
            }
        }
        return loanList;
    }
    
    public String formatAmounts(String amount){
        if(amount != null){
            amount = amount.replaceAll(",", "");            
        }
        return amount;
    }
    private Integer calculateDpdDays(String dpdString) {
        Integer dpd = 0;
        switch (dpdString) {
            case HighmarkConstants.DPD_TYPE_XXX:
            break;
            default:
                if (dpdString.matches("[0-9]*")) {
                    dpd = Integer.parseInt(dpdString);
                }
        }
        return dpd;

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
        } else if ("YEARLY".equalsIgnoreCase(freq)) { return CalendarFrequencyType.YEARLY; }
        return CalendarFrequencyType.INVALID;
    }

    private Double parseDouble(String amount) {
        if (amount == null) { return null; }
        amount = amount.replaceAll("[^0-9.-]", "");
        return Double.parseDouble(amount);
    }

    private LoanStatus convertToLoanStatus(String status) {
        if ("CLOSED".equalsIgnoreCase(status)) {
            return LoanStatus.CLOSED_OBLIGATIONS_MET;
        } else if ("ACTIVE".equalsIgnoreCase(status)) {
            return LoanStatus.ACTIVE;
        } else if ("CURRENT".equalsIgnoreCase(status)) {
            return LoanStatus.APPROVED;
        } else if ("WRITTEN-OFF".equalsIgnoreCase(status)) { return LoanStatus.CLOSED_WRITTEN_OFF; }
        return LoanStatus.INVALID;
    }

    private CreditBureauEnquiryStatus convertStatus(String responseType) {
        if (responseType == null) {
            return CreditBureauEnquiryStatus.INVALID;
        } else if ("ERROR".equalsIgnoreCase(responseType)) {
            return CreditBureauEnquiryStatus.ERROR;
        } else if ("AWAITED".equalsIgnoreCase(responseType)) {
            return CreditBureauEnquiryStatus.PENDING;
        } else if ("INPROCESS".equalsIgnoreCase(responseType)) {
            return CreditBureauEnquiryStatus.PENDING;
        } else if ("COMPLETED".equalsIgnoreCase(responseType)) {
            return CreditBureauEnquiryStatus.SUCCESS;
        } else if ("ACKNOWLEDGEMENT".equalsIgnoreCase(responseType)) { return CreditBureauEnquiryStatus.ACKNOWLEDGED; }
        return CreditBureauEnquiryStatus.INVALID;
    }

}