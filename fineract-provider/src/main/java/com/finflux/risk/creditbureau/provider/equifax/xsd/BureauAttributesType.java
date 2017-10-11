package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BureauAttributesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BureauAttributesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CreditScore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NoOfAccounts" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NoOfOpenAccounts" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NoOfPastDue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NoOfWritenOff" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NoOfZeroBal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MostSevereStatus24Mths" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalBalAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalPastDueAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalHighCredit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalSanctionAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalMthlyPaymentAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AvgOpenBal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RecentAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OldestAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalCreditLimit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SingleHighestCredit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SingleHighestSanctionAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SingleHighestBal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalUnsecuredLoans" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalUnsecuredBal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalUnsecuredPastDueLoans" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalUnsecuredPastDueAmt" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalUnsecuredPastDueBal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalSecuredLoans" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalSecuredBal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalSecuredPastDueLoans" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalSecuredPastDueAmt" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalSecuredPastDueBal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="OverdueAmtMostSevereStatus" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="SumSecuredHighCredit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="SumUnsecuredHighCredit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="CountOfOwnAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CountOfOtherAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenAutoLoanAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenCreditCardAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenHousingLoanAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenPersonalLoanAccounts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenAutoLoanAccountsOwn" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenCreditCardAccountsOwn" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenHousingLoanAccountsOwn" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OpenPersonalLoanAccountsOwn" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalBalOpenAutoLoans" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalBalOpenCreditCards" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalBalOpenHousingLoans" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalBalOpenPersonalLoans" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Ever30Secured" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OverdueAmtEver30Secured" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Ever60DPD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OverdueAmtEver60" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Ever90DPD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OverdueAmtEver90" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BureauAttributesType", propOrder = {
    "creditScore",
    "noOfAccounts",
    "noOfOpenAccounts",
    "noOfPastDue",
    "noOfWritenOff",
    "noOfZeroBal",
    "mostSevereStatus24Mths",
    "totalBalAmt",
    "totalPastDueAmt",
    "totalHighCredit",
    "totalSanctionAmt",
    "totalMthlyPaymentAmt",
    "avgOpenBal",
    "recentAccount",
    "oldestAccount",
    "totalCreditLimit",
    "singleHighestCredit",
    "singleHighestSanctionAmt",
    "singleHighestBal",
    "totalUnsecuredLoans",
    "totalUnsecuredBal",
    "totalUnsecuredPastDueLoans",
    "totalUnsecuredPastDueAmt",
    "totalUnsecuredPastDueBal",
    "totalSecuredLoans",
    "totalSecuredBal",
    "totalSecuredPastDueLoans",
    "totalSecuredPastDueAmt",
    "totalSecuredPastDueBal",
    "overdueAmtMostSevereStatus",
    "sumSecuredHighCredit",
    "sumUnsecuredHighCredit",
    "countOfOwnAccounts",
    "countOfOtherAccounts",
    "openAutoLoanAccounts",
    "openCreditCardAccounts",
    "openHousingLoanAccounts",
    "openPersonalLoanAccounts",
    "openAutoLoanAccountsOwn",
    "openCreditCardAccountsOwn",
    "openHousingLoanAccountsOwn",
    "openPersonalLoanAccountsOwn",
    "totalBalOpenAutoLoans",
    "totalBalOpenCreditCards",
    "totalBalOpenHousingLoans",
    "totalBalOpenPersonalLoans",
    "ever30Secured",
    "overdueAmtEver30Secured",
    "ever60DPD",
    "overdueAmtEver60",
    "ever90DPD",
    "overdueAmtEver90"
})
public class BureauAttributesType {

