package com.finflux.risk.creditbureau.provider.cibil.request;

import org.apache.commons.lang3.StringUtils;

public abstract class RequestSegment {

    protected final static String SIZE_ZERO = "00";
    protected final static String SIZE_ONE = "01";
    protected final static String SIZE_TWO = "02";

    protected final static String TAGTYPE_ONE = "01";
    protected final static String TAGTYPE_TWO = "02";
    protected final static String TAGTYPE_THREE = "03";
    protected final static String TAGTYPE_FOUR = "04";
    protected final static String TAGTYPE_FIVE = "05";
    protected final static String TAGTYPE_SIX = "06";
    protected final static String TAGTYPE_SEVEN = "07";
    protected final static String TAGTYPE_EIGHT = "08";
    protected final static String TAGTYPE_NINE = "09";

    public abstract String prepareTuefPacket();

    protected String getFormattedLength(final String value) {
        if (StringUtils.isEmpty(value)) return SIZE_ZERO;
        int length = value.length();
        if (value.length() < 10) return "0" + value.length();
        return String.valueOf(length);
    }

    protected String getFormattedLength(final int length) {
        if (length < 10) return "0" + length;
        return String.valueOf(length);
    }
}
