
package com.finflux.risk.creditbureau.provider.highmark.xsd.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{}HEADER"/>
 *         &lt;element ref="{}STATUS-DETAILS"/>
 *         &lt;element ref="{}REQUEST"/>
 *         &lt;element ref="{}PERSONAL-INFO-VARIATION" minOccurs="0"/>
 *         &lt;element ref="{}SECONDARY-MATCHES" minOccurs="0"/>
 *         &lt;element ref="{}ACCOUNTS-SUMMARY" minOccurs="0"/>
 *         &lt;element ref="{}RESPONSES" minOccurs="0"/>
 *         &lt;element ref="{}INDV-RESPONSES" minOccurs="0"/>
 *         &lt;element ref="{}GRP-RESPONSES" minOccurs="0"/>
 *         &lt;element ref="{}INQUIRY-HISTORY" minOccurs="0"/>
 *         &lt;element ref="{}COMMENTS" minOccurs="0"/>
 *         &lt;element ref="{}ALERTS" minOccurs="0"/>
 *         &lt;element ref="{}SCORES" minOccurs="0"/>
 *         &lt;element ref="{}PRINTABLE-REPORT" minOccurs="0"/>
 *         &lt;element ref="{}DERIVED-ATTRIBUTES" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "INDV-REPORT")
public class INDVREPORT {