    @XmlElement(name = "CreditScore")
    protected String creditScore;
    @XmlElement(name = "NoOfAccounts")
    protected String noOfAccounts;
    @XmlElement(name = "NoOfOpenAccounts")
    protected String noOfOpenAccounts;
    @XmlElement(name = "NoOfPastDue")
    protected String noOfPastDue;
    @XmlElement(name = "NoOfWritenOff")
    protected String noOfWritenOff;
    @XmlElement(name = "NoOfZeroBal")
    protected String noOfZeroBal;
    @XmlElement(name = "MostSevereStatus24Mths")
    protected String mostSevereStatus24Mths;
    @XmlElement(name = "TotalBalAmt")
    protected String totalBalAmt;
    @XmlElement(name = "TotalPastDueAmt")
    protected String totalPastDueAmt;
    @XmlElement(name = "TotalHighCredit")
    protected String totalHighCredit;
    @XmlElement(name = "TotalSanctionAmt")
    protected String totalSanctionAmt;
    @XmlElement(name = "TotalMthlyPaymentAmt")
    protected String totalMthlyPaymentAmt;
    @XmlElement(name = "AvgOpenBal")
    protected String avgOpenBal;
    @XmlElement(name = "RecentAccount")
    protected String recentAccount;
    @XmlElement(name = "OldestAccount")
    protected String oldestAccount;
    @XmlElement(name = "TotalCreditLimit")
    protected String totalCreditLimit;
    @XmlElement(name = "SingleHighestCredit")
    protected String singleHighestCredit;
    @XmlElement(name = "SingleHighestSanctionAmt")
    protected String singleHighestSanctionAmt;
    @XmlElement(name = "SingleHighestBal")
    protected String singleHighestBal;
    @XmlElement(name = "TotalUnsecuredLoans")
    protected Integer totalUnsecuredLoans;
    @XmlElement(name = "TotalUnsecuredBal")
    protected BigDecimal totalUnsecuredBal;
    @XmlElement(name = "TotalUnsecuredPastDueLoans")
    protected Integer totalUnsecuredPastDueLoans;
    @XmlElement(name = "TotalUnsecuredPastDueAmt")
    protected BigDecimal totalUnsecuredPastDueAmt;
    @XmlElement(name = "TotalUnsecuredPastDueBal")
    protected BigDecimal totalUnsecuredPastDueBal;
    @XmlElement(name = "TotalSecuredLoans")
    protected Integer totalSecuredLoans;
    @XmlElement(name = "TotalSecuredBal")
    protected BigDecimal totalSecuredBal;
    @XmlElement(name = "TotalSecuredPastDueLoans")
    protected Integer totalSecuredPastDueLoans;
    @XmlElement(name = "TotalSecuredPastDueAmt")
    protected BigDecimal totalSecuredPastDueAmt;
    @XmlElement(name = "TotalSecuredPastDueBal")
    protected BigDecimal totalSecuredPastDueBal;
    @XmlElement(name = "OverdueAmtMostSevereStatus")
    protected BigDecimal overdueAmtMostSevereStatus;
    @XmlElement(name = "SumSecuredHighCredit")
    protected BigDecimal sumSecuredHighCredit;
    @XmlElement(name = "SumUnsecuredHighCredit")
    protected BigDecimal sumUnsecuredHighCredit;
    @XmlElement(name = "CountOfOwnAccounts")
    protected Integer countOfOwnAccounts;
    @XmlElement(name = "CountOfOtherAccounts")
    protected Integer countOfOtherAccounts;
    @XmlElement(name = "OpenAutoLoanAccounts")
    protected Integer openAutoLoanAccounts;
    @XmlElement(name = "OpenCreditCardAccounts")
    protected Integer openCreditCardAccounts;
    @XmlElement(name = "OpenHousingLoanAccounts")
    protected Integer openHousingLoanAccounts;
    @XmlElement(name = "OpenPersonalLoanAccounts")
    protected Integer openPersonalLoanAccounts;
    @XmlElement(name = "OpenAutoLoanAccountsOwn")
    protected Integer openAutoLoanAccountsOwn;
    @XmlElement(name = "OpenCreditCardAccountsOwn")
    protected Integer openCreditCardAccountsOwn;
    @XmlElement(name = "OpenHousingLoanAccountsOwn")
    protected Integer openHousingLoanAccountsOwn;
    @XmlElement(name = "OpenPersonalLoanAccountsOwn")
    protected Integer openPersonalLoanAccountsOwn;
    @XmlElement(name = "TotalBalOpenAutoLoans")
    protected BigDecimal totalBalOpenAutoLoans;
    @XmlElement(name = "TotalBalOpenCreditCards")
    protected BigDecimal totalBalOpenCreditCards;
    @XmlElement(name = "TotalBalOpenHousingLoans")
    protected BigDecimal totalBalOpenHousingLoans;
    @XmlElement(name = "TotalBalOpenPersonalLoans")
    protected BigDecimal totalBalOpenPersonalLoans;
    @XmlElement(name = "Ever30Secured")
    protected String ever30Secured;
    @XmlElement(name = "OverdueAmtEver30Secured")
    protected BigDecimal overdueAmtEver30Secured;
    @XmlElement(name = "Ever60DPD")
    protected String ever60DPD;
    @XmlElement(name = "OverdueAmtEver60")
    protected BigDecimal overdueAmtEver60;
    @XmlElement(name = "Ever90DPD")
    protected String ever90DPD;
    @XmlElement(name = "OverdueAmtEver90")
    protected BigDecimal overdueAmtEver90;

