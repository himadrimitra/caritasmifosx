package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for responseBody complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="responseBody"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vidNsdlResponses" type="{http://services.equifax.com/eport/ws/schemas/1.0}vidNsdlResponse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="vidUidaiResponses" type="{http://services.equifax.com/eport/ws/schemas/1.0}vidUidaiResponse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="vidVoterResponses" type="{http://services.equifax.com/eport/ws/schemas/1.0}vidVoterResponse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "responseBody", propOrder = {
    "vidNsdlResponses",
    "vidUidaiResponses",
    "vidVoterResponses"
})
public class ResponseBody {

    @XmlElement(nillable = true)
    protected List<VidNsdlResponse> vidNsdlResponses;
    @XmlElement(nillable = true)
    protected List<VidUidaiResponse> vidUidaiResponses;
    @XmlElement(nillable = true)
    protected List<VidVoterResponse> vidVoterResponses;

    /**
     * Gets the value of the vidNsdlResponses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vidNsdlResponses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVidNsdlResponses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VidNsdlResponse }
     * 
     * 
     */
    public List<VidNsdlResponse> getVidNsdlResponses() {
        if (vidNsdlResponses == null) {
            vidNsdlResponses = new ArrayList<VidNsdlResponse>();
        }
        return this.vidNsdlResponses;
    }

    /**
     * Gets the value of the vidUidaiResponses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vidUidaiResponses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVidUidaiResponses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VidUidaiResponse }
     * 
     * 
     */
    public List<VidUidaiResponse> getVidUidaiResponses() {
        if (vidUidaiResponses == null) {
            vidUidaiResponses = new ArrayList<VidUidaiResponse>();
        }
        return this.vidUidaiResponses;
    }

    /**
     * Gets the value of the vidVoterResponses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vidVoterResponses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVidVoterResponses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VidVoterResponse }
     * 
     * 
     */
    public List<VidVoterResponse> getVidVoterResponses() {
        if (vidVoterResponses == null) {
            vidVoterResponses = new ArrayList<VidVoterResponse>();
        }
        return this.vidVoterResponses;
    }

}
