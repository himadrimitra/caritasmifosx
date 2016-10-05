package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;


public class Inquiry {

    @XmlElement(name = "INQUIRY-UNIQUE-REF-NO")
    private String inquiryReferenceNumber;

    @XmlElement(name = "MBR-ID")
    private Long ClientId;

    @XmlElement(name = "REQUEST-DT-TM")
    private String requestDateTime;

    @XmlElement(name = "RESPONSE-DT-TM")
    private String responseDateTime;

    @XmlElement(name = "RESPONSE-TYPE")
    private String responseType;

    public String getInquiryReferenceNumber() {
        return this.inquiryReferenceNumber;
    }

    public Long getClientId() {
        return this.ClientId;
    }

    public String getRequestDateTime() {
        return this.requestDateTime;
    }

    public String getResponseDateTime() {
        return this.responseDateTime;
    }

    public String getResponseType() {
        return this.responseType;
    }

}
