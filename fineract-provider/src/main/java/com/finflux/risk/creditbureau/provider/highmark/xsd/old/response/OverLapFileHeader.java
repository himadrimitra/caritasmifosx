package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;

public class OverLapFileHeader {

    @XmlElement(name = "FILE-NAME")
    protected String fileName;

    public String getFileName() {
        return this.fileName;
    }
}
