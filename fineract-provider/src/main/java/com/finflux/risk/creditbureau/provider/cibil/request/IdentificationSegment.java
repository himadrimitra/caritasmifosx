package com.finflux.risk.creditbureau.provider.cibil.request;

import java.util.ArrayList;
import java.util.List;

public class IdentificationSegment extends RequestSegment {

    private final static Integer TOTAL_RECORDS = 8;
    private final String IDENTIFIER_TAG_NAME = "ID";
    private final static String[] IDENTITY_SEGMENT_TAGS = { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08" };
    private final static String IDENTITY_TYPE_TAG_ID = "01";
    private final static String IDENTITY_TAG_ID = "02";

    private final List<CustomerIdenfier> identifiers = new ArrayList<>();

    @Override
    public String prepareTuefPacket() {
        final Integer totalRecords = this.identifiers.size() < TOTAL_RECORDS ? this.identifiers.size() : TOTAL_RECORDS;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < totalRecords; i++) {
            CustomerIdenfier identifier = this.identifiers.get(i);
            builder.append(IDENTIFIER_TAG_NAME);
            builder.append(getFormattedLength(IDENTITY_SEGMENT_TAGS[i].length()));
            builder.append(IDENTITY_SEGMENT_TAGS[i]);
            builder.append(IDENTITY_TYPE_TAG_ID);
            builder.append(getFormattedLength(identifier.getIdentityType()));
            builder.append(identifier.getIdentityType());
            builder.append(IDENTITY_TAG_ID);
            builder.append(getFormattedLength(identifier.getIdentity().length()));
            builder.append(identifier.getIdentity());

        }
        return builder.toString();
    }

    public void addIdentifier(final CustomerIdenfier identity) {
        this.identifiers.add(identity);
    }

    public void addIdentifier(final String identityType, final String identity) {
        this.addIdentifier(new CustomerIdenfier(identityType, identity));
    }

    public static String getFieldName(final String errorRecord) {
        final String fieldTag = errorRecord.substring(ERRORTAG_STARTINDEX, ERRORTAG_ENDINDEX);
        String fieldName = "";
        switch (fieldTag) {
            case IDENTITY_TYPE_TAG_ID:
                fieldName = "Identifier Type";
            break;
            case IDENTITY_TAG_ID:
                fieldName = "Identifier";
            break;
        }
        return fieldName;
    }
}