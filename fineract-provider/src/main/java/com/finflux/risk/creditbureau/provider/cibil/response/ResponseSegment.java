package com.finflux.risk.creditbureau.provider.cibil.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.finflux.risk.creditbureau.provider.cibil.response.data.Data;

public class ResponseSegment<T extends Data> {

    private final Integer HEADERTAG_LENGTH = 7;
    private final Integer FIELDTAGTYPE_LENGTH = 2;
    private final Integer FIELDVALUE_LENGTH = 2;

    private Integer sectionLength;

    static Set<String> possibleNextSections = new HashSet<>(Arrays.asList(ResponseSectionTags.IDENTITY, ResponseSectionTags.TELEPHONE,
            ResponseSectionTags.EMAIL, ResponseSectionTags.EMPLOYMENT, ResponseSectionTags.ENQUIERYACCOUNT, ResponseSectionTags.SCORE,
            ResponseSectionTags.ADDRESS, ResponseSectionTags.ACCOUNTS, ResponseSectionTags.ENQUIRY, ResponseSectionTags.DISPUTEREMARKS,
            ResponseSectionTags.EOS));

    private List<T> segmentDataList = new ArrayList<>();

    Integer parseSection(final byte[] response, final Integer startIndex, Class<T> clazz) {
        T data = createSegmentData(clazz);
        Integer index = startIndex;
        Integer headerLength = data.getSegmentHeaderLength();
        final String recordHeader = new String(response, index, headerLength);
        data.setHeaderData(recordHeader);
        index += headerLength;
        boolean isDone = false;
        while (!isDone) {
            final String fieldTagType = new String(response, index, FIELDTAGTYPE_LENGTH);
            if (possibleNextSections.contains(fieldTagType)) {
                this.sectionLength = index - startIndex;
                this.segmentDataList.add(data);
                break;
            }
            index += FIELDTAGTYPE_LENGTH;
            final Integer valueLength = Integer.parseInt(new String(response, index, FIELDVALUE_LENGTH));
            index += FIELDVALUE_LENGTH;
            data.setValue(fieldTagType, new String(response, index, valueLength));
            index += valueLength;
        }
        return this.sectionLength;
    }

    protected Integer getHeaderLength() {
        return this.HEADERTAG_LENGTH;
    }

    protected T createSegmentData(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> getSegmentData() {
        return this.segmentDataList;
    }
}
