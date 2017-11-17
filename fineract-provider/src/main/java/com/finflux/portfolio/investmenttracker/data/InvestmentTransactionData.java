package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;

import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.LocalDate;

public class InvestmentTransactionData {

    private final Long id;
    private final Long investmentAccountId;
    private final OfficeData office;
    private final BigDecimal amount;
    private final BigDecimal runningBalance;
    private final LocalDate dateOf;
    private final InvestmentTransactionEnumData transactionType;
    private final Boolean reversed;
    private final LocalDate createdDate;
    private AppUserData appUser;

    private InvestmentTransactionData(final Long id, final Long investmentAccountId, final OfficeData office, final BigDecimal amount,
            final BigDecimal runningBalance, final LocalDate dateOf, final InvestmentTransactionEnumData transactionType,
            final Boolean reversed, final LocalDate createdDate, final AppUserData appUser) {
        this.id = id;
        this.investmentAccountId = investmentAccountId;
        this.office = office;
        this.amount = amount;
        this.runningBalance = runningBalance;
        this.dateOf = dateOf;
        this.transactionType = transactionType;
        this.reversed = reversed;
        this.createdDate = createdDate;
        this.appUser = appUser;
    }

    public static InvestmentTransactionData create(final Long id, final Long investmentAccountId, final OfficeData office,
            final BigDecimal amount, final BigDecimal runningBalance, final LocalDate dateOf,
            final InvestmentTransactionEnumData transactionType, final Boolean reversed, final LocalDate createdDate,
            final AppUserData appUser) {
        return new InvestmentTransactionData(id, investmentAccountId, office, amount, runningBalance, dateOf, transactionType, reversed,
                createdDate, appUser);
    }

    public Long getId() {
        return this.id;
    }

    public Long getInvestmentAccountId() {
        return this.investmentAccountId;
    }

    public OfficeData getOffice() {
        return this.office;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public LocalDate getDateOf() {
        return this.dateOf;
    }

    public InvestmentTransactionEnumData getTransactionType() {
        return this.transactionType;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public AppUserData getAppUser() {
        return this.appUser;
    }

    public BigDecimal getRunningBalance() {
        return this.runningBalance;
    }

    public Boolean getReversed() {
        return this.reversed;
    }

}
