package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MFIAddlAdrsDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MFIAddlAdrsDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MFIAddressline" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MFIState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MFIPostalPIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://services.equifax.com/eport/ws/schemas/1.0}MFIAdditionalAddressAttributes"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MFIAddlAdrsDetailsType", propOrder = {
    "mfiAddressline",
    "mfiState",
    "mfiPostalPIN"
})
public class MFIAddlAdrsDetailsType {

    @XmlElement(name = "MFIAddressline")
    protected String mfiAddressline;
    @XmlElement(name = "MFIState")
    protected String mfiState;
    @XmlElement(name = "MFIPostalPIN")
    protected String mfiPostalPIN;
    @XmlAttribute(name = "seq")
    protected Integer seq;

    /**
     * Gets the value of the mfiAddressline property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMFIAddressline() {
        return mfiAddressline;
    }

    /**
     * Sets the value of the mfiAddressline property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMFIAddressline(String value) {
        this.mfiAddressline = value;
    }

    /**
     * Gets the value of the mfiState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMFIState() {
        return mfiState;
    }

    /**
     * Sets the value of the mfiState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMFIState(String value) {
        this.mfiState = value;
    }

    /**
     * Gets the value of the mfiPostalPIN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMFIPostalPIN() {
        return mfiPostalPIN;
    }

    /**
     * Sets the value of the mfiPostalPIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMFIPostalPIN(String value) {
        this.mfiPostalPIN = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSeq(Integer value) {
        this.seq = value;
    }

}
