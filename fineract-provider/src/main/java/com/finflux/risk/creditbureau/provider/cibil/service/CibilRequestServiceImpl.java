package com.finflux.risk.creditbureau.provider.cibil.service;

import java.util.List;

import org.apache.fineract.infrastructure.configuration.data.CibilCredentialsData;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.cibil.CibilConstants;
import com.finflux.risk.creditbureau.provider.cibil.CibilStateCodes;
import com.finflux.risk.creditbureau.provider.cibil.request.Address;
import com.finflux.risk.creditbureau.provider.cibil.request.AddressSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.CibilRequest;
import com.finflux.risk.creditbureau.provider.cibil.request.HeaderSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.IdentificationSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.NameSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.TelephoneSegment;
import com.finflux.risk.creditbureau.provider.cibil.response.CibilResponse;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryAddressData;
import com.finflux.risk.creditbureau.provider.data.EnquiryDocumentData;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryData;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;

/**
 * 
 * This class is responsible to connect CIBIL servers to get Customer credit
 * report.
 */
@Service
public class CibilRequestServiceImpl implements CibilRequestService {

    public CibilRequestServiceImpl() {

    }

    @Override
    public CreditBureauResponse sendCibilEnquiry(final EnquiryReferenceData enquiryReferenceData,
            final CibilCredentialsData cibilCredentialsData) {
        final CibilRequest request = createRequest(enquiryReferenceData, cibilCredentialsData);
        String requestString = request.prepareTuefPacket();
        CibilResponse cibilResponse = CibilConnector.getConsumerCreditReport(request, cibilCredentialsData);
        return parseResponse(requestString, cibilResponse, enquiryReferenceData.getLoansReferenceData().get(0));
    }

    private final CibilRequest createRequest(final EnquiryReferenceData enquiryReferenceData,
            final CibilCredentialsData cibilCredentialsData) {
        final CibilRequest request = new CibilRequest();
        final HeaderSegment requestHeader = request.createHeaderSegment();
        populateHeaderProperties(requestHeader, cibilCredentialsData, enquiryReferenceData);
        constructRetailRequest(request, enquiryReferenceData, cibilCredentialsData);
        return request;
    }

    private void populateHeaderProperties(final HeaderSegment requestHeader, final CibilCredentialsData cibilCredentialsData,
            final EnquiryReferenceData enquiryReferenceData) {
        requestHeader.setUserName(cibilCredentialsData.getUserId());
        requestHeader.setPassword(cibilCredentialsData.getPassword());
        List<LoanEnquiryReferenceData> loanRefListData = enquiryReferenceData.getLoansReferenceData();
        final LoanEnquiryReferenceData loanReference = loanRefListData.get(0);
        final LoanEnquiryData loanEnquiryData = loanReference.getEnquiryData();
        requestHeader.setLoanAmount(loanEnquiryData.getLoanAmount());
        requestHeader.setMemberReferenceNumber("CTPL-FF");
    }

    private void constructRetailRequest(final CibilRequest request, final EnquiryReferenceData enquiryReferenceData,
            final CibilCredentialsData cibilCredentialsData) {
        List<LoanEnquiryReferenceData> loanRefListData = enquiryReferenceData.getLoansReferenceData();
        final LoanEnquiryReferenceData loanReference = loanRefListData.get(0);

        // PopulateName
        populateNameSegment(request, loanReference, cibilCredentialsData);

        // Identifiers
        populateIdentifiers(request, loanReference, cibilCredentialsData);
        // We are capturing only mobile number
        populateTelephoneSegment(request, loanReference);

        // Setting client addresses
        populateAdressSegment(request, loanReference, cibilCredentialsData);
        return;

    }

    private void populateNameSegment(final CibilRequest request, final LoanEnquiryReferenceData loanReference,
            final CibilCredentialsData cibilCredentialsData) {
        final LoanEnquiryData loanEnquiryData = loanReference.getEnquiryData();
        NameSegment nameSegment = request.createNameSegment();
        nameSegment.setDateOfBirth(loanReference.getEnquiryData().getClientDOB());
        // Setting the Gender value
        if (loanEnquiryData.getGenderId() != null) {
            if (cibilCredentialsData.getGenderTypeFemale() != null
                    && loanEnquiryData.getGenderId().equals(cibilCredentialsData.getGenderTypeFemale())) {
                nameSegment.setGender(NameSegment.FEMALE);
            } else if (cibilCredentialsData.getGenderTypeMale() != null
                    && loanEnquiryData.getGenderId().equals(cibilCredentialsData.getGenderTypeMale())) {
                nameSegment.setGender(NameSegment.MALE);
            }
        }

        nameSegment.addName(loanReference.getEnquiryData().getClientFirstName());
        nameSegment.addName(loanReference.getEnquiryData().getClientLastName());
        if (loanReference.getEnquiryData().getClientMiddleName() != null) {
            nameSegment.addName(loanReference.getEnquiryData().getClientMiddleName());
        }
    }

