package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.google.gson.JsonArray;

@Entity
@Table(name = "f_investment_account", uniqueConstraints = { @UniqueConstraint(columnNames = { "account_no" }, name = "ia_account_no_UNIQUE"),
        @UniqueConstraint(columnNames = { "external_id" }, name = "ia_external_id_UNIQUE") })
public class InvestmentAccount extends AbstractPersistable<Long>{
    
    @Column(name = "account_no", length = 30, unique = true, nullable = false)
    protected String accountNumber;

    @Column(name = "external_id", nullable = true)
    protected String externalId;
    
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;
    
    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private CodeValue partner;
    
    @ManyToOne
    @JoinColumn(name = "investment_product_id", nullable = false)
    private InvestmentProduct investmentProduct;
    
    @Column(name = "status_enum", nullable = false)
    protected Integer status;
    
    @Embedded
    protected MonetaryCurrency currency;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "submittedon_date", nullable = true)
    protected Date submittedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    protected AppUser submittedBy;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "approvedon_date", nullable = true)
    protected Date approvedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "approvedon_userid", nullable = true)
    protected AppUser approvedBy;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "activatedon_date", nullable = true)
    protected Date activatedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "activatedon_userid", nullable = true)
    protected AppUser activatedBy;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "investmenton_date", nullable = true)
    protected Date investmentOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "investmenton_userid", nullable = true)
    protected AppUser investedBy;
    
    @Column(name = "investment_amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal investmentAmount;

    @Column(name = "interest_rate", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestRate;

    @Column(name = "interest_rate_type", nullable = false)
    private Integer interestRateType;
    
    @Column(name = "investment_term", nullable = false)
    private Integer investmentTerm;
    
    @Column(name = "investment_term_type", nullable = false)
    private Integer investmentTermType;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "maturityon_date", nullable = true)
    protected Date maturityOnDate;
    
    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "maturityon_userid", nullable = true)
    protected AppUser maturityBy;
    
    @Column(name = "maturity_amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal maturityAmount;
    
    @Column(name = "reinvest_after_maturity", nullable = false)
    private boolean reinvestAfterMaturity;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="investmentAccount", orphanRemoval = true)
    private Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="investmentAccount", orphanRemoval = true)
    private Set<InvestmentAccountCharge> investmentAccountCharges;
    
    @OrderBy(value = "dateOf, createdDate, id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "investmentAccount", orphanRemoval = true)
    protected final List<InvestmentTransaction> transactions = new ArrayList<>();
    
    @Transient
    protected boolean accountNumberRequiresAutoGeneration = false;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "rejecton_date", nullable = true)
    protected Date rejectOnDate;
    
    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "rejecton_userid", nullable = true)
    protected AppUser rejectBy;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "closeon_date", nullable = true)
    protected Date closeOnDate;
    
    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "closeon_userid", nullable = true)
    protected AppUser closeBy;
    
    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;
    
    @Column(name = "track_source_accounts", nullable = false)
    private boolean trackSourceAccounts;
    
    protected InvestmentAccount(){
        
    }

    private InvestmentAccount(String accountNumber, String externalId, Office office, CodeValue partner,
            InvestmentProduct investmentProduct, Integer status, MonetaryCurrency currency, Date submittedOnDate, AppUser submittedBy,
            Date approvedOnDate, AppUser approvedBy, Date activatedOnDate, AppUser activatedBy, Date investmentOnDate, AppUser investedBy,
            BigDecimal investmentAmount, BigDecimal interestRate, Integer interestRateType, Integer investmentTerm, Integer investmentTermType,
            Date maturityOnDate, AppUser maturityBy, BigDecimal maturityAmount, boolean reinvestAfterMaturity,
            Staff staff, boolean trackSourceAccounts) {
        if (StringUtils.isBlank(accountNumber)) {
            this.accountNumber = new RandomPasswordGenerator(19).generate();
            this.accountNumberRequiresAutoGeneration = true;
        } else {
            this.accountNumber = accountNumber;
        }
        this.externalId = externalId;
        this.office = office;
        this.partner = partner;
        this.investmentProduct = investmentProduct;
        this.status = status;
        this.currency = currency;
        this.submittedOnDate = submittedOnDate;
        this.submittedBy = submittedBy;
        this.approvedOnDate = approvedOnDate;
        this.approvedBy = approvedBy;
        this.activatedOnDate = activatedOnDate;
        this.activatedBy = activatedBy;
        this.investmentOnDate = investmentOnDate;
        this.investedBy = investedBy;
        this.investmentAmount = investmentAmount;
        this.interestRate = interestRate;
        this.interestRateType = interestRateType;
        this.investmentTerm = investmentTerm;
        this.investmentTermType = investmentTermType;
        this.maturityOnDate = maturityOnDate;
        this.maturityBy = maturityBy;
        this.maturityAmount = maturityAmount;
        this.reinvestAfterMaturity = reinvestAfterMaturity;
        this.investmentAccountSavingsLinkages = null;
        this.investmentAccountCharges = null;
        this.rejectOnDate = null;
        this.rejectBy = null;
        this.closeBy = null;
        this.closeOnDate = null;
        this.staff = staff;
        this.trackSourceAccounts = trackSourceAccounts;
    }
    
    public static InvestmentAccount create(String externalId, Office office, CodeValue partner, InvestmentProduct investmentProduct,
            Integer status, MonetaryCurrency currency, Date submittedOnDate, AppUser submittedBy, Date approvedOnDate,
            AppUser approvedBy, Date activatedOnDate, AppUser activatedBy, Date investmentOnDate, AppUser investedBy, BigDecimal investmentAmount,
            BigDecimal interestRate, Integer interestRateType, Integer investmentTerm, Integer investmentTermType, Date maturityOnDate,
            AppUser maturityBy, BigDecimal maturityAmount, boolean reinvestAfterMaturity, Staff staff,
            boolean trackSourceAccounts) {
       String accountNumber = null;
      return new InvestmentAccount(accountNumber, externalId,  office,  partner, investmentProduct, status,  currency,  submittedOnDate, submittedBy,
              approvedOnDate, approvedBy,  activatedOnDate,  activatedBy, investmentOnDate, investedBy, investmentAmount,interestRate, interestRateType, 
               investmentTerm, investmentTermType, maturityOnDate,  maturityBy,  maturityAmount, reinvestAfterMaturity,staff,
               trackSourceAccounts);
    }

    
    public String getAccountNumber() {
        return this.accountNumber;
    }

    
    public String getExternalId() {
        return this.externalId;
    }

    
    public Office getOffice() {
        return this.office;
    }

    
    public CodeValue getPartner() {
        return this.partner;
    }

    
    public InvestmentProduct getInvestmentProduct() {
        return this.investmentProduct;
    }

    
    public Integer getStatus() {
        return this.status;
    }

    
    public MonetaryCurrency getCurrency() {
        return this.currency;
    }

    
    public LocalDate getSubmittedOnDate() {
        return new LocalDate(this.submittedOnDate);
    }

    
    public AppUser getSubmittedBy() {
        return this.submittedBy;
    }

    
    public LocalDate getApprovedOnDate() {
        return new LocalDate(this.approvedOnDate);
    }

    
    public AppUser getApprovedBy() {
        return this.approvedBy;
    }

    
    public LocalDate getActivatedOnDate() {
        return new LocalDate(this.activatedOnDate);
    }

    
    public AppUser getActivatedBy() {
        return this.activatedBy;
    }

    
    public LocalDate getInvestmentOnDate() {
        return new LocalDate(this.investmentOnDate);
    }

    
    public AppUser getInvestedBy() {
        return this.investedBy;
    }

    
    public BigDecimal getInvestmentAmount() {
        return this.investmentAmount;
    }

    
    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    
    public Integer getInterestRateType() {
        return this.interestRateType;
    }

    
    public Integer getInvestmentTerm() {
        return this.investmentTerm;
    }

    
    public Integer getInvestmentTermType() {
        return this.investmentTermType;
    }

    
    public Date getMaturityOnDate() {
        return this.maturityOnDate;
    }

    
    public AppUser getMaturityBy() {
        return this.maturityBy;
    }

    
    public BigDecimal getMaturityAmount() {
        return this.maturityAmount;
    }

    
    public boolean isReinvestAfterMaturity() {
        return this.reinvestAfterMaturity;
    }

    
    public Set<InvestmentAccountSavingsLinkages> getInvestmentAccountSavingsLinkages() {
        return this.investmentAccountSavingsLinkages;
    }

    
    public Set<InvestmentAccountCharge> getInvestmentAccountCharges() {
        return this.investmentAccountCharges;
    }

    
    public void setInvestmentAccountSavingsLinkages(Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages) {
        this.investmentAccountSavingsLinkages = investmentAccountSavingsLinkages;
    }

    
    public void setInvestmentAccountCharges(Set<InvestmentAccountCharge> investmentAccountCharges) {
        this.investmentAccountCharges = investmentAccountCharges;
    }

    
    public boolean isAccountNumberRequiresAutoGeneration() {
        return this.accountNumberRequiresAutoGeneration;
    }

    public void updateAccountNo(final String accountIdentifier) {
        this.accountNumber = accountIdentifier;
        this.accountNumberRequiresAutoGeneration = false;
    }

    
    public void setStatus(Integer status) {
        this.status = status;
    }

    
    public void setApprovedOnDate(Date approvedOnDate) {
        this.approvedOnDate = approvedOnDate;
    }

    
    public void setApprovedBy(AppUser approvedBy) {
        this.approvedBy = approvedBy;
    }

    
    public void setActivatedOnDate(Date activatedOnDate) {
        this.activatedOnDate = activatedOnDate;
    }

    
    public void setActivatedBy(AppUser activatedBy) {
        this.activatedBy = activatedBy;
    }

    public Date getRejectOnDate() {
        return this.rejectOnDate;
    }

    
    public void setRejectOnDate(Date rejectOnDate) {
        this.rejectOnDate = rejectOnDate;
    }

    
    public AppUser getRejectBy() {
        return this.rejectBy;
    }

    public List<InvestmentTransaction> getTransactions() {
        return this.transactions;
    }
    
    public void setRejectBy(AppUser rejectBy) {
        this.rejectBy = rejectBy;
    }

    
    public void setSubmittedOnDate(Date submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    
    public void setSubmittedBy(AppUser submittedBy) {
        this.submittedBy = submittedBy;
    }
    
    public boolean isTrackSourceAccounts(){
        return this.trackSourceAccounts;
    }
    
    public Map<String, Object> deriveAccountingBridgeData(final CurrencyData currencyData, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final Map<String, Object> accountingBridgeData = new LinkedHashMap<>();
        accountingBridgeData.put("investmentId", getId());
        accountingBridgeData.put("investmentProductId", productId());
        accountingBridgeData.put("currency", currencyData);
        accountingBridgeData.put("officeId", getOfficeId());
        accountingBridgeData.put("cashBasedAccountingEnabled", isCashBasedAccountingEnabledOnSavingsProduct());

        final List<Map<String, Object>> newSavingsTransactions = new ArrayList<>();
        for (final InvestmentTransaction transaction : this.transactions) {
            if (transaction.isReversed() && !existingReversedTransactionIds.contains(transaction.getId())) {
                newSavingsTransactions.add(transaction.toMapData(currencyData));
            } else if (!existingTransactionIds.contains(transaction.getId())) {
                newSavingsTransactions.add(transaction.toMapData(currencyData));
            }
        }

        accountingBridgeData.put("newInvestmentTransactions", newSavingsTransactions);
        return accountingBridgeData;
    }
    
    public Long getOfficeId(){
        return this.office.getId();
    }
    
    public Long productId() {
        return this.investmentProduct.getId();
    }

    private Boolean isCashBasedAccountingEnabledOnSavingsProduct() {
        return this.investmentProduct.isCashBasedAccountingEnabled();
    }
    
    public Collection<Long> findExistingTransactionIds() {

        final Collection<Long> ids = new ArrayList<>();

        for (final InvestmentTransaction transaction : this.transactions) {
            ids.add(transaction.getId());
        }

        return ids;
    }

    public Collection<Long> findExistingReversedTransactionIds() {

        final Collection<Long> ids = new ArrayList<>();

        for (final InvestmentTransaction transaction : this.transactions) {
            if (transaction.isReversed()) {
                ids.add(transaction.getId());
            }
        }

        return ids;
    }

    
    public void setMaturityBy(AppUser maturityBy) {
        this.maturityBy = maturityBy;
    }

    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public void updateOffice(Office office ){
        this.office = office;
    }
    
    public void updatePrtner(final CodeValue partner){
        this.partner = partner;
    }
    
    public void updateInvestmentProduct(final InvestmentProduct investmentProduct){
        this.investmentProduct = investmentProduct;
    }
    
    public void updateStaff(final Staff staff){
        this.staff = staff;
    }
    
    public void updateInvestmentAccountSavingsLinkages(final Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages) {
        this.investmentAccountSavingsLinkages.clear();
        this.investmentAccountSavingsLinkages.addAll(investmentAccountSavingsLinkages);
    }
    
    public void updateInvestmentAccountCharges(final Set<InvestmentAccountCharge> investmentAccountCharges) {
        this.investmentAccountCharges.clear();
        this.investmentAccountCharges.addAll(investmentAccountCharges);
    }

    public void modifyApplication(final JsonCommand command, final Map<String, Object> actualChanges) {
        if (command.isChangeInLongParameterNamed(InvestmentAccountApiConstants.officeIdParamName, this.getOfficeId())) {
            final Long newValue = command.longValueOfParameterNamed(InvestmentAccountApiConstants.officeIdParamName);
            actualChanges.put(InvestmentAccountApiConstants.officeIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(InvestmentAccountApiConstants.partnerIdParamName, this.partner.getId())) {
            final Long newValue = command.longValueOfParameterNamed(InvestmentAccountApiConstants.partnerIdParamName);
            actualChanges.put(InvestmentAccountApiConstants.partnerIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(InvestmentAccountApiConstants.investmetProductIdParamName,
                this.getInvestmentProduct().getId())) {
            final Long newValue = command.longValueOfParameterNamed(InvestmentAccountApiConstants.investmetProductIdParamName);
            actualChanges.put(InvestmentAccountApiConstants.investmetProductIdParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentAccountApiConstants.statusParamName, this.status)) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.statusParamName);
            this.status = newValue;
            actualChanges.put(InvestmentAccountApiConstants.statusParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(InvestmentAccountApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(InvestmentAccountApiConstants.externalIdParamName);
            this.externalId = newValue;
            actualChanges.put(InvestmentAccountApiConstants.externalIdParamName, newValue);
        }

        String currencyCode = this.currency.getCode();
        if (command.isChangeInStringParameterNamed(InvestmentAccountApiConstants.currencyCodeParamName, currencyCode)) {
            currencyCode = command.stringValueOfParameterNamed(InvestmentAccountApiConstants.currencyCodeParamName);
            actualChanges.put(InvestmentAccountApiConstants.currencyCodeParamName, currencyCode);
        }

        Integer digitsAfterDecimal = this.currency.getDigitsAfterDecimal();
        if (command.isChangeInIntegerParameterNamed(InvestmentAccountApiConstants.digitsAfterDecimalParamName, digitsAfterDecimal)) {
            digitsAfterDecimal = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.digitsAfterDecimalParamName);
            actualChanges.put(InvestmentAccountApiConstants.digitsAfterDecimalParamName, digitsAfterDecimal);
        }

        Integer inMultiplesOf = this.currency.getCurrencyInMultiplesOf();
        if (command.isChangeInIntegerParameterNamed(InvestmentAccountApiConstants.inMultiplesOfParamName, inMultiplesOf)) {
            inMultiplesOf = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.inMultiplesOfParamName);
            actualChanges.put(InvestmentAccountApiConstants.inMultiplesOfParamName, inMultiplesOf);
        }

        this.currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal, inMultiplesOf);

        if (command.isChangeInDateParameterNamed(InvestmentAccountApiConstants.submittedOnDateParamName, this.submittedOnDate)) {
            Date newValue = command.DateValueOfParameterNamed(InvestmentAccountApiConstants.submittedOnDateParamName);
            this.submittedOnDate = newValue;
            actualChanges.put(InvestmentAccountApiConstants.submittedOnDateParamName, newValue);
        }

        if (command.isChangeInDateParameterNamed(InvestmentAccountApiConstants.approvedOnDateParamName, this.approvedOnDate)) {
            Date newValue = command.DateValueOfParameterNamed(InvestmentAccountApiConstants.approvedOnDateParamName);
            this.approvedOnDate = newValue;
            actualChanges.put(InvestmentAccountApiConstants.approvedOnDateParamName, newValue);
        }

        if (command.isChangeInDateParameterNamed(InvestmentAccountApiConstants.activatedOnDateParamName, this.activatedOnDate)) {
            Date newValue = command.DateValueOfParameterNamed(InvestmentAccountApiConstants.activatedOnDateParamName);
            this.activatedOnDate = newValue;
            actualChanges.put(InvestmentAccountApiConstants.activatedOnDateParamName, newValue);
        }

        if (command.isChangeInDateParameterNamed(InvestmentAccountApiConstants.investmentOnDateParamName, this.investmentOnDate)) {
            Date newValue = command.DateValueOfParameterNamed(InvestmentAccountApiConstants.investmentOnDateParamName);
            this.investmentOnDate = newValue;
            actualChanges.put(InvestmentAccountApiConstants.investmentOnDateParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(InvestmentAccountApiConstants.investmentAmountParamName, this.investmentAmount)) {
            BigDecimal newValue = command.bigDecimalValueOfParameterNamed(InvestmentAccountApiConstants.investmentAmountParamName);
            this.investmentAmount = newValue;
            actualChanges.put(InvestmentAccountApiConstants.investmentAmountParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(InvestmentAccountApiConstants.interestRateParamName, this.interestRate)) {
            BigDecimal newValue = command.bigDecimalValueOfParameterNamed(InvestmentAccountApiConstants.interestRateParamName);
            this.interestRate = newValue;
            actualChanges.put(InvestmentAccountApiConstants.interestRateParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentAccountApiConstants.interestRateTypeParamName, this.interestRateType)) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.interestRateTypeParamName);
            this.interestRateType = newValue;
            actualChanges.put(InvestmentAccountApiConstants.interestRateTypeParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentAccountApiConstants.investmentTermParamName, this.investmentTerm)) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.investmentTermParamName);
            this.investmentTerm = newValue;
            actualChanges.put(InvestmentAccountApiConstants.investmentTermParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(InvestmentAccountApiConstants.investmentTermTypeParamName, this.investmentTermType)) {
            final Integer newValue = command.integerValueOfParameterNamed(InvestmentAccountApiConstants.investmentTermTypeParamName);
            this.investmentTermType = newValue;
            actualChanges.put(InvestmentAccountApiConstants.investmentTermTypeParamName, newValue);
        }

        if (command.isChangeInDateParameterNamed(InvestmentAccountApiConstants.maturityOnDateParamName, this.maturityOnDate)) {
            Date newValue = command.DateValueOfParameterNamed(InvestmentAccountApiConstants.maturityOnDateParamName);
            this.maturityOnDate = newValue;
            actualChanges.put(InvestmentAccountApiConstants.maturityOnDateParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(InvestmentAccountApiConstants.maturityAmountParamName, this.maturityAmount)) {
            BigDecimal newValue = command.bigDecimalValueOfParameterNamed(InvestmentAccountApiConstants.maturityAmountParamName);
            this.maturityAmount = newValue;
            actualChanges.put(InvestmentAccountApiConstants.maturityAmountParamName, newValue);
        }

        if (command.isChangeInBooleanParameterNamed(InvestmentAccountApiConstants.reinvestAfterMaturityParamName,
                this.reinvestAfterMaturity)) {
            boolean newValue = command.booleanPrimitiveValueOfParameterNamed(InvestmentAccountApiConstants.reinvestAfterMaturityParamName);
            this.reinvestAfterMaturity = newValue;
            actualChanges.put(InvestmentAccountApiConstants.reinvestAfterMaturityParamName, newValue);
        }

        if (command.isChangeInBooleanParameterNamed(InvestmentAccountApiConstants.trackSourceAccountsParamName, this.trackSourceAccounts)) {
            boolean newValue = command.booleanPrimitiveValueOfParameterNamed(InvestmentAccountApiConstants.trackSourceAccountsParamName);
            this.trackSourceAccounts = newValue;
            actualChanges.put(InvestmentAccountApiConstants.trackSourceAccountsParamName, newValue);
        }
        
        Long staffId = null;
        if (this.staff != null) {
            staffId = this.staff.getId();
        }
        if (command.isChangeInLongParameterNamed(InvestmentAccountApiConstants.staffIdParamName, staffId)) {
            final Long newValue = command.longValueOfParameterNamed(InvestmentAccountApiConstants.staffIdParamName);
            actualChanges.put(InvestmentAccountApiConstants.staffIdParamName, newValue);
        }

        if (command.hasParameter(InvestmentAccountApiConstants.chargesParamName)) {
            final JsonArray jsonArray = command.arrayOfParameterNamed(InvestmentAccountApiConstants.chargesParamName);
            if (jsonArray != null) {
                actualChanges.put(InvestmentAccountApiConstants.chargesParamName,
                        command.jsonFragment(InvestmentAccountApiConstants.chargesParamName));
            }
        }

        if (this.trackSourceAccounts) {
            final JsonArray actions = command.arrayOfParameterNamed(InvestmentAccountApiConstants.savingsAccountsParamName);
            if (actions != null) {
                actualChanges.put(InvestmentAccountApiConstants.savingsAccountsParamName,
                        command.jsonFragment(InvestmentAccountApiConstants.savingsAccountsParamName));
            }
        } else {
            this.investmentAccountSavingsLinkages = new HashSet<>();

        }

    }

	public LocalDate getCloseOnDate() {
		return new LocalDate(this.closeOnDate);
	}

	public void setCloseOnDate(Date closeOnDate) {
		this.closeOnDate = closeOnDate;
	} 
    
    
    
}
