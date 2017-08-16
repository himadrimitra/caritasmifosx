package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class IdentifierData extends Data {

    private final String ID_TYPE = "01";
    private final String ID_NUMBER = "02";
    private final String ISSUED_DATE = "03";
    private final String EXPIRY_DATE = "04";
    private final String ENRICHED_ENQUIRY = "90";

    private String idType;
    private String idNumber;
    private String issuedDate;
    private String expiryDate;
    private String enrichedEnquiry;

    public String getIdType() {
        return this.idType;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public String getIssuedDate() {
        return this.issuedDate;
    }

    public String getExpiryDate() {
        return this.expiryDate;
    }

    public String getEnrichedEnquiry() {
        return this.enrichedEnquiry;
    }

    @Override
    public void setValue(final String tagType, final String value) {
        switch (tagType) {
            case ID_TYPE:
                this.idType = value;
            break;
            case ID_NUMBER:
                this.idNumber = value;
            break;
            case ISSUED_DATE:
                this.issuedDate = value;
            break;
            case EXPIRY_DATE:
                this.expiryDate = value;
            break;
            case ENRICHED_ENQUIRY:
                this.enrichedEnquiry = value;
            break;
        }
    }

}
