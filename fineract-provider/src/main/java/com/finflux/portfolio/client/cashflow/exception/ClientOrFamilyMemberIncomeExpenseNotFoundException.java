package com.finflux.portfolio.client.cashflow.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ClientOrFamilyMemberIncomeExpenseNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ClientOrFamilyMemberIncomeExpenseNotFoundException(final Long id) {
        super("error.msg.client.or.family.member.income.expense.id.invalid", "Client or family member income expense id " + id
                + " does not exist", id);
    }
}