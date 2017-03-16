package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AccountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccountType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AdditionalMFIDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}AdditionalMFIDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="ClientName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CurrentBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Institution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AccountType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="OwnershipType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Balance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="PastDueAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="DatePastDue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DisbursedAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="LoanCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LoanPurpose" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LastPayment" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="WriteOffAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Open" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SanctionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="HighCredit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="LastPaymentDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="DateReported" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="DateOpened" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="DateClosed" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="Reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DateWrittenOff" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="LoanCycleID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DateSanctioned" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="DateApplied" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="InterestRate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AppliedAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="NoOfInstallments" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="RepaymentTenure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DisputeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InstallmentAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="KeyPerson" type="{http://services.equifax.com/eport/ws/schemas/1.0}RelationInfoType" minOccurs="0"/&gt;
 *         &lt;element name="Nominee" type="{http://services.equifax.com/eport/ws/schemas/1.0}RelationInfoType" minOccurs="0"/&gt;
 *         &lt;element name="TermFrequency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CreditLimit" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="CollateralValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CollateralType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AccountStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AssetClassification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SuitFiledStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="History48Months" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountHistoryType" minOccurs="0"/&gt;
 *         &lt;element name="History24Months" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountHistoryType" minOccurs="0"/&gt;
 *         &lt;element name="BranchIDMFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="KendraIDMFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DaysPastDue" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TypeOfInsurance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InsurancePolicyAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="NumberOfMeetingsHeld" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberOfMeetingsMissed" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://services.equifax.com/eport/ws/schemas/1.0}AccountAttributes"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountType", propOrder = {
    "additionalMFIDetails",
    "clientName",
    "accountNumber",
    "currentBalance",
    "institution",
    "accountType",
    "ownershipType",
    "balance",
    "pastDueAmount",
    "datePastDue",
    "disbursedAmount",
    "loanCategory",
    "loanPurpose",
    "lastPayment",
    "writeOffAmount",
    "open",
    "sanctionAmount",
    "highCredit",
    "lastPaymentDate",
    "dateReported",
    "dateOpened",
    "dateClosed",
    "reason",
    "dateWrittenOff",
    "loanCycleID",
    "dateSanctioned",
    "dateApplied",
    "interestRate",
    "appliedAmount",
    "noOfInstallments",
    "repaymentTenure",
    "disputeCode",
    "installmentAmount",
    "keyPerson",
    "nominee",
    "termFrequency",
    "creditLimit",
    "collateralValue",
    "collateralType",
    "accountStatus",
    "assetClassification",
    "suitFiledStatus",
    "history48Months",
    "history24Months",
    "branchIDMFI",
    "kendraIDMFI",
    "daysPastDue",
    "typeOfInsurance",
    "insurancePolicyAmount",
    "numberOfMeetingsHeld",
    "numberOfMeetingsMissed"
})
public class AccountType {

