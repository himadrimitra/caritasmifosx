package com.finflux.risk.creditbureau.provider.highmark.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.fineract.infrastructure.configuration.data.HighmarkCredentialsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryAddressData;
import com.finflux.risk.creditbureau.provider.data.EnquiryDocumentData;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkConstants;
import com.finflux.risk.creditbureau.provider.highmark.xsd.ack.INQUIRY;
import com.finflux.risk.creditbureau.provider.highmark.xsd.ack.REPORTFILE;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.ADDRESSSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.APPLICANTSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.APPLICATIONSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.HEADERSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.ObjectFactory;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.REQUESTREQUESTFILE;

@Service
public class HighmarkRequestServiceImpl implements HighmarkRequestService {

    final ObjectFactory requestFactory = new ObjectFactory();
    final HttpSendService httpSendService;

    // final RestTemplate restClient;

    @Autowired
    public HighmarkRequestServiceImpl(HttpSendService httpSendService) {
        this.httpSendService = httpSendService;

        // HttpComponentsClientHttpRequestFactory
        // httpComponentsClientHttpRequestFactory =
        // new
        // HttpComponentsClientHttpRequestFactory(httpSendService.getHttpClient());
        // this.restClient = new
        // RestTemplate(httpComponentsClientHttpRequestFactory);
        // List<HttpMessageConverter<?>> messageConverters = new
        // ArrayList<HttpMessageConverter<?>>();
        // Jaxb2RootElementHttpMessageConverter jaxbMessageConverter = new
        // Jaxb2RootElementHttpMessageConverter();
        //
        // StringHttpMessageConverter stringHttpMessageConverter = new
        // StringHttpMessageConverter();
        // messageConverters.add(jaxbMessageConverter);
        // messageConverters.add(stringHttpMessageConverter);
        // this.restClient.setMessageConverters(messageConverters);
    }

    @Override
    public CreditBureauResponse sendHighmarkEnquiry(EnquiryReferenceData enquiryReferenceData,
            HighmarkCredentialsData highmarkCredentialsData) {
        Map<String, String> headersMap = constructHeadersMap(highmarkCredentialsData);
        REQUESTREQUESTFILE requestFile = constructRequestFile(enquiryReferenceData, highmarkCredentialsData);
        return sendHighmarkAT01Request(headersMap, requestFile, enquiryReferenceData, highmarkCredentialsData.getURL());
    }

    private CreditBureauResponse sendHighmarkAT01Request(Map<String, String> headersMap, REQUESTREQUESTFILE requestFile,
            EnquiryReferenceData requestData, String requestURL) {
        String requestString = null;
        String responseString = null;
        try {
            java.io.StringWriter sw = new StringWriter();
            JAXBContext context = JAXBContext.newInstance("com.finflux.risk.creditbureau.provider.highmark.xsd.request");

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
            reponseContext = JAXBContext.newInstance("com.finflux.risk.creditbureau.provider.highmark.xsd.ack");
            Unmarshaller unmarshaller = reponseContext.createUnmarshaller();
            REPORTFILE reportfile = (REPORTFILE) unmarshaller.unmarshal(responseReader);
            // ResponseEntity<REPORTFILE> reportfileResponseEntity =
            // restClient.postForEntity(requestURL, requestFile,
            // REPORTFILE.class);
            // REPORTFILE reportfile = reportfileResponseEntity.getBody();

            return parseAT01Response(reportfile, requestString, responseString);
        } catch (Exception e) {
            e.printStackTrace();
            EnquiryResponse enquiryResponse = new EnquiryResponse(null, requestString, responseString, null, null,
                    CreditBureauEnquiryStatus.ERROR, null);
            return new CreditBureauResponse(enquiryResponse, null, null, null);
        }
    }

