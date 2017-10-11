
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
 *         &lt;element name="SECONDARY-MATCH" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element ref="{}ADDRESSES"/>
 *                   &lt;element ref="{}IDS"/>
 *                   &lt;element ref="{}PHONES"/>
 *                   &lt;element ref="{}EMAILS"/>
 *                   &lt;element ref="{}RELATIONS"/>
 *                   &lt;element ref="{}LOAN-DETAILS" minOccurs="0"/>
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
    "secondarymatch"
})
@XmlRootElement(name = "SECONDARY-MATCHES")
public class SECONDARYMATCHES {

    @XmlElement(name = "SECONDARY-MATCH", required = true)
    protected List<SECONDARYMATCHES.SECONDARYMATCH> secondarymatch;

    /**
     * Gets the value of the secondarymatch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the secondarymatch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSECONDARYMATCH().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SECONDARYMATCHES.SECONDARYMATCH }
     * 
     * 
     */
    public List<SECONDARYMATCHES.SECONDARYMATCH> getSECONDARYMATCH() {
        if (secondarymatch == null) {
            secondarymatch = new ArrayList<SECONDARYMATCHES.SECONDARYMATCH>();
        }
        return this.secondarymatch;
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
     *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element ref="{}ADDRESSES"/>
     *         &lt;element ref="{}IDS"/>
     *         &lt;element ref="{}PHONES"/>
     *         &lt;element ref="{}EMAILS"/>
     *         &lt;element ref="{}RELATIONS"/>
     *         &lt;element ref="{}LOAN-DETAILS" minOccurs="0"/>
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
    public static class SECONDARYMATCH {

        @XmlElement(name = "NAME", required = true)
        protected String name;
        @XmlElement(name = "DOB")
        protected String dob;
        @XmlElement(name = "ADDRESSES", required = true)
        protected ADDRESSES addresses;
        @XmlElement(name = "IDS", required = true)
        protected IDS ids;
        @XmlElement(name = "PHONES", required = true)
        protected PHONES phones;
        @XmlElement(name = "EMAILS", required = true)
        protected EMAILS emails;
        @XmlElement(name = "RELATIONS", required = true)
        protected RELATIONS relations;
        @XmlElement(name = "LOAN-DETAILS")
        protected LOANDETAILS loandetails;

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
         * Gets the value of the relations property.
         * 
         * @return
         *     possible object is
         *     {@link RELATIONS }
         *     
         */
        public RELATIONS getRELATIONS() {
            return relations;
        }

        /**
         * Sets the value of the relations property.
         * 
         * @param value
         *     allowed object is
         *     {@link RELATIONS }
         *     
         */
        public void setRELATIONS(RELATIONS value) {
            this.relations = value;
        }

        /**
         * Gets the value of the loandetails property.
         * 
         * @return
         *     possible object is
         *     {@link LOANDETAILS }
         *     
         */
        public LOANDETAILS getLOANDETAILS() {
            return loandetails;
        }

        /**
         * Sets the value of the loandetails property.
         * 
         * @param value
         *     allowed object is
         *     {@link LOANDETAILS }
         *     
         */
        public void setLOANDETAILS(LOANDETAILS value) {
            this.loandetails = value;
        }

    }

}
