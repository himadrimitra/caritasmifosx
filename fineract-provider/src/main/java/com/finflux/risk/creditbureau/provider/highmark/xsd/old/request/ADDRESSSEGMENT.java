//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.15 at 09:35:41 AM IST 
//


package com.finflux.risk.creditbureau.provider.highmark.xsd.old.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ADDRESS" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="TYPE"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                         &lt;enumeration value="D01"/&gt;
 *                         &lt;enumeration value="D02"/&gt;
 *                         &lt;enumeration value="D03"/&gt;
 *                         &lt;enumeration value="D04"/&gt;
 *                         &lt;enumeration value="D05"/&gt;
 *                         &lt;enumeration value="D06"/&gt;
 *                         &lt;enumeration value="D07"/&gt;
 *                         &lt;enumeration value="D08"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ADDRESS-1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="CITY" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="STATE"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                         &lt;enumeration value="AP"/&gt;
 *                         &lt;enumeration value="AR"/&gt;
 *                         &lt;enumeration value="AS"/&gt;
 *                         &lt;enumeration value="BR"/&gt;
 *                         &lt;enumeration value="CG"/&gt;
 *                         &lt;enumeration value="GA"/&gt;
 *                         &lt;enumeration value="GJ"/&gt;
 *                         &lt;enumeration value="HR"/&gt;
 *                         &lt;enumeration value="HP"/&gt;
 *                         &lt;enumeration value="JK"/&gt;
 *                         &lt;enumeration value="JH"/&gt;
 *                         &lt;enumeration value="KA"/&gt;
 *                         &lt;enumeration value="KL"/&gt;
 *                         &lt;enumeration value="MP"/&gt;
 *                         &lt;enumeration value="MH"/&gt;
 *                         &lt;enumeration value="MN"/&gt;
 *                         &lt;enumeration value="ML"/&gt;
 *                         &lt;enumeration value="MZ"/&gt;
 *                         &lt;enumeration value="NL"/&gt;
 *                         &lt;enumeration value="OR"/&gt;
 *                         &lt;enumeration value="PB"/&gt;
 *                         &lt;enumeration value="RJ"/&gt;
 *                         &lt;enumeration value="SK"/&gt;
 *                         &lt;enumeration value="TN"/&gt;
 *                         &lt;enumeration value="TR"/&gt;
 *                         &lt;enumeration value="UK"/&gt;
 *                         &lt;enumeration value="UP"/&gt;
 *                         &lt;enumeration value="WB"/&gt;
 *                         &lt;enumeration value="AN"/&gt;
 *                         &lt;enumeration value="CH"/&gt;
 *                         &lt;enumeration value="DN"/&gt;
 *                         &lt;enumeration value="DD"/&gt;
 *                         &lt;enumeration value="DL"/&gt;
 *                         &lt;enumeration value="LD"/&gt;
 *                         &lt;enumeration value="PY"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="PIN" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "address"
})
@XmlRootElement(name = "ADDRESS-SEGMENT")
public class ADDRESSSEGMENT {

    @XmlElement(name = "ADDRESS", required = true)
    protected List<ADDRESSSEGMENT.ADDRESS> address;

    /**
     * Gets the value of the address property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the address property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getADDRESS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ADDRESSSEGMENT.ADDRESS }
     * 
     * 
     */
    public List<ADDRESSSEGMENT.ADDRESS> getADDRESS() {
        if (address == null) {
            address = new ArrayList<ADDRESSSEGMENT.ADDRESS>();
        }
        return this.address;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="TYPE"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *               &lt;enumeration value="D01"/&gt;
     *               &lt;enumeration value="D02"/&gt;
     *               &lt;enumeration value="D03"/&gt;
     *               &lt;enumeration value="D04"/&gt;
     *               &lt;enumeration value="D05"/&gt;
     *               &lt;enumeration value="D06"/&gt;
     *               &lt;enumeration value="D07"/&gt;
     *               &lt;enumeration value="D08"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ADDRESS-1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="CITY" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="STATE"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *               &lt;enumeration value="AP"/&gt;
     *               &lt;enumeration value="AR"/&gt;
     *               &lt;enumeration value="AS"/&gt;
     *               &lt;enumeration value="BR"/&gt;
     *               &lt;enumeration value="CG"/&gt;
     *               &lt;enumeration value="GA"/&gt;
     *               &lt;enumeration value="GJ"/&gt;
     *               &lt;enumeration value="HR"/&gt;
     *               &lt;enumeration value="HP"/&gt;
     *               &lt;enumeration value="JK"/&gt;
     *               &lt;enumeration value="JH"/&gt;
     *               &lt;enumeration value="KA"/&gt;
     *               &lt;enumeration value="KL"/&gt;
     *               &lt;enumeration value="MP"/&gt;
     *               &lt;enumeration value="MH"/&gt;
     *               &lt;enumeration value="MN"/&gt;
     *               &lt;enumeration value="ML"/&gt;
     *               &lt;enumeration value="MZ"/&gt;
     *               &lt;enumeration value="NL"/&gt;
     *               &lt;enumeration value="OR"/&gt;
     *               &lt;enumeration value="PB"/&gt;
     *               &lt;enumeration value="RJ"/&gt;
     *               &lt;enumeration value="SK"/&gt;
     *               &lt;enumeration value="TN"/&gt;
     *               &lt;enumeration value="TR"/&gt;
     *               &lt;enumeration value="UK"/&gt;
     *               &lt;enumeration value="UP"/&gt;
     *               &lt;enumeration value="WB"/&gt;
     *               &lt;enumeration value="AN"/&gt;
     *               &lt;enumeration value="CH"/&gt;
     *               &lt;enumeration value="DN"/&gt;
     *               &lt;enumeration value="DD"/&gt;
     *               &lt;enumeration value="DL"/&gt;
     *               &lt;enumeration value="LD"/&gt;
     *               &lt;enumeration value="PY"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="PIN" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "type",
        "address1",
        "city",
        "state",
        "pin"
    })
    public static class ADDRESS {

        @XmlElement(name = "TYPE", required = true)
        protected String type;
        @XmlElement(name = "ADDRESS-1", required = true)
        protected String address1;
        @XmlElement(name = "CITY", required = true)
        protected String city;
        @XmlElement(name = "STATE", required = true)
        protected String state;
        @XmlElement(name = "PIN")
        @XmlSchemaType(name = "unsignedInt")
        protected long pin;

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTYPE() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTYPE(String value) {
            this.type = value;
        }

        /**
         * Gets the value of the address1 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getADDRESS1() {
            return address1;
        }

        /**
         * Sets the value of the address1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setADDRESS1(String value) {
            this.address1 = value;
        }

        /**
         * Gets the value of the city property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCITY() {
            return city;
        }

        /**
         * Sets the value of the city property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCITY(String value) {
            this.city = value;
        }

        /**
         * Gets the value of the state property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSTATE() {
            return state;
        }

        /**
         * Sets the value of the state property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSTATE(String value) {
            this.state = value;
        }

        /**
         * Gets the value of the pin property.
         * 
         */
        public long getPIN() {
            return pin;
        }

        /**
         * Sets the value of the pin property.
         * 
         */
        public void setPIN(long value) {
            this.pin = value;
        }

    }

}
