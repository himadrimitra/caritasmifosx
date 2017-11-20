package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;



public class InvestmentAccountData {

    private Long id;
    private String accountNo;
    private String externalId;
    private CurrencyData currency;
    private BigDecimal interestRate;
    private EnumOptionData interestRateType;
    private Integer invesmentTermPeriod;
    private EnumOptionData invesmentTermPeriodType;
    private InvestmentAccountTimelineData timelineData;
    private BigDecimal investmentAmount;
    private BigDecimal maturityAmount;
    private Boolean reinvestAfterMaturity;
    private Collection<InvestmentAccountChargeData> investmentAccountCharges;
    private Collection<InvestmentAccountSavingsLinkagesData> investmentSavingsLinkagesData;
    private OfficeData officeData;
    private CodeValueData partnerData;
    private InvestmentProductData investmentProductData;
    private EnumOptionData statusEnum;
    private StaffData staffData;
    private Boolean trackSourceAccounts;
    private Collection<InvestmentTransactionData> investmentAccountTransactions;
    
    //template
    private Collection<CodeValueData> partnerOptions;
    private Collection<InvestmentProductData> investmentProductOptions;
    private Collection<ChargeData> investmentChargeOptions;
    private Collection<OfficeData> officeDataOptions;
    private List<EnumOptionData> interestRateFrequencyTypeOptions;
    private List<EnumOptionData> investmentTermFrequencyTypeOptions;
    private Collection<StaffData> staffOptions;
    private List<SavingsAccountData> savingsAccounts;
    private Collection<EnumOptionData> investmentAccountStatus;
    
