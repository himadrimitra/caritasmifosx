package com.finflux.risk.creditbureau.provider.highmark.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryAddressData;
import com.finflux.risk.creditbureau.provider.data.EnquiryClientRelationshipData;
import com.finflux.risk.creditbureau.provider.data.EnquiryDocumentData;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkConstants;
import com.finflux.risk.creditbureau.provider.highmark.domain.HighmarkRequestValidator;
import com.finflux.risk.creditbureau.provider.highmark.xsd.ack.ERROR;
import com.finflux.risk.creditbureau.provider.highmark.xsd.ack.INQUIRY;
import com.finflux.risk.creditbureau.provider.highmark.xsd.ack.REPORTFILE;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.ADDRESSSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.APPLICANTSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.APPLICATIONSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.HEADERSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.ObjectFactory;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.REQUESTREQUESTFILE;
import com.google.gson.Gson;

@Service
public class HighmarkRequestServiceImpl implements HighmarkRequestService {

    final ObjectFactory requestFactory = new ObjectFactory();
    final HttpSendService httpSendService;
    final HighmarkRequestValidator requestValidator;

    // final RestTemplate restClient;

    @Autowired
    public HighmarkRequestServiceImpl(HttpSendService httpSendService,HighmarkRequestValidator requestValidator) {
        this.httpSendService = httpSendService;
        this.requestValidator=requestValidator;

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
            HighmarkCredentialsData highmarkCredentialsData, String key) {
        Map<String, String> headersMap = constructHeadersMap(highmarkCredentialsData);
        REQUESTREQUESTFILE requestFile = constructRequestFile(enquiryReferenceData, highmarkCredentialsData, key);
        return sendHighmarkAT01Request(headersMap, requestFile, enquiryReferenceData, highmarkCredentialsData.getURL());
    }

