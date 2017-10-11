package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For ERS2.0 Implementation
 * 
 * <p>Java class for ScoringElementsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScoringElementsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ScoringElement" type="{http://services.equifax.com/eport/ws/schemas/1.0}ScoringElementType" maxOccurs="8" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScoringElementsType", propOrder = {
    "scoringElement"
})
public class ScoringElementsType {

    @XmlElement(name = "ScoringElement")
    protected List<ScoringElementType> scoringElement;

    /**
     * Gets the value of the scoringElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scoringElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScoringElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScoringElementType }
     * 
     * 
     */
    public List<ScoringElementType> getScoringElement() {
        if (scoringElement == null) {
            scoringElement = new ArrayList<ScoringElementType>();
        }
        return this.scoringElement;
    }

}
