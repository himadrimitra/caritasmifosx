package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionEnumData;
import com.finflux.portfolio.investmenttracker.service.InvestmentEnumerations;

@Entity
@Table(name = "f_investment_transaction")
public class InvestmentTransaction extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "investment_account_id", nullable = false)
    private InvestmentAccount investmentAccount;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "transaction_type_enum", nullable = false)
    private  Integer typeOf;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private  Date dateOf;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "running_balance", scale = 6, precision = 19, nullable = false)
    private BigDecimal runningBalance;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private  Date createdDate;

    @Column(name = "appuser_id", nullable = true)
    private Long appUserId;

    protected InvestmentTransaction() {
    }

    private InvestmentTransaction(final InvestmentAccount investmentAccount, final Long officeId, final Integer typeOf, final Date dateOf,
            final BigDecimal amount, final BigDecimal runningBalance, final boolean reversed, final Date createdDate, final Long appUserId) {
        this.investmentAccount = investmentAccount;
        this.officeId = officeId;
        this.typeOf = typeOf;
        this.dateOf = dateOf;
        this.amount = amount;
        this.reversed = reversed;
        this.runningBalance = runningBalance;
        this.createdDate = createdDate;
        this.appUserId = appUserId;
    }

    public static InvestmentTransaction deposit(final InvestmentAccount investmentAccount, final Long officeId, final Date dateOf,
            final BigDecimal amount, final BigDecimal runningBalance, final Date createdDate, final Long appUserId) {
        Integer type = InvestmentTransactionType.DEPOSIT.getValue();
        boolean reversed = false;
        return new InvestmentTransaction(investmentAccount, officeId, type, dateOf, amount, runningBalance, reversed, createdDate,
                appUserId);
    }
    
    public static InvestmentTransaction payCharge(final InvestmentAccount investmentAccount, final Long officeId, final Date dateOf,
            final BigDecimal amount, final BigDecimal runningBalance, final Date createdDate, final Long appUserId) {
        Integer type = InvestmentTransactionType.PAY_CHARGE.getValue();
        boolean reversed = false;
        return new InvestmentTransaction(investmentAccount, officeId, type, dateOf, amount, runningBalance, reversed, createdDate,
                appUserId);
    }
    
    public static InvestmentTransaction interestPosting(final InvestmentAccount investmentAccount, final Long officeId, final Date dateOf,
            final BigDecimal amount, final BigDecimal runningBalance, final Date createdDate, final Long appUserId) {
        Integer type = InvestmentTransactionType.INTEREST_POSTING.getValue();
        boolean reversed = false;
        return new InvestmentTransaction(investmentAccount, officeId, type, dateOf, amount, runningBalance, reversed, createdDate,
                appUserId);
    }

    public InvestmentAccount getInvestmentAccount() {
        return this.investmentAccount;
    }

    public void setInvestmentAccount(InvestmentAccount investmentAccount) {
        this.investmentAccount = investmentAccount;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRunningBalance() {
        return this.runningBalance;
    }

    public void setRunningBalance(BigDecimal runningBalance) {
        this.runningBalance = runningBalance;
    }

    public Long getAppUserId() {
        return this.appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public Integer getTypeOf() {
        return this.typeOf;
    }

    public Date getTransactionDate() {
        return this.dateOf;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    
    public boolean isReversed() {
        return this.reversed;
    }

    
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    
    public void setTypeOf(Integer typeOf) {
        this.typeOf = typeOf;
    }

    
    public void setDateOf(Date dateOf) {
        this.dateOf = dateOf;
    }

    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    

    public Map<String, Object> toMapData(final CurrencyData currencyData) {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<>();

        final InvestmentTransactionEnumData transactionType = InvestmentEnumerations.transactionType(this.typeOf);

        thisTransactionData.put("id", getId());
        thisTransactionData.put("officeId", this.officeId);
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("reversed", Boolean.valueOf(isReversed()));
        thisTransactionData.put("date", getTransactionLocalDate());
        thisTransactionData.put("currency", currencyData);
        thisTransactionData.put("amount", this.amount);

        return thisTransactionData;
    }

    private LocalDate getTransactionLocalDate() {
        return new LocalDate(this.dateOf);
    }
    
    public static InvestmentTransaction withDrawal(final InvestmentAccount investmentAccount, final Long officeId, final Date dateOf,
            final BigDecimal amount, final BigDecimal runningBalance, final Date createdDate, final Long appUserId) {
        Integer type = InvestmentTransactionType.WITHDRAWAL.getValue();
        boolean reversed = false;
        return new InvestmentTransaction(investmentAccount, officeId, type, dateOf, amount, runningBalance, reversed, createdDate,
                appUserId);
    }
    
    public static InvestmentTransaction accrualInterest(final InvestmentAccount investmentAccount, final Long officeId, final Date dateOf,
            final BigDecimal amount, final BigDecimal runningBalance, final Date createdDate, final Long appUserId) {
        Integer type = InvestmentTransactionType.ACCRUAL_INTEREST.getValue();
        boolean reversed = false;
        return new InvestmentTransaction(investmentAccount, officeId, type, dateOf, amount, runningBalance, reversed, createdDate,
                appUserId);
    }

}
