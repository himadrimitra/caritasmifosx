
package com.finflux.risk.creditbureau.provider.highmark.xsd.response;

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
 *         &lt;element name="COMMENT" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="COMMENT-TEXT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="COMMENT-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/all>
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
    "comment"
})
@XmlRootElement(name = "COMMENTS")
public class COMMENTS {

    @XmlElement(name = "COMMENT", required = true)
    protected List<COMMENTS.COMMENT> comment;

    /**
     * Gets the value of the comment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCOMMENT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COMMENTS.COMMENT }
     * 
     * 
     */
    public List<COMMENTS.COMMENT> getCOMMENT() {
        if (comment == null) {
            comment = new ArrayList<COMMENTS.COMMENT>();
        }
        return this.comment;
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
     *       &lt;all>
     *         &lt;element name="COMMENT-TEXT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="COMMENT-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class COMMENT {

        @XmlElement(name = "COMMENT-TEXT", required = true)
        protected String commenttext;
        @XmlElement(name = "COMMENT-DATE")
        protected String commentdate;

        /**
         * Gets the value of the commenttext property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCOMMENTTEXT() {
            return commenttext;
        }

        /**
         * Sets the value of the commenttext property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCOMMENTTEXT(String value) {
            this.commenttext = value;
        }

        /**
         * Gets the value of the commentdate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCOMMENTDATE() {
            return commentdate;
        }

        /**
         * Sets the value of the commentdate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCOMMENTDATE(String value) {
            this.commentdate = value;
        }

    }

}