    @XmlElement(name = "HEADER", required = true)
    protected HEADER header;
    @XmlElement(name = "STATUS-DETAILS", required = true)
    protected STATUSDETAILS statusdetails;
    @XmlElement(name = "REQUEST", required = true)
    protected REQUEST request;
    @XmlElement(name = "PERSONAL-INFO-VARIATION")
    protected PERSONALINFOVARIATION personalinfovariation;
    @XmlElement(name = "SECONDARY-MATCHES")
    protected SECONDARYMATCHES secondarymatches;
    @XmlElement(name = "ACCOUNTS-SUMMARY")
    protected ACCOUNTSSUMMARY accountssummary;
    @XmlElement(name = "RESPONSES")
    protected RESPONSES responses;
    @XmlElement(name = "INDV-RESPONSES")
    protected INDVRESPONSES indvresponses;
    @XmlElement(name = "GRP-RESPONSES")
    protected GRPRESPONSES grpresponses;
    @XmlElement(name = "INQUIRY-HISTORY")
    protected INQUIRYHISTORY inquiryhistory;
    @XmlElement(name = "COMMENTS")
    protected COMMENTS comments;
    @XmlElement(name = "ALERTS")
    protected ALERTS alerts;
    @XmlElement(name = "SCORES")
    protected SCORES scores;
    @XmlElement(name = "PRINTABLE-REPORT")
    protected PRINTABLEREPORT printablereport;
    @XmlElement(name = "DERIVED-ATTRIBUTES")
    protected DERIVEDATTRIBUTES derivedattributes;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link HEADER }
     *     
     */
    public HEADER getHEADER() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link HEADER }
     *     
     */
    public void setHEADER(HEADER value) {
        this.header = value;
    }

    /**
     * Gets the value of the statusdetails property.
     * 
     * @return
     *     possible object is
     *     {@link STATUSDETAILS }
     *     
     */
    public STATUSDETAILS getSTATUSDETAILS() {
        return statusdetails;
    }

    /**
     * Sets the value of the statusdetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link STATUSDETAILS }
     *     
     */
    public void setSTATUSDETAILS(STATUSDETAILS value) {
        this.statusdetails = value;
    }

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link REQUEST }
     *     
     */
    public REQUEST getREQUEST() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link REQUEST }
     *     
     */
    public void setREQUEST(REQUEST value) {
        this.request = value;
    }

    /**
     * Gets the value of the personalinfovariation property.
     * 
     * @return
     *     possible object is
     *     {@link PERSONALINFOVARIATION }
     *     
     */
    public PERSONALINFOVARIATION getPERSONALINFOVARIATION() {
        return personalinfovariation;
    }

    /**
     * Sets the value of the personalinfovariation property.
     * 
     * @param value
     *     allowed object is
     *     {@link PERSONALINFOVARIATION }
     *     
     */
    public void setPERSONALINFOVARIATION(PERSONALINFOVARIATION value) {
        this.personalinfovariation = value;
    }

    /**
     * Gets the value of the secondarymatches property.
     * 
     * @return
     *     possible object is
     *     {@link SECONDARYMATCHES }
     *     
     */
    public SECONDARYMATCHES getSECONDARYMATCHES() {
        return secondarymatches;
    }

    /**
     * Sets the value of the secondarymatches property.
     * 
     * @param value
     *     allowed object is
     *     {@link SECONDARYMATCHES }
     *     
     */
    public void setSECONDARYMATCHES(SECONDARYMATCHES value) {
        this.secondarymatches = value;
    }

    /**
     * Gets the value of the accountssummary property.
     * 
     * @return
     *     possible object is
     *     {@link ACCOUNTSSUMMARY }
     *     
     */
    public ACCOUNTSSUMMARY getACCOUNTSSUMMARY() {
        return accountssummary;
    }

    /**
     * Sets the value of the accountssummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACCOUNTSSUMMARY }
     *     
     */
    public void setACCOUNTSSUMMARY(ACCOUNTSSUMMARY value) {
        this.accountssummary = value;
    }

    /**
     * Gets the value of the responses property.
     * 
     * @return
     *     possible object is
     *     {@link RESPONSES }
     *     
     */
    public RESPONSES getRESPONSES() {
        return responses;
    }

    /**
     * Sets the value of the responses property.
     * 
     * @param value
     *     allowed object is
     *     {@link RESPONSES }
     *     
     */
    public void setRESPONSES(RESPONSES value) {
        this.responses = value;
    }

    /**
     * Gets the value of the indvresponses property.
     * 
     * @return
     *     possible object is
     *     {@link INDVRESPONSES }
     *     
     */
    public INDVRESPONSES getINDVRESPONSES() {
        return indvresponses;
    }

    /**
     * Sets the value of the indvresponses property.
     * 
     * @param value
     *     allowed object is
     *     {@link INDVRESPONSES }
     *     
     */
    public void setINDVRESPONSES(INDVRESPONSES value) {
        this.indvresponses = value;
    }

    /**
     * Gets the value of the grpresponses property.
     * 
     * @return
     *     possible object is
     *     {@link GRPRESPONSES }
     *     
     */
    public GRPRESPONSES getGRPRESPONSES() {
        return grpresponses;
    }

    /**
     * Sets the value of the grpresponses property.
     * 
     * @param value
     *     allowed object is
     *     {@link GRPRESPONSES }
     *     
     */
    public void setGRPRESPONSES(GRPRESPONSES value) {
        this.grpresponses = value;
    }

    /**
     * Gets the value of the inquiryhistory property.
     * 
     * @return
     *     possible object is
     *     {@link INQUIRYHISTORY }
     *     
     */
    public INQUIRYHISTORY getINQUIRYHISTORY() {
        return inquiryhistory;
    }

    /**
     * Sets the value of the inquiryhistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link INQUIRYHISTORY }
     *     
     */
    public void setINQUIRYHISTORY(INQUIRYHISTORY value) {
        this.inquiryhistory = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link COMMENTS }
     *     
     */
    public COMMENTS getCOMMENTS() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link COMMENTS }
     *     
     */
    public void setCOMMENTS(COMMENTS value) {
        this.comments = value;
    }

    /**
     * Gets the value of the alerts property.
     * 
     * @return
     *     possible object is
     *     {@link ALERTS }
     *     
     */
    public ALERTS getALERTS() {
        return alerts;
    }

    /**
     * Sets the value of the alerts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ALERTS }
     *     
     */
    public void setALERTS(ALERTS value) {
        this.alerts = value;
    }

    /**
     * Gets the value of the scores property.
     * 
     * @return
     *     possible object is
     *     {@link SCORES }
     *     
     */
    public SCORES getSCORES() {
        return scores;
    }

    /**
     * Sets the value of the scores property.
     * 
     * @param value
     *     allowed object is
     *     {@link SCORES }
     *     
     */
    public void setSCORES(SCORES value) {
        this.scores = value;
    }

    /**
     * Gets the value of the printablereport property.
     * 
     * @return
     *     possible object is
     *     {@link PRINTABLEREPORT }
     *     
     */
    public PRINTABLEREPORT getPRINTABLEREPORT() {
        return printablereport;
    }

    /**
     * Sets the value of the printablereport property.
     * 
     * @param value
     *     allowed object is
     *     {@link PRINTABLEREPORT }
     *     
     */
    public void setPRINTABLEREPORT(PRINTABLEREPORT value) {
        this.printablereport = value;
    }

    /**
     * Gets the value of the derivedattributes property.
     * 
     * @return
     *     possible object is
     *     {@link DERIVEDATTRIBUTES }
     *     
     */
    public DERIVEDATTRIBUTES getDERIVEDATTRIBUTES() {
        return derivedattributes;
    }

    /**
     * Sets the value of the derivedattributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link DERIVEDATTRIBUTES }
     *     
     */
    public void setDERIVEDATTRIBUTES(DERIVEDATTRIBUTES value) {
        this.derivedattributes = value;
    }

}
