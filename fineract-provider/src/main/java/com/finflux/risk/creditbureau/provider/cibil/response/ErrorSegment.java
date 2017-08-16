package com.finflux.risk.creditbureau.provider.cibil.response;

import com.finflux.risk.creditbureau.provider.cibil.response.data.error.UserReferenceErrorData;

public class ErrorSegment {

    private final Integer TAG_LENGTH = 4;
    private final Integer DATE_LENGTH = 8;
    private final Integer TIME_LENGTH = 6;

    private String responseTag;
    private String processedDate;
    private String processedTime;

    private Integer sectionLength;

    private ResponseSegment<UserReferenceErrorData> userReferenceErrorSegment;

    public ErrorSegment() {

    }

    private void parseUserReferenceErrorSegment(final byte[] errorResponse) {
        userReferenceErrorSegment = new ResponseSegment<>();
        userReferenceErrorSegment.parseSection(errorResponse, 0, UserReferenceErrorData.class);
    }

    // This is fixed length packet.
    public Integer parseSection(byte[] response, Integer startIndex) {
        int counter = startIndex;
        this.responseTag = new String(response, counter, TAG_LENGTH);
        counter += TAG_LENGTH;
        this.processedDate = new String(response, counter, DATE_LENGTH);
        counter += DATE_LENGTH;
        this.processedTime = new String(response, counter, TIME_LENGTH);
        counter += TIME_LENGTH;
        parseUserReferenceErrorSegment(response);
        return this.sectionLength;
    }

    public String getResponseTag() {
        return this.responseTag;
    }

    public String getProcessedDate() {
        return this.processedDate;
    }

    public String getProcessedTime() {
        return this.processedTime;
    }

    public Integer getSectionLength() {
        return this.sectionLength;
    }
}