    private CreditBureauResponse parseAT01Response(REPORTFILE reportFile, String requestString, String responseString) {
        EnquiryResponse enquiryResponse = null;
        if (reportFile != null && reportFile.getINQUIRYSTATUS() != null) {
            REPORTFILE.INQUIRYSTATUS inquiryStatus = reportFile.getINQUIRYSTATUS();
            INQUIRY inquiry = inquiryStatus.getINQUIRY().get(0);
            enquiryResponse = new EnquiryResponse(inquiry.getREPORTID(), requestString, responseString, null, null,
                    convertStatus(inquiry.getREPONSETYPE()), inquiry.getREPORTID());
        } else {
            enquiryResponse = new EnquiryResponse(null, null, null, null, null, CreditBureauEnquiryStatus.ERROR, null);

        }
        return new CreditBureauResponse(enquiryResponse, null, null, null);
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
        HEADERSEGMENT.MFI mfi = requestFactory.createHEADERSEGMENTMFI();
        mfi.setINDV(true);
        mfi.setGROUP(true);
        mfi.setSCORE(true);
        headerSegment.setMFI(mfi);
        HEADERSEGMENT.CONSUMER consumer = requestFactory.createHEADERSEGMENTCONSUMER();
        consumer.setINDV(false);
        consumer.setSCORE(false);
        headerSegment.setCONSUMER(consumer);
        headerSegment.setIOI(true);
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

    private REQUESTREQUESTFILE constructRequestFile(EnquiryReferenceData enquiryReferenceData,
            HighmarkCredentialsData highmarkCredentialsData) {

        REQUESTREQUESTFILE requestFile = requestFactory.createREQUESTREQUESTFILE();
        // List<INQUIRY> inquiryList = new ArrayList<>();
        // for(LoanEnquiryReferenceData loanEnquiryReferenceData:
        // enquiryReferenceData.getLoansReferenceData()) {
        LoanEnquiryReferenceData loanEnquiryReferenceData = enquiryReferenceData.getLoansReferenceData().get(0);
        LoanEnquiryData requestData = loanEnquiryReferenceData.getEnquiryData();

        APPLICANTSEGMENT applicantSegment = requestFactory.createAPPLICANTSEGMENT();

        // Name
        APPLICANTSEGMENT.APPLICANTNAME applicantName = requestFactory.createAPPLICANTSEGMENTAPPLICANTNAME();
        applicantName.setNAME1(requestData.getClientFirstName());
        if (requestData.getClientMiddleName() != null && !requestData.getClientMiddleName().isEmpty()) {
            applicantName.setNAME2(requestData.getClientMiddleName());
            applicantName.setNAME3(requestData.getClientLastName());
        } else {
            applicantName.setNAME2(requestData.getClientLastName());
        }
        applicantSegment.setAPPLICANTNAME(applicantName);

        // DOB
        APPLICANTSEGMENT.DOB dob = requestFactory.createAPPLICANTSEGMENTDOB();
        if ((!(requestData.getClientDOB() == null))) {
            dob.setDOBDATE(new SimpleDateFormat("dd-MM-yyyy").format(requestData.getClientDOB()));
            applicantSegment.setDOB(dob);
        }

        if (requestData.getDocumentList() != null && !requestData.getDocumentList().isEmpty()) {
            APPLICANTSEGMENT.IDS ids = requestFactory.createAPPLICANTSEGMENTIDS();
            List<APPLICANTSEGMENT.IDS.ID> idList = ids.getID();
            for (EnquiryDocumentData enquiryDocumentData : requestData.getDocumentList()) {
                APPLICANTSEGMENT.IDS.ID id = requestFactory.createAPPLICANTSEGMENTIDSID();
                if (enquiryDocumentData.getClientIdentificationType().equalsIgnoreCase("1")) {
                    id.setTYPE("ID02");
                } else if (enquiryDocumentData.getClientIdentificationType().equalsIgnoreCase("2")) {
                    id.setTYPE("ID05");
                } else if (enquiryDocumentData.getClientIdentificationType().equalsIgnoreCase("3")) {
                    id.setTYPE("ID03");
                } else {
                    id.setTYPE("");
                }
                id.setVALUE(enquiryDocumentData.getClientIdentification());
                idList.add(id);
            }
            applicantSegment.setIDS(ids);
        }

        APPLICANTSEGMENT.PHONES.PHONE phone = requestFactory.createAPPLICANTSEGMENTPHONESPHONE();
        phone.setTELENOTYPE("P07");

        APPLICANTSEGMENT.PHONES phones = requestFactory.createAPPLICANTSEGMENTPHONES();
        List<APPLICANTSEGMENT.PHONES.PHONE> phoneList = phones.getPHONE();
        if (requestData.getClientMobileNo() == null || requestData.getClientMobileNo().isEmpty()) {
            phone.setTELENO(null);
        } else {
            phone.setTELENO(new BigInteger(requestData.getClientMobileNo()));
            phoneList.add(phone);
            applicantSegment.setPHONES(phones);
        }
        if (requestData.getClientMobileNo() != null && !requestData.getClientMobileNo().isEmpty()) {
            applicantSegment.setPHONES(phones);
        }

        ADDRESSSEGMENT addressSegment = requestFactory.createADDRESSSEGMENT();
        List<ADDRESSSEGMENT.ADDRESS> addressList = addressSegment.getADDRESS();
        for (EnquiryAddressData addressData : requestData.getAddressList()) {
            ADDRESSSEGMENT.ADDRESS address = requestFactory.createADDRESSSEGMENTADDRESS();
            address.setTYPE("D04");
            address.setADDRESS1(addressData.getClientAddress());
            address.setCITY(addressData.getClientCity());
            address.setSTATE("KA");
            if (addressData.getClientPin() != null && !addressData.getClientPin().isEmpty()) {
                address.setPIN(Integer.parseInt(addressData.getClientPin()));
            }
            if (addressData.getClientAddress() != null && !addressData.getClientAddress().isEmpty()) {
                addressList.add(address);
            }
        }

        APPLICATIONSEGMENT applicationSegment = requestFactory.createAPPLICATIONSEGMENT();
        applicationSegment.setINQUIRYUNIQUEREFNO(loanEnquiryReferenceData.getRefNumber());
        applicationSegment.setCREDTRPTID(highmarkCredentialsData.getCREDTRPTID());
        applicationSegment.setCREDTREQTYP(highmarkCredentialsData.getCREDTREQTYP());
        applicationSegment.setCREDTINQPURPSTYP(highmarkCredentialsData.getCREDTINQPURPSTYP());
        applicationSegment.setCREDTINQPURPSTYPDESC(highmarkCredentialsData.getCREDTINQPURPSTYPDESC());
        applicationSegment.setCREDITINQUIRYSTAGE(highmarkCredentialsData.getCREDITINQUIRYSTAGE());
        applicationSegment.setCREDTRPTTRNDTTM(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        applicationSegment.setMBRID(highmarkCredentialsData.getREQMBR());
        applicationSegment.setBRANCHID(requestData.getBranchId().toString());
        String losAppId = null;
        if (loanEnquiryReferenceData.getLoanId() != null) {
            losAppId = "L" + loanEnquiryReferenceData.getLoanId();
        } else {
            losAppId = "LA" + loanEnquiryReferenceData.getLoanApplicationId();
        }
        applicationSegment.setLOSAPPID(losAppId);
        applicationSegment.setLOANAMOUNT(requestData.getLoanAmount().toString());

        com.finflux.risk.creditbureau.provider.highmark.xsd.request.INQUIRY inquiry = requestFactory.createINQUIRY();
        inquiry.setAPPLICANTSEGMENT(applicantSegment);
        inquiry.setADDRESSSEGMENT(addressSegment);
        inquiry.setAPPLICATIONSEGMENT(applicationSegment);
        // inquiryList.add(inquiry);
        // }

        HEADERSEGMENT headerSegment = getHeaderSegment(HighmarkConstants.AT01RequestType, highmarkCredentialsData);
        requestFile.setHEADERSEGMENT(headerSegment);
        requestFile.setINQUIRY(inquiry);
        return requestFile;
    }

    private CreditBureauEnquiryStatus convertStatus(String responseType) {
        if (responseType == null) {
            return CreditBureauEnquiryStatus.INVALID;
        } else if ("ERROR".equalsIgnoreCase(responseType)) {
            return CreditBureauEnquiryStatus.ERROR;
        } else if ("ACKNOWLEDGEMENT".equalsIgnoreCase(responseType)) { return CreditBureauEnquiryStatus.ACKNOWLEDGED; }
        return CreditBureauEnquiryStatus.INVALID;
    }

}
