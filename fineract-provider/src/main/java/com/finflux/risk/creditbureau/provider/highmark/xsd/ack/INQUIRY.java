
package com.finflux.risk.creditbureau.provider.highmark.xsd.ack;

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
 *         &lt;element name="INQUIRY-UNIQUE-REF-NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MBR-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQUEST-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REPORT-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RESPONSE-DT-TM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REPONSE-TYPE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ACKNOWLEDGEMENT"/>
 *               &lt;enumeration value="ERROR"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ERRORS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}ERROR" maxOccurs="unbounded"/>
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
@XmlRootElement(name = "INQUIRY")
public class INQUIRY {

    @XmlElement(name = "INQUIRY-UNIQUE-REF-NO", required = true)
    protected String inquiryuniquerefno;
    @XmlElement(name = "MBR-ID", required = true)
    protected String mbrid;
    @XmlElement(name = "REQUEST-DT-TM", required = true)
    protected String requestdttm;
    @XmlElement(name = "REPORT-ID", required = true)
    protected String reportid;
    @XmlElement(name = "RESPONSE-DT-TM", required = true)
    protected String responsedttm;
    @XmlElement(name = "RESPONSE-TYPE", required = true)
    protected String reponsetype;
    @XmlElement(name = "ERRORS")
    protected INQUIRY.ERRORS errors;

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
     * Gets the value of the requestdttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUESTDTTM() {
        return requestdttm;
    }

    /**
     * Sets the value of the requestdttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUESTDTTM(String value) {
        this.requestdttm = value;
    }

    /**
     * Gets the value of the reportid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREPORTID() {
        return reportid;
    }

    /**
     * Sets the value of the reportid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREPORTID(String value) {
        this.reportid = value;
    }

    /**
     * Gets the value of the responsedttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESPONSEDTTM() {
        return responsedttm;
    }

    /**
     * Sets the value of the responsedttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESPONSEDTTM(String value) {
        this.responsedttm = value;
    }

    /**
     * Gets the value of the reponsetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREPONSETYPE() {
        return reponsetype;
    }

    /**
     * Sets the value of the reponsetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREPONSETYPE(String value) {
        this.reponsetype = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link INQUIRY.ERRORS }
     *     
     */
    public INQUIRY.ERRORS getERRORS() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link INQUIRY.ERRORS }
     *     
     */
    public void setERRORS(INQUIRY.ERRORS value) {
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
     *         &lt;element ref="{}ERROR" maxOccurs="unbounded"/>
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
        protected List<ERROR> error;

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
         * {@link ERROR }
         * 
         * 
         */
        public List<ERROR> getERROR() {
            if (error == null) {
                error = new ArrayList<ERROR>();
            }
            return this.error;
        }

    }

}
