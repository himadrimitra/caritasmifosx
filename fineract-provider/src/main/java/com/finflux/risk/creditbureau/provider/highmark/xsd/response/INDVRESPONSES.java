package com.finflux.risk.creditbureau.provider.highmark.xsd.response;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SUMMARY">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TOTAL-RESPONSES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-OTHER-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-DEFAULT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-CLOSED-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-ACTIVE-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-OWN-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="OWN-MFI-INDECATOR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="ERRORS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TOTAL-OWN-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TOTAL-OWN-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MAX-WORST-DELEQUENCY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PRIMARY-SUMMARY">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="NO-OF-DEFAULT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-RESPONSES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-CLOSED-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-ACTIVE-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-OTHER-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-OWN-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="MAX-WORST-DELEQUENCY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SECONDARY-SUMMARY">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="NO-OF-DEFAULT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-RESPONSES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-CLOSED-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-ACTIVE-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-OTHER-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="NO-OF-OWN-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OTHER-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TOTAL-OWN-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="MAX-WORST-DELEQUENCY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="INDV-RESPONSE-LIST">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="INDV-RESPONSE" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="MATCHED-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="MFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="MFI-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="BRANCH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="KENDRA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="AGE-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element ref="{}RELATIONS"/>
 *                             &lt;element ref="{}PHONES"/>
 *                             &lt;element ref="{}ADDRESSES"/>
 *                             &lt;element ref="{}IDS"/>
 *                             &lt;element ref="{}EMAILS"/>
 *                             &lt;element name="CNSMRMBRID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ENTITY-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="GROUP-CREATION-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="INSERT-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="GROUP-DETAILS" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;all>
 *                                       &lt;element name="GROUP-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-MEMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-DELINQ-MBR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-DPD-30" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-DPD-60" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-DPD-90" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="TOT-WRITE-OFFS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                     &lt;/all>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="LOAN-DETAIL" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;all>
 *                                       &lt;element name="ACCT-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="FREQ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="ACCT-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="WRITE-OFF-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="WRITE-OFF-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="LOAN-CYCLE-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="DISBURSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="CLOSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="RECENT-DELINQ-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="DPD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="COMBINED-PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="INQ-CNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="INFO-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="WORST-DELEQUENCY-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="ACTIVE-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="NO-OF-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                     &lt;/all>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "", propOrder = { "summary", "primarysummary", "secondarysummary", "indvresponselist" })
@XmlRootElement(name = "INDV-RESPONSES")
public class INDVRESPONSES {

    @XmlElement(name = "SUMMARY", required = true)
    protected INDVRESPONSES.SUMMARY summary;
    @XmlElement(name = "PRIMARY-SUMMARY", required = true)
    protected INDVRESPONSES.PRIMARYSUMMARY primarysummary;
    @XmlElement(name = "SECONDARY-SUMMARY", required = true)
    protected INDVRESPONSES.SECONDARYSUMMARY secondarysummary;
    @XmlElement(name = "INDV-RESPONSE-LIST", required = true)
    protected INDVRESPONSES.INDVRESPONSELIST indvresponselist;

    /**
     * Gets the value of the summary property.
     * 
     * @return possible object is {@link INDVRESPONSES.SUMMARY }
     * 
     */
    public INDVRESPONSES.SUMMARY getSUMMARY() {
        return summary;
    }

    /**
     * Sets the value of the summary property.
     * 
     * @param value
     *            allowed object is {@link INDVRESPONSES.SUMMARY }
     * 
     */
    public void setSUMMARY(INDVRESPONSES.SUMMARY value) {
        this.summary = value;
    }

    /**
     * Gets the value of the primarysummary property.
     * 
     * @return possible object is {@link INDVRESPONSES.PRIMARYSUMMARY }
     * 
     */
    public INDVRESPONSES.PRIMARYSUMMARY getPRIMARYSUMMARY() {
        return primarysummary;
    }

    /**
     * Sets the value of the primarysummary property.
     * 
     * @param value
     *            allowed object is {@link INDVRESPONSES.PRIMARYSUMMARY }
     * 
     */
    public void setPRIMARYSUMMARY(INDVRESPONSES.PRIMARYSUMMARY value) {
        this.primarysummary = value;
    }

    /**
     * Gets the value of the secondarysummary property.
     * 
     * @return possible object is {@link INDVRESPONSES.SECONDARYSUMMARY }
     * 
     */
    public INDVRESPONSES.SECONDARYSUMMARY getSECONDARYSUMMARY() {
        return secondarysummary;
    }

    /**
     * Sets the value of the secondarysummary property.
     * 
     * @param value
     *            allowed object is {@link INDVRESPONSES.SECONDARYSUMMARY }
     * 
     */
    public void setSECONDARYSUMMARY(INDVRESPONSES.SECONDARYSUMMARY value) {
        this.secondarysummary = value;
    }

    /**
     * Gets the value of the indvresponselist property.
     * 
     * @return possible object is {@link INDVRESPONSES.INDVRESPONSELIST }
     * 
     */
    public INDVRESPONSES.INDVRESPONSELIST getINDVRESPONSELIST() {
        return indvresponselist;
    }

