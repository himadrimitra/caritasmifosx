
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
 *         &lt;element name="SCORE" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="SCORE-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SCORE-VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SCORE-VERSION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="SCORE-FACTORS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="SCORE-COMMENTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "score"
})
@XmlRootElement(name = "SCORES")
public class SCORES {

    @XmlElement(name = "SCORE", required = true)
    protected List<SCORES.SCORE> score;

    /**
     * Gets the value of the score property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the score property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSCORE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SCORES.SCORE }
     * 
     * 
     */
    public List<SCORES.SCORE> getSCORE() {
        if (score == null) {
            score = new ArrayList<SCORES.SCORE>();
        }
        return this.score;
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
     *         &lt;element name="SCORE-TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SCORE-VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SCORE-VERSION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="SCORE-FACTORS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="SCORE-COMMENTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class SCORE {

        @XmlElement(name = "SCORE-TYPE", required = true)
        protected String scoretype;
        @XmlElement(name = "SCORE-VALUE", required = true)
        protected String scorevalue;
        @XmlElement(name = "SCORE-VERSION")
        protected String scoreversion;
        @XmlElement(name = "SCORE-FACTORS")
        protected String scorefactors;
        @XmlElement(name = "SCORE-COMMENTS")
        protected String scorecomments;

        /**
         * Gets the value of the scoretype property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCORETYPE() {
            return scoretype;
        }

        /**
         * Sets the value of the scoretype property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCORETYPE(String value) {
            this.scoretype = value;
        }

        /**
         * Gets the value of the scorevalue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCOREVALUE() {
            return scorevalue;
        }

        /**
         * Sets the value of the scorevalue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCOREVALUE(String value) {
            this.scorevalue = value;
        }

        /**
         * Gets the value of the scoreversion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCOREVERSION() {
            return scoreversion;
        }

        /**
         * Sets the value of the scoreversion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCOREVERSION(String value) {
            this.scoreversion = value;
        }

        /**
         * Gets the value of the scorefactors property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCOREFACTORS() {
            return scorefactors;
        }

        /**
         * Sets the value of the scorefactors property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCOREFACTORS(String value) {
            this.scorefactors = value;
        }

        /**
         * Gets the value of the scorecomments property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCORECOMMENTS() {
            return scorecomments;
        }

        /**
         * Sets the value of the scorecomments property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCORECOMMENTS(String value) {
            this.scorecomments = value;
        }

    }

}
