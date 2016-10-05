package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Responses {

    @XmlElement(name = "RESPONSE")
    protected List<Response> response;

    public List<Response> getResponse() {
        return this.response;
    }

}