    private CreditBureauResponse sendHighmarkAT01Request(Map<String, String> headersMap, REQUESTREQUESTFILE requestFile,
            final EnquiryReferenceData requestData, String requestURL) {
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

            return parseAT01Response(reportfile, requestString, responseString, requestData);
        } catch (Exception e) {
            e.printStackTrace();
            final String errorsJson = null ;
            EnquiryResponse enquiryResponse = new EnquiryResponse(null, requestString, responseString, null, null,
                    CreditBureauEnquiryStatus.ERROR, null, errorsJson);
            return new CreditBureauResponse(enquiryResponse, null, null, null);
        }
    }

    private CreditBureauResponse parseAT01Response(REPORTFILE reportFile, String requestString, String responseString, final EnquiryReferenceData enquiryReferenceData) {
        EnquiryResponse enquiryResponse = null;
        String errorsJson = null ;
        if (reportFile != null && reportFile.getINQUIRYSTATUS() != null) {
            REPORTFILE.INQUIRYSTATUS inquiryStatus = reportFile.getINQUIRYSTATUS();
            INQUIRY inquiry = inquiryStatus.getINQUIRY().get(0);
            errorsJson = getErrorsJson(inquiry) ;
            enquiryResponse = new EnquiryResponse(inquiry.getREPORTID(), requestString, responseString, null, null,
                    convertStatus(inquiry.getREPONSETYPE()), inquiry.getREPORTID(), errorsJson);
        } else {
            enquiryResponse = new EnquiryResponse(null, null, null, null, null, CreditBureauEnquiryStatus.ERROR, null, errorsJson);

        }
        return new CreditBureauResponse(enquiryResponse, null, null, null);
    }

    private final String getErrorsJson(final INQUIRY inquiry) {
        String errorsJson = null ;
        if(inquiry.getERRORS() != null) {
            List<ERROR> errors = inquiry.getERRORS().getERROR() ;
            List<Map<String, String>> list = new ArrayList<>() ;
            for(ERROR error: errors) {
                Map<String, String> errorsMap = new HashMap<>() ;
                errorsMap.put("code", error.getCODE()) ;
                errorsMap.put("description", error.getDESCRIPTION()) ;
                list.add(errorsMap) ;
            }
            errorsJson = new Gson().toJson(list) ;
        }
        return errorsJson ;
    }
    
    private HEADERSEGMENT getHeaderSegment(String requestType, HighmarkCredentialsData highmarkCredentialsData, String key) {
        HEADERSEGMENT headerSegment = requestFactory.createHEADERSEGMENT();
        headerSegment.setPRODUCTTYP(highmarkCredentialsData.getPRODUCTTYP());
        headerSegment.setPRODUCTVER(highmarkCredentialsData.getPRODUCTVER());
        headerSegment.setREQMBR(highmarkCredentialsData.getREQMBR());
        headerSegment.setSUBMBRID(highmarkCredentialsData.getSUBMBRID());
        headerSegment.setINQDTTM(new SimpleDateFormat(HighmarkConstants.dateFormat).format(new Date())); // requestdate
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
        setMfiOrConsumer(headerSegment, key);
        headerSegment.setIOI(true);
        return headerSegment;
    }
    
    /*
     * set Mfi or consumer parameter based on type of report
     */
    private void setMfiOrConsumer(HEADERSEGMENT headerSegment, String key) {
        HEADERSEGMENT.MFI mfi = requestFactory.createHEADERSEGMENTMFI();
        HEADERSEGMENT.CONSUMER consumer = requestFactory.createHEADERSEGMENTCONSUMER();
        if (key.equalsIgnoreCase(HighmarkConstants.MFI_KEY)) {
            // mfi parameters
            mfi.setINDV(true);
            mfi.setGROUP(true);
            mfi.setSCORE(false);
            // consumer parameters
            consumer.setINDV(false);
            consumer.setSCORE(false);
        } else {
            // mfi parameters
            mfi.setINDV(false);
            mfi.setGROUP(false);
            mfi.setSCORE(false);
            // consumer parameters
            consumer.setINDV(true);
            consumer.setSCORE(false);
        }
        headerSegment.setMFI(mfi);
        headerSegment.setCONSUMER(consumer);
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
            HighmarkCredentialsData highmarkCredentialsData, String key) {

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
            dob.setDOBDATE(new SimpleDateFormat(HighmarkConstants.dateFormat).format(requestData.getClientDOB()));
            applicantSegment.setDOB(dob);
        }

        if (requestData.getGender() != null) {
            applicantSegment.setGENDER(requestData.getGender());
        }

        if (requestData.getDocumentList() != null && !requestData.getDocumentList().isEmpty()) {
            APPLICANTSEGMENT.IDS documentIds = requestFactory.createAPPLICANTSEGMENTIDS();
            List<APPLICANTSEGMENT.IDS.ID> documentList = documentIds.getID();
            for (EnquiryDocumentData enquiryDocumentData : requestData.getDocumentList()) {
                APPLICANTSEGMENT.IDS.ID documentId = requestFactory.createAPPLICANTSEGMENTIDSID();
                if (enquiryDocumentData.getClientIdentificationTypeId() != null) {
                    if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID01_PASSPORT())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID01_PASSPORT);
                    } else if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID02_VOTER_ID())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID02_VOTER_ID);
                    } else if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID03_UID())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID03_UID);
                    } else if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID04_OTHER())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID04_OTHER);
                    } else if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID05_RATION_CARD())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID05_RATION_CARD);
                    } else if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID06_DRIVING_CARD())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID06_DRIVING_CARD);
                    } else if (enquiryDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            highmarkCredentialsData.getDOCUMENT_TYPE_ID07_PAN())) {
                        documentId.setTYPE(HighmarkConstants.DOCUMENT_TYPE_ID07_PAN);
                    } else {
                        //documentId.setTYPE("");
                    	continue;
                    }
                } else {
                    documentId.setTYPE("");
                }
                documentId.setVALUE(enquiryDocumentData.getClientIdentification());
                documentList.add(documentId);
            }
            applicantSegment.setIDS(documentIds);
        }

        if (requestData.getRelationshipList() != null && !requestData.getRelationshipList().isEmpty()) {
            APPLICANTSEGMENT.RELATIONS relations = requestFactory.createAPPLICANTSEGMENTRELATIONS();
            List<APPLICANTSEGMENT.RELATIONS.RELATION> relationshipList = relations.getRELATION();
            for (EnquiryClientRelationshipData relationshipData : requestData.getRelationshipList()) {
                APPLICANTSEGMENT.RELATIONS.RELATION relation = requestFactory.createAPPLICANTSEGMENTRELATIONSRELATION();
                if (highmarkCredentialsData.getRELATIONSHIP_TYPE_K01_FATHER().equalsIgnoreCase(relationshipData.getRelationshipTypeId())) {
                    relation.setTYPE(HighmarkConstants.RELATIONSHIP_TYPE_K01_FATHER);
                    relation.setNAME(relationshipData.getName());
                } else if (highmarkCredentialsData.getRELATIONSHIP_TYPE_SPOUSE().equalsIgnoreCase(relationshipData.getRelationshipTypeId())) {
                    if (highmarkCredentialsData.getGENDER_TYPE_MALE().equalsIgnoreCase(requestData.getGenderId())) {
                        relation.setTYPE(HighmarkConstants.RELATIONSHIP_TYPE_K06_WIFE);
                        relation.setNAME(relationshipData.getName());
                    } else if (highmarkCredentialsData.getGENDER_TYPE_FEMALE().equalsIgnoreCase(requestData.getGenderId())) {
                        relation.setTYPE(HighmarkConstants.RELATIONSHIP_TYPE_K02_HUSBAND);
                        relation.setNAME(relationshipData.getName());
                    }
                }
                relationshipList.add(relation);
            }
            applicantSegment.setRELATIONS(relations);
        }



        //Relations
