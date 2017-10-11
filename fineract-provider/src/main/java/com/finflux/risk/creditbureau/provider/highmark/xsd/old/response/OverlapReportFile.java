package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OVERLAP-REPORT-FILE")
public class OverlapReportFile {

    @XmlElement(name = "OVERLAP-FILE-HEADER")
    private OverLapFileHeader overLapFileHeader;

    @XmlElement(name = "OVERLAP-REPORTS")
    private OverlapReports overlapReports;

    public OverLapFileHeader getOverLapFileHeader() {
        return this.overLapFileHeader;
    }

    public OverlapReports getOverlapReports() {
        return this.overlapReports;
    }

}
