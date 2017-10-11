
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
 *         &lt;element name="PRODUCT-TYP">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="BASE_PLUS_REPORT"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PRODUCT-VER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQ-MBR">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="5"/>
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SUB-MBR-ID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="5"/>
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="INQ-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQ-VOL-TYP">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="INDV"/>
 *               &lt;enumeration value="JOIN"/>
 *               &lt;enumeration value="C01"/>
 *               &lt;enumeration value="C03"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="REQ-ACTN-TYP">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ISSUE"/>
 *               &lt;enumeration value="AT02"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TEST-FLG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="USER-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PWD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AUTH-FLG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AUTH-TITLE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RES-FRMT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MEMBER-PRE-OVERRIDE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RES-FRMT-EMBD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LOS-NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LOS-VENDER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LOS-VERSION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "producttyp",
    "productver",
    "reqmbr",
    "submbrid",
    "inqdttm",
    "reqvoltyp",
    "reqactntyp",
    "testflg",
    "userid",
    "pwd",
    "authflg",
    "authtitle",
    "resfrmt",
    "memberpreoverride",
    "resfrmtembd",
    "losname",
    "losvender",
    "losversion"
})
@XmlRootElement(name = "HEADER-SEGMENT")
public class HEADERSEGMENT {

    @XmlElement(name = "PRODUCT-TYP", required = true)
    protected String producttyp;
    @XmlElement(name = "PRODUCT-VER", required = true)
    protected String productver;
    @XmlElement(name = "REQ-MBR", required = true)
    protected String reqmbr;
    @XmlElement(name = "SUB-MBR-ID", required = true)
    protected String submbrid;
    @XmlElement(name = "INQ-DT-TM", required = true)
    protected String inqdttm;
    @XmlElement(name = "REQ-VOL-TYP", required = true)
    protected String reqvoltyp;
    @XmlElement(name = "REQ-ACTN-TYP", required = true)
    protected String reqactntyp;
    @XmlElement(name = "TEST-FLG", required = true)
    protected String testflg;
    @XmlElement(name = "USER-ID", required = true)
    protected String userid;
    @XmlElement(name = "PWD", required = true)
    protected String pwd;
    @XmlElement(name = "AUTH-FLG", required = true)
    protected String authflg;
    @XmlElement(name = "AUTH-TITLE", required = true)
    protected String authtitle;
    @XmlElement(name = "RES-FRMT", required = true)
    protected String resfrmt;
    @XmlElement(name = "MEMBER-PRE-OVERRIDE", required = true)
    protected String memberpreoverride;
    @XmlElement(name = "RES-FRMT-EMBD", required = true)
    protected String resfrmtembd;
    @XmlElement(name = "LOS-NAME", required = true)
    protected String losname;
    @XmlElement(name = "LOS-VENDER")
    protected String losvender;
    @XmlElement(name = "LOS-VERSION")
    protected String losversion;

    /**
     * Gets the value of the producttyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTTYP() {
        return producttyp;
    }

    /**
     * Sets the value of the producttyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTTYP(String value) {
        this.producttyp = value;
    }

    /**
     * Gets the value of the productver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTVER() {
        return productver;
    }

    /**
     * Sets the value of the productver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTVER(String value) {
        this.productver = value;
    }

    /**
     * Gets the value of the reqmbr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQMBR() {
        return reqmbr;
    }

    /**
     * Sets the value of the reqmbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQMBR(String value) {
        this.reqmbr = value;
    }

    /**
     * Gets the value of the submbrid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUBMBRID() {
        return submbrid;
    }

    /**
     * Sets the value of the submbrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUBMBRID(String value) {
        this.submbrid = value;
    }

    /**
     * Gets the value of the inqdttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINQDTTM() {
        return inqdttm;
    }

    /**
     * Sets the value of the inqdttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINQDTTM(String value) {
        this.inqdttm = value;
    }

    /**
     * Gets the value of the reqvoltyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQVOLTYP() {
        return reqvoltyp;
    }

    /**
     * Sets the value of the reqvoltyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQVOLTYP(String value) {
        this.reqvoltyp = value;
    }

    /**
     * Gets the value of the reqactntyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQACTNTYP() {
        return reqactntyp;
    }

    /**
     * Sets the value of the reqactntyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQACTNTYP(String value) {
        this.reqactntyp = value;
    }

    /**
     * Gets the value of the testflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTESTFLG() {
        return testflg;
    }

    /**
     * Sets the value of the testflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTESTFLG(String value) {
        this.testflg = value;
    }

    /**
     * Gets the value of the userid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSERID() {
        return userid;
    }

    /**
     * Sets the value of the userid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSERID(String value) {
        this.userid = value;
    }

    /**
     * Gets the value of the pwd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPWD() {
        return pwd;
    }

    /**
     * Sets the value of the pwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPWD(String value) {
        this.pwd = value;
    }

    /**
     * Gets the value of the authflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAUTHFLG() {
        return authflg;
    }

    /**
     * Sets the value of the authflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAUTHFLG(String value) {
        this.authflg = value;
    }

    /**
     * Gets the value of the authtitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAUTHTITLE() {
        return authtitle;
    }

    /**
     * Sets the value of the authtitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAUTHTITLE(String value) {
        this.authtitle = value;
    }

    /**
     * Gets the value of the resfrmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESFRMT() {
        return resfrmt;
    }

    /**
     * Sets the value of the resfrmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESFRMT(String value) {
        this.resfrmt = value;
    }

    /**
     * Gets the value of the memberpreoverride property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMEMBERPREOVERRIDE() {
        return memberpreoverride;
    }

    /**
     * Sets the value of the memberpreoverride property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMEMBERPREOVERRIDE(String value) {
        this.memberpreoverride = value;
    }

    /**
     * Gets the value of the resfrmtembd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESFRMTEMBD() {
        return resfrmtembd;
    }

    /**
     * Sets the value of the resfrmtembd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESFRMTEMBD(String value) {
        this.resfrmtembd = value;
    }

    /**
     * Gets the value of the losname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOSNAME() {
        return losname;
    }

    /**
     * Sets the value of the losname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOSNAME(String value) {
        this.losname = value;
    }

    /**
     * Gets the value of the losvender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOSVENDER() {
        return losvender;
    }

    /**
     * Sets the value of the losvender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOSVENDER(String value) {
        this.losvender = value;
    }

    /**
     * Gets the value of the losversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOSVERSION() {
        return losversion;
    }

    /**
     * Sets the value of the losversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOSVERSION(String value) {
        this.losversion = value;
    }

}
