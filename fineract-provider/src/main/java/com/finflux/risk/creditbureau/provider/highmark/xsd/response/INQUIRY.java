
package com.finflux.risk.creditbureau.provider.highmark.xsd.response;

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
 *       &lt;all>
 *         &lt;element name="INQUIRY-UNIQUE-REF-NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQUEST-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RESPONSE-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RESPONSE-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "INQUIRY")
public class INQUIRY {

    @XmlElement(name = "INQUIRY-UNIQUE-REF-NO", required = true)
    protected String inquiryuniquerefno;
    @XmlElement(name = "REQUEST-DT-TM", required = true)
    protected String requestdttm;
    @XmlElement(name = "RESPONSE-DT-TM", required = true)
    protected String responsedttm;
    @XmlElement(name = "RESPONSE-TYPE", required = true)
    protected String responsetype;
    @XmlElement(name = "DESCRIPTION", required = true)
    protected String description;

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
     * Gets the value of the responsedttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESPONSEDTTM() {
        return responsedttm;
    }

    /**
     * Sets the value of the responsedttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESPONSEDTTM(String value) {
        this.responsedttm = value;
    }

    /**
     * Gets the value of the responsetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESPONSETYPE() {
        return responsetype;
    }

    /**
     * Sets the value of the responsetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESPONSETYPE(String value) {
        this.responsetype = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDESCRIPTION() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDESCRIPTION(String value) {
        this.description = value;
    }

}
