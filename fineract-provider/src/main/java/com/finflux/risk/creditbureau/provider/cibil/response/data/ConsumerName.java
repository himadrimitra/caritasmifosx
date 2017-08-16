package com.finflux.risk.creditbureau.provider.cibil.response.data;

import java.util.ArrayList;
import java.util.List;

public class ConsumerName extends Data {

    private final String CONSUMER_NAME1 = "01";
    private final String CONSUMER_NAME2 = "02";
    private final String CONSUMER_NAME3 = "03";
    private final String CONSUMER_NAME4 = "04";
    private final String CONSUMER_NAME5 = "05";
    private final String DOB = "07";
    private final String GENDER = "08";
    private final String ERRORCODE_ENTRY_DATE = "80";
    private final String ERROR_SEGMENT = "81";
    private final String ERROR_CODE = "82";
    private final String CIBILREMARKSCODE_ENTRY_DATE = "83";
    private final String CIBIL_REMARKS_CODE = "84";
    private final String DISPUTEREMARKS_ENTRYDATE = "85";
    private final String DISPUTE_REMARKS_CODE1 = "86";
    private final String DISPUTE_REMARKS_CODE2 = "87";

    private final List<String> consumerNameList = new ArrayList<>();

    public List<String> getConsumerNameList() {
        return this.consumerNameList;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public String getErrorCodeEntryDate() {
        return this.errorCodeEntryDate;
    }

    public String getErrorSegment() {
        return this.errorSegment;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getCibilRemarksEntryDate() {
        return this.cibilRemarksEntryDate;
    }

    public String getCibilRemarksCode() {
        return this.cibilRemarksCode;
    }

    public String getDisputeRemarksEntryDate() {
        return this.disputeRemarksEntryDate;
    }

    public String getDisputeRemarksCode1() {
        return this.disputeRemarksCode1;
    }

    public String getDisputeRemakrsCode2() {
        return this.disputeRemakrsCode2;
    }

    private String dateOfBirth;
    private String gender;
    private String errorCodeEntryDate;
    private String errorSegment;
    private String errorCode;
    private String cibilRemarksEntryDate;
    private String cibilRemarksCode;
    private String disputeRemarksEntryDate;
    private String disputeRemarksCode1;
    private String disputeRemakrsCode2;

    @Override
    public void setValue(final String tagType, String value) {
        switch (tagType) {
            case CONSUMER_NAME1:
            case CONSUMER_NAME2:
            case CONSUMER_NAME3:
            case CONSUMER_NAME4:
            case CONSUMER_NAME5:
                this.consumerNameList.add(value);
            break;
            case DOB:
                this.dateOfBirth = value;
            break;
            case GENDER:
                this.gender = value;
            break;
            case ERRORCODE_ENTRY_DATE:
                this.errorCodeEntryDate = value;
            break;
            case ERROR_SEGMENT:
                this.errorSegment = value;
            break;
            case ERROR_CODE:
                this.errorCode = value;
            break;
            case CIBILREMARKSCODE_ENTRY_DATE:
                this.cibilRemarksEntryDate = value;
            break;
            case CIBIL_REMARKS_CODE:
                this.cibilRemarksCode = value;
            break;
            case DISPUTEREMARKS_ENTRYDATE:
                this.disputeRemarksEntryDate = value;
            break;
            case DISPUTE_REMARKS_CODE1:
                this.disputeRemarksCode1 = value;
            break;
            case DISPUTE_REMARKS_CODE2:
                this.disputeRemakrsCode2 = value;
            break;
        }
    }

}
