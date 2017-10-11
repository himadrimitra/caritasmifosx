
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
 *         &lt;element ref="{}INDV-REPORT"/>
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
    "indvreport"
})
@XmlRootElement(name = "INDV-REPORTS")
public class INDVREPORTS {

    @XmlElement(name = "INDV-REPORT", required = true)
    protected INDVREPORT indvreport;

    /**
     * Gets the value of the indvreport property.
     * 
     * @return
     *     possible object is
     *     {@link INDVREPORT }
     *     
     */
    public INDVREPORT getINDVREPORT() {
        return indvreport;
    }

    /**
     * Sets the value of the indvreport property.
     * 
     * @param value
     *     allowed object is
     *     {@link INDVREPORT }
     *     
     */
    public void setINDVREPORT(INDVREPORT value) {
        this.indvreport = value;
    }

}
