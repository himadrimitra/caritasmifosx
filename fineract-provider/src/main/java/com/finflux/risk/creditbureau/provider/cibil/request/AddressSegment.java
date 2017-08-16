package com.finflux.risk.creditbureau.provider.cibil.request;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

/**
 * The Address Segment(s) contain(s) the known address of the consumer
 */
public class AddressSegment extends RequestSegment {

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
        //CIBIL expects two addresses. If second is not there, we need to add same address again
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
        final String[] tagTypes = { TAGTYPE_ONE, TAGTYPE_TWO, TAGTYPE_THREE, TAGTYPE_FOUR, TAGTYPE_FIVE };
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
        builder.append(TAGTYPE_SIX);
        builder.append(SIZE_TWO);
        builder.append(address.getStateCode());
        builder.append(TAGTYPE_SEVEN);
        builder.append(getFormattedLength(address.getPinCode()));
        builder.append(address.getPinCode());
        builder.append(TAGTYPE_EIGHT);
        builder.append(SIZE_TWO);
        builder.append(address.getAddressCategory());
        builder.append(TAGTYPE_NINE);
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
}
