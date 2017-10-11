package com.finflux.risk.creditbureau.provider.cibil.response;

public class HeaderSegment {

    private final Integer SEGMENTTAG_LENGTH = 4;
    private final Integer VERSION_LENGTH = 2;
    private final Integer MEMBER_REFERENE_LENGTH = 25;
    private final Integer FUTURE_USE1_LENGTH = 2;
    private final Integer FUTURE_USE2_LENGTH = 4;
    private final Integer USERID_LENGTH = 30;
    private final Integer RETURNCODE_LENGTH = 1;
    private final Integer ENQUIRYCONTROL_NUMBER_LENGTH = 12;
    private final Integer DATE_LENGTH = 8;
    private final Integer TIME_LENGTH = 6;

    private String segmentTag;
    private String version;
    private String memberReferenceNumber;
    private String futureUse1;
    private String futureUse2;
    private String userId;
    private String returnCode;
    private String enquiryControlNumber;
    private String dateProcessed;
    private String timeProcessed;

    private final Integer sectionLength = new Integer(94);

    HeaderSegment() {

    }

    public Integer parseSection(final byte[] response, final Integer startIndex) {
        int counter = startIndex;
        this.segmentTag = new String(response, counter, SEGMENTTAG_LENGTH);
        counter += SEGMENTTAG_LENGTH;
        this.version = new String(response, counter, VERSION_LENGTH);
        counter += VERSION_LENGTH;
        this.memberReferenceNumber = new String(response, counter, MEMBER_REFERENE_LENGTH);
        counter += MEMBER_REFERENE_LENGTH;
        this.futureUse1 = new String(response, counter, FUTURE_USE1_LENGTH);
        counter += FUTURE_USE1_LENGTH;
        this.futureUse2 = new String(response, counter, FUTURE_USE2_LENGTH);
        counter += FUTURE_USE2_LENGTH;
        this.userId = new String(response, counter, USERID_LENGTH);
        counter += USERID_LENGTH;
        this.returnCode = new String(response, counter, RETURNCODE_LENGTH);
        counter += RETURNCODE_LENGTH;
        this.enquiryControlNumber = new String(response, counter, ENQUIRYCONTROL_NUMBER_LENGTH);
        counter += ENQUIRYCONTROL_NUMBER_LENGTH;
        this.dateProcessed = new String(response, counter, DATE_LENGTH);
        counter += DATE_LENGTH;
        this.timeProcessed = new String(response, counter, TIME_LENGTH);
        counter += TIME_LENGTH;
        return this.sectionLength;
    }

    public String getSegmentTag() {
        return this.segmentTag;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMemberReferenceNumber() {
        return this.memberReferenceNumber;
    }

    public String getFutureUse1() {
        return this.futureUse1;
    }

    public String getFutureUse2() {
        return this.futureUse2;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getReturnCode() {
        return this.returnCode;
    }

    public String getEnquiryControlNumber() {
        return this.enquiryControlNumber;
    }

    public String getDateProcessed() {
        return this.dateProcessed;
    }

    public String getTimeProcessed() {
        return this.timeProcessed;
    }

    public Integer getSectionLength() {
        return this.sectionLength;
    }

}
