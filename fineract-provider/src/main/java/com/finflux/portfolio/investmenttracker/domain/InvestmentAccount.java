package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

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

    
    public Date getSubmittedOnDate() {
        return this.submittedOnDate;
    }

    
    public AppUser getSubmittedBy() {
        return this.submittedBy;
    }

    
    public Date getApprovedOnDate() {
        return this.approvedOnDate;
    }

    
    public AppUser getApprovedBy() {
        return this.approvedBy;
    }

    
    public Date getActivatedOnDate() {
        return this.activatedOnDate;
    }

    
    public AppUser getActivatedBy() {
        return this.activatedBy;
    }

    
    public Date getInvestmentOnDate() {
        return this.investmentOnDate;
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
    
}
