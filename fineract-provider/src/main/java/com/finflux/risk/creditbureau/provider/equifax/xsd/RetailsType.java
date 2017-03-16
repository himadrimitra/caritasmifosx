package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For CCR AccountSummary Retail Implementation
 * 
 * <p>Java class for RetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumberofOpenAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberofPastDueAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalOutstandinglBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalPastDueAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="RecentAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalSanctionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalHighCredit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="OldestAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetailsType", propOrder = {
    "numberofOpenAccounts",
    "numberofPastDueAccounts",
    "totalOutstandinglBalance",
    "totalAccounts",
    "totalPastDueAmount",
    "recentAccount",
    "totalSanctionAmount",
    "totalHighCredit",
    "oldestAccount"
})
public class RetailsType {

    @XmlElement(name = "NumberofOpenAccounts")
    protected Integer numberofOpenAccounts;
    @XmlElement(name = "NumberofPastDueAccounts")
    protected Integer numberofPastDueAccounts;
    @XmlElement(name = "TotalOutstandinglBalance")
    protected BigDecimal totalOutstandinglBalance;
    @XmlElement(name = "TotalAccounts")
    protected Integer totalAccounts;
    @XmlElement(name = "TotalPastDueAmount")
    protected BigDecimal totalPastDueAmount;
    @XmlElement(name = "RecentAccount")
    protected String recentAccount;
    @XmlElement(name = "TotalSanctionAmount")
    protected BigDecimal totalSanctionAmount;
    @XmlElement(name = "TotalHighCredit")
    protected BigDecimal totalHighCredit;
    @XmlElement(name = "OldestAccount")
    protected String oldestAccount;

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
     * Gets the value of the totalOutstandinglBalance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalOutstandinglBalance() {
        return totalOutstandinglBalance;
    }

    /**
     * Sets the value of the totalOutstandinglBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalOutstandinglBalance(BigDecimal value) {
        this.totalOutstandinglBalance = value;
    }

    /**
     * Gets the value of the totalAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalAccounts() {
        return totalAccounts;
    }

    /**
     * Sets the value of the totalAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalAccounts(Integer value) {
        this.totalAccounts = value;
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
     * Gets the value of the totalSanctionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalSanctionAmount() {
        return totalSanctionAmount;
    }

    /**
     * Sets the value of the totalSanctionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalSanctionAmount(BigDecimal value) {
        this.totalSanctionAmount = value;
    }

    /**
     * Gets the value of the totalHighCredit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalHighCredit() {
        return totalHighCredit;
    }

    /**
     * Sets the value of the totalHighCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalHighCredit(BigDecimal value) {
        this.totalHighCredit = value;
    }

    /**
     * Gets the value of the oldestAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldestAccount() {
        return oldestAccount;
    }

    /**
     * Sets the value of the oldestAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldestAccount(String value) {
        this.oldestAccount = value;
    }

}
