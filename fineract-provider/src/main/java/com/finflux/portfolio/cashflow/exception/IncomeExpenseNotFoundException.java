package com.finflux.portfolio.cashflow.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class IncomeExpenseNotFoundException extends AbstractPlatformResourceNotFoundException {

    public IncomeExpenseNotFoundException(final Long id) {
        super("error.msg.income.expense.id.invalid", "Income or expense id " + id + " does not exist", id);
    }

    public IncomeExpenseNotFoundException(final Long id, final String status) {
        super("error.msg.income.expense.id.is.already." + status, "Income or expense id " + id + " is already " + status, id, status);
    }
}