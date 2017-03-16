package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.equifax.services.eport.ws.schemas._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ConsumerDisputes_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "ConsumerDisputes");
    private final static QName _InquiryRequest_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "InquiryRequest");
    private final static QName _InquiryResponse_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "InquiryResponse");
    private final static QName _PANId_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "PANId");
    private final static QName _PassportId_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "PassportId");
    private final static QName _VoterId_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "VoterId");
    private final static QName _HomePhone_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "HomePhone");
    private final static QName _MobilePhone_QNAME = new QName("http://services.equifax.com/eport/ws/schemas/1.0", "MobilePhone");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.equifax.services.eport.ws.schemas._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsumerDisputesType }
     * 
     */
    public ConsumerDisputesType createConsumerDisputesType() {
        return new ConsumerDisputesType();
    }

    /**
     * Create an instance of {@link InquiryRequestType }
     * 
     */
    public InquiryRequestType createInquiryRequestType() {
        return new InquiryRequestType();
    }

    /**
     * Create an instance of {@link InquiryResponseType }
     * 
     */
    public InquiryResponseType createInquiryResponseType() {
        return new InquiryResponseType();
    }

    /**
     * Create an instance of {@link LandlineType }
     * 
     */
    public LandlineType createLandlineType() {
        return new LandlineType();
    }

    /**
     * Create an instance of {@link IdRequest }
     * 
     */
    public IdRequest createIdRequest() {
        return new IdRequest();
    }

    /**
     * Create an instance of {@link RequestHeader }
     * 
     */
    public RequestHeader createRequestHeader() {
        return new RequestHeader();
    }

    /**
     * Create an instance of {@link RequestBody }
     * 
     */
    public RequestBody createRequestBody() {
        return new RequestBody();
    }

    /**
     * Create an instance of {@link NsdlRequest }
     * 
     */
    public NsdlRequest createNsdlRequest() {
        return new NsdlRequest();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link UidaiRequest }
     * 
     */
    public UidaiRequest createUidaiRequest() {
        return new UidaiRequest();
    }

    /**
     * Create an instance of {@link VoterRequest }
     * 
     */
    public VoterRequest createVoterRequest() {
        return new VoterRequest();
    }

    /**
     * Create an instance of {@link IdResponse }
     * 
     */
    public IdResponse createIdResponse() {
        return new IdResponse();
    }

    /**
     * Create an instance of {@link ResponseHeader }
     * 
     */
    public ResponseHeader createResponseHeader() {
        return new ResponseHeader();
    }

    /**
     * Create an instance of {@link ResponseBody }
     * 
     */
    public ResponseBody createResponseBody() {
        return new ResponseBody();
    }

    /**
     * Create an instance of {@link VidNsdlResponse }
     * 
     */
    public VidNsdlResponse createVidNsdlResponse() {
        return new VidNsdlResponse();
    }

    /**
     * Create an instance of {@link NsdlResponse }
     * 
     */
    public NsdlResponse createNsdlResponse() {
        return new NsdlResponse();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link VidUidaiResponse }
     * 
     */
    public VidUidaiResponse createVidUidaiResponse() {
        return new VidUidaiResponse();
    }

    /**
     * Create an instance of {@link UidaiResponse }
     * 
     */
    public UidaiResponse createUidaiResponse() {
        return new UidaiResponse();
    }

    /**
     * Create an instance of {@link VidVoterResponse }
     * 
     */
    public VidVoterResponse createVidVoterResponse() {
        return new VidVoterResponse();
    }

    /**
     * Create an instance of {@link VoterResponse }
     * 
     */
    public VoterResponse createVoterResponse() {
        return new VoterResponse();
    }

    /**
     * Create an instance of {@link VerifyIdServiceFault }
     * 
     */
    public VerifyIdServiceFault createVerifyIdServiceFault() {
        return new VerifyIdServiceFault();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link NameType }
     * 
     */
    public NameType createNameType() {
        return new NameType();
    }

    /**
     * Create an instance of {@link AliasNameInfoType }
     * 
     */
    public AliasNameInfoType createAliasNameInfoType() {
        return new AliasNameInfoType();
    }

    /**
     * Create an instance of {@link PersonalInfoType }
     * 
     */
    public PersonalInfoType createPersonalInfoType() {
        return new PersonalInfoType();
    }

    /**
     * Create an instance of {@link PlaceOfBirthInfoType }
     * 
     */
    public PlaceOfBirthInfoType createPlaceOfBirthInfoType() {
        return new PlaceOfBirthInfoType();
    }

    /**
     * Create an instance of {@link AgeInfo }
     * 
     */
    public AgeInfo createAgeInfo() {
        return new AgeInfo();
    }

    /**
     * Create an instance of {@link PhoneType }
     * 
     */
    public PhoneType createPhoneType() {
        return new PhoneType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link EmailAddressType }
     * 
     */
    public EmailAddressType createEmailAddressType() {
        return new EmailAddressType();
    }

    /**
     * Create an instance of {@link IDType }
     * 
     */
    public IDType createIDType() {
        return new IDType();
    }

    /**
     * Create an instance of {@link IdentificationType }
     * 
     */
    public IdentificationType createIdentificationType() {
        return new IdentificationType();
    }

    /**
     * Create an instance of {@link EmployerDetailsType }
     * 
     */
    public EmployerDetailsType createEmployerDetailsType() {
        return new EmployerDetailsType();
    }

    /**
     * Create an instance of {@link EnquirySummaryType }
     * 
     */
    public EnquirySummaryType createEnquirySummaryType() {
        return new EnquirySummaryType();
    }

    /**
     * Create an instance of {@link EnquiryType }
     * 
     */
    public EnquiryType createEnquiryType() {
        return new EnquiryType();
    }

    /**
     * Create an instance of {@link CreditReportSummaryType }
     * 
     */
    public CreditReportSummaryType createCreditReportSummaryType() {
        return new CreditReportSummaryType();
    }

    /**
     * Create an instance of {@link RecentActivitiesType }
     * 
     */
    public RecentActivitiesType createRecentActivitiesType() {
        return new RecentActivitiesType();
    }

    /**
     * Create an instance of {@link AccountType }
     * 
     */
    public AccountType createAccountType() {
        return new AccountType();
    }

    /**
     * Create an instance of {@link AdditionalMFIDetailsType }
     * 
     */
    public AdditionalMFIDetailsType createAdditionalMFIDetailsType() {
        return new AdditionalMFIDetailsType();
    }

    /**
     * Create an instance of {@link MFIAdditionalAddressType }
     * 
     */
    public MFIAdditionalAddressType createMFIAdditionalAddressType() {
        return new MFIAdditionalAddressType();
    }

    /**
     * Create an instance of {@link MFIAdditionalIdentityInfoType }
     * 
     */
    public MFIAdditionalIdentityInfoType createMFIAdditionalIdentityInfoType() {
        return new MFIAdditionalIdentityInfoType();
    }

    /**
     * Create an instance of {@link MFIAddlAdrsDetailsType }
     * 
     */
    public MFIAddlAdrsDetailsType createMFIAddlAdrsDetailsType() {
        return new MFIAddlAdrsDetailsType();
    }

    /**
     * Create an instance of {@link RelationInfoType }
     * 
     */
    public RelationInfoType createRelationInfoType() {
        return new RelationInfoType();
    }

    /**
     * Create an instance of {@link AccountHistoryType }
     * 
     */
    public AccountHistoryType createAccountHistoryType() {
        return new AccountHistoryType();
    }

    /**
     * Create an instance of {@link MonthlyDetailType }
     * 
     */
    public MonthlyDetailType createMonthlyDetailType() {
        return new MonthlyDetailType();
    }

    /**
     * Create an instance of {@link AccountDetailsType }
     * 
     */
    public AccountDetailsType createAccountDetailsType() {
        return new AccountDetailsType();
    }

    /**
     * Create an instance of {@link ParentsInfoType }
     * 
     */
    public ParentsInfoType createParentsInfoType() {
        return new ParentsInfoType();
    }

    /**
     * Create an instance of {@link IDAndContactType }
     * 
     */
    public IDAndContactType createIDAndContactType() {
        return new IDAndContactType();
    }

    /**
     * Create an instance of {@link GlossaryInfoType }
     * 
     */
    public GlossaryInfoType createGlossaryInfoType() {
        return new GlossaryInfoType();
    }

    /**
     * Create an instance of {@link AccountStatusType }
     * 
     */
    public AccountStatusType createAccountStatusType() {
        return new AccountStatusType();
    }

    /**
     * Create an instance of {@link GroupCreditSummaryType }
     * 
     */
    public GroupCreditSummaryType createGroupCreditSummaryType() {
        return new GroupCreditSummaryType();
    }

    /**
     * Create an instance of {@link IncomeDetailsType }
     * 
     */
    public IncomeDetailsType createIncomeDetailsType() {
        return new IncomeDetailsType();
    }

    /**
     * Create an instance of {@link FamilyInfo }
     * 
     */
    public FamilyInfo createFamilyInfo() {
        return new FamilyInfo();
    }

    /**
     * Create an instance of {@link AccountInputType }
     * 
     */
    public AccountInputType createAccountInputType() {
        return new AccountInputType();
    }

    /**
     * Create an instance of {@link InquiryAddressType }
     * 
     */
    public InquiryAddressType createInquiryAddressType() {
        return new InquiryAddressType();
    }

    /**
     * Create an instance of {@link InquiryPhoneType }
     * 
     */
    public InquiryPhoneType createInquiryPhoneType() {
        return new InquiryPhoneType();
    }

    /**
     * Create an instance of {@link AdditionalNameTypeDetails }
     * 
     */
    public AdditionalNameTypeDetails createAdditionalNameTypeDetails() {
        return new AdditionalNameTypeDetails();
    }

    /**
     * Create an instance of {@link ScoreType }
     * 
     */
    public ScoreType createScoreType() {
        return new ScoreType();
    }

    /**
     * Create an instance of {@link EquifaxScoreType }
     * 
     */
    public EquifaxScoreType createEquifaxScoreType() {
        return new EquifaxScoreType();
    }

    /**
     * Create an instance of {@link RetailType }
     * 
     */
    public RetailType createRetailType() {
        return new RetailType();
    }

    /**
     * Create an instance of {@link MFIType }
     * 
     */
    public MFIType createMFIType() {
        return new MFIType();
    }

    /**
     * Create an instance of {@link AccountsSummaryType }
     * 
     */
    public AccountsSummaryType createAccountsSummaryType() {
        return new AccountsSummaryType();
    }

    /**
     * Create an instance of {@link ConsolidateCreditSummaryType }
     * 
     */
    public ConsolidateCreditSummaryType createConsolidateCreditSummaryType() {
        return new ConsolidateCreditSummaryType();
    }

    /**
     * Create an instance of {@link OverAllType }
     * 
     */
    public OverAllType createOverAllType() {
        return new OverAllType();
    }

    /**
     * Create an instance of {@link RetailsType }
     * 
     */
    public RetailsType createRetailsType() {
        return new RetailsType();
    }

    /**
     * Create an instance of {@link MicrofinanceType }
     * 
     */
    public MicrofinanceType createMicrofinanceType() {
        return new MicrofinanceType();
    }

    /**
     * Create an instance of {@link AccountsType }
     * 
     */
    public AccountsType createAccountsType() {
        return new AccountsType();
    }

    /**
     * Create an instance of {@link RetailAccountType }
     * 
     */
    public RetailAccountType createRetailAccountType() {
        return new RetailAccountType();
    }

    /**
     * Create an instance of {@link MicrofinancesType }
     * 
     */
    public MicrofinancesType createMicrofinancesType() {
        return new MicrofinancesType();
    }

    /**
     * Create an instance of {@link ScoringElementsType }
     * 
     */
    public ScoringElementsType createScoringElementsType() {
        return new ScoringElementsType();
    }

    /**
     * Create an instance of {@link ScoringElementType }
     * 
     */
    public ScoringElementType createScoringElementType() {
        return new ScoringElementType();
    }

    /**
     * Create an instance of {@link DisputeDetailsType }
     * 
     */
    public DisputeDetailsType createDisputeDetailsType() {
        return new DisputeDetailsType();
    }

    /**
     * Create an instance of {@link PrescreenResponseType }
     * 
     */
    public PrescreenResponseType createPrescreenResponseType() {
        return new PrescreenResponseType();
    }

    /**
     * Create an instance of {@link HeaderDataType }
     * 
     */
    public HeaderDataType createHeaderDataType() {
        return new HeaderDataType();
    }

    /**
     * Create an instance of {@link PIIDataType }
     * 
     */
    public PIIDataType createPIIDataType() {
        return new PIIDataType();
    }

    /**
     * Create an instance of {@link AddressesType }
     * 
     */
    public AddressesType createAddressesType() {
        return new AddressesType();
    }

    /**
     * Create an instance of {@link PhonesType }
     * 
     */
    public PhonesType createPhonesType() {
        return new PhonesType();
    }

    /**
     * Create an instance of {@link BureauAttributesType }
     * 
     */
    public BureauAttributesType createBureauAttributesType() {
        return new BureauAttributesType();
    }

    /**
     * Create an instance of {@link NonBureauAttributesType }
     * 
     */
    public NonBureauAttributesType createNonBureauAttributesType() {
        return new NonBureauAttributesType();
    }

    /**
     * Create an instance of {@link ResponseDataType }
     * 
     */
    public ResponseDataType createResponseDataType() {
        return new ResponseDataType();
    }

    /**
     * Create an instance of {@link ResponseEntitiesType }
     * 
     */
    public ResponseEntitiesType createResponseEntitiesType() {
        return new ResponseEntitiesType();
    }

    /**
     * Create an instance of {@link ResponseEntityType }
     * 
     */
    public ResponseEntityType createResponseEntityType() {
        return new ResponseEntityType();
    }

    /**
     * Create an instance of {@link InquiryCommonAccountDetailsType }
     * 
     */
    public InquiryCommonAccountDetailsType createInquiryCommonAccountDetailsType() {
        return new InquiryCommonAccountDetailsType();
    }

    /**
     * Create an instance of {@link RequestHeaderType }
     * 
     */
    public RequestHeaderType createRequestHeaderType() {
        return new RequestHeaderType();
    }

    /**
     * Create an instance of {@link RequestBodyType }
     * 
     */
    public RequestBodyType createRequestBodyType() {
        return new RequestBodyType();
    }

    /**
     * Create an instance of {@link InquiryCommonInputAddressType }
     * 
     */
    public InquiryCommonInputAddressType createInquiryCommonInputAddressType() {
        return new InquiryCommonInputAddressType();
    }

    /**
     * Create an instance of {@link InquiryCommonInputPhoneType }
     * 
     */
    public InquiryCommonInputPhoneType createInquiryCommonInputPhoneType() {
        return new InquiryCommonInputPhoneType();
    }

    /**
     * Create an instance of {@link InquiryResponseHeaderType }
     * 
     */
    public InquiryResponseHeaderType createInquiryResponseHeaderType() {
        return new InquiryResponseHeaderType();
    }

    /**
     * Create an instance of {@link ReportType }
     * 
     */
    public ReportType createReportType() {
        return new ReportType();
    }

    /**
     * Create an instance of {@link TelecomResponseType }
     * 
     */
    public TelecomResponseType createTelecomResponseType() {
        return new TelecomResponseType();
    }

    /**
     * Create an instance of {@link OtherKeyIndType }
     * 
     */
    public OtherKeyIndType createOtherKeyIndType() {
        return new OtherKeyIndType();
    }

    /**
     * Create an instance of {@link ConsumerDisputesType.Dispute }
     * 
     */
    public ConsumerDisputesType.Dispute createConsumerDisputesTypeDispute() {
        return new ConsumerDisputesType.Dispute();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsumerDisputesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "ConsumerDisputes")
    public JAXBElement<ConsumerDisputesType> createConsumerDisputes(ConsumerDisputesType value) {
        return new JAXBElement<ConsumerDisputesType>(_ConsumerDisputes_QNAME, ConsumerDisputesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InquiryRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "InquiryRequest")
    public JAXBElement<InquiryRequestType> createInquiryRequest(InquiryRequestType value) {
        return new JAXBElement<InquiryRequestType>(_InquiryRequest_QNAME, InquiryRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InquiryResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "InquiryResponse")
    public JAXBElement<InquiryResponseType> createInquiryResponse(InquiryResponseType value) {
        return new JAXBElement<InquiryResponseType>(_InquiryResponse_QNAME, InquiryResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "PANId")
    public JAXBElement<String> createPANId(String value) {
        return new JAXBElement<String>(_PANId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "PassportId")
    public JAXBElement<String> createPassportId(String value) {
        return new JAXBElement<String>(_PassportId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "VoterId")
    public JAXBElement<String> createVoterId(String value) {
        return new JAXBElement<String>(_VoterId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LandlineType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "HomePhone")
    public JAXBElement<LandlineType> createHomePhone(LandlineType value) {
        return new JAXBElement<LandlineType>(_HomePhone_QNAME, LandlineType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.equifax.com/eport/ws/schemas/1.0", name = "MobilePhone")
    public JAXBElement<String> createMobilePhone(String value) {
        return new JAXBElement<String>(_MobilePhone_QNAME, String.class, null, value);
    }

}
