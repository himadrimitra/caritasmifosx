package com.finflux.risk.creditbureau.provider.equifax.service;

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.fineract.infrastructure.configuration.data.EquifaxCredentialsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.finflux.risk.creditbureau.provider.equifax.EquifaxConstants;
import com.finflux.risk.creditbureau.provider.equifax.domain.EquifaxRequestValidator;
import com.finflux.risk.creditbureau.provider.equifax.xsd.AdditionalNameTypeDetails;
import com.finflux.risk.creditbureau.provider.equifax.xsd.FamilyInfo;
import com.finflux.risk.creditbureau.provider.equifax.xsd.GenderTypeCode;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryAddressType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryCommonInputAddressType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryCommonInputPhoneType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryPhoneType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryRequestType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryResponseType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.MaritalStatusOptions;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ReportFormatOptions;
import com.finflux.risk.creditbureau.provider.equifax.xsd.RequestBodyType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.RequestHeaderType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.StateCodeOptions;

/**
 * 
 * This class is responsible to connect Equifax servers to get Customer credit
 * report.
 */
@Service
public class EquifaxRequestServiceImpl implements EquifaxRequestService {

    private final static Logger logger = LoggerFactory.getLogger(EquifaxRequestServiceImpl.class);
    private final EquifaxRequestValidator requestValidator;

    @Autowired
    public EquifaxRequestServiceImpl(final EquifaxRequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    @Override
    public CreditBureauResponse sendEquifaxEnquiry(final EnquiryReferenceData enquiryReferenceData,
            final EquifaxCredentialsData equifaxCredentialsData) {
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
        QName qname = new QName(equifaxCredentialsData.getqName(), equifaxCredentialsData.getqNameVersion());
        String URL = equifaxCredentialsData.getUrl();
        java.net.URL endpoint = null;
        try {
            endpoint = new java.net.URL(URL);
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        }
        javax.xml.ws.Service service = javax.xml.ws.Service.create(endpoint, qname);
        CreditReportWSInquiryPortType inquiryPortType = service.getPort(CreditReportWSInquiryPortType.class);
        BindingProvider bp = (BindingProvider) inquiryPortType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);

        InquiryRequestType input = new InquiryRequestType();
        RequestHeaderType requestHeader = new RequestHeaderType();
        populateHeaderProperties(requestHeader, equifaxCredentialsData);
        input.setRequestHeader(requestHeader);
        RequestBodyType requestBody = constructRetailRequest(enquiryReferenceData, equifaxCredentialsData);
        this.requestValidator.validateEnquiryRequest(requestBody);
        input.setRequestBody(requestBody);
        InquiryResponseType responseType = inquiryPortType.getConsumerCreditReport(input);
        CreditBureauResponse response = null;
        try {
            response = parseResponse(input, responseType, enquiryReferenceData.getLoansReferenceData().get(0));
        } catch (JAXBException e) {
            final String errorsJson = null ;
            logger.error(e.getMessage());
            EnquiryResponse enquiryResponse = new EnquiryResponse(null, generateRequestString(input), null, null, null,
                    CreditBureauEnquiryStatus.ERROR, null, errorsJson);
            return new CreditBureauResponse(enquiryResponse, null, null, null);
        } finally {
            System.getProperties().remove("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize");
        }

        return response;
    }

    private void populateHeaderProperties(final RequestHeaderType requestHeader, final EquifaxCredentialsData equifaxCredentialsData) {
        requestHeader.setCustomerId(equifaxCredentialsData.getCustomerIdentifier());
        requestHeader.setUserId(equifaxCredentialsData.getUserId());
        requestHeader.setMemberNumber(equifaxCredentialsData.getMemberNumber());
        requestHeader.setPassword(equifaxCredentialsData.getPassword());
        requestHeader.setProductCode(equifaxCredentialsData.getProductCode());
        requestHeader.setProductVersion(equifaxCredentialsData.getProductVersion());
        requestHeader.setSecurityCode(equifaxCredentialsData.getSecurityCode());
        requestHeader.setReportFormat(ReportFormatOptions.XML);
    }

