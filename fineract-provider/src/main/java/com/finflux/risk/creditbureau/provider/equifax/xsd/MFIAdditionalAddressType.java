package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MFIAdditionalAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MFIAdditionalAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AdditionalAddressDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}MFIAddlAdrsDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://services.equifax.com/eport/ws/schemas/1.0}MFIAdditionalAddressAttributes"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MFIAdditionalAddressType", propOrder = {
    "additionalAddressDetails"
})
public class MFIAdditionalAddressType {

    @XmlElement(name = "AdditionalAddressDetails")
    protected List<MFIAddlAdrsDetailsType> additionalAddressDetails;
    @XmlAttribute(name = "seq")
    protected Integer seq;

    /**
     * Gets the value of the additionalAddressDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalAddressDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalAddressDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MFIAddlAdrsDetailsType }
     * 
     * 
     */
    public List<MFIAddlAdrsDetailsType> getAdditionalAddressDetails() {
        if (additionalAddressDetails == null) {
            additionalAddressDetails = new ArrayList<MFIAddlAdrsDetailsType>();
        }
        return this.additionalAddressDetails;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSeq(Integer value) {
        this.seq = value;
    }

}