    /**
     * Gets the value of the creditScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditScore() {
        return creditScore;
    }

    /**
     * Sets the value of the creditScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditScore(String value) {
        this.creditScore = value;
    }

    /**
     * Gets the value of the noOfAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfAccounts() {
        return noOfAccounts;
    }

    /**
     * Sets the value of the noOfAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfAccounts(String value) {
        this.noOfAccounts = value;
    }

    /**
     * Gets the value of the noOfOpenAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfOpenAccounts() {
        return noOfOpenAccounts;
    }

    /**
     * Sets the value of the noOfOpenAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfOpenAccounts(String value) {
        this.noOfOpenAccounts = value;
    }

    /**
     * Gets the value of the noOfPastDue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfPastDue() {
        return noOfPastDue;
    }

    /**
     * Sets the value of the noOfPastDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfPastDue(String value) {
        this.noOfPastDue = value;
    }

    /**
     * Gets the value of the noOfWritenOff property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfWritenOff() {
        return noOfWritenOff;
    }

    /**
     * Sets the value of the noOfWritenOff property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfWritenOff(String value) {
        this.noOfWritenOff = value;
    }

    /**
     * Gets the value of the noOfZeroBal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfZeroBal() {
        return noOfZeroBal;
    }

    /**
     * Sets the value of the noOfZeroBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfZeroBal(String value) {
        this.noOfZeroBal = value;
    }

    /**
     * Gets the value of the mostSevereStatus24Mths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMostSevereStatus24Mths() {
        return mostSevereStatus24Mths;
    }

    /**
     * Sets the value of the mostSevereStatus24Mths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMostSevereStatus24Mths(String value) {
        this.mostSevereStatus24Mths = value;
    }

    /**
     * Gets the value of the totalBalAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalBalAmt() {
        return totalBalAmt;
    }

    /**
     * Sets the value of the totalBalAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalBalAmt(String value) {
        this.totalBalAmt = value;
    }

    /**
     * Gets the value of the totalPastDueAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalPastDueAmt() {
        return totalPastDueAmt;
    }

    /**
     * Sets the value of the totalPastDueAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalPastDueAmt(String value) {
        this.totalPastDueAmt = value;
    }

    /**
     * Gets the value of the totalHighCredit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalHighCredit() {
        return totalHighCredit;
    }

    /**
     * Sets the value of the totalHighCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalHighCredit(String value) {
        this.totalHighCredit = value;
    }

    /**
     * Gets the value of the totalSanctionAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSanctionAmt() {
        return totalSanctionAmt;
    }

    /**
     * Sets the value of the totalSanctionAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSanctionAmt(String value) {
        this.totalSanctionAmt = value;
    }

    /**
     * Gets the value of the totalMthlyPaymentAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalMthlyPaymentAmt() {
        return totalMthlyPaymentAmt;
    }

    /**
     * Sets the value of the totalMthlyPaymentAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalMthlyPaymentAmt(String value) {
        this.totalMthlyPaymentAmt = value;
    }

    /**
     * Gets the value of the avgOpenBal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvgOpenBal() {
        return avgOpenBal;
    }

    /**
     * Sets the value of the avgOpenBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvgOpenBal(String value) {
        this.avgOpenBal = value;
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
     * Gets the value of the totalCreditLimit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalCreditLimit() {
        return totalCreditLimit;
    }

    /**
     * Sets the value of the totalCreditLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalCreditLimit(String value) {
        this.totalCreditLimit = value;
    }

    /**
     * Gets the value of the singleHighestCredit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSingleHighestCredit() {
        return singleHighestCredit;
    }

    /**
     * Sets the value of the singleHighestCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSingleHighestCredit(String value) {
        this.singleHighestCredit = value;
    }

    /**
     * Gets the value of the singleHighestSanctionAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSingleHighestSanctionAmt() {
        return singleHighestSanctionAmt;
    }

    /**
     * Sets the value of the singleHighestSanctionAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSingleHighestSanctionAmt(String value) {
        this.singleHighestSanctionAmt = value;
    }

    /**
     * Gets the value of the singleHighestBal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSingleHighestBal() {
        return singleHighestBal;
    }

    /**
     * Sets the value of the singleHighestBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSingleHighestBal(String value) {
        this.singleHighestBal = value;
    }

    /**
     * Gets the value of the totalUnsecuredLoans property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalUnsecuredLoans() {
        return totalUnsecuredLoans;
    }

    /**
     * Sets the value of the totalUnsecuredLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalUnsecuredLoans(Integer value) {
        this.totalUnsecuredLoans = value;
    }

    /**
     * Gets the value of the totalUnsecuredBal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalUnsecuredBal() {
        return totalUnsecuredBal;
    }

    /**
     * Sets the value of the totalUnsecuredBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalUnsecuredBal(BigDecimal value) {
        this.totalUnsecuredBal = value;
    }

    /**
     * Gets the value of the totalUnsecuredPastDueLoans property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalUnsecuredPastDueLoans() {
        return totalUnsecuredPastDueLoans;
    }

    /**
     * Sets the value of the totalUnsecuredPastDueLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalUnsecuredPastDueLoans(Integer value) {
        this.totalUnsecuredPastDueLoans = value;
    }

    /**
     * Gets the value of the totalUnsecuredPastDueAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalUnsecuredPastDueAmt() {
        return totalUnsecuredPastDueAmt;
    }

    /**
     * Sets the value of the totalUnsecuredPastDueAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalUnsecuredPastDueAmt(BigDecimal value) {
        this.totalUnsecuredPastDueAmt = value;
    }

    /**
     * Gets the value of the totalUnsecuredPastDueBal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalUnsecuredPastDueBal() {
        return totalUnsecuredPastDueBal;
    }

    /**
     * Sets the value of the totalUnsecuredPastDueBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalUnsecuredPastDueBal(BigDecimal value) {
        this.totalUnsecuredPastDueBal = value;
    }

    /**
     * Gets the value of the totalSecuredLoans property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalSecuredLoans() {
        return totalSecuredLoans;
    }

    /**
     * Sets the value of the totalSecuredLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalSecuredLoans(Integer value) {
        this.totalSecuredLoans = value;
    }

    /**
     * Gets the value of the totalSecuredBal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalSecuredBal() {
        return totalSecuredBal;
    }

    /**
     * Sets the value of the totalSecuredBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalSecuredBal(BigDecimal value) {
        this.totalSecuredBal = value;
    }

    /**
     * Gets the value of the totalSecuredPastDueLoans property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalSecuredPastDueLoans() {
        return totalSecuredPastDueLoans;
    }

    /**
     * Sets the value of the totalSecuredPastDueLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalSecuredPastDueLoans(Integer value) {
        this.totalSecuredPastDueLoans = value;
    }

    /**
     * Gets the value of the totalSecuredPastDueAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalSecuredPastDueAmt() {
        return totalSecuredPastDueAmt;
    }

    /**
     * Sets the value of the totalSecuredPastDueAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalSecuredPastDueAmt(BigDecimal value) {
        this.totalSecuredPastDueAmt = value;
    }

    /**
     * Gets the value of the totalSecuredPastDueBal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalSecuredPastDueBal() {
        return totalSecuredPastDueBal;
    }

    /**
     * Sets the value of the totalSecuredPastDueBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalSecuredPastDueBal(BigDecimal value) {
        this.totalSecuredPastDueBal = value;
    }

    /**
     * Gets the value of the overdueAmtMostSevereStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOverdueAmtMostSevereStatus() {
        return overdueAmtMostSevereStatus;
    }

    /**
     * Sets the value of the overdueAmtMostSevereStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOverdueAmtMostSevereStatus(BigDecimal value) {
        this.overdueAmtMostSevereStatus = value;
    }

    /**
     * Gets the value of the sumSecuredHighCredit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSumSecuredHighCredit() {
        return sumSecuredHighCredit;
    }

    /**
     * Sets the value of the sumSecuredHighCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSumSecuredHighCredit(BigDecimal value) {
        this.sumSecuredHighCredit = value;
    }

    /**
     * Gets the value of the sumUnsecuredHighCredit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSumUnsecuredHighCredit() {
        return sumUnsecuredHighCredit;
    }

    /**
     * Sets the value of the sumUnsecuredHighCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSumUnsecuredHighCredit(BigDecimal value) {
        this.sumUnsecuredHighCredit = value;
    }

    /**
     * Gets the value of the countOfOwnAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCountOfOwnAccounts() {
        return countOfOwnAccounts;
    }

    /**
     * Sets the value of the countOfOwnAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCountOfOwnAccounts(Integer value) {
        this.countOfOwnAccounts = value;
    }

    /**
     * Gets the value of the countOfOtherAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCountOfOtherAccounts() {
        return countOfOtherAccounts;
    }

    /**
     * Sets the value of the countOfOtherAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCountOfOtherAccounts(Integer value) {
        this.countOfOtherAccounts = value;
    }

    /**
     * Gets the value of the openAutoLoanAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenAutoLoanAccounts() {
        return openAutoLoanAccounts;
    }

    /**
     * Sets the value of the openAutoLoanAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenAutoLoanAccounts(Integer value) {
        this.openAutoLoanAccounts = value;
    }

    /**
     * Gets the value of the openCreditCardAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenCreditCardAccounts() {
        return openCreditCardAccounts;
    }

    /**
     * Sets the value of the openCreditCardAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenCreditCardAccounts(Integer value) {
        this.openCreditCardAccounts = value;
    }

    /**
     * Gets the value of the openHousingLoanAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenHousingLoanAccounts() {
        return openHousingLoanAccounts;
    }

    /**
     * Sets the value of the openHousingLoanAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenHousingLoanAccounts(Integer value) {
        this.openHousingLoanAccounts = value;
    }

    /**
     * Gets the value of the openPersonalLoanAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenPersonalLoanAccounts() {
        return openPersonalLoanAccounts;
    }

    /**
     * Sets the value of the openPersonalLoanAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenPersonalLoanAccounts(Integer value) {
        this.openPersonalLoanAccounts = value;
    }

    /**
     * Gets the value of the openAutoLoanAccountsOwn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenAutoLoanAccountsOwn() {
        return openAutoLoanAccountsOwn;
    }

    /**
     * Sets the value of the openAutoLoanAccountsOwn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenAutoLoanAccountsOwn(Integer value) {
        this.openAutoLoanAccountsOwn = value;
    }

    /**
     * Gets the value of the openCreditCardAccountsOwn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenCreditCardAccountsOwn() {
        return openCreditCardAccountsOwn;
    }

    /**
     * Sets the value of the openCreditCardAccountsOwn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenCreditCardAccountsOwn(Integer value) {
        this.openCreditCardAccountsOwn = value;
    }

    /**
     * Gets the value of the openHousingLoanAccountsOwn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenHousingLoanAccountsOwn() {
        return openHousingLoanAccountsOwn;
    }

    /**
     * Sets the value of the openHousingLoanAccountsOwn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenHousingLoanAccountsOwn(Integer value) {
        this.openHousingLoanAccountsOwn = value;
    }

    /**
     * Gets the value of the openPersonalLoanAccountsOwn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenPersonalLoanAccountsOwn() {
        return openPersonalLoanAccountsOwn;
    }

    /**
     * Sets the value of the openPersonalLoanAccountsOwn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenPersonalLoanAccountsOwn(Integer value) {
        this.openPersonalLoanAccountsOwn = value;
    }

    /**
     * Gets the value of the totalBalOpenAutoLoans property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalBalOpenAutoLoans() {
        return totalBalOpenAutoLoans;
    }

    /**
     * Sets the value of the totalBalOpenAutoLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalBalOpenAutoLoans(BigDecimal value) {
        this.totalBalOpenAutoLoans = value;
    }

    /**
     * Gets the value of the totalBalOpenCreditCards property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalBalOpenCreditCards() {
        return totalBalOpenCreditCards;
    }

    /**
     * Sets the value of the totalBalOpenCreditCards property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalBalOpenCreditCards(BigDecimal value) {
        this.totalBalOpenCreditCards = value;
    }

    /**
     * Gets the value of the totalBalOpenHousingLoans property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalBalOpenHousingLoans() {
        return totalBalOpenHousingLoans;
    }

    /**
     * Sets the value of the totalBalOpenHousingLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalBalOpenHousingLoans(BigDecimal value) {
        this.totalBalOpenHousingLoans = value;
    }

    /**
     * Gets the value of the totalBalOpenPersonalLoans property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalBalOpenPersonalLoans() {
        return totalBalOpenPersonalLoans;
    }

    /**
     * Sets the value of the totalBalOpenPersonalLoans property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalBalOpenPersonalLoans(BigDecimal value) {
        this.totalBalOpenPersonalLoans = value;
    }

    /**
     * Gets the value of the ever30Secured property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEver30Secured() {
        return ever30Secured;
    }

    /**
     * Sets the value of the ever30Secured property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEver30Secured(String value) {
        this.ever30Secured = value;
    }

    /**
     * Gets the value of the overdueAmtEver30Secured property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOverdueAmtEver30Secured() {
        return overdueAmtEver30Secured;
    }

    /**
     * Sets the value of the overdueAmtEver30Secured property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOverdueAmtEver30Secured(BigDecimal value) {
        this.overdueAmtEver30Secured = value;
    }

    /**
     * Gets the value of the ever60DPD property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEver60DPD() {
        return ever60DPD;
    }

    /**
     * Sets the value of the ever60DPD property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEver60DPD(String value) {
        this.ever60DPD = value;
    }

    /**
     * Gets the value of the overdueAmtEver60 property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOverdueAmtEver60() {
        return overdueAmtEver60;
    }

    /**
     * Sets the value of the overdueAmtEver60 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOverdueAmtEver60(BigDecimal value) {
        this.overdueAmtEver60 = value;
    }

    /**
     * Gets the value of the ever90DPD property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEver90DPD() {
        return ever90DPD;
    }

    /**
     * Sets the value of the ever90DPD property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEver90DPD(String value) {
        this.ever90DPD = value;
    }

    /**
     * Gets the value of the overdueAmtEver90 property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOverdueAmtEver90() {
        return overdueAmtEver90;
    }

    /**
     * Sets the value of the overdueAmtEver90 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOverdueAmtEver90(BigDecimal value) {
        this.overdueAmtEver90 = value;
    }

}
