package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RequestBodyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestBodyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InquiryPurpose" type="{http://services.equifax.com/eport/ws/schemas/1.0}InquiryPurposeOptions"/&gt;
 *         &lt;element name="TransactionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="AdditionalSearchField" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FullName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;pattern value="([a-z A-Z.,])+"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="FirstName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *               &lt;pattern value="([a-z A-Z.,])+"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="MiddleName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *               &lt;pattern value="([a-z A-Z.,])+"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LastName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *               &lt;pattern value="([a-z A-Z.,])+"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="FamilyDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}FamilyInfo" minOccurs="0"/&gt;
 *         &lt;element name="AdditionalId1" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AdditionalId2" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AddrLine1" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="220"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Street" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Locality1" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Locality2" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="City" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="State" type="{http://services.equifax.com/eport/ws/schemas/1.0}StateCodeOptions" minOccurs="0"/&gt;
 *         &lt;element name="Postal" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="6"/&gt;
 *               &lt;pattern value="([0-9])*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="InquiryAddresses" type="{http://services.equifax.com/eport/ws/schemas/1.0}InquiryCommonInputAddressType" minOccurs="0"/&gt;
 *         &lt;element name="InquiryPhones" type="{http://services.equifax.com/eport/ws/schemas/1.0}InquiryCommonInputPhoneType" minOccurs="0"/&gt;
 *         &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="MaritalStatus" type="{http://services.equifax.com/eport/ws/schemas/1.0}MaritalStatusOptions" minOccurs="0"/&gt;
 *         &lt;element name="Gender" type="{http://services.equifax.com/eport/ws/schemas/1.0}GenderOptions" minOccurs="0"/&gt;
 *         &lt;element name="NationalIdCard" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RationCard" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element ref="{http://services.equifax.com/eport/ws/schemas/1.0}PANId" minOccurs="0"/&gt;
 *         &lt;element ref="{http://services.equifax.com/eport/ws/schemas/1.0}PassportId" minOccurs="0"/&gt;
 *         &lt;element ref="{http://services.equifax.com/eport/ws/schemas/1.0}VoterId" minOccurs="0"/&gt;
 *         &lt;element ref="{http://services.equifax.com/eport/ws/schemas/1.0}HomePhone" minOccurs="0"/&gt;
 *         &lt;element ref="{http://services.equifax.com/eport/ws/schemas/1.0}MobilePhone" minOccurs="0"/&gt;
 *         &lt;element name="DriverLicense" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RequestAccountDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountInputType" minOccurs="0"/&gt;
 *         &lt;element name="InquiryCommonAccountDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}InquiryCommonAccountDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="InquiryFieldsDsv" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4000"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestBodyType", propOrder = {
    "inquiryPurpose",
    "transactionAmount",
    "additionalSearchField",
    "fullName",
    "firstName",
    "middleName",
    "lastName",
    "familyDetails",
    "additionalId1",
    "additionalId2",
    "addrLine1",
    "street",
    "locality1",
    "locality2",
    "city",
    "state",
    "postal",
    "inquiryAddresses",
    "inquiryPhones",
    "dob",
    "maritalStatus",
    "gender",
    "nationalIdCard",
    "rationCard",
    "panId",
    "passportId",
    "voterId",
    "homePhone",
    "mobilePhone",
    "driverLicense",
    "requestAccountDetails",
    "inquiryCommonAccountDetails",
    "inquiryFieldsDsv"
})
public class RequestBodyType {

    @XmlElement(name = "InquiryPurpose", required = true)
    protected String inquiryPurpose;
    @XmlElement(name = "TransactionAmount")
    protected BigDecimal transactionAmount;
    @XmlElement(name = "AdditionalSearchField")
    protected String additionalSearchField;
    @XmlElement(name = "FullName")
    protected String fullName;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "MiddleName")
    protected String middleName;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "FamilyDetails")
    protected FamilyInfo familyDetails;
    @XmlElement(name = "AdditionalId1")
    protected String additionalId1;
    @XmlElement(name = "AdditionalId2")
    protected String additionalId2;
    @XmlElement(name = "AddrLine1")
    protected String addrLine1;
    @XmlElement(name = "Street")
    protected String street;
    @XmlElement(name = "Locality1")
    protected String locality1;
    @XmlElement(name = "Locality2")
    protected String locality2;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "State")
    @XmlSchemaType(name = "string")
    protected StateCodeOptions state;
    @XmlElement(name = "Postal")
    protected String postal;
    @XmlElement(name = "InquiryAddresses")
    protected InquiryCommonInputAddressType inquiryAddresses;
    @XmlElement(name = "InquiryPhones")
    protected InquiryCommonInputPhoneType inquiryPhones;
    @XmlElement(name = "DOB", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dob;
    @XmlElement(name = "MaritalStatus")
    @XmlSchemaType(name = "string")
    protected MaritalStatusOptions maritalStatus;
    @XmlElement(name = "Gender")
    protected String gender;
    @XmlElement(name = "NationalIdCard")
    protected String nationalIdCard;
    @XmlElement(name = "RationCard")
    protected String rationCard;
    @XmlElement(name = "PANId")
    protected String panId;
    @XmlElement(name = "PassportId")
    protected String passportId;
    @XmlElement(name = "VoterId")
    protected String voterId;
    @XmlElement(name = "HomePhone")
    protected LandlineType homePhone;
    @XmlElement(name = "MobilePhone")
    protected String mobilePhone;
    @XmlElement(name = "DriverLicense")
    protected String driverLicense;
    @XmlElement(name = "RequestAccountDetails")
    protected AccountInputType requestAccountDetails;
    @XmlElement(name = "InquiryCommonAccountDetails")
    protected InquiryCommonAccountDetailsType inquiryCommonAccountDetails;
    @XmlElement(name = "InquiryFieldsDsv")
    protected String inquiryFieldsDsv;

    /**
     * Gets the value of the inquiryPurpose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInquiryPurpose() {
        return inquiryPurpose;
    }

    /**
     * Sets the value of the inquiryPurpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInquiryPurpose(String value) {
        this.inquiryPurpose = value;
    }

    /**
     * Gets the value of the transactionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Sets the value of the transactionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTransactionAmount(BigDecimal value) {
        this.transactionAmount = value;
    }

    /**
     * Gets the value of the additionalSearchField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalSearchField() {
        return additionalSearchField;
    }

    /**
     * Sets the value of the additionalSearchField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalSearchField(String value) {
        this.additionalSearchField = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
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
     * Gets the value of the additionalId1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalId1() {
        return additionalId1;
    }

    /**
     * Sets the value of the additionalId1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalId1(String value) {
        this.additionalId1 = value;
    }

    /**
     * Gets the value of the additionalId2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalId2() {
        return additionalId2;
    }

    /**
     * Sets the value of the additionalId2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalId2(String value) {
        this.additionalId2 = value;
    }

    /**
     * Gets the value of the addrLine1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddrLine1() {
        return addrLine1;
    }

    /**
     * Sets the value of the addrLine1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddrLine1(String value) {
        this.addrLine1 = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the locality1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocality1() {
        return locality1;
    }

    /**
     * Sets the value of the locality1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocality1(String value) {
        this.locality1 = value;
    }

    /**
     * Gets the value of the locality2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocality2() {
        return locality2;
    }

    /**
     * Sets the value of the locality2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocality2(String value) {
        this.locality2 = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link StateCodeOptions }
     *     
     */
    public StateCodeOptions getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateCodeOptions }
     *     
     */
    public void setState(StateCodeOptions value) {
        this.state = value;
    }

    /**
     * Gets the value of the postal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostal() {
        return postal;
    }

    /**
     * Sets the value of the postal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostal(String value) {
        this.postal = value;
    }

    /**
     * Gets the value of the inquiryAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link InquiryCommonInputAddressType }
     *     
     */
    public InquiryCommonInputAddressType getInquiryAddresses() {
        return inquiryAddresses;
    }

    /**
     * Sets the value of the inquiryAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link InquiryCommonInputAddressType }
     *     
     */
    public void setInquiryAddresses(InquiryCommonInputAddressType value) {
        this.inquiryAddresses = value;
    }

    /**
     * Gets the value of the inquiryPhones property.
     * 
     * @return
     *     possible object is
     *     {@link InquiryCommonInputPhoneType }
     *     
     */
    public InquiryCommonInputPhoneType getInquiryPhones() {
        return inquiryPhones;
    }

    /**
     * Sets the value of the inquiryPhones property.
     * 
     * @param value
     *     allowed object is
     *     {@link InquiryCommonInputPhoneType }
     *     
     */
    public void setInquiryPhones(InquiryCommonInputPhoneType value) {
        this.inquiryPhones = value;
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDOB() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDOB(XMLGregorianCalendar value) {
        this.dob = value;
    }

    /**
     * Gets the value of the maritalStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MaritalStatusOptions }
     *     
     */
    public MaritalStatusOptions getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the value of the maritalStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaritalStatusOptions }
     *     
     */
    public void setMaritalStatus(MaritalStatusOptions value) {
        this.maritalStatus = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the nationalIdCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationalIdCard() {
        return nationalIdCard;
    }

    /**
     * Sets the value of the nationalIdCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationalIdCard(String value) {
        this.nationalIdCard = value;
    }

    /**
     * Gets the value of the rationCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRationCard() {
        return rationCard;
    }

    /**
     * Sets the value of the rationCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRationCard(String value) {
        this.rationCard = value;
    }

    /**
     * Gets the value of the panId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPANId() {
        return panId;
    }

    /**
     * Sets the value of the panId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPANId(String value) {
        this.panId = value;
    }

    /**
     * Gets the value of the passportId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassportId() {
        return passportId;
    }

    /**
     * Sets the value of the passportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassportId(String value) {
        this.passportId = value;
    }

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

    /**
     * Gets the value of the homePhone property.
     * 
     * @return
     *     possible object is
     *     {@link LandlineType }
     *     
     */
    public LandlineType getHomePhone() {
        return homePhone;
    }

    /**
     * Sets the value of the homePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link LandlineType }
     *     
     */
    public void setHomePhone(LandlineType value) {
        this.homePhone = value;
    }

    /**
     * Gets the value of the mobilePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Sets the value of the mobilePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhone(String value) {
        this.mobilePhone = value;
    }

    /**
     * Gets the value of the driverLicense property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverLicense() {
        return driverLicense;
    }

    /**
     * Sets the value of the driverLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverLicense(String value) {
        this.driverLicense = value;
    }

    /**
     * Gets the value of the requestAccountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AccountInputType }
     *     
     */
    public AccountInputType getRequestAccountDetails() {
        return requestAccountDetails;
    }

    /**
     * Sets the value of the requestAccountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountInputType }
     *     
     */
    public void setRequestAccountDetails(AccountInputType value) {
        this.requestAccountDetails = value;
    }

    /**
     * Gets the value of the inquiryCommonAccountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link InquiryCommonAccountDetailsType }
     *     
     */
    public InquiryCommonAccountDetailsType getInquiryCommonAccountDetails() {
        return inquiryCommonAccountDetails;
    }

    /**
     * Sets the value of the inquiryCommonAccountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link InquiryCommonAccountDetailsType }
     *     
     */
    public void setInquiryCommonAccountDetails(InquiryCommonAccountDetailsType value) {
        this.inquiryCommonAccountDetails = value;
    }

    /**
     * Gets the value of the inquiryFieldsDsv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInquiryFieldsDsv() {
        return inquiryFieldsDsv;
    }

    /**
     * Sets the value of the inquiryFieldsDsv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInquiryFieldsDsv(String value) {
        this.inquiryFieldsDsv = value;
    }

}
