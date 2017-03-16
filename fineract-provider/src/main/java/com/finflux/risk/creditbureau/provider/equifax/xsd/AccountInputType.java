package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccountInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccountInputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BranchIDMFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="KendraIDMFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountInputType", propOrder = {
    "accountNumber",
    "branchIDMFI",
    "kendraIDMFI"
})
public class AccountInputType {

    @XmlElement(name = "AccountNumber")
    protected String accountNumber;
    @XmlElement(name = "BranchIDMFI")
    protected String branchIDMFI;
    @XmlElement(name = "KendraIDMFI")
    protected String kendraIDMFI;
    @XmlAttribute(name = "seq")
    protected Integer seq;

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

}
