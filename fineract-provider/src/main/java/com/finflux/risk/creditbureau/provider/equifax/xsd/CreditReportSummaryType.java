package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditReportSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditReportSummaryType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NoOfAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NoOfActiveAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NoOfWriteOffs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalPastDue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="MostSevereStatusWithIn24Months" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SingleHighestCredit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="SingleHighestSanctionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalHighCredit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="AverageOpenBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="SingleHighestBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="NoOfPastDueAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NoOfZeroBalanceAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="RecentAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OldestAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalBalanceAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalSanctionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalCreditLimit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalMonthlyPaymentAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalWrittenOffAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditReportSummaryType", propOrder = {
    "noOfAccounts",
    "noOfActiveAccounts",
    "noOfWriteOffs",
    "totalPastDue",
    "mostSevereStatusWithIn24Months",
    "singleHighestCredit",
    "singleHighestSanctionAmount",
    "totalHighCredit",
    "averageOpenBalance",
    "singleHighestBalance",
    "noOfPastDueAccounts",
    "noOfZeroBalanceAccounts",
    "recentAccount",
    "oldestAccount",
    "totalBalanceAmount",
    "totalSanctionAmount",
    "totalCreditLimit",
    "totalMonthlyPaymentAmount",
    "totalWrittenOffAmount"
})
public class CreditReportSummaryType {

    @XmlElement(name = "NoOfAccounts")
    protected Integer noOfAccounts;
    @XmlElement(name = "NoOfActiveAccounts")
    protected Integer noOfActiveAccounts;
    @XmlElement(name = "NoOfWriteOffs")
    protected Integer noOfWriteOffs;
    @XmlElement(name = "TotalPastDue")
    protected BigDecimal totalPastDue;
    @XmlElement(name = "MostSevereStatusWithIn24Months")
    protected String mostSevereStatusWithIn24Months;
    @XmlElement(name = "SingleHighestCredit")
    protected BigDecimal singleHighestCredit;
    @XmlElement(name = "SingleHighestSanctionAmount")
    protected BigDecimal singleHighestSanctionAmount;
    @XmlElement(name = "TotalHighCredit")
    protected BigDecimal totalHighCredit;
    @XmlElement(name = "AverageOpenBalance")
    protected BigDecimal averageOpenBalance;
    @XmlElement(name = "SingleHighestBalance")
    protected BigDecimal singleHighestBalance;
    @XmlElement(name = "NoOfPastDueAccounts")
    protected Integer noOfPastDueAccounts;
    @XmlElement(name = "NoOfZeroBalanceAccounts")
    protected Integer noOfZeroBalanceAccounts;
    @XmlElement(name = "RecentAccount")
    protected String recentAccount;
    @XmlElement(name = "OldestAccount")
    protected String oldestAccount;
    @XmlElement(name = "TotalBalanceAmount")
    protected BigDecimal totalBalanceAmount;
    @XmlElement(name = "TotalSanctionAmount")
    protected BigDecimal totalSanctionAmount;
    @XmlElement(name = "TotalCreditLimit")
    protected BigDecimal totalCreditLimit;
    @XmlElement(name = "TotalMonthlyPaymentAmount")
    protected BigDecimal totalMonthlyPaymentAmount;
    @XmlElement(name = "TotalWrittenOffAmount")
    protected BigDecimal totalWrittenOffAmount;

    /**
     * Gets the value of the noOfAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfAccounts() {
        return noOfAccounts;
    }

    /**
     * Sets the value of the noOfAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfAccounts(Integer value) {
        this.noOfAccounts = value;
    }

    /**
     * Gets the value of the noOfActiveAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfActiveAccounts() {
        return noOfActiveAccounts;
    }

    /**
     * Sets the value of the noOfActiveAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfActiveAccounts(Integer value) {
        this.noOfActiveAccounts = value;
    }

    /**
     * Gets the value of the noOfWriteOffs property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfWriteOffs() {
        return noOfWriteOffs;
    }

    /**
     * Sets the value of the noOfWriteOffs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfWriteOffs(Integer value) {
        this.noOfWriteOffs = value;
    }

    /**
     * Gets the value of the totalPastDue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalPastDue() {
        return totalPastDue;
    }

    /**
     * Sets the value of the totalPastDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalPastDue(BigDecimal value) {
        this.totalPastDue = value;
    }

    /**
     * Gets the value of the mostSevereStatusWithIn24Months property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMostSevereStatusWithIn24Months() {
        return mostSevereStatusWithIn24Months;
    }

    /**
     * Sets the value of the mostSevereStatusWithIn24Months property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMostSevereStatusWithIn24Months(String value) {
        this.mostSevereStatusWithIn24Months = value;
    }

    /**
     * Gets the value of the singleHighestCredit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSingleHighestCredit() {
        return singleHighestCredit;
    }

    /**
     * Sets the value of the singleHighestCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSingleHighestCredit(BigDecimal value) {
        this.singleHighestCredit = value;
    }

    /**
     * Gets the value of the singleHighestSanctionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSingleHighestSanctionAmount() {
        return singleHighestSanctionAmount;
    }

    /**
     * Sets the value of the singleHighestSanctionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSingleHighestSanctionAmount(BigDecimal value) {
        this.singleHighestSanctionAmount = value;
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
     * Gets the value of the averageOpenBalance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAverageOpenBalance() {
        return averageOpenBalance;
    }

    /**
     * Sets the value of the averageOpenBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAverageOpenBalance(BigDecimal value) {
        this.averageOpenBalance = value;
    }

    /**
     * Gets the value of the singleHighestBalance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSingleHighestBalance() {
        return singleHighestBalance;
    }

    /**
     * Sets the value of the singleHighestBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSingleHighestBalance(BigDecimal value) {
        this.singleHighestBalance = value;
    }

    /**
     * Gets the value of the noOfPastDueAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfPastDueAccounts() {
        return noOfPastDueAccounts;
    }

    /**
     * Sets the value of the noOfPastDueAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfPastDueAccounts(Integer value) {
        this.noOfPastDueAccounts = value;
    }

    /**
     * Gets the value of the noOfZeroBalanceAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfZeroBalanceAccounts() {
        return noOfZeroBalanceAccounts;
    }

    /**
     * Sets the value of the noOfZeroBalanceAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfZeroBalanceAccounts(Integer value) {
        this.noOfZeroBalanceAccounts = value;
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

    /**
     * Gets the value of the totalBalanceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalBalanceAmount() {
        return totalBalanceAmount;
    }

    /**
     * Sets the value of the totalBalanceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalBalanceAmount(BigDecimal value) {
        this.totalBalanceAmount = value;
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
     * Gets the value of the totalCreditLimit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalCreditLimit() {
        return totalCreditLimit;
    }

    /**
     * Sets the value of the totalCreditLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalCreditLimit(BigDecimal value) {
        this.totalCreditLimit = value;
    }

    /**
     * Gets the value of the totalMonthlyPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalMonthlyPaymentAmount() {
        return totalMonthlyPaymentAmount;
    }

    /**
     * Sets the value of the totalMonthlyPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalMonthlyPaymentAmount(BigDecimal value) {
        this.totalMonthlyPaymentAmount = value;
    }

    /**
     * Gets the value of the totalWrittenOffAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalWrittenOffAmount() {
        return totalWrittenOffAmount;
    }

    /**
     * Sets the value of the totalWrittenOffAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalWrittenOffAmount(BigDecimal value) {
        this.totalWrittenOffAmount = value;
    }

}
