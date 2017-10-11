package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;

public class OverlapReport {

    @XmlElement(name = "RESPONSES")
    private Responses responses;

    public Responses getResponses() {
        return this.responses;
    }

}