    public InvestmentAccountData(Long id, String accountNo, String externalId,
            CurrencyData currency, BigDecimal interestRate, EnumOptionData interestRateType, Integer invesmentTermPeriod,
            EnumOptionData invesmentTermPeriodType, InvestmentAccountTimelineData timelineData, BigDecimal investmentAmount,
            BigDecimal maturityAmount, Boolean reinvestAfterMaturity, Collection<InvestmentAccountChargeData> investmentAccountCharges,
            Collection<InvestmentAccountSavingsLinkagesData> investmentSavingsLinkagesData, OfficeData officeData,
            CodeValueData partnerData,  InvestmentProductData investmentProductData, EnumOptionData statusEnum,Collection<CodeValueData> partnerOptions,
            Collection<InvestmentProductData> investmentProductOptions, Collection<ChargeData> investmentChargeOptions,
            Collection<OfficeData> officeDataOptions,List<EnumOptionData> interestRateFrequencyTypeOptions,
            List<EnumOptionData> investmentTermFrequencyTypeOptions, StaffData staffData, Boolean trackSourceAccounts,
            Collection<StaffData> staffOptions, List<SavingsAccountData> savingsAccounts, Collection<EnumOptionData> investmentAccountStatus,
            Collection<InvestmentTransactionData> investmentAccountTransactions) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.currency = currency;
        this.interestRate = interestRate;
        this.interestRateType = interestRateType;
        this.invesmentTermPeriod = invesmentTermPeriod;
        this.invesmentTermPeriodType = invesmentTermPeriodType;
        this.timelineData = timelineData;
        this.investmentAmount = investmentAmount;
        this.maturityAmount = maturityAmount;
        this.reinvestAfterMaturity = reinvestAfterMaturity;
        this.investmentAccountCharges = investmentAccountCharges;
        this.investmentSavingsLinkagesData = investmentSavingsLinkagesData;
        this.officeData = officeData;
        this.partnerData = partnerData;
        this.partnerOptions = partnerOptions;
        this.investmentProductOptions = investmentProductOptions;
        this.investmentChargeOptions = investmentChargeOptions;
        this.officeDataOptions = officeDataOptions;
        this.investmentProductData = investmentProductData;
        this.statusEnum = statusEnum;
        this.interestRateFrequencyTypeOptions = interestRateFrequencyTypeOptions;
        this.investmentTermFrequencyTypeOptions = investmentTermFrequencyTypeOptions;
        this.staffData = staffData;
        this.trackSourceAccounts = trackSourceAccounts;
        this.staffOptions = staffOptions;
        this.savingsAccounts = savingsAccounts;
        this.investmentAccountStatus = investmentAccountStatus;
        this.investmentAccountTransactions = investmentAccountTransactions;
    }

    public static InvestmentAccountData onlyTemplateData( Collection<CodeValueData> partnerOptions,
            Collection<InvestmentProductData> investmentProductOptions, Collection<ChargeData> investmentChargeOptions,
            Collection<OfficeData> officeDataOptions, List<EnumOptionData> interestRateFrequencyTypeOptions,
            List<EnumOptionData> investmentTermFrequencyTypeOptions, Collection<StaffData> staffOptions, 
            List<SavingsAccountData> savingsAccounts, Collection<EnumOptionData> investmentAccountStatus){
       
        Long id = null;
        String accountNo = null;
        String externalId = null;
        CurrencyData currency = null;
        BigDecimal interestRate = null;
        EnumOptionData interestRateType = null;
        Integer invesmentTermPeriod = null;
        EnumOptionData invesmentTermPeriodType = null;
        InvestmentAccountTimelineData timelineData = null;
        BigDecimal investmentAmount = null;
        BigDecimal maturityAmount = null;
        Boolean reinvestAfterMaturity = null;
        Collection<InvestmentAccountChargeData> investmentAccountCharges = null;
        Collection<InvestmentAccountSavingsLinkagesData> investmentSavingsLinkagesData = null;
        OfficeData officeData = null;
        CodeValueData partnerData = null;
        InvestmentProductData investmentProductData = null;
        EnumOptionData statusEnum = null;
        StaffData staffData = null; 
        Boolean trackSourceAccounts = null;
        Collection<InvestmentTransactionData> investmentAccountTransactions = null;
        
        return new InvestmentAccountData(id,accountNo,externalId, currency,  interestRate, 
                interestRateType, invesmentTermPeriod,invesmentTermPeriodType, timelineData, investmentAmount,
                 maturityAmount,  reinvestAfterMaturity,  investmentAccountCharges,investmentSavingsLinkagesData, officeData,
                 partnerData, investmentProductData,statusEnum, partnerOptions, investmentProductOptions,investmentChargeOptions, 
                 officeDataOptions,interestRateFrequencyTypeOptions,investmentTermFrequencyTypeOptions, staffData, trackSourceAccounts,
                 staffOptions, savingsAccounts, investmentAccountStatus, investmentAccountTransactions);
    }
    
    public static InvestmentAccountData instance(Long id, String accountNo, String externalId,
            CurrencyData currency, BigDecimal interestRate, EnumOptionData interestRateType, Integer invesmentTermPeriod,
            EnumOptionData invesmentTermPeriodType, InvestmentAccountTimelineData timelineData, BigDecimal investmentAmount,
            BigDecimal maturityAmount, Boolean reinvestAfterMaturity, Collection<InvestmentAccountChargeData> investmentAccountCharges,
            Collection<InvestmentAccountSavingsLinkagesData> investmentSavingsLinkagesData, OfficeData officeData,
            CodeValueData partnerData,  InvestmentProductData investmentProductData, EnumOptionData statusEnum, StaffData staffData,
            Boolean trackSourceAccounts,Collection<InvestmentTransactionData> investmentAccountTransactions) {
        
        Collection<CodeValueData> partnerOptions = null;
        Collection<InvestmentProductData> investmentProductOptions = null;
        Collection<ChargeData> investmentChargeOptions = null;
        Collection<OfficeData> officeDataOptions = null;
        List<EnumOptionData> interestRateFrequencyTypeOptions = null;
        List<EnumOptionData> investmentTermFrequencyTypeOptions = null;
        Collection<StaffData> staffOptions = null;
        List<SavingsAccountData> savingsAccounts = null;
        Collection<EnumOptionData> investmentAccountStatus = null;

        return new InvestmentAccountData(id,accountNo,externalId, currency,  interestRate, 
                interestRateType, invesmentTermPeriod,invesmentTermPeriodType, timelineData, investmentAmount,
                 maturityAmount,  reinvestAfterMaturity,  investmentAccountCharges,investmentSavingsLinkagesData, officeData,
                 partnerData, investmentProductData, statusEnum, partnerOptions, investmentProductOptions,investmentChargeOptions,
                 officeDataOptions, interestRateFrequencyTypeOptions, investmentTermFrequencyTypeOptions, staffData, trackSourceAccounts,
                 staffOptions, savingsAccounts, investmentAccountStatus, investmentAccountTransactions);
    }
    
    public static InvestmentAccountData withTemplateData(InvestmentAccountData investmentAccountData, Collection<CodeValueData> partnerOptions,
            Collection<InvestmentProductData> investmentProductOptions, Collection<ChargeData> investmentChargeOptions,
            Collection<OfficeData> officeDataOptions, List<EnumOptionData> interestRateFrequencyTypeOptions,
            List<EnumOptionData> investmentTermFrequencyTypeOptions, Collection<StaffData> staffOptions,
            List<SavingsAccountData> savingsAccounts, Collection<EnumOptionData> investmentAccountStatus){
        
        return new InvestmentAccountData(investmentAccountData.getId(),investmentAccountData.getAccountNo(),investmentAccountData.getExternalId(), investmentAccountData.getCurrency(),  investmentAccountData.getInterestRate(), 
                investmentAccountData.getInterestRateType(), investmentAccountData.getInvesmentTermPeriod(),investmentAccountData.getInvesmentTermPeriodType(), investmentAccountData.getTimelineData(), investmentAccountData.getInvestmentAmount(),
                investmentAccountData.getMaturityAmount(),  investmentAccountData.getReinvestAfterMaturity(),  investmentAccountData.getInvestmentAccountCharges(),investmentAccountData.getInvestmentSavingsLinkagesData(), investmentAccountData.getOfficeData(),
                investmentAccountData.getPartnerData(), investmentAccountData.getInvestmentProductData(),investmentAccountData.getStatusEnum(), partnerOptions, investmentProductOptions,investmentChargeOptions, officeDataOptions,
                interestRateFrequencyTypeOptions, investmentTermFrequencyTypeOptions, investmentAccountData.staffData, investmentAccountData.trackSourceAccounts, staffOptions,savingsAccounts,
                investmentAccountStatus,investmentAccountData.getInvestmentAccountTransactions());
    }
    
    public Long getId() {
        return this.id;
    }

    
    public String getAccountNo() {
        return this.accountNo;
    }

    
    public String getExternalId() {
        return this.externalId;
    }

    public CurrencyData getCurrency() {
        return this.currency;
    }

    
    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    
    public EnumOptionData getInterestRateType() {
        return this.interestRateType;
    }

    
    public Integer getInvesmentTermPeriod() {
        return this.invesmentTermPeriod;
    }

    
    public EnumOptionData getInvesmentTermPeriodType() {
        return this.invesmentTermPeriodType;
    }

    
    public InvestmentAccountTimelineData getTimelineData() {
        return this.timelineData;
    }

    
    public BigDecimal getInvestmentAmount() {
        return this.investmentAmount;
    }

    
    public BigDecimal getMaturityAmount() {
        return this.maturityAmount;
    }

    
    public boolean isReinvestAfterMaturity() {
        return this.reinvestAfterMaturity;
    }

    
    public Collection<InvestmentAccountChargeData> getInvestmentAccountCharges() {
        return this.investmentAccountCharges;
    }

    
    public Collection<InvestmentAccountSavingsLinkagesData> getInvestmentSavingsLinkagesData() {
        return this.investmentSavingsLinkagesData;
    }

    
    public OfficeData getOfficeData() {
        return this.officeData;
    }

    
    public CodeValueData getPartnerData() {
        return this.partnerData;
    }

    
    public Collection<CodeValueData> getPartnerOptions() {
        return this.partnerOptions;
    }

    
    public Collection<InvestmentProductData> getInvestmentProductOptions() {
        return this.investmentProductOptions;
    }

    
    public Collection<ChargeData> getInvestmentChargeOptions() {
        return this.investmentChargeOptions;
    }
 
    public Collection<OfficeData> getOfficeDataOptions() {
        return this.officeDataOptions;
    }
  
    public Boolean getReinvestAfterMaturity() {
        return this.reinvestAfterMaturity;
    }

    public InvestmentProductData getInvestmentProductData() {
        return this.investmentProductData;
    }

    
    public EnumOptionData getStatusEnum() {
        return this.statusEnum;
    }

    
    public void setInvestmentAccountCharges(Collection<InvestmentAccountChargeData> investmentAccountCharges) {
        this.investmentAccountCharges = investmentAccountCharges;
    }

    
    public void setInvestmentSavingsLinkagesData(Collection<InvestmentAccountSavingsLinkagesData> investmentSavingsLinkagesData) {
        this.investmentSavingsLinkagesData = investmentSavingsLinkagesData;
    }

    
    public List<EnumOptionData> getInterestRateFrequencyTypeOptions() {
        return this.interestRateFrequencyTypeOptions;
    }

    
    public List<EnumOptionData> getInvestmentTermFrequencyTypeOptions() {
        return this.investmentTermFrequencyTypeOptions;
    }

    
    public StaffData getStaffData() {
        return this.staffData;
    }

    
    public Boolean getTrackSourceAccounts() {
        return this.trackSourceAccounts;
    }

    
    public Collection<StaffData> getStaffOptions() {
        return this.staffOptions;
    }
    
    public List<SavingsAccountData> getSavingsAccounts() {
        return this.savingsAccounts;
    }
    
    public Collection<EnumOptionData> getInvestmentAccountStatus() {
        return this.investmentAccountStatus;
    }

    
    public Collection<InvestmentTransactionData> getInvestmentAccountTransactions() {
        return this.investmentAccountTransactions;
    }

    
    public void setInvestmentAccountTransactions(Collection<InvestmentTransactionData> investmentAccountTransactions) {
        this.investmentAccountTransactions = investmentAccountTransactions;
    }

}
