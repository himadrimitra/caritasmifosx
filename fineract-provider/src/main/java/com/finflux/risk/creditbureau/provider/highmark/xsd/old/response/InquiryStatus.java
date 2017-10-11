package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;

public class InquiryStatus {

    @XmlElement(name = "INQUIRY")
    private Inquiry inquiry;

    public Inquiry getInquiry() {
        return this.inquiry;
    }
}
