package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For CCR AccountsSummary Implementation
 * 
 * <p>Java class for AccountsSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccountsSummaryType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ConsolidateCreditSummary" type="{http://services.equifax.com/eport/ws/schemas/1.0}ConsolidateCreditSummaryType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountsSummaryType", propOrder = {
    "consolidateCreditSummary"
})
public class AccountsSummaryType {

    @XmlElement(name = "ConsolidateCreditSummary")
    protected ConsolidateCreditSummaryType consolidateCreditSummary;

    /**
     * Gets the value of the consolidateCreditSummary property.
     * 
     * @return
     *     possible object is
     *     {@link ConsolidateCreditSummaryType }
     *     
     */
    public ConsolidateCreditSummaryType getConsolidateCreditSummary() {
        return consolidateCreditSummary;
    }

    /**
     * Sets the value of the consolidateCreditSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsolidateCreditSummaryType }
     *     
     */
    public void setConsolidateCreditSummary(ConsolidateCreditSummaryType value) {
        this.consolidateCreditSummary = value;
    }

}
