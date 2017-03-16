package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For CCR Scores Implementation
 * 
 * <p>Java class for EquifaxScoreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EquifaxScoreType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Retail" type="{http://services.equifax.com/eport/ws/schemas/1.0}RetailType" minOccurs="0"/&gt;
 *         &lt;element name="MFI" type="{http://services.equifax.com/eport/ws/schemas/1.0}MFIType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EquifaxScoreType", propOrder = {
    "retail",
    "mfi"
})
public class EquifaxScoreType {

    @XmlElement(name = "Retail")
    protected RetailType retail;
    @XmlElement(name = "MFI")
    protected MFIType mfi;

    /**
     * Gets the value of the retail property.
     * 
     * @return
     *     possible object is
     *     {@link RetailType }
     *     
     */
    public RetailType getRetail() {
        return retail;
    }

    /**
     * Sets the value of the retail property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetailType }
     *     
     */
    public void setRetail(RetailType value) {
        this.retail = value;
    }

    /**
     * Gets the value of the mfi property.
     * 
     * @return
     *     possible object is
     *     {@link MFIType }
     *     
     */
    public MFIType getMFI() {
        return mfi;
    }

    /**
     * Sets the value of the mfi property.
     * 
     * @param value
     *     allowed object is
     *     {@link MFIType }
     *     
     */
    public void setMFI(MFIType value) {
        this.mfi = value;
    }

}
