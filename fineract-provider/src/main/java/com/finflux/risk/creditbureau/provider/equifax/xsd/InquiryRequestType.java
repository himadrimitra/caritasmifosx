package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InquiryRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InquiryRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequestHeader" type="{http://services.equifax.com/eport/ws/schemas/1.0}RequestHeaderType"/&gt;
 *         &lt;element name="RequestAccountDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountInputType" minOccurs="0"/&gt;
 *         &lt;element name="InquiryCommonAccountDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}InquiryCommonAccountDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="RequestBody" type="{http://services.equifax.com/eport/ws/schemas/1.0}RequestBodyType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InquiryRequestType", propOrder = {
    "requestHeader",
    "requestAccountDetails",
    "inquiryCommonAccountDetails",
    "requestBody"
})
@XmlRootElement(name = "REQUEST")
public class InquiryRequestType {

    @XmlElement(name = "RequestHeader", required = true)
    protected RequestHeaderType requestHeader;
    @XmlElement(name = "RequestAccountDetails")
    protected AccountInputType requestAccountDetails;
    @XmlElement(name = "InquiryCommonAccountDetails")
    protected InquiryCommonAccountDetailsType inquiryCommonAccountDetails;
    @XmlElement(name = "RequestBody", required = true)
    protected RequestBodyType requestBody;

    /**
     * Gets the value of the requestHeader property.
     * 
     * @return
     *     possible object is
     *     {@link RequestHeaderType }
     *     
     */
    public RequestHeaderType getRequestHeader() {
        return requestHeader;
    }

    /**
     * Sets the value of the requestHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestHeaderType }
     *     
     */
    public void setRequestHeader(RequestHeaderType value) {
        this.requestHeader = value;
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
     * Gets the value of the requestBody property.
     * 
     * @return
     *     possible object is
     *     {@link RequestBodyType }
     *     
     */
    public RequestBodyType getRequestBody() {
        return requestBody;
    }

    /**
     * Sets the value of the requestBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestBodyType }
     *     
     */
    public void setRequestBody(RequestBodyType value) {
        this.requestBody = value;
    }

}