    private RequestBodyType constructRetailRequest(final EnquiryReferenceData enquiryReferenceData,
            final EquifaxCredentialsData equifaxCredentialsData) {
        List<LoanEnquiryReferenceData> loanRefListData = enquiryReferenceData.getLoansReferenceData();
        final LoanEnquiryReferenceData loanReference = loanRefListData.get(0);
        final LoanEnquiryData loanEnquiryData = loanReference.getEnquiryData();

        RequestBodyType requestBody = new RequestBodyType();
        requestBody.setInquiryPurpose("0E"); // Hard coding as we need to have
                                             // mapping between loan products
                                             // and actual inquiry purpose
        requestBody.setTransactionAmount(loanReference.getEnquiryData().getLoanAmount());
        requestBody.setFirstName(loanReference.getEnquiryData().getClientFirstName());
        requestBody.setLastName(loanReference.getEnquiryData().getClientLastName());
        if (loanReference.getEnquiryData().getClientMiddleName() != null) {
            requestBody.setMiddleName(loanReference.getEnquiryData().getClientMiddleName());
        }

        // Setting the Gender value
        if (loanEnquiryData.getGenderId() != null) {
            if (equifaxCredentialsData.getGenderTypeFemale() != null
                    && loanEnquiryData.getGenderId().equals(equifaxCredentialsData.getGenderTypeFemale())) {
                requestBody.setGender(GenderTypeCode.FEMALE.value());
            } else if (equifaxCredentialsData.getGenderTypeMale() != null
                    && loanEnquiryData.getGenderId().equals(equifaxCredentialsData.getGenderTypeMale())) {
                requestBody.setGender(GenderTypeCode.MALE.value());
            }
        }

        // Setting client addresses
        List<EnquiryAddressData> clientAddressList = loanReference.getEnquiryData().getAddressList();
        if (clientAddressList != null && clientAddressList.size() > 0) {
            InquiryCommonInputAddressType addressList = new InquiryCommonInputAddressType();
            for (EnquiryAddressData clientAddressData : clientAddressList) {
                InquiryAddressType requestAddress = new InquiryAddressType();
                requestAddress.setAddressLine(clientAddressData.getClientAddress());
                requestAddress.setPostal(clientAddressData.getClientPin());
                requestAddress.setState(StateCodeOptions.fromValue(clientAddressData.getClientStateCode()));
                requestAddress.setCity(clientAddressData.getClientCity());
                addressList.getInquiryAddress().add(requestAddress);
            }
            requestBody.setInquiryAddresses(addressList);
        }

        // Setting the DOB
        if (loanReference.getEnquiryData().getClientDOB() != null) {
            XMLGregorianCalendar dateOfBirth = getXMLGregorianCalendarDate(loanReference.getEnquiryData().getClientDOB());
            if (dateOfBirth != null) {
                requestBody.setDOB(dateOfBirth);
            }
        }

        // We are not capturing marital status. Do we need to check relations
        // data?
        requestBody.setMaritalStatus(MaritalStatusOptions.NOT_GIVEN);

        // We are capturing only mobile number
        if (loanReference.getEnquiryData().getClientMobileNo() != null) {
            InquiryPhoneType iphonetype = new InquiryPhoneType();
            iphonetype.setNumber(loanReference.getEnquiryData().getClientMobileNo());
            InquiryCommonInputPhoneType inquiryCommonInputPhoneType = new InquiryCommonInputPhoneType();
            List<InquiryPhoneType> list = inquiryCommonInputPhoneType.getInquiryPhone();
            list.add(iphonetype);
            requestBody.setInquiryPhones(inquiryCommonInputPhoneType);
        }

        // Setting the client relations data
        List<EnquiryClientRelationshipData> relations = loanReference.getEnquiryData().getRelationshipList();
        if (relations != null && relations.size() > 0) {
            FamilyInfo family = new FamilyInfo();
            List<AdditionalNameTypeDetails> familyList = family.getAdditionalNameInfo();
            for (EnquiryClientRelationshipData clientRelationData : relations) {

                AdditionalNameTypeDetails familyName = new AdditionalNameTypeDetails();
                familyName.setAdditionalName(clientRelationData.getName());
                familyList.add(familyName);
                if (clientRelationData.getRelationshipTypeId() != null) {
                    if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeFather())) {
                        familyName.setAdditionalNameType(EquifaxConstants.FATHER);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeHusband())) {
                        familyName.setAdditionalNameType(EquifaxConstants.HUSBAND);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeBrother())) {
                        familyName.setAdditionalNameType(EquifaxConstants.BROTHER);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeSon())) {
                        familyName.setAdditionalNameType(EquifaxConstants.SON);
                    } else if (clientRelationData.getRelationshipTypeId()
                            .equalsIgnoreCase(equifaxCredentialsData.getRelationTypeSonInLaw())) {
                        familyName.setAdditionalNameType(EquifaxConstants.SONINLAW);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getRelationTypeFatherInLaw())) {
                        familyName.setAdditionalNameType(EquifaxConstants.FATHERINLAW);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getRelationTypeBrotherInLaw())) {
                        familyName.setAdditionalNameType(EquifaxConstants.BROTHERINLAW);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeMother())) {
                        familyName.setAdditionalNameType(EquifaxConstants.MOTHER);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeWife())) {
                        familyName.setAdditionalNameType(EquifaxConstants.WIFE);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeSister())) {
                        familyName.setAdditionalNameType(EquifaxConstants.SISTER);
                    } else if (clientRelationData.getRelationshipTypeId()
                            .equalsIgnoreCase(equifaxCredentialsData.getRelationTypeDaughter())) {
                        familyName.setAdditionalNameType(EquifaxConstants.DAUGHTER);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getRelationTypeSisterInLaw())) {
                        familyName.setAdditionalNameType(EquifaxConstants.SISTERINLAW);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getRelationTypeDaughterInLaw())) {
                        familyName.setAdditionalNameType(EquifaxConstants.DAUGHTERINLAW);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getRelationTypeMotherInLaw())) {
                        familyName.setAdditionalNameType(EquifaxConstants.MOTHERINLAW);
                    } else if (clientRelationData.getRelationshipTypeId().equalsIgnoreCase(equifaxCredentialsData.getRelationTypeOther())) {
                        familyName.setAdditionalNameType(EquifaxConstants.OTHER);
                    } else if (clientRelationData.getRelationshipTypeId().equals(equifaxCredentialsData.getRelationTypeSpouse())) {
                        if (equifaxCredentialsData.getGenderTypeFemale().equals(loanEnquiryData.getGenderId())) {
                            familyName.setAdditionalNameType(EquifaxConstants.HUSBAND);
                        } else {
                            familyName.setAdditionalNameType(EquifaxConstants.WIFE);
                        }
                    }
                } else {
                    familyName.setAdditionalNameType("");
                }

            }
            requestBody.setFamilyDetails(family);
        }

        // Setting the client identifiers
        List<EnquiryDocumentData> clientDocumentsList = loanReference.getEnquiryData().getDocumentList();
        if (clientDocumentsList != null && clientDocumentsList.size() > 0) {
            for (EnquiryDocumentData clientDocumentData : clientDocumentsList) {
                if (clientDocumentData.getClientIdentificationTypeId() != null) {
                    if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypePassport())) {
                        requestBody.setPassportId(clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypeVoterId())) {
                        requestBody.setVoterId(clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypeAadhar())) {
                        requestBody.setNationalIdCard(clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypeRationCard())) {
                        requestBody.setRationCard(clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypeDrivingLicense())) {
                        requestBody.setDriverLicense(clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypePan())) {
                        requestBody.setPANId(clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId().equalsIgnoreCase(
                            equifaxCredentialsData.getDocumentTypeOther())) {
                        requestBody.setAdditionalId1(clientDocumentData.getClientIdentification());
                    }
                }
            }
        }
        return requestBody;

    }

    private String generateRequestString(final InquiryRequestType input) {
        String requestString = null;
        JAXBContext requestContext;
        try {
            requestContext = JAXBContext.newInstance(InquiryRequestType.class);
            StringWriter requestWriter = new StringWriter();
            Marshaller requestMarshaller = requestContext.createMarshaller();
            requestMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            requestString = requestWriter.toString().trim();
            requestString = requestString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
            requestString = requestString.replace("\r\n", " ").replace("\n", " ");
            requestMarshaller.marshal(input, requestWriter);
        } catch (JAXBException e) {
            logger.error(e.getMessage());
        }
        return requestString;
    }

    private CreditBureauResponse parseResponse(InquiryRequestType input, InquiryResponseType responseType,
            final LoanEnquiryReferenceData loanEnquiryReferenceData) throws JAXBException {

        // Request
        JAXBContext requestContext = JAXBContext.newInstance(InquiryRequestType.class);
        StringWriter requestWriter = new StringWriter();
        Marshaller requestMarshaller = requestContext.createMarshaller();
        requestMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        requestMarshaller.marshal(input, requestWriter);
        String requestString = requestWriter.toString().trim();
        requestString = requestString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
        requestString = requestString.replace("\r\n", " ").replace("\n", " ");

        // Response
        JAXBContext jc = JAXBContext.newInstance(InquiryResponseType.class);
        EnquiryResponse enquiryResponse = null;

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(responseType, writer);
        String responseString = writer.toString();
        final String errorsJson = null ;
        enquiryResponse = new EnquiryResponse(loanEnquiryReferenceData.getAcknowledgementNumber(), requestString, responseString, null,
                null, CreditBureauEnquiryStatus.SUCCESS, loanEnquiryReferenceData.getCbReportId(), errorsJson);
        CreditBureauResponse creditBureauResponse = new CreditBureauResponse(enquiryResponse, null, null, null);
        return creditBureauResponse;

    }

    private XMLGregorianCalendar getXMLGregorianCalendarDate(final Date date) {
        XMLGregorianCalendar dobXml = null;
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(date);
        try {
            dobXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return dobXml;
    }
}