    @XmlElement(name = "AdditionalMFIDetails")
    protected AdditionalMFIDetailsType additionalMFIDetails;
    @XmlElement(name = "ClientName")
    protected String clientName;
    @XmlElement(name = "AccountNumber")
    protected String accountNumber;
    @XmlElement(name = "CurrentBalance")
    protected BigDecimal currentBalance;
    @XmlElement(name = "Institution")
    protected String institution;
    @XmlElement(name = "AccountType")
    protected String accountType;
    @XmlElement(name = "OwnershipType")
    protected String ownershipType;
    @XmlElement(name = "Balance")
    protected BigDecimal balance;
    @XmlElement(name = "PastDueAmount")
    protected BigDecimal pastDueAmount;
    @XmlElement(name = "DatePastDue")
    protected String datePastDue;
    @XmlElement(name = "DisbursedAmount")
    protected BigDecimal disbursedAmount;
    @XmlElement(name = "LoanCategory")
    protected String loanCategory;
    @XmlElement(name = "LoanPurpose")
    protected String loanPurpose;
    @XmlElement(name = "LastPayment")
    protected BigDecimal lastPayment;
    @XmlElement(name = "WriteOffAmount")
    protected BigDecimal writeOffAmount;
    @XmlElement(name = "Open")
    protected String open;
    @XmlElement(name = "SanctionAmount")
    protected BigDecimal sanctionAmount;
    @XmlElement(name = "HighCredit")
    protected BigDecimal highCredit;
    @XmlElement(name = "LastPaymentDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar lastPaymentDate;
    @XmlElement(name = "DateReported")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateReported;
    @XmlElement(name = "DateOpened")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOpened;
    @XmlElement(name = "DateClosed")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateClosed;
    @XmlElement(name = "Reason")
    protected String reason;
    @XmlElement(name = "DateWrittenOff")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateWrittenOff;
    @XmlElement(name = "LoanCycleID")
    protected String loanCycleID;
    @XmlElement(name = "DateSanctioned")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateSanctioned;
    @XmlElement(name = "DateApplied")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateApplied;
    @XmlElement(name = "InterestRate")
    protected String interestRate;
    @XmlElement(name = "AppliedAmount")
    protected BigDecimal appliedAmount;
    @XmlElement(name = "NoOfInstallments")
    protected Integer noOfInstallments;
    @XmlElement(name = "RepaymentTenure")
    protected String repaymentTenure;
    @XmlElement(name = "DisputeCode")
    protected String disputeCode;
    @XmlElement(name = "InstallmentAmount")
    protected BigDecimal installmentAmount;
    @XmlElement(name = "KeyPerson")
    protected RelationInfoType keyPerson;
    @XmlElement(name = "Nominee")
    protected RelationInfoType nominee;
    @XmlElement(name = "TermFrequency")
    protected String termFrequency;
    @XmlElement(name = "CreditLimit")
    protected BigDecimal creditLimit;
    @XmlElement(name = "CollateralValue")
    protected String collateralValue;
    @XmlElement(name = "CollateralType")
    protected String collateralType;
    @XmlElement(name = "AccountStatus")
    protected String accountStatus;
    @XmlElement(name = "AssetClassification")
    protected String assetClassification;
    @XmlElement(name = "SuitFiledStatus")
    protected String suitFiledStatus;
    @XmlElement(name = "History48Months")
    protected AccountHistoryType history48Months;
    @XmlElement(name = "History24Months")
    protected AccountHistoryType history24Months;
    @XmlElement(name = "BranchIDMFI")
    protected String branchIDMFI;
    @XmlElement(name = "KendraIDMFI")
    protected String kendraIDMFI;
    @XmlElement(name = "DaysPastDue")
    protected Integer daysPastDue;
    @XmlElement(name = "TypeOfInsurance")
    protected String typeOfInsurance;
    @XmlElement(name = "InsurancePolicyAmount")
    protected BigDecimal insurancePolicyAmount;
    @XmlElement(name = "NumberOfMeetingsHeld")
    protected Integer numberOfMeetingsHeld;
    @XmlElement(name = "NumberOfMeetingsMissed")
    protected Integer numberOfMeetingsMissed;
    @XmlAttribute(name = "typeCode")
    protected String typeCode;
    @XmlAttribute(name = "seq")
    protected Integer seq;
    @XmlAttribute(name = "id")
    protected Integer id;
    @XmlAttribute(name = "ReportedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reportedDate;

    /**
     * Gets the value of the additionalMFIDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalMFIDetailsType }
     *     
     */
    public AdditionalMFIDetailsType getAdditionalMFIDetails() {
        return additionalMFIDetails;
    }

    /**
     * Sets the value of the additionalMFIDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalMFIDetailsType }
     *     
     */
    public void setAdditionalMFIDetails(AdditionalMFIDetailsType value) {
        this.additionalMFIDetails = value;
    }

    /**
     * Gets the value of the clientName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the value of the clientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientName(String value) {
        this.clientName = value;
    }

    /**
     * Gets the value of the accountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the value of the accountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
    }

    /**
     * Gets the value of the currentBalance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    /**
     * Sets the value of the currentBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCurrentBalance(BigDecimal value) {
        this.currentBalance = value;
    }

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitution(String value) {
        this.institution = value;
    }

    /**
     * Gets the value of the accountType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountType(String value) {
        this.accountType = value;
    }

    /**
     * Gets the value of the ownershipType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnershipType() {
        return ownershipType;
    }

    /**
     * Sets the value of the ownershipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnershipType(String value) {
        this.ownershipType = value;
    }

    /**
     * Gets the value of the balance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setBalance(BigDecimal value) {
        this.balance = value;
    }

    /**
     * Gets the value of the pastDueAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPastDueAmount() {
        return pastDueAmount;
    }

    /**
     * Sets the value of the pastDueAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPastDueAmount(BigDecimal value) {
        this.pastDueAmount = value;
    }

    /**
     * Gets the value of the datePastDue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatePastDue() {
        return datePastDue;
    }

    /**
     * Sets the value of the datePastDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatePastDue(String value) {
        this.datePastDue = value;
    }

    /**
     * Gets the value of the disbursedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDisbursedAmount() {
        return disbursedAmount;
    }

    /**
     * Sets the value of the disbursedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDisbursedAmount(BigDecimal value) {
        this.disbursedAmount = value;
    }

    /**
     * Gets the value of the loanCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoanCategory() {
        return loanCategory;
    }

    /**
     * Sets the value of the loanCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoanCategory(String value) {
        this.loanCategory = value;
    }

    /**
     * Gets the value of the loanPurpose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoanPurpose() {
        return loanPurpose;
    }

    /**
     * Sets the value of the loanPurpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoanPurpose(String value) {
        this.loanPurpose = value;
    }

    /**
     * Gets the value of the lastPayment property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLastPayment() {
        return lastPayment;
    }

    /**
     * Sets the value of the lastPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLastPayment(BigDecimal value) {
        this.lastPayment = value;
    }

    /**
     * Gets the value of the writeOffAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getWriteOffAmount() {
        return writeOffAmount;
    }

    /**
     * Sets the value of the writeOffAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setWriteOffAmount(BigDecimal value) {
        this.writeOffAmount = value;
    }

    /**
     * Gets the value of the open property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpen() {
        return open;
    }

    /**
     * Sets the value of the open property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpen(String value) {
        this.open = value;
    }

    /**
     * Gets the value of the sanctionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSanctionAmount() {
        return sanctionAmount;
    }

    /**
     * Sets the value of the sanctionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSanctionAmount(BigDecimal value) {
        this.sanctionAmount = value;
    }

    /**
     * Gets the value of the highCredit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHighCredit() {
        return highCredit;
    }

    /**
     * Sets the value of the highCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHighCredit(BigDecimal value) {
        this.highCredit = value;
    }

    /**
     * Gets the value of the lastPaymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastPaymentDate() {
        return lastPaymentDate;
    }

    /**
     * Sets the value of the lastPaymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastPaymentDate(XMLGregorianCalendar value) {
        this.lastPaymentDate = value;
    }

    /**
     * Gets the value of the dateReported property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateReported() {
        return dateReported;
    }

    /**
     * Sets the value of the dateReported property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateReported(XMLGregorianCalendar value) {
        this.dateReported = value;
    }

    /**
     * Gets the value of the dateOpened property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOpened() {
        return dateOpened;
    }

    /**
     * Sets the value of the dateOpened property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOpened(XMLGregorianCalendar value) {
        this.dateOpened = value;
    }

    /**
     * Gets the value of the dateClosed property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateClosed() {
        return dateClosed;
    }

    /**
     * Sets the value of the dateClosed property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateClosed(XMLGregorianCalendar value) {
        this.dateClosed = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the dateWrittenOff property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateWrittenOff() {
        return dateWrittenOff;
    }

    /**
     * Sets the value of the dateWrittenOff property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateWrittenOff(XMLGregorianCalendar value) {
        this.dateWrittenOff = value;
    }

    /**
     * Gets the value of the loanCycleID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoanCycleID() {
        return loanCycleID;
    }

    /**
     * Sets the value of the loanCycleID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoanCycleID(String value) {
        this.loanCycleID = value;
    }

    /**
     * Gets the value of the dateSanctioned property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateSanctioned() {
        return dateSanctioned;
    }

    /**
     * Sets the value of the dateSanctioned property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateSanctioned(XMLGregorianCalendar value) {
        this.dateSanctioned = value;
    }

    /**
     * Gets the value of the dateApplied property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateApplied() {
        return dateApplied;
    }

    /**
     * Sets the value of the dateApplied property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateApplied(XMLGregorianCalendar value) {
        this.dateApplied = value;
    }

    /**
     * Gets the value of the interestRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the value of the interestRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterestRate(String value) {
        this.interestRate = value;
    }

    /**
     * Gets the value of the appliedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAppliedAmount() {
        return appliedAmount;
    }

    /**
     * Sets the value of the appliedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAppliedAmount(BigDecimal value) {
        this.appliedAmount = value;
    }

    /**
     * Gets the value of the noOfInstallments property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfInstallments() {
        return noOfInstallments;
    }

    /**
     * Sets the value of the noOfInstallments property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfInstallments(Integer value) {
        this.noOfInstallments = value;
    }

    /**
     * Gets the value of the repaymentTenure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepaymentTenure() {
        return repaymentTenure;
    }

    /**
     * Sets the value of the repaymentTenure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepaymentTenure(String value) {
        this.repaymentTenure = value;
    }

    /**
     * Gets the value of the disputeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisputeCode() {
        return disputeCode;
    }

    /**
     * Sets the value of the disputeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisputeCode(String value) {
        this.disputeCode = value;
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
     * Gets the value of the keyPerson property.
     * 
     * @return
     *     possible object is
     *     {@link RelationInfoType }
     *     
     */
    public RelationInfoType getKeyPerson() {
        return keyPerson;
    }

    /**
     * Sets the value of the keyPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationInfoType }
     *     
     */
    public void setKeyPerson(RelationInfoType value) {
        this.keyPerson = value;
    }

    /**
     * Gets the value of the nominee property.
     * 
     * @return
     *     possible object is
     *     {@link RelationInfoType }
     *     
     */
    public RelationInfoType getNominee() {
        return nominee;
    }

    /**
     * Sets the value of the nominee property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationInfoType }
     *     
     */
    public void setNominee(RelationInfoType value) {
        this.nominee = value;
    }

    /**
     * Gets the value of the termFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTermFrequency() {
        return termFrequency;
    }

    /**
     * Sets the value of the termFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTermFrequency(String value) {
        this.termFrequency = value;
    }

    /**
     * Gets the value of the creditLimit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the value of the creditLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCreditLimit(BigDecimal value) {
        this.creditLimit = value;
    }

    /**
     * Gets the value of the collateralValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollateralValue() {
        return collateralValue;
    }

    /**
     * Sets the value of the collateralValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollateralValue(String value) {
        this.collateralValue = value;
    }

    /**
     * Gets the value of the collateralType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollateralType() {
        return collateralType;
    }

    /**
     * Sets the value of the collateralType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollateralType(String value) {
        this.collateralType = value;
    }

    /**
     * Gets the value of the accountStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountStatus() {
        return accountStatus;
    }

    /**
     * Sets the value of the accountStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountStatus(String value) {
        this.accountStatus = value;
    }

    /**
     * Gets the value of the assetClassification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetClassification() {
        return assetClassification;
    }

    /**
     * Sets the value of the assetClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetClassification(String value) {
        this.assetClassification = value;
    }

    /**
     * Gets the value of the suitFiledStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuitFiledStatus() {
        return suitFiledStatus;
    }

    /**
     * Sets the value of the suitFiledStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuitFiledStatus(String value) {
        this.suitFiledStatus = value;
    }

    /**
     * Gets the value of the history48Months property.
     * 
     * @return
     *     possible object is
     *     {@link AccountHistoryType }
     *     
     */
    public AccountHistoryType getHistory48Months() {
        return history48Months;
    }

    /**
     * Sets the value of the history48Months property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountHistoryType }
     *     
     */
    public void setHistory48Months(AccountHistoryType value) {
        this.history48Months = value;
    }

    /**
     * Gets the value of the history24Months property.
     * 
     * @return
     *     possible object is
     *     {@link AccountHistoryType }
     *     
     */
    public AccountHistoryType getHistory24Months() {
        return history24Months;
    }

    /**
     * Sets the value of the history24Months property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountHistoryType }
     *     
     */
    public void setHistory24Months(AccountHistoryType value) {
        this.history24Months = value;
    }

    /**
     * Gets the value of the branchIDMFI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBranchIDMFI() {
        return branchIDMFI;
    }

    /**
     * Sets the value of the branchIDMFI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchIDMFI(String value) {
        this.branchIDMFI = value;
    }

    /**
     * Gets the value of the kendraIDMFI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKendraIDMFI() {
        return kendraIDMFI;
    }

    /**
     * Sets the value of the kendraIDMFI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKendraIDMFI(String value) {
        this.kendraIDMFI = value;
    }

    /**
     * Gets the value of the daysPastDue property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDaysPastDue() {
        return daysPastDue;
    }

    /**
     * Sets the value of the daysPastDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDaysPastDue(Integer value) {
        this.daysPastDue = value;
    }

    /**
     * Gets the value of the typeOfInsurance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeOfInsurance() {
        return typeOfInsurance;
    }

    /**
     * Sets the value of the typeOfInsurance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeOfInsurance(String value) {
        this.typeOfInsurance = value;
    }

    /**
     * Gets the value of the insurancePolicyAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInsurancePolicyAmount() {
        return insurancePolicyAmount;
    }

    /**
     * Sets the value of the insurancePolicyAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInsurancePolicyAmount(BigDecimal value) {
        this.insurancePolicyAmount = value;
    }

    /**
     * Gets the value of the numberOfMeetingsHeld property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfMeetingsHeld() {
        return numberOfMeetingsHeld;
    }

    /**
     * Sets the value of the numberOfMeetingsHeld property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfMeetingsHeld(Integer value) {
        this.numberOfMeetingsHeld = value;
    }

    /**
     * Gets the value of the numberOfMeetingsMissed property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfMeetingsMissed() {
        return numberOfMeetingsMissed;
    }

    /**
     * Sets the value of the numberOfMeetingsMissed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfMeetingsMissed(Integer value) {
        this.numberOfMeetingsMissed = value;
    }

    /**
     * Gets the value of the typeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * Sets the value of the typeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeCode(String value) {
        this.typeCode = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSeq(Integer value) {
        this.seq = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the reportedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReportedDate() {
        return reportedDate;
    }

    /**
     * Sets the value of the reportedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReportedDate(XMLGregorianCalendar value) {
        this.reportedDate = value;
    }

}
