package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for nsdlRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nsdlRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://services.equifax.com/eport/ws/schemas/1.0}request"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PANNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nsdlRequest", propOrder = {
    "panNumber"
})
public class NsdlRequest
    extends Request
{

    @XmlElement(name = "PANNumber")
    protected String panNumber;

    /**
     * Gets the value of the panNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPANNumber() {
        return panNumber;
    }

    /**
     * Sets the value of the panNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPANNumber(String value) {
        this.panNumber = value;
    }

}
