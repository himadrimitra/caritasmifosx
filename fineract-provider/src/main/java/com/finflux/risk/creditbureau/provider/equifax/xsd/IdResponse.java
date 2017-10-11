package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for idResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="idResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="responseHeader" type="{http://services.equifax.com/eport/ws/schemas/1.0}responseHeader" minOccurs="0"/&gt;
 *         &lt;element name="responseBody" type="{http://services.equifax.com/eport/ws/schemas/1.0}responseBody" minOccurs="0"/&gt;
 *         &lt;element name="fault" type="{http://services.equifax.com/eport/ws/schemas/1.0}verifyIdServiceFault" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "idResponse", propOrder = {
    "responseHeader",
    "responseBody",
    "fault"
})
public class IdResponse {

    protected ResponseHeader responseHeader;
    protected ResponseBody responseBody;
    protected VerifyIdServiceFault fault;

    /**
     * Gets the value of the responseHeader property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseHeader }
     *     
     */
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    /**
     * Sets the value of the responseHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseHeader }
     *     
     */
    public void setResponseHeader(ResponseHeader value) {
        this.responseHeader = value;
    }

    /**
     * Gets the value of the responseBody property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseBody }
     *     
     */
    public ResponseBody getResponseBody() {
        return responseBody;
    }

    /**
     * Sets the value of the responseBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseBody }
     *     
     */
    public void setResponseBody(ResponseBody value) {
        this.responseBody = value;
    }

    /**
     * Gets the value of the fault property.
     * 
     * @return
     *     possible object is
     *     {@link VerifyIdServiceFault }
     *     
     */
    public VerifyIdServiceFault getFault() {
        return fault;
    }

    /**
     * Sets the value of the fault property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerifyIdServiceFault }
     *     
     */
    public void setFault(VerifyIdServiceFault value) {
        this.fault = value;
    }

}
