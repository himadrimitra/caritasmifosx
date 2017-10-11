package com.finflux.risk.creditbureau.provider.cibil.request;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

/**
 * The Address Segment(s) contain(s) the known address of the consumer
 */
public class AddressSegment extends RequestSegment {

    private final static String ADDRESS_LINE1 = TAGTYPE_ONE;
    private final static String ADDRESS_LINE2 = TAGTYPE_TWO;
    private final static String ADDRESS_LINE3 = TAGTYPE_THREE;
    private final static String ADDRESS_LINE4 = TAGTYPE_FOUR;
    private final static String ADDRESS_LINE5 = TAGTYPE_FIVE;
    private final static String STATE_CODE = TAGTYPE_SIX;
    private final static String PIN_CODE = TAGTYPE_SEVEN;
    private final static String ADDRESS_CATEGORY = TAGTYPE_EIGHT;
    private final static String RESIDENCE_CODE = TAGTYPE_NINE;

    private final static Integer ADDRESSLINE_LENGTH = 40;
    private final static Integer TOTAL_ADDRESS_LINES = 5;
    private final static String ADDRESS_TAG = "PA";
    private final static String[] ADDRESS_SEGMENT_TAGS = { "A01", "A02" };
    private final List<Address> addresses = new ArrayList<>(2);

    @Override
    public String prepareTuefPacket() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.addresses.size(); i++) {
            Address address = this.addresses.get(i);
            builder.append(ADDRESS_TAG);
            builder.append(getFormattedLength(ADDRESS_SEGMENT_TAGS[i].length()));
            builder.append(ADDRESS_SEGMENT_TAGS[i]);
            appendData(builder, address);
        }
        // CIBIL expects two addresses. If second is not there, we need to add
        // same address again
        if (this.addresses.size() == 1) {
            Address address = this.addresses.get(0);
            builder.append(ADDRESS_TAG);
            builder.append(getFormattedLength(ADDRESS_SEGMENT_TAGS[1].length()));
            builder.append(ADDRESS_SEGMENT_TAGS[1]);
            appendData(builder, address);
        }

        return builder.toString();
    }

    private void appendData(final StringBuilder builder, final Address address) {
        final List<String> addressLines = generateFormattedAddressLines(address);
        final String[] tagTypes = { ADDRESS_LINE1, ADDRESS_LINE2, ADDRESS_LINE3, ADDRESS_LINE4, ADDRESS_LINE5 };
        final Integer addressListSize = addressLines.size();
        for (int i = 0; i < TOTAL_ADDRESS_LINES; i++) {
            builder.append(tagTypes[i]);
            if (i < addressListSize && !StringUtils.isEmpty(addressLines.get(i))) {
                builder.append(getFormattedLength(addressLines.get(i)));
                builder.append(addressLines.get(i));
            } else {
                builder.append(SIZE_ZERO);
            }
        }
        builder.append(STATE_CODE);
        builder.append(SIZE_TWO);
        builder.append(address.getStateCode());
        builder.append(PIN_CODE);
        builder.append(getFormattedLength(address.getPinCode()));
        builder.append(address.getPinCode());
        builder.append(ADDRESS_CATEGORY);
        builder.append(SIZE_TWO);
        builder.append(address.getAddressCategory());
        builder.append(RESIDENCE_CODE);
        builder.append(SIZE_TWO);
        builder.append(address.getResideneCode());
    }

    public void addAddress(final Address address) {
        this.addresses.add(address);
    }

    private List<String> generateFormattedAddressLines(final Address address) {
        StringTokenizer tokens = new StringTokenizer(address.getAddressLine(), " ");
        List<String> addresses = new ArrayList<>();
        StringBuffer buff = new StringBuffer();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if ((buff.length() + token.length() + 1) > ADDRESSLINE_LENGTH) {
                addresses.add(buff.toString());
                buff.setLength(0);
                buff.append(token);
            } else {
                if (buff.length() > 0) buff.append(" ");
                buff.append(token);
            }
        }
        return addresses;
    }

    public static String getFieldName(final String errorRecord) {
        final String fieldTag = errorRecord.substring(ERRORTAG_STARTINDEX, ERRORTAG_ENDINDEX);
        String fieldName = "";
        switch (fieldTag) {
            case ADDRESS_LINE1:
                fieldName = "Address Line1";
            break;
            case ADDRESS_LINE2:
                fieldName = "Address Line2";
            break;
            case ADDRESS_LINE3:
                fieldName = "Address Line3";
            break;
            case ADDRESS_LINE4:
                fieldName = "Address Line4";
            break;
            case ADDRESS_LINE5:
                fieldName = "Address Line5";
            break;
            case STATE_CODE:
                fieldName = "State Code";
            break;
            case PIN_CODE:
                fieldName = "Pin Code";
            break;
            case ADDRESS_CATEGORY:
                fieldName = "Address Category";
            break;
            case RESIDENCE_CODE:
                fieldName = "Residence Code";
            break;
        }
        return fieldName;
    }
}
