package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For CCR ConsolidateCreditSummary Implementation
 * 
 * <p>Java class for ConsolidateCreditSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConsolidateCreditSummaryType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OverAll" type="{http://services.equifax.com/eport/ws/schemas/1.0}OverAllType" minOccurs="0"/&gt;
 *         &lt;element name="Retail" type="{http://services.equifax.com/eport/ws/schemas/1.0}RetailsType" minOccurs="0"/&gt;
 *         &lt;element name="Microfinance" type="{http://services.equifax.com/eport/ws/schemas/1.0}MicrofinanceType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsolidateCreditSummaryType", propOrder = {
    "overAll",
    "retail",
    "microfinance"
})
public class ConsolidateCreditSummaryType {

    @XmlElement(name = "OverAll")
    protected OverAllType overAll;
    @XmlElement(name = "Retail")
    protected RetailsType retail;
    @XmlElement(name = "Microfinance")
    protected MicrofinanceType microfinance;

    /**
     * Gets the value of the overAll property.
     * 
     * @return
     *     possible object is
     *     {@link OverAllType }
     *     
     */
    public OverAllType getOverAll() {
        return overAll;
    }

    /**
     * Sets the value of the overAll property.
     * 
     * @param value
     *     allowed object is
     *     {@link OverAllType }
     *     
     */
    public void setOverAll(OverAllType value) {
        this.overAll = value;
    }

    /**
     * Gets the value of the retail property.
     * 
     * @return
     *     possible object is
     *     {@link RetailsType }
     *     
     */
    public RetailsType getRetail() {
        return retail;
    }

    /**
     * Sets the value of the retail property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetailsType }
     *     
     */
    public void setRetail(RetailsType value) {
        this.retail = value;
    }

    /**
     * Gets the value of the microfinance property.
     * 
     * @return
     *     possible object is
     *     {@link MicrofinanceType }
     *     
     */
    public MicrofinanceType getMicrofinance() {
        return microfinance;
    }

    /**
     * Sets the value of the microfinance property.
     * 
     * @param value
     *     allowed object is
     *     {@link MicrofinanceType }
     *     
     */
    public void setMicrofinance(MicrofinanceType value) {
        this.microfinance = value;
    }

}
