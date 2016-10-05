
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
 *         &lt;element name="STATUS" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="OPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="OPTION-STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ERRORS" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ERROR" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="ERROR-DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
    "status"
})
@XmlRootElement(name = "STATUS-DETAILS")
public class STATUSDETAILS {

    @XmlElement(name = "STATUS", required = true)
    protected List<STATUSDETAILS.STATUS> status;

    /**
     * Gets the value of the status property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the status property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSTATUS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link STATUSDETAILS.STATUS }
     * 
     * 
     */
    public List<STATUSDETAILS.STATUS> getSTATUS() {
        if (status == null) {
            status = new ArrayList<STATUSDETAILS.STATUS>();
        }
        return this.status;
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
     *         &lt;element name="OPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="OPTION-STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ERRORS" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ERROR" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="ERROR-DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
    public static class STATUS {

        @XmlElement(name = "OPTION", required = true)
        protected String option;
        @XmlElement(name = "OPTION-STATUS", required = true)
        protected String optionstatus;
        @XmlElement(name = "ERRORS")
        protected STATUSDETAILS.STATUS.ERRORS errors;

        /**
         * Gets the value of the option property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOPTION() {
            return option;
        }

        /**
         * Sets the value of the option property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOPTION(String value) {
            this.option = value;
        }

        /**
         * Gets the value of the optionstatus property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOPTIONSTATUS() {
            return optionstatus;
        }

        /**
         * Sets the value of the optionstatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOPTIONSTATUS(String value) {
            this.optionstatus = value;
        }

        /**
         * Gets the value of the errors property.
         * 
         * @return
         *     possible object is
         *     {@link STATUSDETAILS.STATUS.ERRORS }
         *     
         */
        public STATUSDETAILS.STATUS.ERRORS getERRORS() {
            return errors;
        }

        /**
         * Sets the value of the errors property.
         * 
         * @param value
         *     allowed object is
         *     {@link STATUSDETAILS.STATUS.ERRORS }
         *     
         */
        public void setERRORS(STATUSDETAILS.STATUS.ERRORS value) {
            this.errors = value;
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
         *       &lt;sequence>
         *         &lt;element name="ERROR" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="ERROR-DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                 &lt;/sequence>
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
            "error"
        })
        public static class ERRORS {

            @XmlElement(name = "ERROR", required = true)
            protected List<STATUSDETAILS.STATUS.ERRORS.ERROR> error;

            /**
             * Gets the value of the error property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the error property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getERROR().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link STATUSDETAILS.STATUS.ERRORS.ERROR }
             * 
             * 
             */
            public List<STATUSDETAILS.STATUS.ERRORS.ERROR> getERROR() {
                if (error == null) {
                    error = new ArrayList<STATUSDETAILS.STATUS.ERRORS.ERROR>();
                }
                return this.error;
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
             *       &lt;sequence>
             *         &lt;element name="ERROR-DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "errordescription"
            })
            public static class ERROR {

                @XmlElement(name = "ERROR-DESCRIPTION", required = true)
                protected String errordescription;

                /**
                 * Gets the value of the errordescription property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getERRORDESCRIPTION() {
                    return errordescription;
                }

                /**
                 * Sets the value of the errordescription property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setERRORDESCRIPTION(String value) {
                    this.errordescription = value;
                }

            }

        }

    }

}
