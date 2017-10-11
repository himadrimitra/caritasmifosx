package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlossaryInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlossaryInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AccountStatus" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountStatusType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PaymentHistoryStatus" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountStatusType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="AssetClassificationStatus" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountStatusType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SuitFiledStatus" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountStatusType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlossaryInfoType", propOrder = {
    "accountStatus",
    "paymentHistoryStatus",
    "assetClassificationStatus",
    "suitFiledStatus"
})
public class GlossaryInfoType {

    @XmlElement(name = "AccountStatus")
    protected List<AccountStatusType> accountStatus;
    @XmlElement(name = "PaymentHistoryStatus")
    protected List<AccountStatusType> paymentHistoryStatus;
    @XmlElement(name = "AssetClassificationStatus")
    protected List<AccountStatusType> assetClassificationStatus;
    @XmlElement(name = "SuitFiledStatus")
    protected List<AccountStatusType> suitFiledStatus;

    /**
     * Gets the value of the accountStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountStatusType }
     * 
     * 
     */
    public List<AccountStatusType> getAccountStatus() {
        if (accountStatus == null) {
            accountStatus = new ArrayList<AccountStatusType>();
        }
        return this.accountStatus;
    }

    /**
     * Gets the value of the paymentHistoryStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentHistoryStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentHistoryStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountStatusType }
     * 
     * 
     */
    public List<AccountStatusType> getPaymentHistoryStatus() {
        if (paymentHistoryStatus == null) {
            paymentHistoryStatus = new ArrayList<AccountStatusType>();
        }
        return this.paymentHistoryStatus;
    }

    /**
     * Gets the value of the assetClassificationStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assetClassificationStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssetClassificationStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountStatusType }
     * 
     * 
     */
    public List<AccountStatusType> getAssetClassificationStatus() {
        if (assetClassificationStatus == null) {
            assetClassificationStatus = new ArrayList<AccountStatusType>();
        }
        return this.assetClassificationStatus;
    }

    /**
     * Gets the value of the suitFiledStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the suitFiledStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSuitFiledStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountStatusType }
     * 
     * 
     */
    public List<AccountStatusType> getSuitFiledStatus() {
        if (suitFiledStatus == null) {
            suitFiledStatus = new ArrayList<AccountStatusType>();
        }
        return this.suitFiledStatus;
    }

}
