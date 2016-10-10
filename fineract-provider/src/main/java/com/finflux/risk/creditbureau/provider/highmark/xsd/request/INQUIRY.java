
package com.finflux.risk.creditbureau.provider.highmark.xsd.request;

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
 *         &lt;element ref="{}APPLICANT-SEGMENT"/>
 *         &lt;element ref="{}ADDRESS-SEGMENT"/>
 *         &lt;element ref="{}APPLICATION-SEGMENT"/>
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
    "applicantsegment",
    "addresssegment",
    "applicationsegment"
})
@XmlRootElement(name = "INQUIRY")
public class INQUIRY {

    @XmlElement(name = "APPLICANT-SEGMENT", required = true)
    protected APPLICANTSEGMENT applicantsegment;
    @XmlElement(name = "ADDRESS-SEGMENT", required = true)
    protected ADDRESSSEGMENT addresssegment;
    @XmlElement(name = "APPLICATION-SEGMENT", required = true)
    protected APPLICATIONSEGMENT applicationsegment;

    /**
     * Gets the value of the applicantsegment property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT }
     *     
     */
    public APPLICANTSEGMENT getAPPLICANTSEGMENT() {
        return applicantsegment;
    }

    /**
     * Sets the value of the applicantsegment property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT }
     *     
     */
    public void setAPPLICANTSEGMENT(APPLICANTSEGMENT value) {
        this.applicantsegment = value;
    }

    /**
     * Gets the value of the addresssegment property.
     * 
     * @return
     *     possible object is
     *     {@link ADDRESSSEGMENT }
     *     
     */
    public ADDRESSSEGMENT getADDRESSSEGMENT() {
        return addresssegment;
    }

    /**
     * Sets the value of the addresssegment property.
     * 
     * @param value
     *     allowed object is
     *     {@link ADDRESSSEGMENT }
     *     
     */
    public void setADDRESSSEGMENT(ADDRESSSEGMENT value) {
        this.addresssegment = value;
    }

    /**
     * Gets the value of the applicationsegment property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICATIONSEGMENT }
     *     
     */
    public APPLICATIONSEGMENT getAPPLICATIONSEGMENT() {
        return applicationsegment;
    }

    /**
     * Sets the value of the applicationsegment property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICATIONSEGMENT }
     *     
     */
    public void setAPPLICATIONSEGMENT(APPLICATIONSEGMENT value) {
        this.applicationsegment = value;
    }

}
