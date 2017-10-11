
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
 *         &lt;element name="ALERT" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="ALERT-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ALERT-DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "alert"
})
@XmlRootElement(name = "ALERTS")
public class ALERTS {

    @XmlElement(name = "ALERT", required = true)
    protected List<ALERTS.ALERT> alert;

    /**
     * Gets the value of the alert property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alert property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getALERT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ALERTS.ALERT }
     * 
     * 
     */
    public List<ALERTS.ALERT> getALERT() {
        if (alert == null) {
            alert = new ArrayList<ALERTS.ALERT>();
        }
        return this.alert;
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
     *         &lt;element name="ALERT-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ALERT-DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class ALERT {

        @XmlElement(name = "ALERT-TYPE", required = true)
        protected String alerttype;
        @XmlElement(name = "ALERT-DESC")
        protected String alertdesc;

        /**
         * Gets the value of the alerttype property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getALERTTYPE() {
            return alerttype;
        }

        /**
         * Sets the value of the alerttype property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setALERTTYPE(String value) {
            this.alerttype = value;
        }

        /**
         * Gets the value of the alertdesc property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getALERTDESC() {
            return alertdesc;
        }

        /**
         * Sets the value of the alertdesc property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setALERTDESC(String value) {
            this.alertdesc = value;
        }

    }

}
