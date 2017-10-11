package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OtherKeyIndType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OtherKeyIndType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AgeOfOldestTrade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NumberOfOpenTrades" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AllLinesEVERWritten" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AllLinesEVERWrittenIn9Months" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AllLinesEVERWrittenIn6Months" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OtherKeyIndType", propOrder = {
    "ageOfOldestTrade",
    "numberOfOpenTrades",
    "allLinesEVERWritten",
    "allLinesEVERWrittenIn9Months",
    "allLinesEVERWrittenIn6Months"
})
public class OtherKeyIndType {

    @XmlElement(name = "AgeOfOldestTrade")
    protected String ageOfOldestTrade;
    @XmlElement(name = "NumberOfOpenTrades", required = true)
    protected String numberOfOpenTrades;
    @XmlElement(name = "AllLinesEVERWritten")
    protected String allLinesEVERWritten;
    @XmlElement(name = "AllLinesEVERWrittenIn9Months")
    protected String allLinesEVERWrittenIn9Months;
    @XmlElement(name = "AllLinesEVERWrittenIn6Months")
    protected String allLinesEVERWrittenIn6Months;

    /**
     * Gets the value of the ageOfOldestTrade property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgeOfOldestTrade() {
        return ageOfOldestTrade;
    }

    /**
     * Sets the value of the ageOfOldestTrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgeOfOldestTrade(String value) {
        this.ageOfOldestTrade = value;
    }

    /**
     * Gets the value of the numberOfOpenTrades property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfOpenTrades() {
        return numberOfOpenTrades;
    }

    /**
     * Sets the value of the numberOfOpenTrades property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfOpenTrades(String value) {
        this.numberOfOpenTrades = value;
    }

    /**
     * Gets the value of the allLinesEVERWritten property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllLinesEVERWritten() {
        return allLinesEVERWritten;
    }

    /**
     * Sets the value of the allLinesEVERWritten property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllLinesEVERWritten(String value) {
        this.allLinesEVERWritten = value;
    }

    /**
     * Gets the value of the allLinesEVERWrittenIn9Months property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllLinesEVERWrittenIn9Months() {
        return allLinesEVERWrittenIn9Months;
    }

    /**
     * Sets the value of the allLinesEVERWrittenIn9Months property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllLinesEVERWrittenIn9Months(String value) {
        this.allLinesEVERWrittenIn9Months = value;
    }

    /**
     * Gets the value of the allLinesEVERWrittenIn6Months property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllLinesEVERWrittenIn6Months() {
        return allLinesEVERWrittenIn6Months;
    }

    /**
     * Sets the value of the allLinesEVERWrittenIn6Months property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllLinesEVERWrittenIn6Months(String value) {
        this.allLinesEVERWrittenIn6Months = value;
    }

}
