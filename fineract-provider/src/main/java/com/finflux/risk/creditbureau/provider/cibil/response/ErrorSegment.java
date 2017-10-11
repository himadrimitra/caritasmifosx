package com.finflux.risk.creditbureau.provider.cibil.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.finflux.risk.creditbureau.provider.cibil.response.data.error.UserReferenceErrorData;
import com.google.gson.Gson;

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
        int start = TAG_LENGTH + DATE_LENGTH + TIME_LENGTH ;
        userReferenceErrorSegment.parseSection(errorResponse, start, UserReferenceErrorData.class);
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

    public String getErrorsAsJson() {
        String errorsJson = null;
        List<Map<String, String>> errors = new ArrayList<>();
        List<UserReferenceErrorData> errorsList = this.userReferenceErrorSegment.getSegmentData();
        if (errorsList != null && !errorsList.isEmpty()) {
            UserReferenceErrorData errorsData = errorsList.get(0);
            errors.addAll(errorsData.getErrors());
            errorsJson = new Gson().toJson(errors, List.class);
        }
        return errorsJson;
    }
}
