
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
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AKA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SPOUSE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FATHER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MOTHER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AGE-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{}IDS"/>
 *         &lt;element name="GENDER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OWNERSHIP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{}ADDRESSES"/>
 *         &lt;element ref="{}PHONES"/>
 *         &lt;element ref="{}EMAILS"/>
 *         &lt;element name="BRANCH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KENDRA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MBR-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LOS-APP-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREDIT-INQ-PURPS-TYP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREDIT-INQ-PURPS-TYP-DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREDIT-INQUIRY-STAGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREDIT-RPT-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREDIT-REQ-TYP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREDIT-RPT-TRN-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AC-OPEN-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LOAN-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ENTITY-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlRootElement(name = "REQUEST")
public class REQUEST {

    @XmlElement(name = "NAME", required = true)
    protected String name;
    @XmlElement(name = "AKA")
    protected String aka;
    @XmlElement(name = "SPOUSE")
    protected String spouse;
    @XmlElement(name = "FATHER")
    protected String father;
    @XmlElement(name = "MOTHER")
    protected String mother;
    @XmlElement(name = "DOB")
    protected String dob;
    @XmlElement(name = "AGE")
    protected String age;
    @XmlElement(name = "AGE-AS-ON")
    protected String ageason;
    @XmlElement(name = "IDS", required = true)
    protected IDS ids;
    @XmlElement(name = "GENDER")
    protected String gender;
    @XmlElement(name = "OWNERSHIP")
    protected String ownership;
    @XmlElement(name = "ADDRESSES", required = true)
    protected ADDRESSES addresses;
    @XmlElement(name = "PHONES", required = true)
    protected PHONES phones;
    @XmlElement(name = "EMAILS", required = true)
    protected EMAILS emails;
    @XmlElement(name = "BRANCH")
    protected String branch;
    @XmlElement(name = "KENDRA")
    protected String kendra;
    @XmlElement(name = "MBR-ID")
    protected String mbrid;
    @XmlElement(name = "LOS-APP-ID")
    protected String losappid;
    @XmlElement(name = "CREDIT-INQ-PURPS-TYP")
    protected String creditinqpurpstyp;
    @XmlElement(name = "CREDIT-INQ-PURPS-TYP-DESC")
    protected String creditinqpurpstypdesc;
    @XmlElement(name = "CREDIT-INQUIRY-STAGE")
    protected String creditinquirystage;
    @XmlElement(name = "CREDIT-RPT-ID")
    protected String creditrptid;
    @XmlElement(name = "CREDIT-REQ-TYP")
    protected String creditreqtyp;
    @XmlElement(name = "CREDIT-RPT-TRN-DT-TM")
    protected String creditrpttrndttm;
    @XmlElement(name = "AC-OPEN-DT")
    protected String acopendt;
    @XmlElement(name = "LOAN-AMOUNT")
    protected String loanamount;
    @XmlElement(name = "ENTITY-ID")
    protected String entityid;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the aka property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAKA() {
        return aka;
    }

    /**
     * Sets the value of the aka property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAKA(String value) {
        this.aka = value;
    }

    /**
     * Gets the value of the spouse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSPOUSE() {
        return spouse;
    }

    /**
     * Sets the value of the spouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSPOUSE(String value) {
        this.spouse = value;
    }

    /**
     * Gets the value of the father property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFATHER() {
        return father;
    }

    /**
     * Sets the value of the father property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFATHER(String value) {
        this.father = value;
    }

    /**
     * Gets the value of the mother property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOTHER() {
        return mother;
    }

    /**
     * Sets the value of the mother property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOTHER(String value) {
        this.mother = value;
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOB() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOB(String value) {
        this.dob = value;
    }

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAGE() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAGE(String value) {
        this.age = value;
    }

    /**
     * Gets the value of the ageason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAGEASON() {
        return ageason;
    }

    /**
     * Sets the value of the ageason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAGEASON(String value) {
        this.ageason = value;
    }

    /**
     * Gets the value of the ids property.
     * 
     * @return
     *     possible object is
     *     {@link IDS }
     *     
     */
    public IDS getIDS() {
        return ids;
    }

