
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
 *         &lt;element ref="{}PRIMARY-ACCOUNTS-SUMMARY" minOccurs="0"/>
 *         &lt;element ref="{}SECONDARY-ACCOUNTS-SUMMARY" minOccurs="0"/>
 *         &lt;element ref="{}ACC-DERIVED-ATTRIBUTES" minOccurs="0"/>
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
@XmlRootElement(name = "ACCOUNTS-SUMMARY")
public class ACCOUNTSSUMMARY {

    @XmlElement(name = "PRIMARY-ACCOUNTS-SUMMARY")
    protected PRIMARYACCOUNTSSUMMARY primaryaccountssummary;
    @XmlElement(name = "SECONDARY-ACCOUNTS-SUMMARY")
    protected SECONDARYACCOUNTSSUMMARY secondaryaccountssummary;
    @XmlElement(name = "ACC-DERIVED-ATTRIBUTES")
    protected ACCDERIVEDATTRIBUTES accderivedattributes;

    /**
     * Gets the value of the primaryaccountssummary property.
     * 
     * @return
     *     possible object is
     *     {@link PRIMARYACCOUNTSSUMMARY }
     *     
     */
    public PRIMARYACCOUNTSSUMMARY getPRIMARYACCOUNTSSUMMARY() {
        return primaryaccountssummary;
    }

    /**
     * Sets the value of the primaryaccountssummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link PRIMARYACCOUNTSSUMMARY }
     *     
     */
    public void setPRIMARYACCOUNTSSUMMARY(PRIMARYACCOUNTSSUMMARY value) {
        this.primaryaccountssummary = value;
    }

    /**
     * Gets the value of the secondaryaccountssummary property.
     * 
     * @return
     *     possible object is
     *     {@link SECONDARYACCOUNTSSUMMARY }
     *     
     */
    public SECONDARYACCOUNTSSUMMARY getSECONDARYACCOUNTSSUMMARY() {
        return secondaryaccountssummary;
    }

    /**
     * Sets the value of the secondaryaccountssummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link SECONDARYACCOUNTSSUMMARY }
     *     
     */
    public void setSECONDARYACCOUNTSSUMMARY(SECONDARYACCOUNTSSUMMARY value) {
        this.secondaryaccountssummary = value;
    }

    /**
     * Gets the value of the accderivedattributes property.
     * 
     * @return
     *     possible object is
     *     {@link ACCDERIVEDATTRIBUTES }
     *     
     */
    public ACCDERIVEDATTRIBUTES getACCDERIVEDATTRIBUTES() {
        return accderivedattributes;
    }

    /**
     * Sets the value of the accderivedattributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACCDERIVEDATTRIBUTES }
     *     
     */
    public void setACCDERIVEDATTRIBUTES(ACCDERIVEDATTRIBUTES value) {
        this.accderivedattributes = value;
    }

}
