package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FamilyInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FamilyInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AdditionalNameInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}AdditionalNameTypeDetails" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="NoOfDependents" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FamilyInfo", propOrder = {
    "additionalNameInfo",
    "noOfDependents"
})
public class FamilyInfo {

    @XmlElement(name = "AdditionalNameInfo")
    protected List<AdditionalNameTypeDetails> additionalNameInfo;
    @XmlElement(name = "NoOfDependents")
    protected Integer noOfDependents;

    /**
     * Gets the value of the additionalNameInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalNameInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalNameInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalNameTypeDetails }
     * 
     * 
     */
    public List<AdditionalNameTypeDetails> getAdditionalNameInfo() {
        if (additionalNameInfo == null) {
            additionalNameInfo = new ArrayList<AdditionalNameTypeDetails>();
        }
        return this.additionalNameInfo;
    }

    /**
     * Gets the value of the noOfDependents property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoOfDependents() {
        return noOfDependents;
    }

    /**
     * Sets the value of the noOfDependents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoOfDependents(Integer value) {
        this.noOfDependents = value;
    }

}
