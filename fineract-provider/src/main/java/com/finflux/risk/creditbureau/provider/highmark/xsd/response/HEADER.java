
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
 *         &lt;element name="DATE-OF-REQUEST" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PREPARED-FOR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PREPARED-FOR-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DATE-OF-ISSUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REPORT-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BATCH-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlRootElement(name = "HEADER")
public class HEADER {

    @XmlElement(name = "DATE-OF-REQUEST", required = true)
    protected String dateofrequest;
    @XmlElement(name = "PREPARED-FOR", required = true)
    protected String preparedfor;
    @XmlElement(name = "PREPARED-FOR-ID")
    protected String preparedforid;
    @XmlElement(name = "DATE-OF-ISSUE", required = true)
    protected String dateofissue;
    @XmlElement(name = "REPORT-ID", required = true)
    protected String reportid;
    @XmlElement(name = "BATCH-ID", required = true)
    protected String batchid;

    /**
     * Gets the value of the dateofrequest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATEOFREQUEST() {
        return dateofrequest;
    }

    /**
     * Sets the value of the dateofrequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATEOFREQUEST(String value) {
        this.dateofrequest = value;
    }

    /**
     * Gets the value of the preparedfor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPREPAREDFOR() {
        return preparedfor;
    }

    /**
     * Sets the value of the preparedfor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPREPAREDFOR(String value) {
        this.preparedfor = value;
    }

    /**
     * Gets the value of the preparedforid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPREPAREDFORID() {
        return preparedforid;
    }

    /**
     * Sets the value of the preparedforid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPREPAREDFORID(String value) {
        this.preparedforid = value;
    }

    /**
     * Gets the value of the dateofissue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATEOFISSUE() {
        return dateofissue;
    }

    /**
     * Sets the value of the dateofissue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATEOFISSUE(String value) {
        this.dateofissue = value;
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
     * Gets the value of the batchid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBATCHID() {
        return batchid;
    }

    /**
     * Sets the value of the batchid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBATCHID(String value) {
        this.batchid = value;
    }

}
