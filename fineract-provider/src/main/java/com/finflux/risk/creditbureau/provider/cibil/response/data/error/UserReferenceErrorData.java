package com.finflux.risk.creditbureau.provider.cibil.response.data.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.risk.creditbureau.provider.cibil.request.AddressSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.IdentificationSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.NameSegment;
import com.finflux.risk.creditbureau.provider.cibil.request.TelephoneSegment;
import com.finflux.risk.creditbureau.provider.cibil.response.ResponseSectionTags;
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

    private String memberReferenceNumber = null;
    private String invalidVersion = null;
    private String invalidLength = null;
    private String invalidTotalLength = null;
    private String invalidPurpose = null;
    private String invalidAmount = null;
    private String invalidMemberUser = null;
    private String invalidInputOutMedia = null;
    private String responseSizeExceeded = null;
    private String requiredSegmentMissing = null;
    private String cibilSystemError = null;
    private String invalidEnquiryData = null;
    private String invalidSegmentTag = null;
    private String invalidSegmentOrder = null;
    private String invalidFieldTagOrder = null;
    private String missingRequiredField = null;

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

    public List<Map<String, String>> getErrors() {
        final List<Map<String, String>> errors = new ArrayList<>();
        if (this.invalidVersion != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidVersion);
            errorMap.put("description", "TUEF Enquiry Header Segment- Invalid version");
            errors.add(errorMap);
        }

        if (this.invalidLength != null) {
            Map<String, String> errorMap = getSegmentSpecificLengthError();
            errors.add(errorMap);
        }

        if (this.invalidTotalLength != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidTotalLength);
            errorMap.put("description", "TUEF Enquiry record length doesn't match with ES05 section");
            errors.add(errorMap);
        }
        if (this.invalidPurpose != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidPurpose);
            errorMap.put("description", "TUEF Enquiry record is having invalid loan purpose");
            errors.add(errorMap);
        }

        if (this.invalidAmount != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidAmount);
            errorMap.put("description", "TUEF Enquiry record is having invalid loan amount");
            errors.add(errorMap);
        }

        if (this.invalidMemberUser != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidMemberUser);
            errorMap.put("description", "TUEF Enquiry record is provided with invalid credentials");
            errors.add(errorMap);
        }

        if (this.requiredSegmentMissing != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.requiredSegmentMissing);
            errorMap.put("description", "Required segment is missed in TUEF Enquiry record");
            errors.add(errorMap);
        }

        if (this.invalidEnquiryData != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidEnquiryData);
            errorMap.put("description", "Invalid enquiry data in TUEF Enquiry record");
            errors.add(errorMap);
        }
        if (this.cibilSystemError != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.cibilSystemError);
            errorMap.put("description", "CIBIL system error");
            errors.add(errorMap);
        }

        if (this.invalidSegmentTag != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidSegmentTag);
            errorMap.put("description", "Invalid segment tag defined in TUEF enquiry record");
            errors.add(errorMap);
        }

        if (this.invalidSegmentOrder != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidSegmentTag);
            errorMap.put("description", "Segment order is not correct TUEF enquiry record");
            errors.add(errorMap);
        }

        if (this.invalidFieldTagOrder != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidFieldTagOrder);
            errorMap.put("description", "Invalid segment tag order in TUEF enquiry record");
            errors.add(errorMap);
        }
        if (this.missingRequiredField != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.missingRequiredField);
            errorMap.put("description", "Required field is missed in TUEF enquiry record");
            errors.add(errorMap);
        }

        if (this.responseSizeExceeded != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.responseSizeExceeded);
            errorMap.put("description", "Response size is exceeded");
            errors.add(errorMap);
        }

        if (this.invalidInputOutMedia != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", this.invalidInputOutMedia);
            errorMap.put("description", "Invalid input/output media in TUEF enquiry record");
            errors.add(errorMap);
        }
        return errors;
    }

    private Map<String, String> getSegmentSpecificLengthError() {
        final StringBuilder builder = new StringBuilder();
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("code", this.invalidLength);
        // Check whether header segment errors can come here. If yes, then
        // segment length should be 4
        final String segment = this.invalidLength.substring(0, 2);
        String record;
        switch (segment) {
            case ResponseSectionTags.NAME:
                record = this.invalidLength.substring(2);
                builder.append(NameSegment.getFieldName(record));
            break;
            case ResponseSectionTags.IDENTITY:
                record = this.invalidLength.substring(2);
                builder.append(IdentificationSegment.getFieldName(record));
            break;
            case ResponseSectionTags.TELEPHONE:
                record = this.invalidLength.substring(2);
                builder.append(TelephoneSegment.getFieldName(record));
            break;
            case ResponseSectionTags.ADDRESS:
                record = this.invalidLength.substring(2);
                builder.append(AddressSegment.getFieldName(record));
            break;
        }
        if (builder.length() > 0) {
            builder.append(" having invalid length");
        }
        errorMap.put("description", builder.toString());
        return errorMap;
    }

}
