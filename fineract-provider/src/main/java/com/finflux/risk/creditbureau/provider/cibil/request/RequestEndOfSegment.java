package com.finflux.risk.creditbureau.provider.cibil.request;

import org.apache.commons.lang3.StringUtils;

public class RequestEndOfSegment extends RequestSegment {

    private final static int PACKET_SIZE = 15;
    private final static String EOS_TAG = "ES05";
    private final static int TAG_LENGTH = 5;
    private final static String EOS_CHARS = "0102**";
    private String requestPacketSize = null;

    public RequestEndOfSegment(final String allSections) {
        requestPacketSize = String.valueOf(allSections.getBytes().length) + PACKET_SIZE;
        requestPacketSize = StringUtils.leftPad(requestPacketSize, TAG_LENGTH - requestPacketSize.length(), "0");
    }

    @Override
    public String prepareTuefPacket() {
        StringBuilder builder = new StringBuilder();
        builder.append(EOS_TAG);
        builder.append(requestPacketSize);
        builder.append(EOS_CHARS);
        return builder.toString();
    }

}
