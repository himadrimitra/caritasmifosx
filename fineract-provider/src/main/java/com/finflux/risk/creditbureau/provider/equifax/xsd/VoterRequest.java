package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voterRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voterRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://services.equifax.com/eport/ws/schemas/1.0}request"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="voterId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voterRequest", propOrder = {
    "voterId"
})
public class VoterRequest
    extends Request
{

    protected String voterId;

    /**
     * Gets the value of the voterId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoterId() {
        return voterId;
    }

    /**
     * Sets the value of the voterId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoterId(String value) {
        this.voterId = value;
    }

}