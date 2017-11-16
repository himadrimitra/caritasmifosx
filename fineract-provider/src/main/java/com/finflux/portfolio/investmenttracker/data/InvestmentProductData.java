package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

public class InvestmentProductData {

    private final Long id;
    private final String name;
    private final String shortName;
    private final String description;
    private final CurrencyData currency;
    private final BigDecimal minNominalInterestRate;
    private final BigDecimal defaultNominalInterestRate;
    private final BigDecimal maxNominalInterestRate;
    private final EnumOptionData interestRateType;
    private final EnumOptionData compoundingPeriodType;
    private final Integer minInvesmentTermPeriod;
    private final Integer defaultInvesmentTermPeriod;
    private final Integer maxInvesmentTermPeriod;
    private final EnumOptionData invesmentTermPeriodType;
    private final boolean overrideTermsInvesmentAccounts;
    private final boolean nominalIntersetRate;
    private final boolean interestCompoundingPeriod;
    private final boolean invesmentTerm;

    // accounting
    private final EnumOptionData accountingRuleType;
    private final Map<String, Object> accountingMappings;
    private final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings;
    private final Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings;

    // charges
    private final Collection<ChargeData> charges;

    // template
    private final Collection<CurrencyData> currencyOptions;
    private final List<EnumOptionData> interestRateFrequencyTypeOptions;
    private final List<EnumOptionData> investmentCompoundingPeriodTypeOptions;
    private final List<EnumOptionData> investmentTermFrequencyTypeOptions;
    private final Collection<ChargeData> chargeOptions;
    private final List<EnumOptionData> accountingRuleOptions;
    private final Map<String, List<GLAccountData>> accountingMappingOptions;
    private final Collection<PaymentTypeData> paymentTypeOptions;

