package com.finflux.risk.creditbureau.provider.cibil.response;

public class ResponseEndOfSegment {

    public final static int ES_SEGMENT_LENGTH = 17;
    private final static String EOS_TAG = "ES07";
    private final static int TAG_LENGTH = 7;
    private final static String EOS_CHARS = "0102**";

    private final boolean isValidPacket;

    public ResponseEndOfSegment(final byte[] allSections) {
        final String eosPacketString = new String(allSections, allSections.length - ES_SEGMENT_LENGTH - 1, ES_SEGMENT_LENGTH);
        int eosTagIndex = eosPacketString.indexOf(EOS_TAG); // It should be 0
        final String totalPacketSizeString = eosPacketString.substring(EOS_TAG.length(), TAG_LENGTH + EOS_TAG.length());
        String derivedEosChars = eosPacketString.substring(ES_SEGMENT_LENGTH - EOS_CHARS.length(),
                EOS_CHARS.length() + ES_SEGMENT_LENGTH - EOS_CHARS.length());
        this.isValidPacket = eosTagIndex == 0 && allSections.length - 1 == Long.parseLong(totalPacketSizeString)
                && EOS_CHARS.equals(derivedEosChars);
    }

    public boolean isValidPacket() {
        return this.isValidPacket;
    }
}
