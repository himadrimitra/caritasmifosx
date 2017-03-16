package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for vidUidaiResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vidUidaiResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="uidaiRequest" type="{http://services.equifax.com/eport/ws/schemas/1.0}uidaiRequest" minOccurs="0"/&gt;
 *         &lt;element name="uidaiResponse" type="{http://services.equifax.com/eport/ws/schemas/1.0}uidaiResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vidUidaiResponse", propOrder = {
    "uidaiRequest",
    "uidaiResponse"
})
public class VidUidaiResponse {

    protected UidaiRequest uidaiRequest;
    protected UidaiResponse uidaiResponse;

    /**
     * Gets the value of the uidaiRequest property.
     * 
     * @return
     *     possible object is
     *     {@link UidaiRequest }
     *     
     */
    public UidaiRequest getUidaiRequest() {
        return uidaiRequest;
    }

    /**
     * Sets the value of the uidaiRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link UidaiRequest }
     *     
     */
    public void setUidaiRequest(UidaiRequest value) {
        this.uidaiRequest = value;
    }

    /**
     * Gets the value of the uidaiResponse property.
     * 
     * @return
     *     possible object is
     *     {@link UidaiResponse }
     *     
     */
    public UidaiResponse getUidaiResponse() {
        return uidaiResponse;
    }

    /**
     * Sets the value of the uidaiResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link UidaiResponse }
     *     
     */
    public void setUidaiResponse(UidaiResponse value) {
        this.uidaiResponse = value;
    }

}
