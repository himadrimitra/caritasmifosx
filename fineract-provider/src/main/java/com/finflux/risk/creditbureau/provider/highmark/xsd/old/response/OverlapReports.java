package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;

public class OverlapReports {

    @XmlElement(name = "OVERLAP-REPORT")
    private OverlapReport overlapReport;

    public OverlapReport getOverlapReport() {
        return this.overlapReport;
    }

}
