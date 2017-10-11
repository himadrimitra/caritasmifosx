package com.finflux.portfolio.client.cashflow.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_client_month_wise_income_expense")
public class ClientMonthWiseIncomeExpense extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "client_income_expense_id", nullable = false)
    private ClientIncomeExpense clientIncomeExpense;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "income_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal incomeAmount;

    @Column(name = "expense_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal expenseAmount;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    protected ClientMonthWiseIncomeExpense() {}

    private ClientMonthWiseIncomeExpense(final ClientIncomeExpense clientIncomeExpense, final Integer month, final Integer year,
            final BigDecimal incomeAmount, final BigDecimal expenseAmount, final Boolean isActive) {
        this.clientIncomeExpense = clientIncomeExpense;
        this.month = month;
        this.year = year;
        this.incomeAmount = incomeAmount;
        this.expenseAmount = expenseAmount;
        this.isActive = isActive;
    }

    public static ClientMonthWiseIncomeExpense create(final ClientIncomeExpense clientIncomeExpense, final Integer month,
            final Integer year, final BigDecimal incomeAmount, final BigDecimal expenseAmount, final Boolean isActive) {
        return new ClientMonthWiseIncomeExpense(clientIncomeExpense, month, year, incomeAmount, expenseAmount, isActive);
    }
}