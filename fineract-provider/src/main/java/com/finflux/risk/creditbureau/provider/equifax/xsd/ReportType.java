package com.finflux.risk.creditbureau.provider.equifax.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReportType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReportType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="Error" type="{http://services.equifax.com/eport/ws/schemas/1.0}ErrorType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="VerifyIDResponse" type="{http://services.equifax.com/eport/ws/schemas/1.0}responseBody" minOccurs="0"/&gt;
 *           &lt;element name="IDAndContactInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}IDAndContactType" minOccurs="0"/&gt;
 *           &lt;element name="IncomeDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}IncomeDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="EmploymentInfo" type="{http://services.equifax.com/eport/ws/schemas/1.0}EmployerDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="Score" type="{http://services.equifax.com/eport/ws/schemas/1.0}ScoreType" minOccurs="0"/&gt;
 *           &lt;element name="EquifaxScore" type="{http://services.equifax.com/eport/ws/schemas/1.0}EquifaxScoreType" minOccurs="0"/&gt;
 *           &lt;element name="AccountsSummary" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountsSummaryType" minOccurs="0"/&gt;
 *           &lt;element name="Accounts" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountsType" minOccurs="0"/&gt;
 *           &lt;element name="AccountSummary" type="{http://services.equifax.com/eport/ws/schemas/1.0}CreditReportSummaryType" minOccurs="0"/&gt;
 *           &lt;element name="GrpBalLoansAccountSummary" type="{http://services.equifax.com/eport/ws/schemas/1.0}CreditReportSummaryType" minOccurs="0"/&gt;
 *           &lt;element name="RecentActivities" type="{http://services.equifax.com/eport/ws/schemas/1.0}RecentActivitiesType" minOccurs="0"/&gt;
 *           &lt;element name="OtherKeyInd" type="{http://services.equifax.com/eport/ws/schemas/1.0}OtherKeyIndType" minOccurs="0"/&gt;
 *           &lt;element name="EnquirySummary" type="{http://services.equifax.com/eport/ws/schemas/1.0}EnquirySummaryType" minOccurs="0"/&gt;
 *           &lt;element name="Enquiries" type="{http://services.equifax.com/eport/ws/schemas/1.0}EnquiryType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="AccountDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountDetailsType" minOccurs="0"/&gt;
 *           &lt;element name="GrpBalLoansAccountDetails" type="{http://services.equifax.com/eport/ws/schemas/1.0}AccountDetailsType" minOccurs="0"/&gt;
 *           &lt;element name="GroupCreditSummary" type="{http://services.equifax.com/eport/ws/schemas/1.0}GroupCreditSummaryType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="Glossary" type="{http://services.equifax.com/eport/ws/schemas/1.0}GlossaryInfoType" minOccurs="0"/&gt;
 *           &lt;element name="Disclaimer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *           &lt;element name="ScoringElements" type="{http://services.equifax.com/eport/ws/schemas/1.0}ScoringElementsType" minOccurs="0"/&gt;
 *           &lt;element name="ConsumerDispute" type="{http://services.equifax.com/eport/ws/schemas/1.0}ConsumerDisputesType" minOccurs="0"/&gt;
 *           &lt;element name="TelecomResponse" type="{http://services.equifax.com/eport/ws/schemas/1.0}TelecomResponseType" minOccurs="0"/&gt;
 *           &lt;element name="PrescreenResponse" type="{http://services.equifax.com/eport/ws/schemas/1.0}PrescreenResponseType" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportType", propOrder = {
    "error",
    "verifyIDResponse",
    "idAndContactInfo",
    "incomeDetails",
    "employmentInfo",
    "score",
    "equifaxScore",
    "accountsSummary",
    "accounts",
    "accountSummary",
    "grpBalLoansAccountSummary",
    "recentActivities",
    "otherKeyInd",
    "enquirySummary",
    "enquiries",
    "accountDetails",
    "grpBalLoansAccountDetails",
    "groupCreditSummary",
    "glossary",
    "disclaimer",
    "scoringElements",
    "consumerDispute",
    "telecomResponse",
    "prescreenResponse"
})
public class ReportType {

    @XmlElement(name = "Error")
    protected List<ErrorType> error;
    @XmlElement(name = "VerifyIDResponse")
    protected ResponseBody verifyIDResponse;
    @XmlElement(name = "IDAndContactInfo")
    protected IDAndContactType idAndContactInfo;
    @XmlElement(name = "IncomeDetails")
    protected List<IncomeDetailsType> incomeDetails;
    @XmlElement(name = "EmploymentInfo")
    protected List<EmployerDetailsType> employmentInfo;
    @XmlElement(name = "Score")
    protected ScoreType score;
    @XmlElement(name = "EquifaxScore")
    protected EquifaxScoreType equifaxScore;
    @XmlElement(name = "AccountsSummary")
    protected AccountsSummaryType accountsSummary;
    @XmlElement(name = "Accounts")
    protected AccountsType accounts;
    @XmlElement(name = "AccountSummary")
    protected CreditReportSummaryType accountSummary;
    @XmlElement(name = "GrpBalLoansAccountSummary")
    protected CreditReportSummaryType grpBalLoansAccountSummary;
    @XmlElement(name = "RecentActivities")
    protected RecentActivitiesType recentActivities;
    @XmlElement(name = "OtherKeyInd")
    protected OtherKeyIndType otherKeyInd;
    @XmlElement(name = "EnquirySummary")
    protected EnquirySummaryType enquirySummary;
    @XmlElement(name = "Enquiries")
    protected List<EnquiryType> enquiries;
    @XmlElement(name = "AccountDetails")
    protected AccountDetailsType accountDetails;
    @XmlElement(name = "GrpBalLoansAccountDetails")
    protected AccountDetailsType grpBalLoansAccountDetails;
    @XmlElement(name = "GroupCreditSummary")
    protected List<GroupCreditSummaryType> groupCreditSummary;
    @XmlElement(name = "Glossary")
    protected GlossaryInfoType glossary;
    @XmlElement(name = "Disclaimer")
    protected String disclaimer;
    @XmlElement(name = "ScoringElements")
    protected ScoringElementsType scoringElements;
    @XmlElement(name = "ConsumerDispute")
    protected ConsumerDisputesType consumerDispute;
    @XmlElement(name = "TelecomResponse")
    protected TelecomResponseType telecomResponse;
    @XmlElement(name = "PrescreenResponse")
    protected PrescreenResponseType prescreenResponse;

    /**
     * Gets the value of the error property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorType }
     * 
     * 
     */
    public List<ErrorType> getError() {
        if (error == null) {
            error = new ArrayList<ErrorType>();
        }
        return this.error;
    }

    /**
     * Gets the value of the verifyIDResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseBody }
     *     
     */
    public ResponseBody getVerifyIDResponse() {
        return verifyIDResponse;
    }

    /**
     * Sets the value of the verifyIDResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseBody }
     *     
     */
    public void setVerifyIDResponse(ResponseBody value) {
        this.verifyIDResponse = value;
    }

    /**
     * Gets the value of the idAndContactInfo property.
     * 
     * @return
     *     possible object is
     *     {@link IDAndContactType }
     *     
     */
    public IDAndContactType getIDAndContactInfo() {
        return idAndContactInfo;
    }

    /**
     * Sets the value of the idAndContactInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDAndContactType }
     *     
     */
    public void setIDAndContactInfo(IDAndContactType value) {
        this.idAndContactInfo = value;
    }

    /**
     * Gets the value of the incomeDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the incomeDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncomeDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IncomeDetailsType }
     * 
     * 
     */
    public List<IncomeDetailsType> getIncomeDetails() {
        if (incomeDetails == null) {
            incomeDetails = new ArrayList<IncomeDetailsType>();
        }
        return this.incomeDetails;
    }

    /**
     * Gets the value of the employmentInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the employmentInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmploymentInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EmployerDetailsType }
     * 
     * 
     */
    public List<EmployerDetailsType> getEmploymentInfo() {
        if (employmentInfo == null) {
            employmentInfo = new ArrayList<EmployerDetailsType>();
        }
        return this.employmentInfo;
    }

    /**
     * Gets the value of the score property.
     * 
     * @return
     *     possible object is
     *     {@link ScoreType }
     *     
     */
    public ScoreType getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoreType }
     *     
     */
    public void setScore(ScoreType value) {
        this.score = value;
    }

    /**
     * Gets the value of the equifaxScore property.
     * 
     * @return
     *     possible object is
     *     {@link EquifaxScoreType }
     *     
     */
    public EquifaxScoreType getEquifaxScore() {
        return equifaxScore;
    }

    /**
     * Sets the value of the equifaxScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquifaxScoreType }
     *     
     */
    public void setEquifaxScore(EquifaxScoreType value) {
        this.equifaxScore = value;
    }

    /**
     * Gets the value of the accountsSummary property.
     * 
     * @return
     *     possible object is
     *     {@link AccountsSummaryType }
     *     
     */
    public AccountsSummaryType getAccountsSummary() {
        return accountsSummary;
    }

    /**
     * Sets the value of the accountsSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountsSummaryType }
     *     
     */
    public void setAccountsSummary(AccountsSummaryType value) {
        this.accountsSummary = value;
    }

    /**
     * Gets the value of the accounts property.
     * 
     * @return
     *     possible object is
     *     {@link AccountsType }
     *     
     */
    public AccountsType getAccounts() {
        return accounts;
    }

    /**
     * Sets the value of the accounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountsType }
     *     
     */
    public void setAccounts(AccountsType value) {
        this.accounts = value;
    }

    /**
     * Gets the value of the accountSummary property.
     * 
     * @return
     *     possible object is
     *     {@link CreditReportSummaryType }
     *     
     */
    public CreditReportSummaryType getAccountSummary() {
        return accountSummary;
    }

    /**
     * Sets the value of the accountSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditReportSummaryType }
     *     
     */
    public void setAccountSummary(CreditReportSummaryType value) {
        this.accountSummary = value;
    }

    /**
     * Gets the value of the grpBalLoansAccountSummary property.
     * 
     * @return
     *     possible object is
     *     {@link CreditReportSummaryType }
     *     
     */
    public CreditReportSummaryType getGrpBalLoansAccountSummary() {
        return grpBalLoansAccountSummary;
    }

    /**
     * Sets the value of the grpBalLoansAccountSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditReportSummaryType }
     *     
     */
    public void setGrpBalLoansAccountSummary(CreditReportSummaryType value) {
        this.grpBalLoansAccountSummary = value;
    }

    /**
     * Gets the value of the recentActivities property.
     * 
     * @return
     *     possible object is
     *     {@link RecentActivitiesType }
     *     
     */
    public RecentActivitiesType getRecentActivities() {
        return recentActivities;
    }

    /**
     * Sets the value of the recentActivities property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecentActivitiesType }
     *     
     */
    public void setRecentActivities(RecentActivitiesType value) {
        this.recentActivities = value;
    }

    /**
     * Gets the value of the otherKeyInd property.
     * 
     * @return
     *     possible object is
     *     {@link OtherKeyIndType }
     *     
     */
    public OtherKeyIndType getOtherKeyInd() {
        return otherKeyInd;
    }

    /**
     * Sets the value of the otherKeyInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherKeyIndType }
     *     
     */
    public void setOtherKeyInd(OtherKeyIndType value) {
        this.otherKeyInd = value;
    }

    /**
     * Gets the value of the enquirySummary property.
     * 
     * @return
     *     possible object is
     *     {@link EnquirySummaryType }
     *     
     */
    public EnquirySummaryType getEnquirySummary() {
        return enquirySummary;
    }

    /**
     * Sets the value of the enquirySummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnquirySummaryType }
     *     
     */
    public void setEnquirySummary(EnquirySummaryType value) {
        this.enquirySummary = value;
    }

    /**
     * Gets the value of the enquiries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the enquiries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnquiries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnquiryType }
     * 
     * 
     */
    public List<EnquiryType> getEnquiries() {
        if (enquiries == null) {
            enquiries = new ArrayList<EnquiryType>();
        }
        return this.enquiries;
    }

    /**
     * Gets the value of the accountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AccountDetailsType }
     *     
     */
    public AccountDetailsType getAccountDetails() {
        return accountDetails;
    }

    /**
     * Sets the value of the accountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountDetailsType }
     *     
     */
    public void setAccountDetails(AccountDetailsType value) {
        this.accountDetails = value;
    }

    /**
     * Gets the value of the grpBalLoansAccountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AccountDetailsType }
     *     
     */
    public AccountDetailsType getGrpBalLoansAccountDetails() {
        return grpBalLoansAccountDetails;
    }

    /**
     * Sets the value of the grpBalLoansAccountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountDetailsType }
     *     
     */
    public void setGrpBalLoansAccountDetails(AccountDetailsType value) {
        this.grpBalLoansAccountDetails = value;
    }

    /**
     * Gets the value of the groupCreditSummary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupCreditSummary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupCreditSummary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GroupCreditSummaryType }
     * 
     * 
     */
    public List<GroupCreditSummaryType> getGroupCreditSummary() {
        if (groupCreditSummary == null) {
            groupCreditSummary = new ArrayList<GroupCreditSummaryType>();
        }
        return this.groupCreditSummary;
    }

    /**
     * Gets the value of the glossary property.
     * 
     * @return
     *     possible object is
     *     {@link GlossaryInfoType }
     *     
     */
    public GlossaryInfoType getGlossary() {
        return glossary;
    }

    /**
     * Sets the value of the glossary property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlossaryInfoType }
     *     
     */
    public void setGlossary(GlossaryInfoType value) {
        this.glossary = value;
    }

    /**
     * Gets the value of the disclaimer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisclaimer() {
        return disclaimer;
    }

    /**
     * Sets the value of the disclaimer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisclaimer(String value) {
        this.disclaimer = value;
    }

    /**
     * Gets the value of the scoringElements property.
     * 
     * @return
     *     possible object is
     *     {@link ScoringElementsType }
     *     
     */
    public ScoringElementsType getScoringElements() {
        return scoringElements;
    }

    /**
     * Sets the value of the scoringElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoringElementsType }
     *     
     */
    public void setScoringElements(ScoringElementsType value) {
        this.scoringElements = value;
    }

    /**
     * Gets the value of the consumerDispute property.
     * 
     * @return
     *     possible object is
     *     {@link ConsumerDisputesType }
     *     
     */
    public ConsumerDisputesType getConsumerDispute() {
        return consumerDispute;
    }

    /**
     * Sets the value of the consumerDispute property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsumerDisputesType }
     *     
     */
    public void setConsumerDispute(ConsumerDisputesType value) {
        this.consumerDispute = value;
    }

    /**
     * Gets the value of the telecomResponse property.
     * 
     * @return
     *     possible object is
     *     {@link TelecomResponseType }
     *     
     */
    public TelecomResponseType getTelecomResponse() {
        return telecomResponse;
    }

    /**
     * Sets the value of the telecomResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link TelecomResponseType }
     *     
     */
    public void setTelecomResponse(TelecomResponseType value) {
        this.telecomResponse = value;
    }

    /**
     * Gets the value of the prescreenResponse property.
     * 
     * @return
     *     possible object is
     *     {@link PrescreenResponseType }
     *     
     */
    public PrescreenResponseType getPrescreenResponse() {
        return prescreenResponse;
    }

    /**
     * Sets the value of the prescreenResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrescreenResponseType }
     *     
     */
    public void setPrescreenResponse(PrescreenResponseType value) {
        this.prescreenResponse = value;
    }

}
