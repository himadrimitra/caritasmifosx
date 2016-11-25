package com.finflux.risk.creditbureau.provider.data;

public class EnquiryDocumentData {

    final String clientIdentificationType;
    final String clientIdentificationTypeId;
    final String clientIdentification;

    public EnquiryDocumentData(final String clientIdentificationType, final String clientIdentificationTypeId,
            final String clientIdentification) {
        super();
        this.clientIdentificationType = clientIdentificationType;
        this.clientIdentificationTypeId = clientIdentificationTypeId;
        this.clientIdentification = clientIdentification;
    }

    public String getClientIdentificationType() {
        return clientIdentificationType;
    }

    public String getClientIdentification() {
        return clientIdentification;
    }

    public String getClientIdentificationTypeId() {
        return this.clientIdentificationTypeId;
    }
}