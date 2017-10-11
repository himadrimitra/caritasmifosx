
package com.finflux.risk.creditbureau.provider.highmark.xsd.response;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="SECURITY-DETAIL" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="SECURITY-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="OWNER-NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="SECURITY-VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DATE-OF-VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SECURITY-CHARGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="PROPERTY-ADDRESS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="AUTOMOBILE-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="YEAR-OF-MANUFACTURE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="REGISTRATION-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="ENGINE-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="CHASSIS-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "securitydetail"
})
@XmlRootElement(name = "SECURITY-DETAILS")
public class SECURITYDETAILS {

    @XmlElement(name = "SECURITY-DETAIL", required = true)
    protected List<SECURITYDETAILS.SECURITYDETAIL> securitydetail;

    /**
     * Gets the value of the securitydetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the securitydetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSECURITYDETAIL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SECURITYDETAILS.SECURITYDETAIL }
     * 
     * 
     */
    public List<SECURITYDETAILS.SECURITYDETAIL> getSECURITYDETAIL() {
        if (securitydetail == null) {
            securitydetail = new ArrayList<SECURITYDETAILS.SECURITYDETAIL>();
        }
        return this.securitydetail;
    }


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
     *         &lt;element name="SECURITY-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="OWNER-NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="SECURITY-VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="DATE-OF-VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SECURITY-CHARGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="PROPERTY-ADDRESS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="AUTOMOBILE-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="YEAR-OF-MANUFACTURE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="REGISTRATION-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="ENGINE-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="CHASSIS-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class SECURITYDETAIL {

        @XmlElement(name = "SECURITY-TYPE", required = true)
        protected String securitytype;
        @XmlElement(name = "OWNER-NAME")
        protected String ownername;
        @XmlElement(name = "SECURITY-VALUE", required = true)
        protected String securityvalue;
        @XmlElement(name = "DATE-OF-VALUE", required = true)
        protected String dateofvalue;
        @XmlElement(name = "SECURITY-CHARGE")
        protected String securitycharge;
        @XmlElement(name = "PROPERTY-ADDRESS")
        protected String propertyaddress;
        @XmlElement(name = "AUTOMOBILE-TYPE")
        protected String automobiletype;
        @XmlElement(name = "YEAR-OF-MANUFACTURE")
        protected String yearofmanufacture;
        @XmlElement(name = "REGISTRATION-NUMBER")
        protected String registrationnumber;
        @XmlElement(name = "ENGINE-NUMBER")
        protected String enginenumber;
        @XmlElement(name = "CHASSIS-NUMBER")
        protected String chassisnumber;

        /**
         * Gets the value of the securitytype property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSECURITYTYPE() {
            return securitytype;
        }

        /**
         * Sets the value of the securitytype property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSECURITYTYPE(String value) {
            this.securitytype = value;
        }

        /**
         * Gets the value of the ownername property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOWNERNAME() {
            return ownername;
        }

        /**
         * Sets the value of the ownername property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOWNERNAME(String value) {
            this.ownername = value;
        }

        /**
         * Gets the value of the securityvalue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSECURITYVALUE() {
            return securityvalue;
        }

        /**
         * Sets the value of the securityvalue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSECURITYVALUE(String value) {
            this.securityvalue = value;
        }

        /**
         * Gets the value of the dateofvalue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDATEOFVALUE() {
            return dateofvalue;
        }

        /**
         * Sets the value of the dateofvalue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDATEOFVALUE(String value) {
            this.dateofvalue = value;
        }

        /**
         * Gets the value of the securitycharge property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSECURITYCHARGE() {
            return securitycharge;
        }

        /**
         * Sets the value of the securitycharge property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSECURITYCHARGE(String value) {
            this.securitycharge = value;
        }

        /**
         * Gets the value of the propertyaddress property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPROPERTYADDRESS() {
            return propertyaddress;
        }

        /**
         * Sets the value of the propertyaddress property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPROPERTYADDRESS(String value) {
            this.propertyaddress = value;
        }

        /**
         * Gets the value of the automobiletype property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAUTOMOBILETYPE() {
            return automobiletype;
        }

        /**
         * Sets the value of the automobiletype property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAUTOMOBILETYPE(String value) {
            this.automobiletype = value;
        }

        /**
         * Gets the value of the yearofmanufacture property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getYEAROFMANUFACTURE() {
            return yearofmanufacture;
        }

        /**
         * Sets the value of the yearofmanufacture property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setYEAROFMANUFACTURE(String value) {
            this.yearofmanufacture = value;
        }

        /**
         * Gets the value of the registrationnumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getREGISTRATIONNUMBER() {
            return registrationnumber;
        }

        /**
         * Sets the value of the registrationnumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setREGISTRATIONNUMBER(String value) {
            this.registrationnumber = value;
        }

        /**
         * Gets the value of the enginenumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getENGINENUMBER() {
            return enginenumber;
        }

        /**
         * Sets the value of the enginenumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setENGINENUMBER(String value) {
            this.enginenumber = value;
        }

        /**
         * Gets the value of the chassisnumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCHASSISNUMBER() {
            return chassisnumber;
        }

        /**
         * Sets the value of the chassisnumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCHASSISNUMBER(String value) {
            this.chassisnumber = value;
        }

    }

}
