package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class TelephoneData extends Data {

    private final String TELEPHONE_NUMBER = "01";
    private final String TELEPHONE_EXTENSION = "02";
    private final String TELEPHONE_TYPE = "03";
    private final String ENRICHED_ENQUIRY = "90";

    private String telephoneNumber;
    private String telephoneExtension;
    private String telephoneType;
    private String enrichThroughEnquiry; // only Y

    public TelephoneData() {

    }

    public TelephoneData(String telephoneNumber, String telephoneExtension, String telephoneType, String enrichThroughEnquiry) {
        super();
        this.telephoneNumber = telephoneNumber;
        this.telephoneExtension = telephoneExtension;
        this.telephoneType = telephoneType;
        this.enrichThroughEnquiry = enrichThroughEnquiry;
    }

    public String getTelephoneNumber() {
        return this.telephoneNumber;
    }

    public String getTelephoneExtension() {
        return this.telephoneExtension;
    }

    public String getTelephoneType() {
        return this.telephoneType;
    }

    public String getEnrichThroughEnquiry() {
        return this.enrichThroughEnquiry;
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case TELEPHONE_NUMBER:
                this.telephoneNumber = value;
            break;
            case TELEPHONE_EXTENSION:
                this.telephoneExtension = value;
            break;
            case TELEPHONE_TYPE:
                this.telephoneType = value;
            break;
            case ENRICHED_ENQUIRY:
                this.enrichThroughEnquiry = value;
            break;
        }
    }
}
