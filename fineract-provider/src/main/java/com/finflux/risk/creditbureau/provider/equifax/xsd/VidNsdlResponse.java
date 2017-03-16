package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for vidNsdlResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vidNsdlResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nsdlRequest" type="{http://services.equifax.com/eport/ws/schemas/1.0}nsdlRequest" minOccurs="0"/&gt;
 *         &lt;element name="nsdlResponse" type="{http://services.equifax.com/eport/ws/schemas/1.0}nsdlResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vidNsdlResponse", propOrder = {
    "nsdlRequest",
    "nsdlResponse"
})
public class VidNsdlResponse {

    protected NsdlRequest nsdlRequest;
    protected NsdlResponse nsdlResponse;

    /**
     * Gets the value of the nsdlRequest property.
     * 
     * @return
     *     possible object is
     *     {@link NsdlRequest }
     *     
     */
    public NsdlRequest getNsdlRequest() {
        return nsdlRequest;
    }

    /**
     * Sets the value of the nsdlRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link NsdlRequest }
     *     
     */
    public void setNsdlRequest(NsdlRequest value) {
        this.nsdlRequest = value;
    }

    /**
     * Gets the value of the nsdlResponse property.
     * 
     * @return
     *     possible object is
     *     {@link NsdlResponse }
     *     
     */
    public NsdlResponse getNsdlResponse() {
        return nsdlResponse;
    }

    /**
     * Sets the value of the nsdlResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link NsdlResponse }
     *     
     */
    public void setNsdlResponse(NsdlResponse value) {
        this.nsdlResponse = value;
    }

}
