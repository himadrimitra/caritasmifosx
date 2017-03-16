package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for vidVoterResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vidVoterResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="voterRequest" type="{http://services.equifax.com/eport/ws/schemas/1.0}voterRequest" minOccurs="0"/&gt;
 *         &lt;element name="voterResponse" type="{http://services.equifax.com/eport/ws/schemas/1.0}voterResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vidVoterResponse", propOrder = {
    "voterRequest",
    "voterResponse"
})
public class VidVoterResponse {

    protected VoterRequest voterRequest;
    protected VoterResponse voterResponse;

    /**
     * Gets the value of the voterRequest property.
     * 
     * @return
     *     possible object is
     *     {@link VoterRequest }
     *     
     */
    public VoterRequest getVoterRequest() {
        return voterRequest;
    }

    /**
     * Sets the value of the voterRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoterRequest }
     *     
     */
    public void setVoterRequest(VoterRequest value) {
        this.voterRequest = value;
    }

    /**
     * Gets the value of the voterResponse property.
     * 
     * @return
     *     possible object is
     *     {@link VoterResponse }
     *     
     */
    public VoterResponse getVoterResponse() {
        return voterResponse;
    }

    /**
     * Sets the value of the voterResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoterResponse }
     *     
     */
    public void setVoterResponse(VoterResponse value) {
        this.voterResponse = value;
    }

}