    /**
     * Sets the value of the ids property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDS }
     *     
     */
    public void setIDS(IDS value) {
        this.ids = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGENDER() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGENDER(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the ownership property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOWNERSHIP() {
        return ownership;
    }

    /**
     * Sets the value of the ownership property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOWNERSHIP(String value) {
        this.ownership = value;
    }

    /**
     * Gets the value of the addresses property.
     * 
     * @return
     *     possible object is
     *     {@link ADDRESSES }
     *     
     */
    public ADDRESSES getADDRESSES() {
        return addresses;
    }

    /**
     * Sets the value of the addresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ADDRESSES }
     *     
     */
    public void setADDRESSES(ADDRESSES value) {
        this.addresses = value;
    }

    /**
     * Gets the value of the phones property.
     * 
     * @return
     *     possible object is
     *     {@link PHONES }
     *     
     */
    public PHONES getPHONES() {
        return phones;
    }

    /**
     * Sets the value of the phones property.
     * 
     * @param value
     *     allowed object is
     *     {@link PHONES }
     *     
     */
    public void setPHONES(PHONES value) {
        this.phones = value;
    }

    /**
     * Gets the value of the emails property.
     * 
     * @return
     *     possible object is
     *     {@link EMAILS }
     *     
     */
    public EMAILS getEMAILS() {
        return emails;
    }

    /**
     * Sets the value of the emails property.
     * 
     * @param value
     *     allowed object is
     *     {@link EMAILS }
     *     
     */
    public void setEMAILS(EMAILS value) {
        this.emails = value;
    }

    /**
     * Gets the value of the branch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBRANCH() {
        return branch;
    }

    /**
     * Sets the value of the branch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBRANCH(String value) {
        this.branch = value;
    }

    /**
     * Gets the value of the kendra property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKENDRA() {
        return kendra;
    }

    /**
     * Sets the value of the kendra property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKENDRA(String value) {
        this.kendra = value;
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
     * Gets the value of the creditinqpurpstyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDITINQPURPSTYP() {
        return creditinqpurpstyp;
    }

    /**
     * Sets the value of the creditinqpurpstyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDITINQPURPSTYP(String value) {
        this.creditinqpurpstyp = value;
    }

    /**
     * Gets the value of the creditinqpurpstypdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDITINQPURPSTYPDESC() {
        return creditinqpurpstypdesc;
    }

    /**
     * Sets the value of the creditinqpurpstypdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDITINQPURPSTYPDESC(String value) {
        this.creditinqpurpstypdesc = value;
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
     * Gets the value of the creditrptid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDITRPTID() {
        return creditrptid;
    }

    /**
     * Sets the value of the creditrptid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDITRPTID(String value) {
        this.creditrptid = value;
    }

    /**
     * Gets the value of the creditreqtyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDITREQTYP() {
        return creditreqtyp;
    }

    /**
     * Sets the value of the creditreqtyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDITREQTYP(String value) {
        this.creditreqtyp = value;
    }

    /**
     * Gets the value of the creditrpttrndttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREDITRPTTRNDTTM() {
        return creditrpttrndttm;
    }

    /**
     * Sets the value of the creditrpttrndttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREDITRPTTRNDTTM(String value) {
        this.creditrpttrndttm = value;
    }

    /**
     * Gets the value of the acopendt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACOPENDT() {
        return acopendt;
    }

    /**
     * Sets the value of the acopendt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACOPENDT(String value) {
        this.acopendt = value;
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

    /**
     * Gets the value of the entityid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getENTITYID() {
        return entityid;
    }

    /**
     * Sets the value of the entityid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setENTITYID(String value) {
        this.entityid = value;
    }

}