//        APPLICANTSEGMENT.RELATIONS relations = requestFactory.createAPPLICANTSEGMENTRELATIONS();
//        List<APPLICANTSEGMENT.RELATIONS.RELATION> relationList = relations.getRELATION();
//
//        APPLICANTSEGMENT.RELATIONS.RELATION spouseRelation = requestFactory.createAPPLICANTSEGMENTRELATIONSRELATION();
//
//        spouseRelation.setNAME();
//        spouseRelation.setTYPE();
//
//        APPLICANTSEGMENT.RELATIONS.RELATION fatherRelation = requestFactory.createAPPLICANTSEGMENTRELATIONSRELATION();
//
//        fatherRelation.setNAME();
//        fatherRelation.setTYPE();

        //Phones

        APPLICANTSEGMENT.PHONES.PHONE phone = requestFactory.createAPPLICANTSEGMENTPHONESPHONE();
        phone.setTELENOTYPE("");

        APPLICANTSEGMENT.PHONES phones = requestFactory.createAPPLICANTSEGMENTPHONES();
        List<APPLICANTSEGMENT.PHONES.PHONE> phoneList = phones.getPHONE();
        if (requestData.getClientMobileNo() == null || requestData.getClientMobileNo().isEmpty()) {
            phone.setTELENO(null);
        } else {
            phone.setTELENOTYPE(HighmarkConstants.PHONE_TYPE_P03_MOBILE);
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
            if (addressData.getClientAddressTypeId() != null && addressData.getClientAddressTypeId().length() > 0) {
                if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D01_RESIDENCE())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D01_RESIDENCE);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D02_COMPANY())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D02_COMPANY);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D03_RESCUMOFF())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D03_RESCUMOFF);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D04_PERMANENT())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D04_PERMANENT);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D05_CURRENT())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D05_CURRENT);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D06_FOREIGN())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D06_FOREIGN);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D07_MILITARY())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D07_MILITARY);
                } else if (addressData.getClientAddressTypeId().equalsIgnoreCase(highmarkCredentialsData.getADDRESS_TYPE_D08_OTHER())) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D08_OTHER);
                } else if (addressData.getClientAddressTypeId() != null) {
                    address.setTYPE(HighmarkConstants.ADDRESS_TYPE_D08_OTHER);
                } else {
                    address.setTYPE("");
                }
            } else {
                address.setTYPE("");
            }
            address.setADDRESS1(addressData.getClientAddress());
            address.setCITY(addressData.getClientCity());
            if (addressData.getClientStateCode() != null) {
                address.setSTATE(addressData.getClientStateCode());
            } else {
                address.setSTATE("");
            }
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
        applicationSegment.setCREDTRPTTRNDTTM(new SimpleDateFormat(HighmarkConstants.dateFormat).format(new Date()));
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

        HEADERSEGMENT headerSegment = getHeaderSegment(HighmarkConstants.AT01RequestType, highmarkCredentialsData, key);
        requestFile.setHEADERSEGMENT(headerSegment);
        requestFile.setINQUIRY(inquiry);
        this.requestValidator.validateEnquiryRequest(requestFile);
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
