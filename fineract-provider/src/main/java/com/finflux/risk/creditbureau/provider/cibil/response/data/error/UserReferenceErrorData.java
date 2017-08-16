package com.finflux.risk.creditbureau.provider.cibil.response.data.error;

import com.finflux.risk.creditbureau.provider.cibil.response.data.Data;

public class UserReferenceErrorData extends Data {

    private final String MEMBER_REFERENCE_NUMBER = "01";
    private final String INVALID_VERSION = "03";
    private final String INVALID_LENGTH = "04";
    private final String INVALID_TOTAL_LENGTH = "05";
    private final String INVALID_ENQUIRY_PURPOSE = "06";
    private final String INVALID_ENQUIRY_AMOUNT = "07";
    private final String INVALID_ENQUIRY_MEMBER_USER = "08";
    private final String REQUIRED_ENQUIRY_SEGMENT_MISSING = "09";
    private final String INVALID_ENQUIRY_DATA = "10";
    private final String CIBIL_SYSTEM_ERROR = "11";
    private final String INVALID_SEGMENT_TAG = "12";
    private final String INVALID_SEGMENT_ORDER = "13";
    private final String INVALID_FIELD_TAG_ORDER = "14";
    private final String MISSING_REQUIRED_FIELD = "15";
    private final String REQUESTED_RESPONSE_SIZE_EXCEEDED = "16";
    private final String INVALID_INPUT_OUTPUT_MEDIA = "17";

    private String memberReferenceNumber;
    private String invalidVersion;
    private String invalidLength;
    private String invalidTotalLength;
    private String invalidPurpose;
    private String invalidAmount;
    private String invalidMemberUser;
    private String invalidInputOutMedia;
    private String responseSizeExceeded;
    private String requiredSegmentMissing;
    private String cibilSystemError;
    private String invalidEnquiryData;
    private String invalidSegmentTag;
    private String invalidSegmentOrder;
    private String invalidFieldTagOrder;
    private String missingRequiredField;

    @Override
    public void setValue(final String tagType, final String value) {
        switch (tagType) {
            case MEMBER_REFERENCE_NUMBER:
                this.memberReferenceNumber = value;
            break;
            case INVALID_VERSION:
                this.invalidVersion = value;
            break;
            case INVALID_LENGTH:
                this.invalidLength = value;
            break;
            case INVALID_TOTAL_LENGTH:
                this.invalidTotalLength = value;
            break;
            case INVALID_ENQUIRY_PURPOSE:
                this.invalidPurpose = value;
            break;
            case INVALID_ENQUIRY_AMOUNT:
                this.invalidAmount = value;
            break;
            case INVALID_ENQUIRY_MEMBER_USER:
                this.invalidMemberUser = value;
            break;
            case REQUIRED_ENQUIRY_SEGMENT_MISSING:
                this.requiredSegmentMissing = value;
            break;
            case INVALID_ENQUIRY_DATA:
                this.invalidEnquiryData = value;
            break;
            case CIBIL_SYSTEM_ERROR:
                this.cibilSystemError = value;
            break;
            case INVALID_SEGMENT_TAG:
                this.invalidSegmentTag = value;
            break;
            case INVALID_SEGMENT_ORDER:
                this.invalidSegmentOrder = value;
            break;
            case INVALID_FIELD_TAG_ORDER:
                this.invalidFieldTagOrder = value;
            break;
            case MISSING_REQUIRED_FIELD:
                this.missingRequiredField = value;
            break;
            case REQUESTED_RESPONSE_SIZE_EXCEEDED:
                this.responseSizeExceeded = value;
            break;
            case INVALID_INPUT_OUTPUT_MEDIA:
                this.invalidInputOutMedia = value;
            break;
        }
    }

    public String getMemberReferenceNumber() {
        return this.memberReferenceNumber;
    }

    public String getInvalidVersion() {
        return this.invalidVersion;
    }

    public String getInvalidLength() {
        return this.invalidLength;
    }

    public String getInvalidTotalLength() {
        return this.invalidTotalLength;
    }

    public String getInvalidPurpose() {
        return this.invalidPurpose;
    }

    public String getInvalidAmount() {
        return this.invalidAmount;
    }

    public String getInvalidMemberUser() {
        return this.invalidMemberUser;
    }

    public String getInvalidInputOutMedia() {
        return this.invalidInputOutMedia;
    }

    public String getResponseSizeExceeded() {
        return this.responseSizeExceeded;
    }

    public String getRequiredSegmentMissing() {
        return this.requiredSegmentMissing;
    }

    public String getCibilSystemError() {
        return this.cibilSystemError;
    }

    public String getInvalidEnquiryData() {
        return this.invalidEnquiryData;
    }

    public String getInvalidSegmentTag() {
        return this.invalidSegmentTag;
    }

    public String getInvalidSegmentOrder() {
        return this.invalidSegmentOrder;
    }

    public String getInvalidFieldTagOrder() {
        return this.invalidFieldTagOrder;
    }

    public String getMissingRequiredField() {
        return this.missingRequiredField;
    }

}
