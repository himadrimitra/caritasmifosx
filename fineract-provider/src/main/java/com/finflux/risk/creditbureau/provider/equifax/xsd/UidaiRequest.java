
package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uidaiRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uidaiRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://services.equifax.com/eport/ws/schemas/1.0}request"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UIDAI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uidaiRequest", propOrder = {
    "uidai",
    "name"
})
public class UidaiRequest
    extends Request
{

    @XmlElement(name = "UIDAI")
    protected String uidai;
    protected String name;

    /**
     * Gets the value of the uidai property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUIDAI() {
        return uidai;
    }

    /**
     * Sets the value of the uidai property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUIDAI(String value) {
        this.uidai = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
