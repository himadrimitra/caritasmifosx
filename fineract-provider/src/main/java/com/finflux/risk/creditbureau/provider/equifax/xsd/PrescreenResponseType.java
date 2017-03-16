package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrescreenResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrescreenResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HeaderData" type="{http://services.equifax.com/eport/ws/schemas/1.0}HeaderDataType" minOccurs="0"/&gt;
 *         &lt;element name="PIIData" type="{http://services.equifax.com/eport/ws/schemas/1.0}PIIDataType" minOccurs="0"/&gt;
 *         &lt;element name="BureauAttributes" type="{http://services.equifax.com/eport/ws/schemas/1.0}BureauAttributesType" minOccurs="0"/&gt;
 *         &lt;element name="NonBureauAttributes" type="{http://services.equifax.com/eport/ws/schemas/1.0}NonBureauAttributesType" minOccurs="0"/&gt;
 *         &lt;element name="ResponseData" type="{http://services.equifax.com/eport/ws/schemas/1.0}ResponseDataType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrescreenResponseType", propOrder = {
    "headerData",
    "piiData",
    "bureauAttributes",
    "nonBureauAttributes",
    "responseData"
})
public class PrescreenResponseType {

    @XmlElement(name = "HeaderData")
    protected HeaderDataType headerData;
    @XmlElement(name = "PIIData")
    protected PIIDataType piiData;
    @XmlElement(name = "BureauAttributes")
    protected BureauAttributesType bureauAttributes;
    @XmlElement(name = "NonBureauAttributes")
    protected NonBureauAttributesType nonBureauAttributes;
    @XmlElement(name = "ResponseData")
    protected ResponseDataType responseData;

    /**
     * Gets the value of the headerData property.
     * 
     * @return
     *     possible object is
     *     {@link HeaderDataType }
     *     
     */
    public HeaderDataType getHeaderData() {
        return headerData;
    }

    /**
     * Sets the value of the headerData property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderDataType }
     *     
     */
    public void setHeaderData(HeaderDataType value) {
        this.headerData = value;
    }

    /**
     * Gets the value of the piiData property.
     * 
     * @return
     *     possible object is
     *     {@link PIIDataType }
     *     
     */
    public PIIDataType getPIIData() {
        return piiData;
    }

    /**
     * Sets the value of the piiData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PIIDataType }
     *     
     */
    public void setPIIData(PIIDataType value) {
        this.piiData = value;
    }

    /**
     * Gets the value of the bureauAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link BureauAttributesType }
     *     
     */
    public BureauAttributesType getBureauAttributes() {
        return bureauAttributes;
    }

    /**
     * Sets the value of the bureauAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BureauAttributesType }
     *     
     */
    public void setBureauAttributes(BureauAttributesType value) {
        this.bureauAttributes = value;
    }

    /**
     * Gets the value of the nonBureauAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link NonBureauAttributesType }
     *     
     */
    public NonBureauAttributesType getNonBureauAttributes() {
        return nonBureauAttributes;
    }

    /**
     * Sets the value of the nonBureauAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonBureauAttributesType }
     *     
     */
    public void setNonBureauAttributes(NonBureauAttributesType value) {
        this.nonBureauAttributes = value;
    }

    /**
     * Gets the value of the responseData property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseDataType }
     *     
     */
    public ResponseDataType getResponseData() {
        return responseData;
    }

    /**
     * Sets the value of the responseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseDataType }
     *     
     */
    public void setResponseData(ResponseDataType value) {
        this.responseData = value;
    }

}
