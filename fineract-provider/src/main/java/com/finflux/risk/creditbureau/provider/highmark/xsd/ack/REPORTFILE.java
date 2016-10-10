
package com.finflux.risk.creditbureau.provider.highmark.xsd.ack;

import java.util.ArrayList;
import java.util.List;
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
 *       &lt;sequence>
 *         &lt;element name="INQUIRY-STATUS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}INQUIRY" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "inquirystatus"
})
@XmlRootElement(name = "REPORT-FILE")
public class REPORTFILE {

    @XmlElement(name = "INQUIRY-STATUS", required = true)
    protected REPORTFILE.INQUIRYSTATUS inquirystatus;

    /**
     * Gets the value of the inquirystatus property.
     * 
     * @return
     *     possible object is
     *     {@link REPORTFILE.INQUIRYSTATUS }
     *     
     */
    public REPORTFILE.INQUIRYSTATUS getINQUIRYSTATUS() {
        return inquirystatus;
    }

    /**
     * Sets the value of the inquirystatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link REPORTFILE.INQUIRYSTATUS }
     *     
     */
    public void setINQUIRYSTATUS(REPORTFILE.INQUIRYSTATUS value) {
        this.inquirystatus = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{}INQUIRY" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "inquiry"
    })
    public static class INQUIRYSTATUS {

        @XmlElement(name = "INQUIRY", required = true)
        protected List<INQUIRY> inquiry;

        /**
         * Gets the value of the inquiry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the inquiry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getINQUIRY().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link INQUIRY }
         * 
         * 
         */
        public List<INQUIRY> getINQUIRY() {
            if (inquiry == null) {
                inquiry = new ArrayList<INQUIRY>();
            }
            return this.inquiry;
        }

    }

}
