
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
 *       &lt;all>
 *         &lt;element name="NAME-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ADDRESS-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PAN-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DRIVING-LICENSE-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DATE-OF-BIRTH-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VOTER-ID-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PASSPORT-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PHONE-NUMBER-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RATION-CARD-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EMAIL-VARIATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
@XmlRootElement(name = "PERSONAL-INFO-VARIATION")
public class PERSONALINFOVARIATION {

    @XmlElement(name = "NAME-VARIATIONS")
    protected PERSONALINFOVARIATION.NAMEVARIATIONS namevariations;
    @XmlElement(name = "ADDRESS-VARIATIONS")
    protected PERSONALINFOVARIATION.ADDRESSVARIATIONS addressvariations;
    @XmlElement(name = "PAN-VARIATIONS")
    protected PERSONALINFOVARIATION.PANVARIATIONS panvariations;
    @XmlElement(name = "DRIVING-LICENSE-VARIATIONS")
    protected PERSONALINFOVARIATION.DRIVINGLICENSEVARIATIONS drivinglicensevariations;
    @XmlElement(name = "DATE-OF-BIRTH-VARIATIONS")
    protected PERSONALINFOVARIATION.DATEOFBIRTHVARIATIONS dateofbirthvariations;
    @XmlElement(name = "VOTER-ID-VARIATIONS")
    protected PERSONALINFOVARIATION.VOTERIDVARIATIONS voteridvariations;
    @XmlElement(name = "PASSPORT-VARIATIONS")
    protected PERSONALINFOVARIATION.PASSPORTVARIATIONS passportvariations;
    @XmlElement(name = "PHONE-NUMBER-VARIATIONS")
    protected PERSONALINFOVARIATION.PHONENUMBERVARIATIONS phonenumbervariations;
    @XmlElement(name = "RATION-CARD-VARIATIONS")
    protected PERSONALINFOVARIATION.RATIONCARDVARIATIONS rationcardvariations;
    @XmlElement(name = "EMAIL-VARIATIONS")
    protected PERSONALINFOVARIATION.EMAILVARIATIONS emailvariations;

    /**
     * Gets the value of the namevariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.NAMEVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.NAMEVARIATIONS getNAMEVARIATIONS() {
        return namevariations;
    }

    /**
     * Sets the value of the namevariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.NAMEVARIATIONS }
     *     
     */
    public void setNAMEVARIATIONS(PERSONALINFOVARIATION.NAMEVARIATIONS value) {
        this.namevariations = value;
    }

    /**
     * Gets the value of the addressvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.ADDRESSVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.ADDRESSVARIATIONS getADDRESSVARIATIONS() {
        return addressvariations;
    }

    /**
     * Sets the value of the addressvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.ADDRESSVARIATIONS }
     *     
     */
    public void setADDRESSVARIATIONS(PERSONALINFOVARIATION.ADDRESSVARIATIONS value) {
        this.addressvariations = value;
    }

    /**
     * Gets the value of the panvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.PANVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.PANVARIATIONS getPANVARIATIONS() {
        return panvariations;
    }

    /**
     * Sets the value of the panvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.PANVARIATIONS }
     *     
     */
    public void setPANVARIATIONS(PERSONALINFOVARIATION.PANVARIATIONS value) {
        this.panvariations = value;
    }

    /**
     * Gets the value of the drivinglicensevariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.DRIVINGLICENSEVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.DRIVINGLICENSEVARIATIONS getDRIVINGLICENSEVARIATIONS() {
        return drivinglicensevariations;
    }

    /**
     * Sets the value of the drivinglicensevariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.DRIVINGLICENSEVARIATIONS }
     *     
     */
    public void setDRIVINGLICENSEVARIATIONS(PERSONALINFOVARIATION.DRIVINGLICENSEVARIATIONS value) {
        this.drivinglicensevariations = value;
    }

    /**
     * Gets the value of the dateofbirthvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.DATEOFBIRTHVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.DATEOFBIRTHVARIATIONS getDATEOFBIRTHVARIATIONS() {
        return dateofbirthvariations;
    }

    /**
     * Sets the value of the dateofbirthvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.DATEOFBIRTHVARIATIONS }
     *     
     */
    public void setDATEOFBIRTHVARIATIONS(PERSONALINFOVARIATION.DATEOFBIRTHVARIATIONS value) {
        this.dateofbirthvariations = value;
    }

    /**
     * Gets the value of the voteridvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.VOTERIDVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.VOTERIDVARIATIONS getVOTERIDVARIATIONS() {
        return voteridvariations;
    }

    /**
     * Sets the value of the voteridvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.VOTERIDVARIATIONS }
     *     
     */
    public void setVOTERIDVARIATIONS(PERSONALINFOVARIATION.VOTERIDVARIATIONS value) {
        this.voteridvariations = value;
    }

    /**
     * Gets the value of the passportvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.PASSPORTVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.PASSPORTVARIATIONS getPASSPORTVARIATIONS() {
        return passportvariations;
    }

    /**
     * Sets the value of the passportvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.PASSPORTVARIATIONS }
     *     
     */
    public void setPASSPORTVARIATIONS(PERSONALINFOVARIATION.PASSPORTVARIATIONS value) {
        this.passportvariations = value;
    }

    /**
     * Gets the value of the phonenumbervariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.PHONENUMBERVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.PHONENUMBERVARIATIONS getPHONENUMBERVARIATIONS() {
        return phonenumbervariations;
    }

    /**
     * Sets the value of the phonenumbervariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.PHONENUMBERVARIATIONS }
     *     
     */
    public void setPHONENUMBERVARIATIONS(PERSONALINFOVARIATION.PHONENUMBERVARIATIONS value) {
        this.phonenumbervariations = value;
    }

    /**
     * Gets the value of the rationcardvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.RATIONCARDVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.RATIONCARDVARIATIONS getRATIONCARDVARIATIONS() {
        return rationcardvariations;
    }

    /**
     * Sets the value of the rationcardvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.RATIONCARDVARIATIONS }
     *     
     */
    public void setRATIONCARDVARIATIONS(PERSONALINFOVARIATION.RATIONCARDVARIATIONS value) {
        this.rationcardvariations = value;
    }

    /**
     * Gets the value of the emailvariations property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION.EMAILVARIATIONS }
     *     
     */
    public PERSONALINFOVARIATION.EMAILVARIATIONS getEMAILVARIATIONS() {
        return emailvariations;
    }

    /**
     * Sets the value of the emailvariations property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION.EMAILVARIATIONS }
     *     
     */
    public void setEMAILVARIATIONS(PERSONALINFOVARIATION.EMAILVARIATIONS value) {
        this.emailvariations = value;
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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class ADDRESSVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class DATEOFBIRTHVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class DRIVINGLICENSEVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class EMAILVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class NAMEVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class PANVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class PASSPORTVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class PHONENUMBERVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class RATIONCARDVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

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
     *         &lt;element name="VARIATION" type="{}VARIATION" maxOccurs="unbounded"/>
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
        "variation"
    })
    public static class VOTERIDVARIATIONS {

        @XmlElement(name = "VARIATION", required = true)
        protected List<VARIATION> variation;

        /**
         * Gets the value of the variation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVARIATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VARIATION }
         * 
         * 
         */
        public List<VARIATION> getVARIATION() {
            if (variation == null) {
                variation = new ArrayList<VARIATION>();
            }
            return this.variation;
        }

    }

}
