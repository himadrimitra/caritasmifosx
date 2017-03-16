package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PersonalInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonalInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://services.equifax.com/eport/ws/schemas/1.0}NameType" minOccurs="0"/&gt;
 *         &lt;element name="PreviousName" type="{http://services.equifax.com/eport/ws/schemas/1.0}NameType" maxOccurs="10" minOccurs="0"/&gt;
 *         &lt;element name="AliasNameInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}AliasNameInfoType" minOccurs="0"/&gt;
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="Gender" type="{http://services.equifax.com/eport/ws/schemas/1.0}GenderTypeCode" minOccurs="0"/&gt;
 *         &lt;element name="Age" type="{http://services.equifax.com/eport/ws/schemas/1.0}AgeInfo" minOccurs="0"/&gt;
 *         &lt;element name="PlaceOfBirthInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}PlaceOfBirthInfoType" minOccurs="0"/&gt;
 *         &lt;element name="TotalIncome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Occupation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MaritalStatus" type="{http://services.equifax.com/eport/ws/schemas/1.0}MaritalStatusOptions" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonalInfoType", propOrder = {
    "name",
    "previousName",
    "aliasNameInfo",
    "dateOfBirth",
    "gender",
    "age",
    "placeOfBirthInfo",
    "totalIncome",
    "occupation",
    "maritalStatus"
})
public class PersonalInfoType {

    @XmlElement(name = "Name")
    protected NameType name;
    @XmlElement(name = "PreviousName")
    protected List<NameType> previousName;
    @XmlElement(name = "AliasNameInfo")
    protected AliasNameInfoType aliasNameInfo;
    @XmlElement(name = "DateOfBirth")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(name = "Gender")
    @XmlSchemaType(name = "string")
    protected GenderTypeCode gender;
    @XmlElement(name = "Age")
    protected AgeInfo age;
    @XmlElement(name = "PlaceOfBirthInfo")
    protected PlaceOfBirthInfoType placeOfBirthInfo;
    @XmlElement(name = "TotalIncome")
    protected String totalIncome;
    @XmlElement(name = "Occupation")
    protected String occupation;
    @XmlElement(name = "MaritalStatus")
    @XmlSchemaType(name = "string")
    protected MaritalStatusOptions maritalStatus;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link NameType }
     *     
     */
    public NameType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameType }
     *     
     */
    public void setName(NameType value) {
        this.name = value;
    }

    /**
     * Gets the value of the previousName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the previousName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPreviousName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameType }
     * 
     * 
     */
    public List<NameType> getPreviousName() {
        if (previousName == null) {
            previousName = new ArrayList<NameType>();
        }
        return this.previousName;
    }

    /**
     * Gets the value of the aliasNameInfo property.
     * 
     * @return
     *     possible object is
     *     {@link AliasNameInfoType }
     *     
     */
    public AliasNameInfoType getAliasNameInfo() {
        return aliasNameInfo;
    }

    /**
     * Sets the value of the aliasNameInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AliasNameInfoType }
     *     
     */
    public void setAliasNameInfo(AliasNameInfoType value) {
        this.aliasNameInfo = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link GenderTypeCode }
     *     
     */
    public GenderTypeCode getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderTypeCode }
     *     
     */
    public void setGender(GenderTypeCode value) {
        this.gender = value;
    }

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link AgeInfo }
     *     
     */
    public AgeInfo getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgeInfo }
     *     
     */
    public void setAge(AgeInfo value) {
        this.age = value;
    }

    /**
     * Gets the value of the placeOfBirthInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PlaceOfBirthInfoType }
     *     
     */
    public PlaceOfBirthInfoType getPlaceOfBirthInfo() {
        return placeOfBirthInfo;
    }

    /**
     * Sets the value of the placeOfBirthInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlaceOfBirthInfoType }
     *     
     */
    public void setPlaceOfBirthInfo(PlaceOfBirthInfoType value) {
        this.placeOfBirthInfo = value;
    }

    /**
     * Gets the value of the totalIncome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalIncome() {
        return totalIncome;
    }

    /**
     * Sets the value of the totalIncome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalIncome(String value) {
        this.totalIncome = value;
    }

    /**
     * Gets the value of the occupation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Sets the value of the occupation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupation(String value) {
        this.occupation = value;
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

}
