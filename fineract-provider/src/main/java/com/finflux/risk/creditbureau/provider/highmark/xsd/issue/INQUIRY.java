
package com.finflux.risk.creditbureau.provider.highmark.xsd.issue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="INQUIRY-UNIQUE-REF-NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQUEST-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REPORT-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "inquiryuniquerefno",
    "requestdttm",
    "reportid"
})
@XmlRootElement(name = "INQUIRY")
public class INQUIRY {

    @XmlElement(name = "INQUIRY-UNIQUE-REF-NO", required = true)
    protected String inquiryuniquerefno;
    @XmlElement(name = "REQUEST-DT-TM", required = true)
    protected String requestdttm;
    @XmlElement(name = "REPORT-ID", required = true)
    protected String reportid;

    /**
     * Gets the value of the inquiryuniquerefno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINQUIRYUNIQUEREFNO() {
        return inquiryuniquerefno;
    }

    /**
     * Sets the value of the inquiryuniquerefno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINQUIRYUNIQUEREFNO(String value) {
        this.inquiryuniquerefno = value;
    }

    /**
     * Gets the value of the requestdttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUESTDTTM() {
        return requestdttm;
    }

    /**
     * Sets the value of the requestdttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUESTDTTM(String value) {
        this.requestdttm = value;
    }

    /**
     * Gets the value of the reportid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREPORTID() {
        return reportid;
    }

    /**
     * Sets the value of the reportid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREPORTID(String value) {
        this.reportid = value;
    }

}