    public InvestmentProductData(final Long id, final String name, final String shortName, final String description,
            final CurrencyData currency, final BigDecimal minNominalInterestRate, final BigDecimal defaultNominalInterestRate,
            final BigDecimal maxNominalInterestRate, final EnumOptionData interestRateType, final EnumOptionData compoundingPeriodType,
            final Integer minInvesmentTermPeriod, final Integer defaultInvesmentTermPeriod, final Integer maxInvesmentTermPeriod,
            final EnumOptionData invesmentTermPeriodType, final boolean overrideTermsInvesmentAccounts, final boolean nominalIntersetRate,
            final boolean interestCompoundingPeriod, final boolean invesmentTerm, final EnumOptionData accountingRuleType,
            final Map<String, Object> accountingMappings,
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings, final Collection<ChargeData> charges,
            final Collection<CurrencyData> currencyOptions, final List<EnumOptionData> interestRateFrequencyTypeOptions,
            final List<EnumOptionData> investmentCompoundingPeriodTypeOptions,
            final List<EnumOptionData> investmentTermFrequencyTypeOptions, final Collection<ChargeData> chargesOptions,
            final List<EnumOptionData> accountingRuleOptions, final Map<String, List<GLAccountData>> accountingMappingOptions,
            final Collection<PaymentTypeData> paymentTypeOptions) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.currency = currency;
        this.minNominalInterestRate = minNominalInterestRate;
        this.defaultNominalInterestRate = defaultNominalInterestRate;
        this.maxNominalInterestRate = maxNominalInterestRate;
        this.interestRateType = interestRateType;
        this.compoundingPeriodType = compoundingPeriodType;
        this.minInvesmentTermPeriod = minInvesmentTermPeriod;
        this.defaultInvesmentTermPeriod = defaultInvesmentTermPeriod;
        this.maxInvesmentTermPeriod = maxInvesmentTermPeriod;
        this.invesmentTermPeriodType = invesmentTermPeriodType;
        this.overrideTermsInvesmentAccounts = overrideTermsInvesmentAccounts;
        this.nominalIntersetRate = nominalIntersetRate;
        this.interestCompoundingPeriod = interestCompoundingPeriod;
        this.invesmentTerm = invesmentTerm;
        this.accountingRuleType = accountingRuleType;
        this.accountingMappings = accountingMappings;
        this.paymentChannelToFundSourceMappings = paymentChannelToFundSourceMappings;
        this.feeToExpenseAccountMappings = feeToExpenseAccountMappings;
        this.charges = charges;
        this.currencyOptions = currencyOptions;
        this.interestRateFrequencyTypeOptions = interestRateFrequencyTypeOptions;
        this.investmentCompoundingPeriodTypeOptions = investmentCompoundingPeriodTypeOptions;
        this.investmentTermFrequencyTypeOptions = investmentTermFrequencyTypeOptions;
        this.chargeOptions = chargesOptions;
        this.accountingRuleOptions = accountingRuleOptions;
        this.accountingMappingOptions = accountingMappingOptions;
        this.paymentTypeOptions = paymentTypeOptions;
    }

    public static InvestmentProductData investmentProductDetails(final Long id, final String name, final String shortName,
            final String description, final CurrencyData currency, final BigDecimal minNominalInterestRate,
            final BigDecimal defaultNominalInterestRate, final BigDecimal maxNominalInterestRate, final EnumOptionData interestRateType,
            final EnumOptionData compoundingPeriodType, final Integer minInvesmentTermPeriod,
            final Integer defaultInvesmentTermPeriod, final Integer maxInvesmentTermPeriod,
            final EnumOptionData invesmentTermPeriodType, final boolean overrideTermsInvesmentAccounts, final boolean nominalIntersetRate,
            final boolean interestCompoundingPeriod, final boolean invesmentTerm, final EnumOptionData accountingRuleType) {
        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;
        final Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings = null;
        final Collection<ChargeData> charges = null;
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final List<EnumOptionData> investmentCompoundingPeriodTypeOptions = null;
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = null;
        final Collection<ChargeData> chargesOptions = null;
        final List<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;

        return new InvestmentProductData(id, name, shortName, description, currency, minNominalInterestRate, defaultNominalInterestRate,
                maxNominalInterestRate, interestRateType, compoundingPeriodType, minInvesmentTermPeriod, defaultInvesmentTermPeriod,
                maxInvesmentTermPeriod, invesmentTermPeriodType, overrideTermsInvesmentAccounts, nominalIntersetRate,
                interestCompoundingPeriod, invesmentTerm, accountingRuleType, accountingMappings, paymentChannelToFundSourceMappings,
                feeToExpenseAccountMappings, charges, currencyOptions, interestRateFrequencyTypeOptions,
                investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions, chargesOptions, accountingRuleOptions,
                accountingMappingOptions, paymentTypeOptions);
    }

    public static InvestmentProductData investmentProductDetailsWithTemplate(final Long id, final String name, final String shortName,
            final String description, final CurrencyData currency, final BigDecimal minNominalInterestRate,
            final BigDecimal defaultNominalInterestRate, final BigDecimal maxNominalInterestRate, final EnumOptionData interestRateType,
            final EnumOptionData compoundingPeriodType, final Integer minInvesmentTermPeriod,
            final Integer defaultInvesmentTermPeriod, final Integer maxInvesmentTermPeriod,
            final EnumOptionData invesmentTermPeriodType, final boolean overrideTermsInvesmentAccounts, final boolean nominalIntersetRate,
            final boolean interestCompoundingPeriod, final boolean invesmentTerm, final EnumOptionData accountingRuleType,
            final Map<String, Object> accountingMappings,
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings, final Collection<ChargeData> charges,
            final Collection<CurrencyData> currencyOptions, final List<EnumOptionData> interestRateFrequencyTypeOptions,
            final List<EnumOptionData> investmentCompoundingPeriodTypeOptions,
            final List<EnumOptionData> investmentTermFrequencyTypeOptions, final Collection<ChargeData> chargesOptions,
            final List<EnumOptionData> accountingRuleOptions, final Map<String, List<GLAccountData>> accountingMappingOptions,
            final Collection<PaymentTypeData> paymentTypeOptions) {
        return new InvestmentProductData(id, name, shortName, description, currency, minNominalInterestRate, defaultNominalInterestRate,
                maxNominalInterestRate, interestRateType, compoundingPeriodType, minInvesmentTermPeriod, defaultInvesmentTermPeriod,
                maxInvesmentTermPeriod, invesmentTermPeriodType, overrideTermsInvesmentAccounts, nominalIntersetRate,
                interestCompoundingPeriod, invesmentTerm, accountingRuleType, accountingMappings, paymentChannelToFundSourceMappings,
                feeToExpenseAccountMappings, charges, currencyOptions, interestRateFrequencyTypeOptions,
                investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions, chargesOptions, accountingRuleOptions,
                accountingMappingOptions, paymentTypeOptions);

    }

    public static InvestmentProductData withAccountDetails(final InvestmentProductData investmentProductData,
            final Map<String, Object> accountingMappings,
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings) {
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final List<EnumOptionData> investmentCompoundingPeriodTypeOptions = null;
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = null;
        final Collection<ChargeData> chargesOptions = null;
        final List<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;

        return new InvestmentProductData(investmentProductData.getId(), investmentProductData.getName(),
                investmentProductData.getShortName(), investmentProductData.getDescription(), investmentProductData.getCurrency(),
                investmentProductData.getMinNominalInterestRate(), investmentProductData.getDefaultNominalInterestRate(),
                investmentProductData.getMaxNominalInterestRate(), investmentProductData.getInterestRateType(),
                investmentProductData.getCompoundingPeriodType(), investmentProductData.getMinInvesmentTermPeriod(),
                investmentProductData.getDefaultInvesmentTermPeriod(), investmentProductData.getMaxInvesmentTermPeriod(),
                investmentProductData.getInvesmentTermPeriodType(), investmentProductData.isOverrideTermsInvesmentAccounts(),
                investmentProductData.isNominalIntersetRate(), investmentProductData.isInterestCompoundingPeriod(),
                investmentProductData.isInvesmentTerm(), investmentProductData.getAccountingRuleType(), accountingMappings,
                paymentChannelToFundSourceMappings, feeToExpenseAccountMappings, investmentProductData.getCharges(), currencyOptions,
                interestRateFrequencyTypeOptions, investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions,
                chargesOptions, accountingRuleOptions, accountingMappingOptions, paymentTypeOptions);
    }

    public static InvestmentProductData withTemplateDetails(final InvestmentProductData investmentProductData,
            final Collection<CurrencyData> currencyOptions, final List<EnumOptionData> interestRateFrequencyTypeOptions,
            final List<EnumOptionData> investmentCompoundingPeriodTypeOptions,
            final List<EnumOptionData> investmentTermFrequencyTypeOptions, final Collection<ChargeData> chargesOptions,
            final List<EnumOptionData> accountingRuleOptions, final Map<String, List<GLAccountData>> accountingMappingOptions,
            final Collection<PaymentTypeData> paymentTypeOptions) {

        return new InvestmentProductData(investmentProductData.getId(), investmentProductData.getName(),
                investmentProductData.getShortName(), investmentProductData.getDescription(), investmentProductData.getCurrency(),
                investmentProductData.getMinNominalInterestRate(), investmentProductData.getDefaultNominalInterestRate(),
                investmentProductData.getMaxNominalInterestRate(), investmentProductData.getInterestRateType(),
                investmentProductData.getCompoundingPeriodType(), investmentProductData.getMinInvesmentTermPeriod(),
                investmentProductData.getDefaultInvesmentTermPeriod(), investmentProductData.getMaxInvesmentTermPeriod(),
                investmentProductData.getInvesmentTermPeriodType(), investmentProductData.isOverrideTermsInvesmentAccounts(),
                investmentProductData.isNominalIntersetRate(), investmentProductData.isInterestCompoundingPeriod(),
                investmentProductData.isInvesmentTerm(), investmentProductData.accountingRuleType,
                investmentProductData.getAccountingMappings(), investmentProductData.getPaymentChannelToFundSourceMappings(),
                investmentProductData.getFeeToExpenseAccountMappings(), investmentProductData.getCharges(), currencyOptions,
                interestRateFrequencyTypeOptions, investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions,
                chargesOptions, accountingRuleOptions, accountingMappingOptions, paymentTypeOptions);
    }

    public static InvestmentProductData onlyTemplateDetails(final Collection<CurrencyData> currencyOptions,
            final List<EnumOptionData> interestRateFrequencyTypeOptions, final List<EnumOptionData> investmentCompoundingPeriodTypeOptions,
            final List<EnumOptionData> investmentTermFrequencyTypeOptions, final Collection<ChargeData> chargesOptions,
            final List<EnumOptionData> accountingRuleOptions, final Map<String, List<GLAccountData>> accountingMappingOptions,
            final Collection<PaymentTypeData> paymentTypeOptions) {

        Long id = null;
        String name = null;
        String shortName = null;
        String description = null;
        CurrencyData currency = null;
        BigDecimal minNominalInterestRate = null;
        BigDecimal defaultNominalInterestRate = null;
        BigDecimal maxNominalInterestRate = null;
        EnumOptionData interestRateType = null;
        EnumOptionData compoundingPeriodType = null;
        Integer minInvesmentTermPeriod = null;
        Integer defaultInvesmentTermPeriod = null;
        Integer maxInvesmentTermPeriod = null;
        EnumOptionData invesmentTermPeriodType = null;
        boolean overrideTermsInvesmentAccounts = false;
        boolean nominalIntersetRate = false;
        boolean interestCompoundingPeriod = false;
        boolean invesmentTerm = false;
        EnumOptionData accountingRuleType = null;
        Map<String, Object> accountingMappings = null;
        Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;
        Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings = null;
        Collection<ChargeData> charges = null;

        return new InvestmentProductData(id, name, shortName, description, currency, minNominalInterestRate, defaultNominalInterestRate,
                maxNominalInterestRate, interestRateType, compoundingPeriodType, minInvesmentTermPeriod, defaultInvesmentTermPeriod,
                maxInvesmentTermPeriod, invesmentTermPeriodType, overrideTermsInvesmentAccounts, nominalIntersetRate,
                interestCompoundingPeriod, invesmentTerm, accountingRuleType, accountingMappings, paymentChannelToFundSourceMappings,
                feeToExpenseAccountMappings, charges, currencyOptions, interestRateFrequencyTypeOptions,
                investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions, chargesOptions, accountingRuleOptions,
                accountingMappingOptions, paymentTypeOptions);
    }

    public static InvestmentProductData withCharges(final InvestmentProductData investmentProductData, final Collection<ChargeData> charges) {
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final List<EnumOptionData> investmentCompoundingPeriodTypeOptions = null;
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = null;
        final Collection<ChargeData> chargesOptions = null;
        final List<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;

        return new InvestmentProductData(investmentProductData.getId(), investmentProductData.getName(),
                investmentProductData.getShortName(), investmentProductData.getDescription(), investmentProductData.getCurrency(),
                investmentProductData.getMinNominalInterestRate(), investmentProductData.getDefaultNominalInterestRate(),
                investmentProductData.getMaxNominalInterestRate(), investmentProductData.getInterestRateType(),
                investmentProductData.getCompoundingPeriodType(), investmentProductData.getMinInvesmentTermPeriod(),
                investmentProductData.getDefaultInvesmentTermPeriod(), investmentProductData.getMaxInvesmentTermPeriod(),
                investmentProductData.getInvesmentTermPeriodType(), investmentProductData.isOverrideTermsInvesmentAccounts(),
                investmentProductData.isNominalIntersetRate(), investmentProductData.isInterestCompoundingPeriod(),
                investmentProductData.isInvesmentTerm(), investmentProductData.getAccountingRuleType(),
                investmentProductData.getAccountingMappings(), investmentProductData.getPaymentChannelToFundSourceMappings(),
                investmentProductData.getFeeToExpenseAccountMappings(), charges, currencyOptions, interestRateFrequencyTypeOptions,
                investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions, chargesOptions, accountingRuleOptions,
                accountingMappingOptions, paymentTypeOptions);
    }

    public boolean hasAccountingEnabled() {
        return this.accountingRuleType.getId() > AccountingRuleType.NONE.getValue();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public CurrencyData getCurrency() {
        return this.currency;
    }

    public BigDecimal getMinNominalInterestRate() {
        return this.minNominalInterestRate;
    }

    public BigDecimal getDefaultNominalInterestRate() {
        return this.defaultNominalInterestRate;
    }

    public BigDecimal getMaxNominalInterestRate() {
        return this.maxNominalInterestRate;
    }

    public EnumOptionData getInterestRateType() {
        return this.interestRateType;
    }

    public EnumOptionData getCompoundingPeriodType() {
        return this.compoundingPeriodType;
    }

    public Integer getMinInvesmentTermPeriod() {
        return this.minInvesmentTermPeriod;
    }

    public Integer getDefaultInvesmentTermPeriod() {
        return this.defaultInvesmentTermPeriod;
    }

    public Integer getMaxInvesmentTermPeriod() {
        return this.maxInvesmentTermPeriod;
    }

    public EnumOptionData getInvesmentTermPeriodType() {
        return this.invesmentTermPeriodType;
    }

    public boolean isOverrideTermsInvesmentAccounts() {
        return this.overrideTermsInvesmentAccounts;
    }

    public boolean isNominalIntersetRate() {
        return this.nominalIntersetRate;
    }

    public boolean isInterestCompoundingPeriod() {
        return this.interestCompoundingPeriod;
    }

    public boolean isInvesmentTerm() {
        return this.invesmentTerm;
    }

    public EnumOptionData getAccountingRuleType() {
        return this.accountingRuleType;
    }

    public Map<String, Object> getAccountingMappings() {
        return this.accountingMappings;
    }

    public Collection<PaymentTypeToGLAccountMapper> getPaymentChannelToFundSourceMappings() {
        return this.paymentChannelToFundSourceMappings;
    }

    public Collection<ChargeToGLAccountMapper> getFeeToExpenseAccountMappings() {
        return this.feeToExpenseAccountMappings;
    }

    public Collection<ChargeData> getCharges() {
        return this.charges;
    }

    public Collection<CurrencyData> getCurrencyOptions() {
        return this.currencyOptions;
    }

    public List<EnumOptionData> getInterestRateFrequencyTypeOptions() {
        return this.interestRateFrequencyTypeOptions;
    }

    public List<EnumOptionData> getInvestmentCompoundingPeriodTypeOptions() {
        return this.investmentCompoundingPeriodTypeOptions;
    }

    public List<EnumOptionData> getInvestmentTermFrequencyTypeOptions() {
        return this.investmentTermFrequencyTypeOptions;
    }

    public Collection<ChargeData> getChargeOptions() {
        return this.chargeOptions;
    }

    public List<EnumOptionData> getAccountingRuleOptions() {
        return this.accountingRuleOptions;
    }

    public Map<String, List<GLAccountData>> getAccountingMappingOptions() {
        return this.accountingMappingOptions;
    }

    public Collection<PaymentTypeData> getPaymentTypeOptions() {
        return this.paymentTypeOptions;
    }

    public int accountingRuleTypeId() {
        return this.accountingRuleType.getId().intValue();
    }

    public static InvestmentProductData lookup(long id, String name){
        String shortName = null;
        String description = null;
        CurrencyData currency = null;
        BigDecimal minNominalInterestRate = null;
        BigDecimal defaultNominalInterestRate = null;
        BigDecimal maxNominalInterestRate = null;
        EnumOptionData interestRateType = null;
        EnumOptionData compoundingPeriodType = null;
        Integer minInvesmentTermPeriod = null;
        Integer defaultInvesmentTermPeriod = null;
        Integer maxInvesmentTermPeriod = null;
        EnumOptionData invesmentTermPeriodType = null;
        boolean overrideTermsInvesmentAccounts = false;
        boolean nominalIntersetRate = false;
        boolean interestCompoundingPeriod = false;
        boolean invesmentTerm = false;
        EnumOptionData accountingRuleType = null;
        Map<String, Object> accountingMappings = null;
        Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;
        Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings = null;
        Collection<ChargeData> charges = null;
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final List<EnumOptionData> investmentCompoundingPeriodTypeOptions = null;
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = null;
        final Collection<ChargeData> chargesOptions = null;
        final List<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        
        return new InvestmentProductData(id, name, shortName, description, currency, minNominalInterestRate, defaultNominalInterestRate,
                maxNominalInterestRate, interestRateType, compoundingPeriodType, minInvesmentTermPeriod, defaultInvesmentTermPeriod,
                maxInvesmentTermPeriod, invesmentTermPeriodType, overrideTermsInvesmentAccounts, nominalIntersetRate,
                interestCompoundingPeriod, invesmentTerm, accountingRuleType, accountingMappings, paymentChannelToFundSourceMappings,
                feeToExpenseAccountMappings, charges, currencyOptions, interestRateFrequencyTypeOptions,
                investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions, chargesOptions, accountingRuleOptions,
                accountingMappingOptions, paymentTypeOptions);
    }
}
