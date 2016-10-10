
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
 *         &lt;element ref="{}INQUIRY"/>
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
    "inquiry"
})
@XmlRootElement(name = "INQUIRY-STATUS")
public class INQUIRYSTATUS {

    @XmlElement(name = "INQUIRY", required = true)
    protected INQUIRY inquiry;

    /**
     * Gets the value of the inquiry property.
     * 
     * @return
     *     possible object is
     *     {@link INQUIRY }
     *     
     */
    public INQUIRY getINQUIRY() {
        return inquiry;
    }

    /**
     * Sets the value of the inquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link INQUIRY }
     *     
     */
    public void setINQUIRY(INQUIRY value) {
        this.inquiry = value;
    }

}
