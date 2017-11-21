package com.finflux.portfolio.investmenttracker.domain;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.inMultiplesOfParamName;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.portfolio.investmenttracker.api.InvestmentProductApiconstants;
import com.google.gson.JsonArray;

@Entity
@Table(name = "f_investment_product", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unq_name"),
        @UniqueConstraint(columnNames = { "short_name" }, name = "unq_short_name") })
public class InvestmentProduct extends AbstractPersistable<Long> {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "short_name", nullable = false, unique = true)
    private String shortName;

    @Column(name = "description")
    private String description;

    @Embedded
    private MonetaryCurrency currency;

    @Column(name = "min_nominal_interest_rate", scale = 6, precision = 19, nullable = true)
    private BigDecimal minNominalInterestRate;

    @Column(name = "default_nominal_interest_rate", scale = 6, precision = 19, nullable = false)
    private BigDecimal defaultNominalInterestRate;

    @Column(name = "max_nominal_interest_rate", scale = 6, precision = 19, nullable = true)
    private BigDecimal maxNominalInterestRate;

    @Column(name = "nominal_interest_rate_type", nullable = false)
    private Integer nominalInterestRateEnum;

    @Column(name = "interest_compounding_period_type", nullable = false)
    private Integer interestCompoundingPeriodEnum;

    @Column(name = "min_investment_term_period", nullable = true)
    private Integer minInvestmentTermPeriod;

    @Column(name = "default_investment_term_period", nullable = false)
    private Integer defaultInvestmentTermPeriod;

    @Column(name = "max_investment_term_period", nullable = true)
    private Integer maxInvestmentTermPeriod;

    @Column(name = "investment_term_type", nullable = false)
    private Integer investmentTermEnum;

    @Column(name = "override_terms_in_investment_accounts", nullable = false)
    private boolean overrideTermsInInvestmentAccounts;

    @Column(name = "nominal_interset_rate", nullable = false)
    private boolean nominalInterestRate;

    @Column(name = "interest_compounding_period", nullable = false)
    private boolean interestCompoundingPeriod;

    @Column(name = "investment_term", nullable = false)
    private boolean investmentTerm;

    @Column(name = "accounting_type", nullable = false)
    private Integer accountingType;

    @ManyToMany
    @JoinTable(name = "f_investment_product_charge", joinColumns = @JoinColumn(name = "investment_product_id"), inverseJoinColumns = @JoinColumn(name = "charge_id"))
    protected Set<Charge> charges;

    protected InvestmentProduct() {}

    public InvestmentProduct(final String name, final String shortName, final String description, final MonetaryCurrency currency,
            final BigDecimal minNominalInterestRate, final BigDecimal defaultNominalInterestRate, final BigDecimal maxNominalInterestRate,
            final Integer nominalInterestRateEnum, final Integer interestCompoundingPeriodEnum, final Integer minInvestmentTermPeriod,
            final Integer defaultInvestmentTermPeriod, final Integer maxInvestmentTermPeriod, final Integer investmentTermEnum,
            boolean overrideTermsInInvestmentAccounts, final boolean nominalInterestRate, final boolean interestCompoundingPeriod,
            final boolean investmentTerm, final Integer accountingType, final Set<Charge> charges) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.currency = currency;
        this.minNominalInterestRate = minNominalInterestRate;
        this.defaultNominalInterestRate = defaultNominalInterestRate;
        this.maxNominalInterestRate = maxNominalInterestRate;
        this.nominalInterestRateEnum = nominalInterestRateEnum;
        this.interestCompoundingPeriodEnum = interestCompoundingPeriodEnum;
        this.minInvestmentTermPeriod = minInvestmentTermPeriod;
        this.defaultInvestmentTermPeriod = defaultInvestmentTermPeriod;
        this.maxInvestmentTermPeriod = maxInvestmentTermPeriod;
        this.investmentTermEnum = investmentTermEnum;
        this.overrideTermsInInvestmentAccounts = overrideTermsInInvestmentAccounts;
        this.nominalInterestRate = nominalInterestRate;
        this.interestCompoundingPeriod = interestCompoundingPeriod;
        this.investmentTerm = investmentTerm;
        this.accountingType = accountingType;
        this.charges = charges;
    }

    public static InvestmentProduct createInvestmentProduct(final String name, final String shortName, final String description,
            final MonetaryCurrency currency, final BigDecimal minNominalInterestRate, final BigDecimal defaultNominalInterestRate,
            final BigDecimal maxNominalInterestRate, final Integer nominalInterestRateEnum, final Integer interestCompoundingPeriodEnum,
            final Integer minInvestmentTermPeriod, final Integer defaultInvestmentTermPeriod,
            final Integer maxInvestmentTermPeriod, final Integer investmentTermEnum, boolean overrideTermsInInvestmentAccounts,
            final boolean nominalInterestRate, final boolean interestCompoundingPeriod, final boolean investmentTerm,
            final Integer accountingType, final Set<Charge> charges) {

        return new InvestmentProduct(name, shortName, description, currency, minNominalInterestRate, defaultNominalInterestRate,
                maxNominalInterestRate, nominalInterestRateEnum, interestCompoundingPeriodEnum, minInvestmentTermPeriod,
                defaultInvestmentTermPeriod, maxInvestmentTermPeriod, investmentTermEnum, overrideTermsInInvestmentAccounts,
                nominalInterestRate, interestCompoundingPeriod, investmentTerm, accountingType, charges);
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

    public MonetaryCurrency getCurrency() {
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

    public Integer getNominalInterestRateEnum() {
        return this.nominalInterestRateEnum;
    }

    public Integer getInterestCompoundingPeriodEnum() {
        return this.interestCompoundingPeriodEnum;
    }

    public Integer getMinInvestmentTermPeriod() {
        return this.minInvestmentTermPeriod;
    }

    public Integer getDefaultInvestmentTermPeriod() {
        return this.defaultInvestmentTermPeriod;
    }

    public Integer getMaxInvestmentTermPeriod() {
        return this.maxInvestmentTermPeriod;
    }

    public Integer getInvestmentTermEnum() {
        return this.investmentTermEnum;
    }

    public boolean isOverrideTermsInInvestmentAccounts() {
        return this.overrideTermsInInvestmentAccounts;
    }

    public boolean isNominalInterestRate() {
        return this.nominalInterestRate;
    }

    public boolean isInterestCompoundingPeriod() {
        return this.interestCompoundingPeriod;
    }

    public boolean isInvestmentTerm() {
        return this.investmentTerm;
    }

    public Integer getAccountingType() {
        return this.accountingType;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        final String localeAsInput = command.locale();

        if (command.isChangeInStringParameterNamed(InvestmentProductApiconstants.nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(InvestmentProductApiconstants.nameParamName);
            actualChanges.put(InvestmentProductApiconstants.nameParamName, newValue);
            this.name = newValue;
        }

        if (command.isChangeInStringParameterNamed(InvestmentProductApiconstants.shortNameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(InvestmentProductApiconstants.shortNameParamName);
            actualChanges.put(InvestmentProductApiconstants.shortNameParamName, newValue);
            this.shortName = newValue;
        }

        if (command.isChangeInStringParameterNamed(InvestmentProductApiconstants.descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(InvestmentProductApiconstants.descriptionParamName);
            actualChanges.put(InvestmentProductApiconstants.descriptionParamName, newValue);
            this.description = newValue;
        }

        Integer digitsAfterDecimal = this.currency.getDigitsAfterDecimal();
        if (command.isChangeInIntegerParameterNamed(InvestmentProductApiconstants.digitsAfterDecimalParamName, digitsAfterDecimal)) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentProductApiconstants.digitsAfterDecimalParamName);
            actualChanges.put(InvestmentProductApiconstants.digitsAfterDecimalParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            digitsAfterDecimal = newValue;
            this.currency = new MonetaryCurrency(this.currency.getCode(), digitsAfterDecimal, this.currency.getCurrencyInMultiplesOf());
        }

        String currencyCode = this.currency.getCode();
        if (command.isChangeInStringParameterNamed(InvestmentProductApiconstants.currencyCodeParamName, currencyCode)) {
            final String newValue = command.stringValueOfParameterNamed(InvestmentProductApiconstants.currencyCodeParamName);
            actualChanges.put(InvestmentProductApiconstants.currencyCodeParamName, newValue);
            currencyCode = newValue;
            this.currency = new MonetaryCurrency(currencyCode, this.currency.getDigitsAfterDecimal(),
                    this.currency.getCurrencyInMultiplesOf());
        }

        Integer inMultiplesOf = this.currency.getCurrencyInMultiplesOf();
        if (command.isChangeInIntegerParameterNamed(inMultiplesOfParamName, inMultiplesOf)) {
            final Integer newValue = command.integerValueOfParameterNamed(inMultiplesOfParamName);
            actualChanges.put(InvestmentProductApiconstants.inMultiplesOfParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            inMultiplesOf = newValue;
            this.currency = new MonetaryCurrency(this.currency.getCode(), this.currency.getDigitsAfterDecimal(), inMultiplesOf);
        }

        if (command.isChangeInBigDecimalParameterNamed(InvestmentProductApiconstants.minNominalInterestRateParamName,
                this.getMinNominalInterestRate())) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(InvestmentProductApiconstants.minNominalInterestRateParamName);
            actualChanges.put(InvestmentProductApiconstants.minNominalInterestRateParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            this.minNominalInterestRate = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(InvestmentProductApiconstants.defaultNominalInterestRateParamName,
                this.getDefaultNominalInterestRate())) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(InvestmentProductApiconstants.defaultNominalInterestRateParamName);
            actualChanges.put(InvestmentProductApiconstants.defaultNominalInterestRateParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            this.defaultNominalInterestRate = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(InvestmentProductApiconstants.maxNominalInterestRateParamName,
                this.getMaxNominalInterestRate())) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(InvestmentProductApiconstants.maxNominalInterestRateParamName);
            actualChanges.put(InvestmentProductApiconstants.maxNominalInterestRateParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            this.maxNominalInterestRate = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentProductApiconstants.nominalInterestRateEnumParamName,
                this.getNominalInterestRateEnum())) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentProductApiconstants.nominalInterestRateEnumParamName);
            actualChanges.put(InvestmentProductApiconstants.nominalInterestRateEnumParamName, newValue);
            this.nominalInterestRateEnum = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName,
                this.getInterestCompoundingPeriodEnum())) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName);
            actualChanges.put(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName, newValue);
            this.interestCompoundingPeriodEnum = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentProductApiconstants.minInvestmentTermPeriodParamName,
                this.getMinInvestmentTermPeriod())) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(InvestmentProductApiconstants.minInvestmentTermPeriodParamName);
            actualChanges.put(InvestmentProductApiconstants.minInvestmentTermPeriodParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            this.minInvestmentTermPeriod = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName,
                this.getDefaultInvestmentTermPeriod())) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName);
            actualChanges.put(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            this.defaultInvestmentTermPeriod = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName,
                this.getMaxInvestmentTermPeriod())) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName);
            actualChanges.put(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName, newValue);
            actualChanges.put(InvestmentProductApiconstants.localeParamName, localeAsInput);
            this.maxInvestmentTermPeriod = newValue;
        }

        if (command
                .isChangeInIntegerParameterNamed(InvestmentProductApiconstants.investmentTermEnumParamName, this.getInvestmentTermEnum())) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentProductApiconstants.investmentTermEnumParamName);
            actualChanges.put(InvestmentProductApiconstants.investmentTermEnumParamName, newValue);
            this.investmentTermEnum = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(InvestmentProductApiconstants.overrideTermsParamName,
                this.isOverrideTermsInInvestmentAccounts())) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.overrideTermsParamName);
            actualChanges.put(InvestmentProductApiconstants.overrideTermsParamName, newValue);
            this.overrideTermsInInvestmentAccounts = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(InvestmentProductApiconstants.nominalInterestRateParamName,
                this.isNominalInterestRate())) {
            final boolean newValue = command
                    .booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.nominalInterestRateParamName);
            actualChanges.put(InvestmentProductApiconstants.nominalInterestRateParamName, newValue);
            this.nominalInterestRate = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(InvestmentProductApiconstants.interestCompoundingPeriodParamName,
                this.isInterestCompoundingPeriod())) {
            final boolean newValue = command
                    .booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.interestCompoundingPeriodParamName);
            actualChanges.put(InvestmentProductApiconstants.interestCompoundingPeriodParamName, newValue);
            this.interestCompoundingPeriod = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(InvestmentProductApiconstants.investmentTermParamName, this.isInvestmentTerm())) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.investmentTermParamName);
            actualChanges.put(InvestmentProductApiconstants.investmentTermParamName, newValue);
            this.investmentTerm = newValue;
        }

        // charges
        if (command.hasParameter(InvestmentProductApiconstants.chargesParamName)) {
            final JsonArray jsonArray = command.arrayOfParameterNamed(InvestmentProductApiconstants.chargesParamName);
            if (jsonArray != null) {
                actualChanges.put(InvestmentProductApiconstants.chargesParamName,
                        command.jsonFragment(InvestmentProductApiconstants.chargesParamName));
            }
        }

        return actualChanges;
    }

    public MonetaryCurrency currency() {
        return this.currency.copy();
    }

    public boolean update(final Set<Charge> newInvestmentProductCharges) {
        if (newInvestmentProductCharges == null) { return false; }

        boolean updated = false;
        if (this.charges != null) {
            final Set<Charge> currentSetOfCharges = new HashSet<>(this.charges);
            final Set<Charge> newSetOfCharges = new HashSet<>(newInvestmentProductCharges);

            if (!(currentSetOfCharges.equals(newSetOfCharges))) {
                updated = true;
                this.charges = newInvestmentProductCharges;
            }
        } else {
            updated = true;
            this.charges = newInvestmentProductCharges;
        }
        return updated;
    }
    
   public boolean isCashBasedAccountingEnabled(){
       return this.accountingType.equals(AccountingRuleType.CASH_BASED.getValue());
   }

}
