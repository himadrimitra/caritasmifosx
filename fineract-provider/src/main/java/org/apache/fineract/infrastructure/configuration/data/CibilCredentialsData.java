package org.apache.fineract.infrastructure.configuration.data;

public class CibilCredentialsData {

    private final String userId;

    private final String password;

    private final String genderTypeFemale;

    private final String genderTypeMale;

    private final CibilDocumentTypes documentTypes;

    private final CibilAddressTypes addressTypes;

    private String hostName;
    private Integer port;

    public CibilCredentialsData(final String hostName, final Integer port, String userId, String password,
            final CibilDocumentTypes documentTypes, final String genderTypeFemale, final String genderTypeMale,
            final CibilAddressTypes addressTypes) {
        super();
        this.hostName = hostName;
        this.port = port;
        this.userId = userId;
        this.password = password;
        this.documentTypes = documentTypes;
        this.genderTypeFemale = genderTypeFemale;
        this.genderTypeMale = genderTypeMale;
        this.addressTypes = addressTypes;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPassword() {
        return this.password;
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

    public String getGenderTypeFemale() {
        return this.genderTypeFemale;
    }

    public String getGenderTypeMale() {
        return this.genderTypeMale;
    }

    public String getHostName() {
        return this.hostName;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getAddressTypeResidence() {
        return addressTypes.getAddressTypeResidence();
    }

    public String getAddressTypepermanent() {
        return addressTypes.getAddressTypepermanent();
    }

    public String getAddressTypeOffice() {
        return addressTypes.getAddressTypeOffice();
    }
}
