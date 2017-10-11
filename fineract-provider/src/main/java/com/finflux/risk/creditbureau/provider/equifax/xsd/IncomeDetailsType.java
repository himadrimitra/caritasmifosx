package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 *             Income Details Type
 *          
 * 
 * <p>Java class for IncomeDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IncomeDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Occupation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MonthlyIncome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MonthlyExpense" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PovertyIndex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AssetOwnership" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "IncomeDetailsType", propOrder = {
    "occupation",
    "monthlyIncome",
    "monthlyExpense",
    "povertyIndex",
    "assetOwnership"
})
public class IncomeDetailsType {

    @XmlElement(name = "Occupation")
    protected String occupation;
    @XmlElement(name = "MonthlyIncome")
    protected String monthlyIncome;
    @XmlElement(name = "MonthlyExpense")
    protected String monthlyExpense;
    @XmlElement(name = "PovertyIndex")
    protected String povertyIndex;
    @XmlElement(name = "AssetOwnership")
    protected String assetOwnership;
    @XmlAttribute(name = "seq")
    protected Integer seq;
    @XmlAttribute(name = "ReportedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reportedDate;

    /**
     * Gets the value of the occupation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Sets the value of the occupation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupation(String value) {
        this.occupation = value;
    }

    /**
     * Gets the value of the monthlyIncome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    /**
     * Sets the value of the monthlyIncome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonthlyIncome(String value) {
        this.monthlyIncome = value;
    }

    /**
     * Gets the value of the monthlyExpense property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonthlyExpense() {
        return monthlyExpense;
    }

    /**
     * Sets the value of the monthlyExpense property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonthlyExpense(String value) {
        this.monthlyExpense = value;
    }

    /**
     * Gets the value of the povertyIndex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPovertyIndex() {
        return povertyIndex;
    }

    /**
     * Sets the value of the povertyIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPovertyIndex(String value) {
        this.povertyIndex = value;
    }

    /**
     * Gets the value of the assetOwnership property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetOwnership() {
        return assetOwnership;
    }

    /**
     * Sets the value of the assetOwnership property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetOwnership(String value) {
        this.assetOwnership = value;
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
