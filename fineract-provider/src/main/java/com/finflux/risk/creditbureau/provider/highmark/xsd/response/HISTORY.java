
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
 *         &lt;element name="MEMBER-NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="INQUIRY-DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PURPOSE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OWNERSHIP-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REMARK" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlRootElement(name = "HISTORY")
public class HISTORY {

    @XmlElement(name = "MEMBER-NAME", required = true)
    protected String membername;
    @XmlElement(name = "INQUIRY-DATE", required = true)
    protected String inquirydate;
    @XmlElement(name = "PURPOSE", required = true)
    protected String purpose;
    @XmlElement(name = "OWNERSHIP-TYPE")
    protected String ownershiptype;
    @XmlElement(name = "AMOUNT")
    protected String amount;
    @XmlElement(name = "REMARK")
    protected String remark;

    /**
     * Gets the value of the membername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMEMBERNAME() {
        return membername;
    }

    /**
     * Sets the value of the membername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMEMBERNAME(String value) {
        this.membername = value;
    }

    /**
     * Gets the value of the inquirydate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINQUIRYDATE() {
        return inquirydate;
    }

    /**
     * Sets the value of the inquirydate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINQUIRYDATE(String value) {
        this.inquirydate = value;
    }

    /**
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPURPOSE() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPURPOSE(String value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the ownershiptype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOWNERSHIPTYPE() {
        return ownershiptype;
    }

    /**
     * Sets the value of the ownershiptype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOWNERSHIPTYPE(String value) {
        this.ownershiptype = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAMOUNT() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAMOUNT(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the remark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREMARK() {
        return remark;
    }

    /**
     * Sets the value of the remark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREMARK(String value) {
        this.remark = value;
    }

}
