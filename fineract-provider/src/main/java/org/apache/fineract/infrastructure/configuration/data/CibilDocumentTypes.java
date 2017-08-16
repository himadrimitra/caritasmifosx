package org.apache.fineract.infrastructure.configuration.data;

public class CibilDocumentTypes {

    private final String documentTypePassport;

    private final String documentTypePan;

    private final String documentTypeAadhar;

    private final String documentTypeVoterId;

    private final String documentTypeDrivingLicense;

    private final String documentTypeRationCard;

    private final String documentTypeOther;

    public CibilDocumentTypes(String documentTypePassport, String documentTypePan, String documentTypeAadhar, String documentTypeVoterId,
            String documentTypeDrivingLicense, String documentTypeRationCard, String documentTypeOther) {
        super();
        this.documentTypePassport = documentTypePassport;
        this.documentTypePan = documentTypePan;
        this.documentTypeAadhar = documentTypeAadhar;
        this.documentTypeVoterId = documentTypeVoterId;
        this.documentTypeDrivingLicense = documentTypeDrivingLicense;
        this.documentTypeRationCard = documentTypeRationCard;
        this.documentTypeOther = documentTypeOther;
    }

    public String getDocumentTypePassport() {
        return this.documentTypePassport;
    }

    public String getDocumentTypePan() {
        return this.documentTypePan;
    }

    public String getDocumentTypeAadhar() {
        return this.documentTypeAadhar;
    }

    public String getDocumentTypeVoterId() {
        return this.documentTypeVoterId;
    }

    public String getDocumentTypeDrivingLicense() {
        return this.documentTypeDrivingLicense;
    }

    public String getDocumentTypeRationCard() {
        return this.documentTypeRationCard;
    }

    public String getDocumentTypeOther() {
        return this.documentTypeOther;
    }
}
