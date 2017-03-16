package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PANId" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PassportID" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DriverLicence" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="VoterID" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="NationalIDCard" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RationCard" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="IDCard" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PhotoCreditCard" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="IDOther" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentificationType", propOrder = {
    "panId",
    "passportID",
    "driverLicence",
    "voterID",
    "nationalIDCard",
    "rationCard",
    "idCard",
    "photoCreditCard",
    "idOther"
})
public class IdentificationType {

    @XmlElement(name = "PANId")
    protected List<IDType> panId;
    @XmlElement(name = "PassportID")
    protected List<IDType> passportID;
    @XmlElement(name = "DriverLicence")
    protected List<IDType> driverLicence;
    @XmlElement(name = "VoterID")
    protected List<IDType> voterID;
    @XmlElement(name = "NationalIDCard")
    protected List<IDType> nationalIDCard;
    @XmlElement(name = "RationCard")
    protected List<IDType> rationCard;
    @XmlElement(name = "IDCard")
    protected List<IDType> idCard;
    @XmlElement(name = "PhotoCreditCard")
    protected List<IDType> photoCreditCard;
    @XmlElement(name = "IDOther")
    protected List<IDType> idOther;

    /**
     * Gets the value of the panId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the panId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPANId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getPANId() {
        if (panId == null) {
            panId = new ArrayList<IDType>();
        }
        return this.panId;
    }

    /**
     * Gets the value of the passportID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passportID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassportID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getPassportID() {
        if (passportID == null) {
            passportID = new ArrayList<IDType>();
        }
        return this.passportID;
    }

    /**
     * Gets the value of the driverLicence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driverLicence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriverLicence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getDriverLicence() {
        if (driverLicence == null) {
            driverLicence = new ArrayList<IDType>();
        }
        return this.driverLicence;
    }

    /**
     * Gets the value of the voterID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the voterID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVoterID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getVoterID() {
        if (voterID == null) {
            voterID = new ArrayList<IDType>();
        }
        return this.voterID;
    }

    /**
     * Gets the value of the nationalIDCard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nationalIDCard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNationalIDCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getNationalIDCard() {
        if (nationalIDCard == null) {
            nationalIDCard = new ArrayList<IDType>();
        }
        return this.nationalIDCard;
    }

    /**
     * Gets the value of the rationCard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rationCard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRationCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getRationCard() {
        if (rationCard == null) {
            rationCard = new ArrayList<IDType>();
        }
        return this.rationCard;
    }

    /**
     * Gets the value of the idCard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idCard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIDCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getIDCard() {
        if (idCard == null) {
            idCard = new ArrayList<IDType>();
        }
        return this.idCard;
    }

    /**
     * Gets the value of the photoCreditCard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the photoCreditCard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhotoCreditCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getPhotoCreditCard() {
        if (photoCreditCard == null) {
            photoCreditCard = new ArrayList<IDType>();
        }
        return this.photoCreditCard;
    }

    /**
     * Gets the value of the idOther property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idOther property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIDOther().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getIDOther() {
        if (idOther == null) {
            idOther = new ArrayList<IDType>();
        }
        return this.idOther;
    }

}