    /**
     * Sets the value of the indvresponselist property.
     * 
     * @param value
     *            allowed object is {@link INDVRESPONSES.INDVRESPONSELIST }
     * 
     */
    public void setINDVRESPONSELIST(INDVRESPONSES.INDVRESPONSELIST value) {
        this.indvresponselist = value;
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="INDV-RESPONSE" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="MATCHED-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="MFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="MFI-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="BRANCH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="KENDRA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="AGE-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element ref="{}RELATIONS"/>
     *                   &lt;element ref="{}PHONES"/>
     *                   &lt;element ref="{}ADDRESSES"/>
     *                   &lt;element ref="{}IDS"/>
     *                   &lt;element ref="{}EMAILS"/>
     *                   &lt;element name="CNSMRMBRID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ENTITY-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="GROUP-CREATION-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="INSERT-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="GROUP-DETAILS" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;all>
     *                             &lt;element name="GROUP-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-MEMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-DELINQ-MBR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-DPD-30" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-DPD-60" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-DPD-90" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="TOT-WRITE-OFFS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                           &lt;/all>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="LOAN-DETAIL" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;all>
     *                             &lt;element name="ACCT-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="FREQ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="ACCT-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="WRITE-OFF-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="WRITE-OFF-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="LOAN-CYCLE-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="DISBURSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="CLOSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="RECENT-DELINQ-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="DPD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="COMBINED-PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="INQ-CNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="INFO-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="WORST-DELEQUENCY-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="ACTIVE-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="NO-OF-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                           &lt;/all>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
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
    @XmlType(name = "", propOrder = { "indvresponse" })
    public static class INDVRESPONSELIST {

        @XmlElement(name = "INDV-RESPONSE")
        protected List<INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE> indvresponse;

        /**
         * Gets the value of the indvresponse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the indvresponse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getINDVRESPONSE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE }
         * 
         * 
         */
        public List<INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE> getINDVRESPONSE() {
            if (indvresponse == null) {
                indvresponse = new ArrayList<INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE>();
            }
            return this.indvresponse;
        }

        /**
         * <p>
         * Java class for anonymous complex type.
         * 
         * <p>
         * The following schema fragment specifies the expected content
         * contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;all>
         *         &lt;element name="MATCHED-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="MFI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="MFI-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="BRANCH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="KENDRA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="AGE-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element ref="{}RELATIONS"/>
         *         &lt;element ref="{}PHONES"/>
         *         &lt;element ref="{}ADDRESSES"/>
         *         &lt;element ref="{}IDS"/>
         *         &lt;element ref="{}EMAILS"/>
         *         &lt;element name="CNSMRMBRID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ENTITY-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="GROUP-CREATION-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="INSERT-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="GROUP-DETAILS" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;all>
         *                   &lt;element name="GROUP-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-MEMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-DELINQ-MBR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-DPD-30" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-DPD-60" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-DPD-90" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="TOT-WRITE-OFFS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                 &lt;/all>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="LOAN-DETAIL" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;all>
         *                   &lt;element name="ACCT-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="FREQ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="ACCT-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="WRITE-OFF-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="WRITE-OFF-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="LOAN-CYCLE-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="DISBURSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="CLOSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="RECENT-DELINQ-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="DPD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="COMBINED-PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="INQ-CNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="INFO-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="WORST-DELEQUENCY-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="ACTIVE-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="NO-OF-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                 &lt;/all>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
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
        public static class INDVRESPONSE {

            @XmlElement(name = "MATCHED-TYPE")
            protected String matchedtype;
            @XmlElement(name = "MFI")
            protected String mfi;
            @XmlElement(name = "MFI-ID")
            protected String mfiid;
            @XmlElement(name = "BRANCH")
            protected String branch;
            @XmlElement(name = "KENDRA")
            protected String kendra;
            @XmlElement(name = "NAME", required = true)
            protected String name;
            @XmlElement(name = "DOB")
            protected String dob;
            @XmlElement(name = "AGE")
            protected String age;
            @XmlElement(name = "AGE-AS-ON")
            protected String ageason;
            @XmlElement(name = "RELATIONS", required = true)
            protected RELATIONS relations;
            @XmlElement(name = "PHONES", required = true)
            protected PHONES phones;
            @XmlElement(name = "ADDRESSES", required = true)
            protected ADDRESSES addresses;
            @XmlElement(name = "IDS", required = true)
            protected IDS ids;
            @XmlElement(name = "EMAILS", required = true)
            protected EMAILS emails;
            @XmlElement(name = "CNSMRMBRID")
            protected String cnsmrmbrid;
            @XmlElement(name = "ENTITY-ID")
            protected String entityid;
            @XmlElement(name = "GROUP-CREATION-DATE")
            protected String groupcreationdate;
            @XmlElement(name = "INSERT-DATE")
            protected String insertdate;
            @XmlElement(name = "GROUP-DETAILS")
            protected INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.GROUPDETAILS groupdetails;
            @XmlElement(name = "LOAN-DETAIL")
            protected INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL loandetail;

            /**
             * Gets the value of the matchedtype property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getMATCHEDTYPE() {
                return matchedtype;
            }

            /**
             * Sets the value of the matchedtype property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setMATCHEDTYPE(String value) {
                this.matchedtype = value;
            }

            /**
             * Gets the value of the mfi property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getMFI() {
                return mfi;
            }

            /**
             * Sets the value of the mfi property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setMFI(String value) {
                this.mfi = value;
            }

            /**
             * Gets the value of the mfiid property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getMFIID() {
                return mfiid;
            }

            /**
             * Sets the value of the mfiid property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setMFIID(String value) {
                this.mfiid = value;
            }

            /**
             * Gets the value of the branch property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getBRANCH() {
                return branch;
            }

            /**
             * Sets the value of the branch property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setBRANCH(String value) {
                this.branch = value;
            }

            /**
             * Gets the value of the kendra property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getKENDRA() {
                return kendra;
            }

            /**
             * Sets the value of the kendra property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setKENDRA(String value) {
                this.kendra = value;
            }

            /**
             * Gets the value of the name property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getNAME() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setNAME(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the dob property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getDOB() {
                return dob;
            }

            /**
             * Sets the value of the dob property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setDOB(String value) {
                this.dob = value;
            }

            /**
             * Gets the value of the age property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getAGE() {
                return age;
            }

            /**
             * Sets the value of the age property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setAGE(String value) {
                this.age = value;
            }

            /**
             * Gets the value of the ageason property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getAGEASON() {
                return ageason;
            }

            /**
             * Sets the value of the ageason property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setAGEASON(String value) {
                this.ageason = value;
            }

            /**
             * Gets the value of the relations property.
             * 
             * @return possible object is {@link RELATIONS }
             * 
             */
            public RELATIONS getRELATIONS() {
                return relations;
            }

            /**
             * Sets the value of the relations property.
             * 
             * @param value
             *            allowed object is {@link RELATIONS }
             * 
             */
            public void setRELATIONS(RELATIONS value) {
                this.relations = value;
            }

            /**
             * Gets the value of the phones property.
             * 
             * @return possible object is {@link PHONES }
             * 
             */
            public PHONES getPHONES() {
                return phones;
            }

            /**
             * Sets the value of the phones property.
             * 
             * @param value
             *            allowed object is {@link PHONES }
             * 
             */
            public void setPHONES(PHONES value) {
                this.phones = value;
            }

            /**
             * Gets the value of the addresses property.
             * 
             * @return possible object is {@link ADDRESSES }
             * 
             */
            public ADDRESSES getADDRESSES() {
                return addresses;
            }

            /**
             * Sets the value of the addresses property.
             * 
             * @param value
             *            allowed object is {@link ADDRESSES }
             * 
             */
            public void setADDRESSES(ADDRESSES value) {
                this.addresses = value;
            }

            /**
             * Gets the value of the ids property.
             * 
             * @return possible object is {@link IDS }
             * 
             */
            public IDS getIDS() {
                return ids;
            }

            /**
             * Sets the value of the ids property.
             * 
             * @param value
             *            allowed object is {@link IDS }
             * 
             */
            public void setIDS(IDS value) {
                this.ids = value;
            }

            /**
             * Gets the value of the emails property.
             * 
             * @return possible object is {@link EMAILS }
             * 
             */
            public EMAILS getEMAILS() {
                return emails;
            }

            /**
             * Sets the value of the emails property.
             * 
             * @param value
             *            allowed object is {@link EMAILS }
             * 
             */
            public void setEMAILS(EMAILS value) {
                this.emails = value;
            }

            /**
             * Gets the value of the cnsmrmbrid property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getCNSMRMBRID() {
                return cnsmrmbrid;
            }

            /**
             * Sets the value of the cnsmrmbrid property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setCNSMRMBRID(String value) {
                this.cnsmrmbrid = value;
            }

            /**
             * Gets the value of the entityid property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getENTITYID() {
                return entityid;
            }

            /**
             * Sets the value of the entityid property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setENTITYID(String value) {
                this.entityid = value;
            }

            /**
             * Gets the value of the groupcreationdate property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getGROUPCREATIONDATE() {
                return groupcreationdate;
            }

            /**
             * Sets the value of the groupcreationdate property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setGROUPCREATIONDATE(String value) {
                this.groupcreationdate = value;
            }

            /**
             * Gets the value of the insertdate property.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getINSERTDATE() {
                return insertdate;
            }

            /**
             * Sets the value of the insertdate property.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setINSERTDATE(String value) {
                this.insertdate = value;
            }

            /**
             * Gets the value of the groupdetails property.
             * 
             * @return possible object is
             *         {@link INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.GROUPDETAILS }
             * 
             */
            public INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.GROUPDETAILS getGROUPDETAILS() {
                return groupdetails;
            }

            /**
             * Sets the value of the groupdetails property.
             * 
             * @param value
             *            allowed object is
             *            {@link INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.GROUPDETAILS }
             * 
             */
            public void setGROUPDETAILS(INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.GROUPDETAILS value) {
                this.groupdetails = value;
            }

            /**
             * Gets the value of the loandetail property.
             * 
             * @return possible object is
             *         {@link INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL }
             * 
             */
            public INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL getLOANDETAIL() {
                return loandetail;
            }

            /**
             * Sets the value of the loandetail property.
             * 
             * @param value
             *            allowed object is
             *            {@link INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL }
             * 
             */
            public void setLOANDETAIL(INDVRESPONSES.INDVRESPONSELIST.INDVRESPONSE.LOANDETAIL value) {
                this.loandetail = value;
            }

            /**
             * <p>
             * Java class for anonymous complex type.
             * 
             * <p>
             * The following schema fragment specifies the expected content
             * contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;all>
             *         &lt;element name="GROUP-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-MEMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-DELINQ-MBR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-DPD-30" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-DPD-60" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-DPD-90" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="TOT-WRITE-OFFS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
            public static class GROUPDETAILS {

                @XmlElement(name = "GROUP-ID")
                protected String groupid;
                @XmlElement(name = "TOT-DISBURSED-AMT")
                protected String totdisbursedamt;
                @XmlElement(name = "TOT-CURRENT-BAL")
                protected String totcurrentbal;
                @XmlElement(name = "TOT-INSTALLMENT-AMT")
                protected String totinstallmentamt;
                @XmlElement(name = "TOT-OVERDUE-AMT")
                protected String totoverdueamt;
                @XmlElement(name = "TOT-ACCOUNTS")
                protected String totaccounts;
                @XmlElement(name = "TOT-MEMBER")
                protected String totmember;
                @XmlElement(name = "TOT-DELINQ-MBR")
                protected String totdelinqmbr;
                @XmlElement(name = "TOT-DPD-30")
                protected String totdpd30;
                @XmlElement(name = "TOT-DPD-60")
                protected String totdpd60;
                @XmlElement(name = "TOT-DPD-90")
                protected String totdpd90;
                @XmlElement(name = "TOT-WRITE-OFFS")
                protected String totwriteoffs;

                /**
                 * Gets the value of the groupid property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getGROUPID() {
                    return groupid;
                }

                /**
                 * Sets the value of the groupid property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setGROUPID(String value) {
                    this.groupid = value;
                }

                /**
                 * Gets the value of the totdisbursedamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTDISBURSEDAMT() {
                    return totdisbursedamt;
                }

                /**
                 * Sets the value of the totdisbursedamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTDISBURSEDAMT(String value) {
                    this.totdisbursedamt = value;
                }

                /**
                 * Gets the value of the totcurrentbal property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTCURRENTBAL() {
                    return totcurrentbal;
                }

                /**
                 * Sets the value of the totcurrentbal property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTCURRENTBAL(String value) {
                    this.totcurrentbal = value;
                }

                /**
                 * Gets the value of the totinstallmentamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTINSTALLMENTAMT() {
                    return totinstallmentamt;
                }

                /**
                 * Sets the value of the totinstallmentamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTINSTALLMENTAMT(String value) {
                    this.totinstallmentamt = value;
                }

                /**
                 * Gets the value of the totoverdueamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTOVERDUEAMT() {
                    return totoverdueamt;
                }

                /**
                 * Sets the value of the totoverdueamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTOVERDUEAMT(String value) {
                    this.totoverdueamt = value;
                }

                /**
                 * Gets the value of the totaccounts property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTACCOUNTS() {
                    return totaccounts;
                }

                /**
                 * Sets the value of the totaccounts property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTACCOUNTS(String value) {
                    this.totaccounts = value;
                }

                /**
                 * Gets the value of the totmember property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTMEMBER() {
                    return totmember;
                }

                /**
                 * Sets the value of the totmember property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTMEMBER(String value) {
                    this.totmember = value;
                }

                /**
                 * Gets the value of the totdelinqmbr property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTDELINQMBR() {
                    return totdelinqmbr;
                }

                /**
                 * Sets the value of the totdelinqmbr property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTDELINQMBR(String value) {
                    this.totdelinqmbr = value;
                }

                /**
                 * Gets the value of the totdpd30 property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTDPD30() {
                    return totdpd30;
                }

                /**
                 * Sets the value of the totdpd30 property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTDPD30(String value) {
                    this.totdpd30 = value;
                }

                /**
                 * Gets the value of the totdpd60 property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTDPD60() {
                    return totdpd60;
                }

                /**
                 * Sets the value of the totdpd60 property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTDPD60(String value) {
                    this.totdpd60 = value;
                }

                /**
                 * Gets the value of the totdpd90 property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTDPD90() {
                    return totdpd90;
                }

                /**
                 * Sets the value of the totdpd90 property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTDPD90(String value) {
                    this.totdpd90 = value;
                }

                /**
                 * Gets the value of the totwriteoffs property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getTOTWRITEOFFS() {
                    return totwriteoffs;
                }

                /**
                 * Sets the value of the totwriteoffs property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setTOTWRITEOFFS(String value) {
                    this.totwriteoffs = value;
                }

            }

            /**
             * <p>
             * Java class for anonymous complex type.
             * 
             * <p>
             * The following schema fragment specifies the expected content
             * contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;all>
             *         &lt;element name="ACCT-TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="FREQ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="ACCT-NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="DISBURSED-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="CURRENT-BAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="INSTALLMENT-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="OVERDUE-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="WRITE-OFF-AMT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="WRITE-OFF-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="LOAN-CYCLE-ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="DISBURSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="CLOSED-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="RECENT-DELINQ-DT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="DPD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="COMBINED-PAYMENT-HISTORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="INQ-CNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="INFO-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="WORST-DELEQUENCY-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="ACTIVE-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="NO-OF-BORROWERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
            public static class LOANDETAIL {

                @XmlElement(name = "ACCT-TYPE")
                protected String accttype;
                @XmlElement(name = "FREQ")
                protected String freq;
                @XmlElement(name = "STATUS")
                protected String status;
                @XmlElement(name = "ACCT-NUMBER")
                protected String acctnumber;
                @XmlElement(name = "DISBURSED-AMT")
                protected String disbursedamt;
                @XmlElement(name = "CURRENT-BAL")
                protected String currentbal;
                @XmlElement(name = "INSTALLMENT-AMT")
                protected String installmentamt;
                @XmlElement(name = "OVERDUE-AMT")
                protected String overdueamt;
                @XmlElement(name = "WRITE-OFF-AMT")
                protected String writeoffamt;
                @XmlElement(name = "WRITE-OFF-DT")
                protected String writeoffdt;
                @XmlElement(name = "LOAN-CYCLE-ID")
                protected String loancycleid;
                @XmlElement(name = "DISBURSED-DT")
                protected String disburseddt;
                @XmlElement(name = "CLOSED-DT")
                protected String closeddt;
                @XmlElement(name = "RECENT-DELINQ-DT")
                protected String recentdelinqdt;
                @XmlElement(name = "DPD")
                protected String dpd;
                @XmlElement(name = "PAYMENT-HISTORY")
                protected String paymenthistory;
                @XmlElement(name = "COMBINED-PAYMENT-HISTORY")
                protected String combinedpaymenthistory;
                @XmlElement(name = "INQ-CNT")
                protected String inqcnt;
                @XmlElement(name = "INFO-AS-ON")
                protected String infoason;
                @XmlElement(name = "WORST-DELEQUENCY-AMOUNT")
                protected String worstdelequencyamount;
                @XmlElement(name = "ACTIVE-BORROWERS")
                protected String activeborrowers;
                @XmlElement(name = "NO-OF-BORROWERS")
                protected String noofborrowers;
                @XmlElement(name = "COMMENT")
                protected String comment;

                /**
                 * Gets the value of the accttype property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getACCTTYPE() {
                    return accttype;
                }

                /**
                 * Sets the value of the accttype property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setACCTTYPE(String value) {
                    this.accttype = value;
                }

                /**
                 * Gets the value of the freq property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getFREQ() {
                    return freq;
                }

                /**
                 * Sets the value of the freq property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setFREQ(String value) {
                    this.freq = value;
                }

                /**
                 * Gets the value of the status property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getSTATUS() {
                    return status;
                }

                /**
                 * Sets the value of the status property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setSTATUS(String value) {
                    this.status = value;
                }

                /**
                 * Gets the value of the acctnumber property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getACCTNUMBER() {
                    return acctnumber;
                }

                /**
                 * Sets the value of the acctnumber property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setACCTNUMBER(String value) {
                    this.acctnumber = value;
                }

                /**
                 * Gets the value of the disbursedamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getDISBURSEDAMT() {
                    return disbursedamt;
                }

                /**
                 * Sets the value of the disbursedamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setDISBURSEDAMT(String value) {
                    this.disbursedamt = value;
                }

                /**
                 * Gets the value of the currentbal property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getCURRENTBAL() {
                    return currentbal;
                }

                /**
                 * Sets the value of the currentbal property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setCURRENTBAL(String value) {
                    this.currentbal = value;
                }

                /**
                 * Gets the value of the installmentamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getINSTALLMENTAMT() {
                    return installmentamt;
                }

                /**
                 * Sets the value of the installmentamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setINSTALLMENTAMT(String value) {
                    this.installmentamt = value;
                }

                /**
                 * Gets the value of the overdueamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getOVERDUEAMT() {
                    return overdueamt;
                }

                /**
                 * Sets the value of the overdueamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setOVERDUEAMT(String value) {
                    this.overdueamt = value;
                }

                /**
                 * Gets the value of the writeoffamt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getWRITEOFFAMT() {
                    return writeoffamt;
                }

                /**
                 * Sets the value of the writeoffamt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setWRITEOFFAMT(String value) {
                    this.writeoffamt = value;
                }

                /**
                 * Gets the value of the writeoffdt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getWRITEOFFDT() {
                    return writeoffdt;
                }

                /**
                 * Sets the value of the writeoffdt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setWRITEOFFDT(String value) {
                    this.writeoffdt = value;
                }

                /**
                 * Gets the value of the loancycleid property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getLOANCYCLEID() {
                    return loancycleid;
                }

                /**
                 * Sets the value of the loancycleid property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setLOANCYCLEID(String value) {
                    this.loancycleid = value;
                }

                /**
                 * Gets the value of the disburseddt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getDISBURSEDDT() {
                    return disburseddt;
                }

                /**
                 * Sets the value of the disburseddt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setDISBURSEDDT(String value) {
                    this.disburseddt = value;
                }

                /**
                 * Gets the value of the closeddt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getCLOSEDDT() {
                    return closeddt;
                }

                /**
                 * Sets the value of the closeddt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setCLOSEDDT(String value) {
                    this.closeddt = value;
                }

                /**
                 * Gets the value of the recentdelinqdt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getRECENTDELINQDT() {
                    return recentdelinqdt;
                }

                /**
                 * Sets the value of the recentdelinqdt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setRECENTDELINQDT(String value) {
                    this.recentdelinqdt = value;
                }

                /**
                 * Gets the value of the dpd property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getDPD() {
                    return dpd;
                }

                /**
                 * Sets the value of the dpd property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setDPD(String value) {
                    this.dpd = value;
                }

                /**
                 * Gets the value of the paymenthistory property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getPAYMENTHISTORY() {
                    return paymenthistory;
                }

                /**
                 * Sets the value of the paymenthistory property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setPAYMENTHISTORY(String value) {
                    this.paymenthistory = value;
                }

                /**
                 * Gets the value of the combinedpaymenthistory property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getCOMBINEDPAYMENTHISTORY() {
                    return combinedpaymenthistory;
                }

                /**
                 * Sets the value of the combinedpaymenthistory property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setCOMBINEDPAYMENTHISTORY(String value) {
                    this.combinedpaymenthistory = value;
                }

                /**
                 * Gets the value of the inqcnt property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getINQCNT() {
                    return inqcnt;
                }

                /**
                 * Sets the value of the inqcnt property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setINQCNT(String value) {
                    this.inqcnt = value;
                }

                /**
                 * Gets the value of the infoason property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getINFOASON() {
                    return infoason;
                }

                /**
                 * Sets the value of the infoason property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setINFOASON(String value) {
                    this.infoason = value;
                }

                /**
                 * Gets the value of the worstdelequencyamount property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getWORSTDELEQUENCYAMOUNT() {
                    return worstdelequencyamount;
                }

                /**
                 * Sets the value of the worstdelequencyamount property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setWORSTDELEQUENCYAMOUNT(String value) {
                    this.worstdelequencyamount = value;
                }

                /**
                 * Gets the value of the activeborrowers property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getACTIVEBORROWERS() {
                    return activeborrowers;
                }

                /**
                 * Sets the value of the activeborrowers property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setACTIVEBORROWERS(String value) {
                    this.activeborrowers = value;
                }

                /**
                 * Gets the value of the noofborrowers property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getNOOFBORROWERS() {
                    return noofborrowers;
                }

                /**
                 * Sets the value of the noofborrowers property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setNOOFBORROWERS(String value) {
                    this.noofborrowers = value;
                }

                /**
                 * Gets the value of the comment property.
                 * 
                 * @return possible object is {@link String }
                 * 
                 */
                public String getCOMMENT() {
                    return comment;
                }

                /**
                 * Sets the value of the comment property.
                 * 
                 * @param value
                 *            allowed object is {@link String }
                 * 
                 */
                public void setCOMMENT(String value) {
                    this.comment = value;
                }

            }

        }

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="NO-OF-DEFAULT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-RESPONSES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-CLOSED-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-ACTIVE-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-OTHER-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-OWN-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="MAX-WORST-DELEQUENCY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class PRIMARYSUMMARY {

        @XmlElement(name = "NO-OF-DEFAULT-ACCOUNTS")
        protected String noofdefaultaccounts;
        @XmlElement(name = "TOTAL-RESPONSES")
        protected String totalresponses;
        @XmlElement(name = "NO-OF-CLOSED-ACCOUNTS")
        protected String noofclosedaccounts;
        @XmlElement(name = "NO-OF-ACTIVE-ACCOUNTS")
        protected String noofactiveaccounts;
        @XmlElement(name = "NO-OF-OTHER-MFIS")
        protected String noofothermfis;
        @XmlElement(name = "NO-OF-OWN-MFIS")
        protected String noofownmfis;
        @XmlElement(name = "TOTAL-OTHER-DISBURSED-AMOUNT")
        protected String totalotherdisbursedamount;
        @XmlElement(name = "TOTAL-OTHER-CURRENT-BALANCE")
        protected String totalothercurrentbalance;
        @XmlElement(name = "TOTAL-OTHER-INSTALLMENT-AMOUNT")
        protected String totalotherinstallmentamount;
        @XmlElement(name = "TOTAL-OWN-DISBURSED-AMOUNT")
        protected String totalowndisbursedamount;
        @XmlElement(name = "TOTAL-OWN-CURRENT-BALANCE")
        protected String totalowncurrentbalance;
        @XmlElement(name = "TOTAL-OWN-INSTALLMENT-AMOUNT")
        protected String totalowninstallmentamount;
        @XmlElement(name = "MAX-WORST-DELEQUENCY")
        protected String maxworstdelequency;

        /**
         * Gets the value of the noofdefaultaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFDEFAULTACCOUNTS() {
            return noofdefaultaccounts;
        }

        /**
         * Sets the value of the noofdefaultaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFDEFAULTACCOUNTS(String value) {
            this.noofdefaultaccounts = value;
        }

        /**
         * Gets the value of the totalresponses property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALRESPONSES() {
            return totalresponses;
        }

        /**
         * Sets the value of the totalresponses property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALRESPONSES(String value) {
            this.totalresponses = value;
        }

        /**
         * Gets the value of the noofclosedaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFCLOSEDACCOUNTS() {
            return noofclosedaccounts;
        }

        /**
         * Sets the value of the noofclosedaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFCLOSEDACCOUNTS(String value) {
            this.noofclosedaccounts = value;
        }

        /**
         * Gets the value of the noofactiveaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFACTIVEACCOUNTS() {
            return noofactiveaccounts;
        }

        /**
         * Sets the value of the noofactiveaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFACTIVEACCOUNTS(String value) {
            this.noofactiveaccounts = value;
        }

        /**
         * Gets the value of the noofothermfis property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFOTHERMFIS() {
            return noofothermfis;
        }

        /**
         * Sets the value of the noofothermfis property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFOTHERMFIS(String value) {
            this.noofothermfis = value;
        }

        /**
         * Gets the value of the noofownmfis property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFOWNMFIS() {
            return noofownmfis;
        }

        /**
         * Sets the value of the noofownmfis property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFOWNMFIS(String value) {
            this.noofownmfis = value;
        }

        /**
         * Gets the value of the totalotherdisbursedamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERDISBURSEDAMOUNT() {
            return totalotherdisbursedamount;
        }

        /**
         * Sets the value of the totalotherdisbursedamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERDISBURSEDAMOUNT(String value) {
            this.totalotherdisbursedamount = value;
        }

        /**
         * Gets the value of the totalothercurrentbalance property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERCURRENTBALANCE() {
            return totalothercurrentbalance;
        }

        /**
         * Sets the value of the totalothercurrentbalance property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERCURRENTBALANCE(String value) {
            this.totalothercurrentbalance = value;
        }

        /**
         * Gets the value of the totalotherinstallmentamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERINSTALLMENTAMOUNT() {
            return totalotherinstallmentamount;
        }

        /**
         * Sets the value of the totalotherinstallmentamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERINSTALLMENTAMOUNT(String value) {
            this.totalotherinstallmentamount = value;
        }

        /**
         * Gets the value of the totalowndisbursedamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNDISBURSEDAMOUNT() {
            return totalowndisbursedamount;
        }

        /**
         * Sets the value of the totalowndisbursedamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNDISBURSEDAMOUNT(String value) {
            this.totalowndisbursedamount = value;
        }

        /**
         * Gets the value of the totalowncurrentbalance property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNCURRENTBALANCE() {
            return totalowncurrentbalance;
        }

        /**
         * Sets the value of the totalowncurrentbalance property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNCURRENTBALANCE(String value) {
            this.totalowncurrentbalance = value;
        }

        /**
         * Gets the value of the totalowninstallmentamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNINSTALLMENTAMOUNT() {
            return totalowninstallmentamount;
        }

        /**
         * Sets the value of the totalowninstallmentamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNINSTALLMENTAMOUNT(String value) {
            this.totalowninstallmentamount = value;
        }

        /**
         * Gets the value of the maxworstdelequency property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getMAXWORSTDELEQUENCY() {
            return maxworstdelequency;
        }

        /**
         * Sets the value of the maxworstdelequency property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setMAXWORSTDELEQUENCY(String value) {
            this.maxworstdelequency = value;
        }

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="NO-OF-DEFAULT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-RESPONSES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-CLOSED-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-ACTIVE-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-OTHER-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-OWN-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="MAX-WORST-DELEQUENCY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class SECONDARYSUMMARY {

        @XmlElement(name = "NO-OF-DEFAULT-ACCOUNTS")
        protected String noofdefaultaccounts;
        @XmlElement(name = "TOTAL-RESPONSES")
        protected String totalresponses;
        @XmlElement(name = "NO-OF-CLOSED-ACCOUNTS")
        protected String noofclosedaccounts;
        @XmlElement(name = "NO-OF-ACTIVE-ACCOUNTS")
        protected String noofactiveaccounts;
        @XmlElement(name = "NO-OF-OTHER-MFIS")
        protected String noofothermfis;
        @XmlElement(name = "NO-OF-OWN-MFIS")
        protected String noofownmfis;
        @XmlElement(name = "TOTAL-OTHER-DISBURSED-AMOUNT")
        protected String totalotherdisbursedamount;
        @XmlElement(name = "TOTAL-OTHER-CURRENT-BALANCE")
        protected String totalothercurrentbalance;
        @XmlElement(name = "TOTAL-OTHER-INSTALLMENT-AMOUNT")
        protected String totalotherinstallmentamount;
        @XmlElement(name = "TOTAL-OWN-DISBURSED-AMOUNT")
        protected String totalowndisbursedamount;
        @XmlElement(name = "TOTAL-OWN-CURRENT-BALANCE")
        protected String totalowncurrentbalance;
        @XmlElement(name = "TOTAL-OWN-INSTALLMENT-AMOUNT")
        protected String totalowninstallmentamount;
        @XmlElement(name = "MAX-WORST-DELEQUENCY")
        protected String maxworstdelequency;

        /**
         * Gets the value of the noofdefaultaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFDEFAULTACCOUNTS() {
            return noofdefaultaccounts;
        }

        /**
         * Sets the value of the noofdefaultaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFDEFAULTACCOUNTS(String value) {
            this.noofdefaultaccounts = value;
        }

        /**
         * Gets the value of the totalresponses property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALRESPONSES() {
            return totalresponses;
        }

        /**
         * Sets the value of the totalresponses property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALRESPONSES(String value) {
            this.totalresponses = value;
        }

        /**
         * Gets the value of the noofclosedaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFCLOSEDACCOUNTS() {
            return noofclosedaccounts;
        }

        /**
         * Sets the value of the noofclosedaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFCLOSEDACCOUNTS(String value) {
            this.noofclosedaccounts = value;
        }

        /**
         * Gets the value of the noofactiveaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFACTIVEACCOUNTS() {
            return noofactiveaccounts;
        }

        /**
         * Sets the value of the noofactiveaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFACTIVEACCOUNTS(String value) {
            this.noofactiveaccounts = value;
        }

        /**
         * Gets the value of the noofothermfis property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFOTHERMFIS() {
            return noofothermfis;
        }

        /**
         * Sets the value of the noofothermfis property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFOTHERMFIS(String value) {
            this.noofothermfis = value;
        }

        /**
         * Gets the value of the noofownmfis property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFOWNMFIS() {
            return noofownmfis;
        }

        /**
         * Sets the value of the noofownmfis property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFOWNMFIS(String value) {
            this.noofownmfis = value;
        }

        /**
         * Gets the value of the totalotherdisbursedamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERDISBURSEDAMOUNT() {
            return totalotherdisbursedamount;
        }

        /**
         * Sets the value of the totalotherdisbursedamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERDISBURSEDAMOUNT(String value) {
            this.totalotherdisbursedamount = value;
        }

        /**
         * Gets the value of the totalothercurrentbalance property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERCURRENTBALANCE() {
            return totalothercurrentbalance;
        }

        /**
         * Sets the value of the totalothercurrentbalance property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERCURRENTBALANCE(String value) {
            this.totalothercurrentbalance = value;
        }

        /**
         * Gets the value of the totalotherinstallmentamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERINSTALLMENTAMOUNT() {
            return totalotherinstallmentamount;
        }

        /**
         * Sets the value of the totalotherinstallmentamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERINSTALLMENTAMOUNT(String value) {
            this.totalotherinstallmentamount = value;
        }

        /**
         * Gets the value of the totalowndisbursedamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNDISBURSEDAMOUNT() {
            return totalowndisbursedamount;
        }

        /**
         * Sets the value of the totalowndisbursedamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNDISBURSEDAMOUNT(String value) {
            this.totalowndisbursedamount = value;
        }

        /**
         * Gets the value of the totalowncurrentbalance property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNCURRENTBALANCE() {
            return totalowncurrentbalance;
        }

        /**
         * Sets the value of the totalowncurrentbalance property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNCURRENTBALANCE(String value) {
            this.totalowncurrentbalance = value;
        }

        /**
         * Gets the value of the totalowninstallmentamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNINSTALLMENTAMOUNT() {
            return totalowninstallmentamount;
        }

        /**
         * Sets the value of the totalowninstallmentamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNINSTALLMENTAMOUNT(String value) {
            this.totalowninstallmentamount = value;
        }

        /**
         * Gets the value of the maxworstdelequency property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getMAXWORSTDELEQUENCY() {
            return maxworstdelequency;
        }

        /**
         * Sets the value of the maxworstdelequency property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setMAXWORSTDELEQUENCY(String value) {
            this.maxworstdelequency = value;
        }

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TOTAL-RESPONSES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-OTHER-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-DEFAULT-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-CLOSED-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-ACTIVE-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="NO-OF-OWN-MFIS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="OWN-MFI-INDECATOR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="ERRORS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OWN-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TOTAL-OWN-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TOTAL-OWN-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TOTAL-OTHER-INSTALLMENT-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MAX-WORST-DELEQUENCY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    public static class SUMMARY {

        @XmlElement(name = "STATUS", required = true)
        protected String status;
        @XmlElement(name = "TOTAL-RESPONSES")
        protected String totalresponses;
        @XmlElement(name = "NO-OF-OTHER-MFIS")
        protected String noofothermfis;
        @XmlElement(name = "NO-OF-DEFAULT-ACCOUNTS")
        protected String noofdefaultaccounts;
        @XmlElement(name = "NO-OF-CLOSED-ACCOUNTS")
        protected String noofclosedaccounts;
        @XmlElement(name = "NO-OF-ACTIVE-ACCOUNTS")
        protected String noofactiveaccounts;
        @XmlElement(name = "NO-OF-OWN-MFIS")
        protected String noofownmfis;
        @XmlElement(name = "OWN-MFI-INDECATOR")
        protected String ownmfiindecator;
        @XmlElement(name = "ERRORS")
        protected String errors;
        @XmlElement(name = "TOTAL-OWN-DISBURSED-AMOUNT")
        protected String totalowndisbursedamount;
        @XmlElement(name = "TOTAL-OTHER-DISBURSED-AMOUNT", required = true)
        protected String totalotherdisbursedamount;
        @XmlElement(name = "TOTAL-OWN-CURRENT-BALANCE")
        protected String totalowncurrentbalance;
        @XmlElement(name = "TOTAL-OTHER-CURRENT-BALANCE", required = true)
        protected String totalothercurrentbalance;
        @XmlElement(name = "TOTAL-OWN-INSTALLMENT-AMOUNT")
        protected String totalowninstallmentamount;
        @XmlElement(name = "TOTAL-OTHER-INSTALLMENT-AMOUNT", required = true)
        protected String totalotherinstallmentamount;
        @XmlElement(name = "MAX-WORST-DELEQUENCY")
        protected String maxworstdelequency;

        /**
         * Gets the value of the status property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getSTATUS() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setSTATUS(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the totalresponses property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALRESPONSES() {
            return totalresponses;
        }

        /**
         * Sets the value of the totalresponses property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALRESPONSES(String value) {
            this.totalresponses = value;
        }

        /**
         * Gets the value of the noofothermfis property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFOTHERMFIS() {
            return noofothermfis;
        }

        /**
         * Sets the value of the noofothermfis property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFOTHERMFIS(String value) {
            this.noofothermfis = value;
        }

        /**
         * Gets the value of the noofdefaultaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFDEFAULTACCOUNTS() {
            return noofdefaultaccounts;
        }

        /**
         * Sets the value of the noofdefaultaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFDEFAULTACCOUNTS(String value) {
            this.noofdefaultaccounts = value;
        }

        /**
         * Gets the value of the noofclosedaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFCLOSEDACCOUNTS() {
            return noofclosedaccounts;
        }

        /**
         * Sets the value of the noofclosedaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFCLOSEDACCOUNTS(String value) {
            this.noofclosedaccounts = value;
        }

        /**
         * Gets the value of the noofactiveaccounts property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFACTIVEACCOUNTS() {
            return noofactiveaccounts;
        }

        /**
         * Sets the value of the noofactiveaccounts property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFACTIVEACCOUNTS(String value) {
            this.noofactiveaccounts = value;
        }

        /**
         * Gets the value of the noofownmfis property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNOOFOWNMFIS() {
            return noofownmfis;
        }

        /**
         * Sets the value of the noofownmfis property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNOOFOWNMFIS(String value) {
            this.noofownmfis = value;
        }

        /**
         * Gets the value of the ownmfiindecator property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getOWNMFIINDECATOR() {
            return ownmfiindecator;
        }

        /**
         * Sets the value of the ownmfiindecator property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setOWNMFIINDECATOR(String value) {
            this.ownmfiindecator = value;
        }

        /**
         * Gets the value of the errors property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getERRORS() {
            return errors;
        }

        /**
         * Sets the value of the errors property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setERRORS(String value) {
            this.errors = value;
        }

        /**
         * Gets the value of the totalowndisbursedamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNDISBURSEDAMOUNT() {
            return totalowndisbursedamount;
        }

        /**
         * Sets the value of the totalowndisbursedamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNDISBURSEDAMOUNT(String value) {
            this.totalowndisbursedamount = value;
        }

        /**
         * Gets the value of the totalotherdisbursedamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERDISBURSEDAMOUNT() {
            return totalotherdisbursedamount;
        }

        /**
         * Sets the value of the totalotherdisbursedamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERDISBURSEDAMOUNT(String value) {
            this.totalotherdisbursedamount = value;
        }

        /**
         * Gets the value of the totalowncurrentbalance property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNCURRENTBALANCE() {
            return totalowncurrentbalance;
        }

        /**
         * Sets the value of the totalowncurrentbalance property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNCURRENTBALANCE(String value) {
            this.totalowncurrentbalance = value;
        }

        /**
         * Gets the value of the totalothercurrentbalance property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERCURRENTBALANCE() {
            return totalothercurrentbalance;
        }

        /**
         * Sets the value of the totalothercurrentbalance property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERCURRENTBALANCE(String value) {
            this.totalothercurrentbalance = value;
        }

        /**
         * Gets the value of the totalowninstallmentamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOWNINSTALLMENTAMOUNT() {
            return totalowninstallmentamount;
        }

        /**
         * Sets the value of the totalowninstallmentamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOWNINSTALLMENTAMOUNT(String value) {
            this.totalowninstallmentamount = value;
        }

        /**
         * Gets the value of the totalotherinstallmentamount property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTOTALOTHERINSTALLMENTAMOUNT() {
            return totalotherinstallmentamount;
        }

        /**
         * Sets the value of the totalotherinstallmentamount property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTOTALOTHERINSTALLMENTAMOUNT(String value) {
            this.totalotherinstallmentamount = value;
        }

        /**
         * Gets the value of the maxworstdelequency property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getMAXWORSTDELEQUENCY() {
            return maxworstdelequency;
        }

        /**
         * Sets the value of the maxworstdelequency property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setMAXWORSTDELEQUENCY(String value) {
            this.maxworstdelequency = value;
        }

    }

}
