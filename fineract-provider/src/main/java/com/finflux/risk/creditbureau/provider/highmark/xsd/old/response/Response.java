package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;

public class Response {

    @XmlElement(name = "LOAN-DETAILS")
    protected LoanDetails loanDetails;

    public LoanDetails getLoanDetails() {
        return this.loanDetails;
    }

}
