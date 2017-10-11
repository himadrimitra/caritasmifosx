
package com.finflux.risk.creditbureau.provider.highmark.xsd.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="PRIMARY-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-ACTIVE-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-OVERDUE-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-SANCTIONED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-SECURED-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-UNSECURED-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRIMARY-UNTAGGED-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "PRIMARY-ACCOUNTS-SUMMARY")
public class PRIMARYACCOUNTSSUMMARY {

    @XmlElement(name = "PRIMARY-NUMBER-OF-ACCOUNTS")
    protected String primarynumberofaccounts;
    @XmlElement(name = "PRIMARY-ACTIVE-NUMBER-OF-ACCOUNTS")
    protected String primaryactivenumberofaccounts;
    @XmlElement(name = "PRIMARY-OVERDUE-NUMBER-OF-ACCOUNTS")
    protected String primaryoverduenumberofaccounts;
    @XmlElement(name = "PRIMARY-CURRENT-BALANCE")
    protected String primarycurrentbalance;
    @XmlElement(name = "PRIMARY-SANCTIONED-AMOUNT")
    protected String primarysanctionedamount;
    @XmlElement(name = "PRIMARY-DISBURSED-AMOUNT")
    protected String primarydisbursedamount;
    @XmlElement(name = "PRIMARY-SECURED-NUMBER-OF-ACCOUNTS")
    protected String primarysecurednumberofaccounts;
    @XmlElement(name = "PRIMARY-UNSECURED-NUMBER-OF-ACCOUNTS")
    protected String primaryunsecurednumberofaccounts;
    @XmlElement(name = "PRIMARY-UNTAGGED-NUMBER-OF-ACCOUNTS")
    protected String primaryuntaggednumberofaccounts;

    /**
     * Gets the value of the primarynumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYNUMBEROFACCOUNTS() {
        return primarynumberofaccounts;
    }

    /**
     * Sets the value of the primarynumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYNUMBEROFACCOUNTS(String value) {
        this.primarynumberofaccounts = value;
    }

    /**
     * Gets the value of the primaryactivenumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYACTIVENUMBEROFACCOUNTS() {
        return primaryactivenumberofaccounts;
    }

    /**
     * Sets the value of the primaryactivenumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYACTIVENUMBEROFACCOUNTS(String value) {
        this.primaryactivenumberofaccounts = value;
    }

    /**
     * Gets the value of the primaryoverduenumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYOVERDUENUMBEROFACCOUNTS() {
        return primaryoverduenumberofaccounts;
    }

    /**
     * Sets the value of the primaryoverduenumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYOVERDUENUMBEROFACCOUNTS(String value) {
        this.primaryoverduenumberofaccounts = value;
    }

    /**
     * Gets the value of the primarycurrentbalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYCURRENTBALANCE() {
        return primarycurrentbalance;
    }

    /**
     * Sets the value of the primarycurrentbalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYCURRENTBALANCE(String value) {
        this.primarycurrentbalance = value;
    }

    /**
     * Gets the value of the primarysanctionedamount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYSANCTIONEDAMOUNT() {
        return primarysanctionedamount;
    }

    /**
     * Sets the value of the primarysanctionedamount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYSANCTIONEDAMOUNT(String value) {
        this.primarysanctionedamount = value;
    }

    /**
     * Gets the value of the primarydisbursedamount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYDISBURSEDAMOUNT() {
        return primarydisbursedamount;
    }

    /**
     * Sets the value of the primarydisbursedamount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYDISBURSEDAMOUNT(String value) {
        this.primarydisbursedamount = value;
    }

    /**
     * Gets the value of the primarysecurednumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYSECUREDNUMBEROFACCOUNTS() {
        return primarysecurednumberofaccounts;
    }

    /**
     * Sets the value of the primarysecurednumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYSECUREDNUMBEROFACCOUNTS(String value) {
        this.primarysecurednumberofaccounts = value;
    }

    /**
     * Gets the value of the primaryunsecurednumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYUNSECUREDNUMBEROFACCOUNTS() {
        return primaryunsecurednumberofaccounts;
    }

    /**
     * Sets the value of the primaryunsecurednumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYUNSECUREDNUMBEROFACCOUNTS(String value) {
        this.primaryunsecurednumberofaccounts = value;
    }

    /**
     * Gets the value of the primaryuntaggednumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMARYUNTAGGEDNUMBEROFACCOUNTS() {
        return primaryuntaggednumberofaccounts;
    }

    /**
     * Sets the value of the primaryuntaggednumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMARYUNTAGGEDNUMBEROFACCOUNTS(String value) {
        this.primaryuntaggednumberofaccounts = value;
    }

}
