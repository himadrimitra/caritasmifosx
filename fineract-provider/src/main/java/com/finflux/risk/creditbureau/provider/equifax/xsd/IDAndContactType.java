package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IDAndContactType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IDAndContactType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PersonalInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}PersonalInfoType" minOccurs="0"/&gt;
 *         &lt;element name="IdentityInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}IdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="AddressInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}AddressType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="FamilyDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}FamilyInfo" minOccurs="0"/&gt;
 *         &lt;element name="PhoneInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}PhoneType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="EmailAddressInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}EmailAddressType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IDAndContactType", propOrder = {
    "personalInfo",
    "identityInfo",
    "addressInfo",
    "familyDetails",
    "phoneInfo",
    "emailAddressInfo"
})
public class IDAndContactType {

    @XmlElement(name = "PersonalInfo")
    protected PersonalInfoType personalInfo;
    @XmlElement(name = "IdentityInfo")
    protected IdentificationType identityInfo;
    @XmlElement(name = "AddressInfo")
    protected List<AddressType> addressInfo;
    @XmlElement(name = "FamilyDetails")
    protected FamilyInfo familyDetails;
    @XmlElement(name = "PhoneInfo")
    protected List<PhoneType> phoneInfo;
    @XmlElement(name = "EmailAddressInfo")
    protected List<EmailAddressType> emailAddressInfo;

    /**
     * Gets the value of the personalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PersonalInfoType }
     *     
     */
    public PersonalInfoType getPersonalInfo() {
        return personalInfo;
    }

    /**
     * Sets the value of the personalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonalInfoType }
     *     
     */
    public void setPersonalInfo(PersonalInfoType value) {
        this.personalInfo = value;
    }

    /**
     * Gets the value of the identityInfo property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificationType }
     *     
     */
    public IdentificationType getIdentityInfo() {
        return identityInfo;
    }

    /**
     * Sets the value of the identityInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificationType }
     *     
     */
    public void setIdentityInfo(IdentificationType value) {
        this.identityInfo = value;
    }

    /**
     * Gets the value of the addressInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressType }
     * 
     * 
     */
    public List<AddressType> getAddressInfo() {
        if (addressInfo == null) {
            addressInfo = new ArrayList<AddressType>();
        }
        return this.addressInfo;
    }

    /**
     * Gets the value of the familyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link FamilyInfo }
     *     
     */
    public FamilyInfo getFamilyDetails() {
        return familyDetails;
    }

    /**
     * Sets the value of the familyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link FamilyInfo }
     *     
     */
    public void setFamilyDetails(FamilyInfo value) {
        this.familyDetails = value;
    }

    /**
     * Gets the value of the phoneInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phoneInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhoneInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhoneType }
     * 
     * 
     */
    public List<PhoneType> getPhoneInfo() {
        if (phoneInfo == null) {
            phoneInfo = new ArrayList<PhoneType>();
        }
        return this.phoneInfo;
    }

    /**
     * Gets the value of the emailAddressInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the emailAddressInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmailAddressInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EmailAddressType }
     * 
     * 
     */
    public List<EmailAddressType> getEmailAddressInfo() {
        if (emailAddressInfo == null) {
            emailAddressInfo = new ArrayList<EmailAddressType>();
        }
        return this.emailAddressInfo;
    }

}
