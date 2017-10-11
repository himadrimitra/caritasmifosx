package com.finflux.risk.creditbureau.provider.cibil.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

public class CibilRequest extends RequestSegment {

    // DDMMYYYY format , 8 bytes
    protected final DateFormat dateFormat_DDMMYYYY = new SimpleDateFormat("ddMMyyyy");
    protected final boolean truncateData = true;

    private HeaderSegment headerSegment = null;

    private NameSegment nameSegment = null;

    private IdentificationSegment identificationSegment = null;

    private TelephoneSegment telephoneSegment = null;

    private AddressSegment addressSegment = null;

    private AccountSegment accountSegment = null;

    public HeaderSegment createHeaderSegment() {
        if (this.headerSegment == null) {
            this.headerSegment = new HeaderSegment(this);
        }
        return this.headerSegment;
    }

    public NameSegment createNameSegment() {
        if (this.nameSegment == null) {
            this.nameSegment = new NameSegment(this);
        }
        return this.nameSegment;
    }

    public IdentificationSegment createIdentificationSegment() {
        if (this.identificationSegment == null) {
            this.identificationSegment = new IdentificationSegment();
        }
        return this.identificationSegment;
    }

    public TelephoneSegment createTelephoneSegment() {
        if (this.telephoneSegment == null) {
            this.telephoneSegment = new TelephoneSegment();
        }
        return this.telephoneSegment;
    }

    public AddressSegment createAddressSegment() {
        if (this.addressSegment == null) {
            this.addressSegment = new AddressSegment();
        }
        return this.addressSegment;
    }

    public AccountSegment createAccountSegment() {
        if (this.accountSegment == null) {
            this.accountSegment = new AccountSegment();
        }
        return this.accountSegment;
    }

    @Override
    public String prepareTuefPacket() {
        StringBuilder builder = new StringBuilder();
        if (this.headerSegment != null) {
            builder.append(this.headerSegment.prepareTuefPacket());
        }
        if (this.nameSegment != null) {
            builder.append(this.nameSegment.prepareTuefPacket());
        }
        if (this.identificationSegment != null) {
            builder.append(this.identificationSegment.prepareTuefPacket());
        }
        if (this.telephoneSegment != null) {
            builder.append(this.telephoneSegment.prepareTuefPacket());
        }
        if (this.addressSegment != null) {
            builder.append(this.addressSegment.prepareTuefPacket());
        }
        if (this.accountSegment != null) {
            builder.append(this.accountSegment.prepareTuefPacket());
        }
        EndOfSegment endOfSegment = new EndOfSegment(builder.toString());
        builder.append(endOfSegment.prepareTuefPacket());
        return builder.toString();
    }

    class EndOfSegment extends RequestSegment {

        private final static int PACKET_SIZE = 15;
        private final static String EOS_TAG = "ES05";
        private final static int TAG_LENGTH = 5;
        private final static String EOS_CHARS = "0102**";
        private String requestPacketSize = null;

        public EndOfSegment(final String allSections) {
            Integer totalSize = allSections.getBytes().length + PACKET_SIZE;
            requestPacketSize = StringUtils.leftPad(String.valueOf(totalSize), TAG_LENGTH, "0");
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
}
