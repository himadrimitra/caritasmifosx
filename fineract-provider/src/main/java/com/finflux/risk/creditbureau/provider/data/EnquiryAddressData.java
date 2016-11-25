package com.finflux.risk.creditbureau.provider.data;

public class EnquiryAddressData {

    final String clientAddressType;
    final String clientAddress;
    final String clientCity;
    final String clientState;
    final String clientPin;

    public EnquiryAddressData(final String clientAddressType, final String clientAddress, final String clientCity,
            final String clientState, final String clientPin) {
        super();
        this.clientAddressType = clientAddressType;
        this.clientAddress = clientAddress;
        this.clientCity = clientCity;
        this.clientState = clientState;
        this.clientPin = clientPin;
    }

    public String getClientAddressType() {
        return clientAddressType;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getClientCity() {
        return clientCity;
    }

    public String getClientState() {
        return clientState;
    }

    public String getClientPin() {
        return clientPin;
    }

}
