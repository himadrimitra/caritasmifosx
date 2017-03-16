package org.apache.fineract.infrastructure.configuration.data;

public class EquifaxCredentialsData {

	private final Integer customerIdentifier;

	private final String userId;

	private final String password;

	private final String memberNumber;

	private final String securityCode;

	private final String productCode;

	private final String productVersion;

	private final String customerReferenceId;

	private final String url;

	private final String qName;

	private final String qNameVersion;

	private final String genderTypeFemale ;
	
	private final String genderTypeMale ;
	
	private final EquifaxDocumentTypes documentTypes;
	
	private final EquifaxRelationTypes relationTypes ;

	public EquifaxCredentialsData(Integer customerIdentifier, String userId, String password, String memberNumber,
			String securityCode, final String productCode, String productVersion, String customerReferenceId,
			String url, String qName, String qNameVersion, final EquifaxDocumentTypes documentTypes,
			final EquifaxRelationTypes relationTypes, final String genderTypeFemale, final String genderTypeMale) {
		super();
		this.customerIdentifier = customerIdentifier;
		this.userId = userId;
		this.password = password;
		this.memberNumber = memberNumber;
		this.securityCode = securityCode;
		this.productCode = productCode;
		this.productVersion = productVersion;
		this.customerReferenceId = customerReferenceId;
		this.url = url;
		this.qName = qName;
		this.qNameVersion = qNameVersion;
		this.documentTypes = documentTypes;
		this.relationTypes = relationTypes ;
		this.genderTypeFemale = genderTypeFemale ;
		this.genderTypeMale = genderTypeMale ;
	}

	public Integer getCustomerIdentifier() {
		return this.customerIdentifier;
	}

	public String getUserId() {
		return this.userId;
	}

	public String getPassword() {
		return this.password;
	}

	public String getMemberNumber() {
		return this.memberNumber;
	}

	public String getSecurityCode() {
		return this.securityCode;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public String getProductVersion() {
		return this.productVersion;
	}

	public String getCustomerReferenceId() {
		return this.customerReferenceId;
	}

	public String getUrl() {
		return this.url;
	}

	public String getqName() {
		return this.qName;
	}

	public String getqNameVersion() {
		return this.qNameVersion;
	}

	public String getDocumentTypePassport() {
		return this.documentTypes.getDocumentTypePassport();
	}

	public String getDocumentTypePan() {
		return this.documentTypes.getDocumentTypePan();
	}

	public String getDocumentTypeAadhar() {
		return this.documentTypes.getDocumentTypeAadhar();
	}

	public String getDocumentTypeVoterId() {
		return this.documentTypes.getDocumentTypeVoterId();
	}

	public String getDocumentTypeDrivingLicense() {
		return this.documentTypes.getDocumentTypeDrivingLicense();
	}

	public String getDocumentTypeRationCard() {
		return this.documentTypes.getDocumentTypeRationCard();
	}

	public String getDocumentTypeOther() {
		return this.documentTypes.getDocumentTypeOther();
	}
	
	public String getRelationTypeFather() {
		return this.relationTypes.getRelationTypeFather();
	}

	public String getRelationTypeHusband() {
		return this.relationTypes.getRelationTypeHusband() ;
	}

	public String getRelationTypeBrother() {
		return this.relationTypes.getRelationTypeBrother();
	}

	public String getRelationTypeSon() {
		return this.relationTypes.getRelationTypeSon();
	}

	public String getRelationTypeSonInLaw() {
		return this.relationTypes.getRelationTypeSonInLaw();
	}

	public String getRelationTypeFatherInLaw() {
		return this.relationTypes.getRelationTypeFatherInLaw();
	}

	public String getRelationTypeBrotherInLaw() {
		return this.relationTypes.getRelationTypeBrotherInLaw();
	}

	public String getRelationTypeMother() {
		return this.relationTypes.getRelationTypeMother();
	}

	public String getRelationTypeWife() {
		return this.relationTypes.getRelationTypeWife();
	}

	public String getRelationTypeSister() {
		return this.relationTypes.getRelationTypeSister();
	}

	public String getRelationTypeDaughter() {
		return this.relationTypes.getRelationTypeDaughter();
	}

	public String getRelationTypeDaughterInLaw() {
		return this.relationTypes.getRelationTypeDaughterInLaw();
	}

	public String getRelationTypeMotherInLaw() {
		return this.relationTypes.getRelationTypeMotherInLaw();
	}

	public String getRelationTypeSisterInLaw() {
		return this.relationTypes.getRelationTypeSisterInLaw();
	}

	public String getRelationTypeSpouse() {
		return this.relationTypes.getRelationTypeSpouse();
	}
	public String getRelationTypeOther() {
		return this.relationTypes.getRelationTypeOther();
	}

	public String getGenderTypeFemale() {
		return this.genderTypeFemale;
	}

	public String getGenderTypeMale() {
		return this.genderTypeMale;
	}
	
	
}
