
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
 *       &lt;sequence>
 *         &lt;element ref="{}LOAN-DETAILS"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "loandetails"
})
@XmlRootElement(name = "RESPONSE")
public class RESPONSE {

    @XmlElement(name = "LOAN-DETAILS", required = true)
    protected LOANDETAILS loandetails;

    /**
     * Gets the value of the loandetails property.
     * 
     * @return
     *     possible object is
     *     {@link LOANDETAILS }
     *     
     */
    public LOANDETAILS getLOANDETAILS() {
        return loandetails;
    }

    /**
     * Sets the value of the loandetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link LOANDETAILS }
     *     
     */
    public void setLOANDETAILS(LOANDETAILS value) {
        this.loandetails = value;
    }

}
