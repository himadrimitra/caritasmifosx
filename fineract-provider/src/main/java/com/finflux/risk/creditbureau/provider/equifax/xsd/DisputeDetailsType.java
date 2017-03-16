package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Individual Disputes
 * 
 * <p>Java class for DisputeDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DisputeDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AccNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DisputeComments" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ResolvedDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DisputeDetailsType", propOrder = {
    "accNum",
    "disputeComments",
    "status",
    "resolvedDate"
})
public class DisputeDetailsType {

    @XmlElement(name = "AccNum")
    protected String accNum;
    @XmlElement(name = "DisputeComments", required = true)
    protected String disputeComments;
    @XmlElement(name = "Status", required = true)
    protected String status;
    @XmlElement(name = "ResolvedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar resolvedDate;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the accNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccNum() {
        return accNum;
    }

    /**
     * Sets the value of the accNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccNum(String value) {
        this.accNum = value;
    }

    /**
     * Gets the value of the disputeComments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisputeComments() {
        return disputeComments;
    }

    /**
     * Sets the value of the disputeComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisputeComments(String value) {
        this.disputeComments = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the resolvedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getResolvedDate() {
        return resolvedDate;
    }

    /**
     * Sets the value of the resolvedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setResolvedDate(XMLGregorianCalendar value) {
        this.resolvedDate = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
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
    public void setType(String value) {
        this.type = value;
    }

}
