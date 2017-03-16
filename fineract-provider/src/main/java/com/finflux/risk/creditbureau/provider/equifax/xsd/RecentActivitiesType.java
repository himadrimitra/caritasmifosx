package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RecentActivitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecentActivitiesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AccountsDeliquent" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="AccountsOpened" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="TotalInquiries" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="AccountsUpdated" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecentActivitiesType", propOrder = {
    "accountsDeliquent",
    "accountsOpened",
    "totalInquiries",
    "accountsUpdated"
})
public class RecentActivitiesType {

    @XmlElement(name = "AccountsDeliquent")
    protected Integer accountsDeliquent;
    @XmlElement(name = "AccountsOpened")
    protected Integer accountsOpened;
    @XmlElement(name = "TotalInquiries")
    protected Integer totalInquiries;
    @XmlElement(name = "AccountsUpdated")
    protected Integer accountsUpdated;

    /**
     * Gets the value of the accountsDeliquent property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountsDeliquent() {
        return accountsDeliquent;
    }

    /**
     * Sets the value of the accountsDeliquent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountsDeliquent(Integer value) {
        this.accountsDeliquent = value;
    }

    /**
     * Gets the value of the accountsOpened property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountsOpened() {
        return accountsOpened;
    }

    /**
     * Sets the value of the accountsOpened property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountsOpened(Integer value) {
        this.accountsOpened = value;
    }

    /**
     * Gets the value of the totalInquiries property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalInquiries() {
        return totalInquiries;
    }

    /**
     * Sets the value of the totalInquiries property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalInquiries(Integer value) {
        this.totalInquiries = value;
    }

    /**
     * Gets the value of the accountsUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountsUpdated() {
        return accountsUpdated;
    }

    /**
     * Sets the value of the accountsUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountsUpdated(Integer value) {
        this.accountsUpdated = value;
    }

}
