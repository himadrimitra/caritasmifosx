package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class EnquiryData extends Data {

    private final Integer HEADERTAG_LENGTH = 8;

    private final String ENQUIRY_DATE = "01";
    private final String ENQUIRY_MEMBER_SHORT_NAME = "04";
    private final String ENQUIRY_PURPOSE = "05";
    private final String ENQUIRY_AMOUNT = "06";

    private String enquiryDate;
    private String memberShortName;
    private String enqiryPurpose;
    private String enquiryAmount;

    public EnquiryData() {}

    public EnquiryData(String enquiryDate, String memberShortName, String enqiryPurpose, String enquiryAmount) {
        super();
        this.enquiryDate = enquiryDate;
        this.memberShortName = memberShortName;
        this.enqiryPurpose = enqiryPurpose;
        this.enquiryAmount = enquiryAmount;
    }

    public String getEnquiryDate() {
        return this.enquiryDate;
    }

    public String getMemberShortName() {
        return this.memberShortName;
    }

    public String getEnqiryPurpose() {
        return this.enqiryPurpose;
    }

    public String getEnquiryAmount() {
        return this.enquiryAmount;
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case ENQUIRY_DATE:
                this.enquiryDate = value;
            break;
            case ENQUIRY_MEMBER_SHORT_NAME:
                this.memberShortName = value;
            break;
            case ENQUIRY_PURPOSE:
                this.enqiryPurpose = value;
            break;
            case ENQUIRY_AMOUNT:
                this.enquiryAmount = value;
            break;
        }
    }

    @Override
    public Integer getSegmentHeaderLength() {
        return this.HEADERTAG_LENGTH;
    }
}
