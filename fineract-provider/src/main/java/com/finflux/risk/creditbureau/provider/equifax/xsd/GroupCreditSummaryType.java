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
 * <p>Java class for GroupCreditSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GroupCreditSummaryType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Institution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CurrentBalance" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DateReported" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="NoOfMembers" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="PastDueAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="SanctionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="DateOpened" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="AccountNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MembersPastDue" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="WriteOffAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="WriteOffDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://services.equifax.com/eport/ws/schemas/1.0}SeqDate"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupCreditSummaryType", propOrder = {
    "institution",
    "currentBalance",
    "status",
    "dateReported",
    "noOfMembers",
    "pastDueAmount",
    "sanctionAmount",
    "dateOpened",
    "accountNo",
    "membersPastDue",
    "writeOffAmount",
    "writeOffDate"
})
public class GroupCreditSummaryType {

    @XmlElement(name = "Institution")
    protected String institution;
    @XmlElement(name = "CurrentBalance")
    protected BigDecimal currentBalance;
    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "DateReported")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateReported;
    @XmlElement(name = "NoOfMembers")
    protected Integer noOfMembers;
    @XmlElement(name = "PastDueAmount")
    protected BigDecimal pastDueAmount;
    @XmlElement(name = "SanctionAmount")
    protected BigDecimal sanctionAmount;
    @XmlElement(name = "DateOpened")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOpened;
    @XmlElement(name = "AccountNo")
    protected String accountNo;
    @XmlElement(name = "MembersPastDue")
    protected Integer membersPastDue;
    @XmlElement(name = "WriteOffAmount")
    protected BigDecimal writeOffAmount;
    @XmlElement(name = "WriteOffDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar writeOffDate;
    @XmlAttribute(name = "seq")
    protected Integer seq;
    @XmlAttribute(name = "ReportedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reportedDate;

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
     * Gets the value of the noOfMembers property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfMembers() {
        return noOfMembers;
    }

    /**
     * Sets the value of the noOfMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfMembers(Integer value) {
        this.noOfMembers = value;
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
     * Gets the value of the accountNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * Sets the value of the accountNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNo(String value) {
        this.accountNo = value;
    }

    /**
     * Gets the value of the membersPastDue property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMembersPastDue() {
        return membersPastDue;
    }

    /**
     * Sets the value of the membersPastDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMembersPastDue(Integer value) {
        this.membersPastDue = value;
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
     * Gets the value of the writeOffDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getWriteOffDate() {
        return writeOffDate;
    }

    /**
     * Sets the value of the writeOffDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setWriteOffDate(XMLGregorianCalendar value) {
        this.writeOffDate = value;
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
