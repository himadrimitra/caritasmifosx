package com.finflux.portfolio.cashflow.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CashFlowCategoryNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CashFlowCategoryNotFoundException(final Long id) {
        super("error.msg.cash.flow.category.id.invalid", "Cash flow category id " + id + " does not exist", id);
    }

    public CashFlowCategoryNotFoundException(final Long id, final String status) {
        super("error.msg.cash.flow.category.id.already." + status, "Cash flow category id " + id + " already " + status, id, status);
    }
}