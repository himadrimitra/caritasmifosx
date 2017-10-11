
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
 *         &lt;element name="INQUIRIES-IN-LAST-SIX-MONTHS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LENGTH-OF-CREDIT-HISTORY-YEAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LENGTH-OF-CREDIT-HISTORY-MONTH" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AVERAGE-ACCOUNT-AGE-YEAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AVERAGE-ACCOUNT-AGE-MONTH" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NEW-ACCOUNTS-IN-LAST-SIX-MONTHS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NEW-DELINQ-ACCOUNT-IN-LAST-SIX-MONTHS" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlRootElement(name = "ACC-DERIVED-ATTRIBUTES")
public class ACCDERIVEDATTRIBUTES {

    @XmlElement(name = "INQUIRIES-IN-LAST-SIX-MONTHS", required = true)
    protected String inquiriesinlastsixmonths;
    @XmlElement(name = "LENGTH-OF-CREDIT-HISTORY-YEAR", required = true)
    protected String lengthofcredithistoryyear;
    @XmlElement(name = "LENGTH-OF-CREDIT-HISTORY-MONTH", required = true)
    protected String lengthofcredithistorymonth;
    @XmlElement(name = "AVERAGE-ACCOUNT-AGE-YEAR", required = true)
    protected String averageaccountageyear;
    @XmlElement(name = "AVERAGE-ACCOUNT-AGE-MONTH", required = true)
    protected String averageaccountagemonth;
    @XmlElement(name = "NEW-ACCOUNTS-IN-LAST-SIX-MONTHS", required = true)
    protected String newaccountsinlastsixmonths;
    @XmlElement(name = "NEW-DELINQ-ACCOUNT-IN-LAST-SIX-MONTHS", required = true)
    protected String newdelinqaccountinlastsixmonths;

    /**
     * Gets the value of the inquiriesinlastsixmonths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINQUIRIESINLASTSIXMONTHS() {
        return inquiriesinlastsixmonths;
    }

    /**
     * Sets the value of the inquiriesinlastsixmonths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINQUIRIESINLASTSIXMONTHS(String value) {
        this.inquiriesinlastsixmonths = value;
    }

    /**
     * Gets the value of the lengthofcredithistoryyear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLENGTHOFCREDITHISTORYYEAR() {
        return lengthofcredithistoryyear;
    }

    /**
     * Sets the value of the lengthofcredithistoryyear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLENGTHOFCREDITHISTORYYEAR(String value) {
        this.lengthofcredithistoryyear = value;
    }

    /**
     * Gets the value of the lengthofcredithistorymonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLENGTHOFCREDITHISTORYMONTH() {
        return lengthofcredithistorymonth;
    }

    /**
     * Sets the value of the lengthofcredithistorymonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLENGTHOFCREDITHISTORYMONTH(String value) {
        this.lengthofcredithistorymonth = value;
    }

    /**
     * Gets the value of the averageaccountageyear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAVERAGEACCOUNTAGEYEAR() {
        return averageaccountageyear;
    }

    /**
     * Sets the value of the averageaccountageyear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAVERAGEACCOUNTAGEYEAR(String value) {
        this.averageaccountageyear = value;
    }

    /**
     * Gets the value of the averageaccountagemonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAVERAGEACCOUNTAGEMONTH() {
        return averageaccountagemonth;
    }

    /**
     * Sets the value of the averageaccountagemonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAVERAGEACCOUNTAGEMONTH(String value) {
        this.averageaccountagemonth = value;
    }

    /**
     * Gets the value of the newaccountsinlastsixmonths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNEWACCOUNTSINLASTSIXMONTHS() {
        return newaccountsinlastsixmonths;
    }

    /**
     * Sets the value of the newaccountsinlastsixmonths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNEWACCOUNTSINLASTSIXMONTHS(String value) {
        this.newaccountsinlastsixmonths = value;
    }

    /**
     * Gets the value of the newdelinqaccountinlastsixmonths property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNEWDELINQACCOUNTINLASTSIXMONTHS() {
        return newdelinqaccountinlastsixmonths;
    }

    /**
     * Sets the value of the newdelinqaccountinlastsixmonths property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNEWDELINQACCOUNTINLASTSIXMONTHS(String value) {
        this.newdelinqaccountinlastsixmonths = value;
    }

}
