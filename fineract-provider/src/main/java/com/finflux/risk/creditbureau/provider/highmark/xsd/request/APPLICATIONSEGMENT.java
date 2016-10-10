
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
 *         &lt;element name="INQUIRY-UNIQUE-REF-NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CREDT-INQ-PURPS-TYP">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ACCT-ORIG"/>
 *               &lt;enumeration value="ACCT-MAINT"/>
 *               &lt;enumeration value="OTHER"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CREDT-INQ-PURPS-TYP-DESC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CREDIT-INQUIRY-STAGE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PRE-SCREEN"/>
 *               &lt;enumeration value="PRE-DISB"/>
 *               &lt;enumeration value="UW-REVIEW"/>
 *               &lt;enumeration value="COLLECTION"/>
 *               &lt;enumeration value="RENEWAL"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CREDT-RPT-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CREDT-REQ-TYP">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="INDV"/>
 *               &lt;enumeration value="JOIN"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CREDT-RPT-TRN-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MBR-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="KENDRA-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BRANCH-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LOS-APP-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LOAN-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "credtinqpurpstyp",
    "credtinqpurpstypdesc",
    "creditinquirystage",
    "credtrptid",
    "credtreqtyp",
    "credtrpttrndttm",
    "mbrid",
    "kendraid",
    "branchid",
    "losappid",
    "loanamount"
})
@XmlRootElement(name = "APPLICATION-SEGMENT")
public class APPLICATIONSEGMENT {

    @XmlElement(name = "INQUIRY-UNIQUE-REF-NO", required = true)
    protected String inquiryuniquerefno;
    @XmlElement(name = "CREDT-INQ-PURPS-TYP", required = true)
    protected String credtinqpurpstyp;
    @XmlElement(name = "CREDT-INQ-PURPS-TYP-DESC", required = true)
    protected String credtinqpurpstypdesc;
    @XmlElement(name = "CREDIT-INQUIRY-STAGE", required = true)
    protected String creditinquirystage;
    @XmlElement(name = "CREDT-RPT-ID", required = true)
    protected String credtrptid;
    @XmlElement(name = "CREDT-REQ-TYP", required = true)
    protected String credtreqtyp;
    @XmlElement(name = "CREDT-RPT-TRN-DT-TM", required = true)
    protected String credtrpttrndttm;
    @XmlElement(name = "MBR-ID", required = true)
    protected String mbrid;
    @XmlElement(name = "KENDRA-ID", required = true)
    protected String kendraid;
    @XmlElement(name = "BRANCH-ID", required = true)
    protected String branchid;
    @XmlElement(name = "LOS-APP-ID", required = true)
    protected String losappid;
    @XmlElement(name = "LOAN-AMOUNT")
    protected String loanamount;

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
     * Gets the value of the credtinqpurpstyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDTINQPURPSTYP() {
        return credtinqpurpstyp;
    }

    /**
     * Sets the value of the credtinqpurpstyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDTINQPURPSTYP(String value) {
        this.credtinqpurpstyp = value;
    }

    /**
     * Gets the value of the credtinqpurpstypdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDTINQPURPSTYPDESC() {
        return credtinqpurpstypdesc;
    }

    /**
     * Sets the value of the credtinqpurpstypdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDTINQPURPSTYPDESC(String value) {
        this.credtinqpurpstypdesc = value;
    }

    /**
     * Gets the value of the creditinquirystage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDITINQUIRYSTAGE() {
        return creditinquirystage;
    }

    /**
     * Sets the value of the creditinquirystage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDITINQUIRYSTAGE(String value) {
        this.creditinquirystage = value;
    }

    /**
     * Gets the value of the credtrptid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDTRPTID() {
        return credtrptid;
    }

    /**
     * Sets the value of the credtrptid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDTRPTID(String value) {
        this.credtrptid = value;
    }

    /**
     * Gets the value of the credtreqtyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDTREQTYP() {
        return credtreqtyp;
    }

    /**
     * Sets the value of the credtreqtyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDTREQTYP(String value) {
        this.credtreqtyp = value;
    }

    /**
     * Gets the value of the credtrpttrndttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDTRPTTRNDTTM() {
        return credtrpttrndttm;
    }

    /**
     * Sets the value of the credtrpttrndttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDTRPTTRNDTTM(String value) {
        this.credtrpttrndttm = value;
    }

    /**
     * Gets the value of the mbrid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMBRID() {
        return mbrid;
    }

    /**
     * Sets the value of the mbrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMBRID(String value) {
        this.mbrid = value;
    }

    /**
     * Gets the value of the kendraid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKENDRAID() {
        return kendraid;
    }

    /**
     * Sets the value of the kendraid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKENDRAID(String value) {
        this.kendraid = value;
    }

    /**
     * Gets the value of the branchid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBRANCHID() {
        return branchid;
    }

    /**
     * Sets the value of the branchid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBRANCHID(String value) {
        this.branchid = value;
    }

    /**
     * Gets the value of the losappid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOSAPPID() {
        return losappid;
    }

    /**
     * Sets the value of the losappid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOSAPPID(String value) {
        this.losappid = value;
    }

    /**
     * Gets the value of the loanamount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOANAMOUNT() {
        return loanamount;
    }

    /**
     * Sets the value of the loanamount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOANAMOUNT(String value) {
        this.loanamount = value;
    }

}
