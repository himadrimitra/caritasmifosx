package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For CCR AccountSummary Microfinance Implementation
 * 
 * <p>Java class for MicrofinanceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MicrofinanceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumberofOpenAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberofPastDueAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalOutstandingBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="RecentAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InstallmentAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalPastDueAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MicrofinanceType", propOrder = {
    "numberofOpenAccounts",
    "numberofPastDueAccounts",
    "totalOutstandingBalance",
    "recentAccount",
    "installmentAmount",
    "totalPastDueAmount"
})
public class MicrofinanceType {

    @XmlElement(name = "NumberofOpenAccounts")
    protected Integer numberofOpenAccounts;
    @XmlElement(name = "NumberofPastDueAccounts")
    protected Integer numberofPastDueAccounts;
    @XmlElement(name = "TotalOutstandingBalance")
    protected BigDecimal totalOutstandingBalance;
    @XmlElement(name = "RecentAccount")
    protected String recentAccount;
    @XmlElement(name = "InstallmentAmount")
    protected BigDecimal installmentAmount;
    @XmlElement(name = "TotalPastDueAmount")
    protected BigDecimal totalPastDueAmount;

    /**
     * Gets the value of the numberofOpenAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberofOpenAccounts() {
        return numberofOpenAccounts;
    }

    /**
     * Sets the value of the numberofOpenAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberofOpenAccounts(Integer value) {
        this.numberofOpenAccounts = value;
    }

    /**
     * Gets the value of the numberofPastDueAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberofPastDueAccounts() {
        return numberofPastDueAccounts;
    }

    /**
     * Sets the value of the numberofPastDueAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberofPastDueAccounts(Integer value) {
        this.numberofPastDueAccounts = value;
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

    /**
     * Gets the value of the recentAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecentAccount() {
        return recentAccount;
    }

    /**
     * Sets the value of the recentAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecentAccount(String value) {
        this.recentAccount = value;
    }

    /**
     * Gets the value of the installmentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    /**
     * Sets the value of the installmentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInstallmentAmount(BigDecimal value) {
        this.installmentAmount = value;
    }

    /**
     * Gets the value of the totalPastDueAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalPastDueAmount() {
        return totalPastDueAmount;
    }

    /**
     * Sets the value of the totalPastDueAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalPastDueAmount(BigDecimal value) {
        this.totalPastDueAmount = value;
    }

}
