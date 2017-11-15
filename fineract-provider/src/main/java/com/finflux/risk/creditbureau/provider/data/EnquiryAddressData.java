package com.finflux.risk.creditbureau.provider.data;

public class EnquiryAddressData {

    final String clientAddressTypeId;
    final String clientAddressType;
    final String clientAddress;
    final String clientCity;
    final String clientStateCode;
    final String clientPin;
    final String stateName ;
    
    public EnquiryAddressData(final String clientAddressTypeId, final String clientAddressType, final String clientAddress,
            final String clientCity, final String clientStateCode, final String stateName, final String clientPin) {
        super();
        this.clientAddressTypeId = clientAddressTypeId;
        this.clientAddressType = clientAddressType;
        this.clientAddress = clientAddress;
        this.clientCity = clientCity;
        this.clientStateCode = clientStateCode;
        this.stateName = stateName ;
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

    public String getClientStateCode() {
        return clientStateCode;
    }
    
    public String getClientStateName() {
        return this.stateName ;
    }
    
    public String getClientPin() {
        return clientPin;
    }

    public String getClientAddressTypeId() {
        return this.clientAddressTypeId;
    }
}