    private void populateIdentifiers(final CibilRequest request, final LoanEnquiryReferenceData loanReference,
            final CibilCredentialsData cibilCredentialsData) {
        IdentificationSegment identificationSegment = request.createIdentificationSegment();

        // Setting the client identifiers
        List<EnquiryDocumentData> clientDocumentsList = loanReference.getEnquiryData().getDocumentList();
        if (clientDocumentsList != null && clientDocumentsList.size() > 0) {
            for (EnquiryDocumentData clientDocumentData : clientDocumentsList) {
                if (clientDocumentData.getClientIdentificationTypeId() != null) {
                    if (clientDocumentData.getClientIdentificationTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getDocumentTypePassport())) {
                        identificationSegment.addIdentifier(CibilConstants.PASSPORT, clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getDocumentTypeVoterId())) {
                        identificationSegment.addIdentifier(CibilConstants.VOTER_ID, clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getDocumentTypeAadhar())) {
                        identificationSegment.addIdentifier(CibilConstants.AADHAAR_UID, clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getDocumentTypeRationCard())) {
                        identificationSegment.addIdentifier(CibilConstants.RATIONCARD, clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getDocumentTypeDrivingLicense())) {
                        identificationSegment.addIdentifier(CibilConstants.DRIVINGLICENSE, clientDocumentData.getClientIdentification());
                    } else if (clientDocumentData.getClientIdentificationTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getDocumentTypePan())) {
                        identificationSegment.addIdentifier(CibilConstants.PAN, clientDocumentData.getClientIdentification());
                    }
                }
            }
        }
    }

    private void populateTelephoneSegment(final CibilRequest request, final LoanEnquiryReferenceData loanReference) {
        if (loanReference.getEnquiryData().getClientMobileNo() != null) {
            TelephoneSegment telephoneSegment = request.createTelephoneSegment();
            telephoneSegment.addMobilePhone(loanReference.getEnquiryData().getClientMobileNo());
        }
    }

    private void populateAdressSegment(final CibilRequest request, final LoanEnquiryReferenceData loanReference,
            final CibilCredentialsData cibilCredentialsData) {
        AddressSegment addressSegment = request.createAddressSegment();
        List<EnquiryAddressData> clientAddressList = loanReference.getEnquiryData().getAddressList();
        if (clientAddressList != null && clientAddressList.size() > 0) {
            for (EnquiryAddressData clientAddressData : clientAddressList) {
                String type = Address.NOTCATEGORIZED;
                if (clientAddressData.getClientAddressTypeId() != null && clientAddressData.getClientAddressTypeId().length() > 0) {
                    if (clientAddressData.getClientAddressTypeId().equalsIgnoreCase(cibilCredentialsData.getAddressTypeResidence())) {
                        type = Address.RESIDENT;
                    } else if (clientAddressData.getClientAddressTypeId()
                            .equalsIgnoreCase(cibilCredentialsData.getAddressTypepermanent())) {
                        type = Address.PERMANENT;
                    } else if (clientAddressData.getClientAddressTypeId().equalsIgnoreCase(cibilCredentialsData.getAddressTypeOffice())) {
                        type = Address.OFFICE;
                    }
                }
                Address address = new Address(type, clientAddressData.getClientAddress(),
                        CibilStateCodes.toCibilCode(clientAddressData.getClientStateName()), clientAddressData.getClientPin(),
                        Address.RENTED);
                addressSegment.addAddress(address);
            }
        }
    }

    private CreditBureauResponse parseResponse(final String requestString, final CibilResponse response,
            final LoanEnquiryReferenceData loanEnquiryReferenceData) {
        final String errorsJson = null ;
        EnquiryResponse enquiryResponse = new EnquiryResponse(loanEnquiryReferenceData.getAcknowledgementNumber(), requestString,
                response.getResponseAsString(), null, null, CreditBureauEnquiryStatus.SUCCESS, loanEnquiryReferenceData.getCbReportId(), errorsJson);
        CreditBureauResponse creditBureauResponse = new CreditBureauResponse(enquiryResponse, null, null, null);
        return creditBureauResponse;

    }
}
