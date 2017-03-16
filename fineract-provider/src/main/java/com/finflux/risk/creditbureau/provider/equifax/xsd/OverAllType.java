package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For CCR AccountSummary OverAll Implementation
 * 
 * <p>Java class for OverAllType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OverAllType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumberOfOpenAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberOfPastDueAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalOutstandingBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OverAllType", propOrder = {
    "numberOfOpenAccounts",
    "numberOfPastDueAccounts",
    "totalOutstandingBalance"
})
public class OverAllType {

    @XmlElement(name = "NumberOfOpenAccounts")
    protected Integer numberOfOpenAccounts;
    @XmlElement(name = "NumberOfPastDueAccounts")
    protected Integer numberOfPastDueAccounts;
    @XmlElement(name = "TotalOutstandingBalance")
    protected BigDecimal totalOutstandingBalance;

    /**
     * Gets the value of the numberOfOpenAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfOpenAccounts() {
        return numberOfOpenAccounts;
    }

    /**
     * Sets the value of the numberOfOpenAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfOpenAccounts(Integer value) {
        this.numberOfOpenAccounts = value;
    }

    /**
     * Gets the value of the numberOfPastDueAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfPastDueAccounts() {
        return numberOfPastDueAccounts;
    }

    /**
     * Sets the value of the numberOfPastDueAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfPastDueAccounts(Integer value) {
        this.numberOfPastDueAccounts = value;
    }

    /**
     * Gets the value of the totalOutstandingBalance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalOutstandingBalance() {
        return totalOutstandingBalance;
    }

    /**
     * Sets the value of the totalOutstandingBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalOutstandingBalance(BigDecimal value) {
        this.totalOutstandingBalance = value;
    }

}
