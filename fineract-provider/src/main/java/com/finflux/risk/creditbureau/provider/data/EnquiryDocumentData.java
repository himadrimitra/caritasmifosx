package com.finflux.risk.creditbureau.provider.data;

public class EnquiryDocumentData {

    final String clientIdentificationType;
    final String clientIdentification;

    public EnquiryDocumentData(final String clientIdentificationType, final String clientIdentification) {
        super();
        this.clientIdentificationType = clientIdentificationType;
        this.clientIdentification = clientIdentification;
    }

    public String getClientIdentificationType() {
        return clientIdentificationType;
    }

    public String getClientIdentification() {
        return clientIdentification;
    }
}