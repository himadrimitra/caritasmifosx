package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import javax.xml.bind.annotation.XmlElement;

public class FileSubmissionDetails {
    @XmlElement(name = "BATCH-ID")
    private String BATCHID;
    @XmlElement (name  = "REQUEST-DT-TM")
    private String REQUESTDTTM;
    @XmlElement (name  = "RESPONSE-DT-TM")
    private String RESPONSEDTTM;
    @XmlElement (name  = "RESPONSE-TYPE")
    private String RESPONSETYPE;
    @XmlElement (name  = "DESCRIPTION")
    private String DESCRIPTION;
    
    public String getBATCHID() {
        return this.BATCHID;
    }
    
    public String getREQUESTDTTM() {
        return this.REQUESTDTTM;
    }
    
    public String getRESPONSEDTTM() {
        return this.RESPONSEDTTM;
    }
    
    public String getRESPONSETYPE() {
        return this.RESPONSETYPE;
    }
    
    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }
    
}
