package com.finflux.portfolio.loanproduct.creditbureau.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CreditBureauLoanProductMappingNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CreditBureauLoanProductMappingNotFoundException(final Long id) {
        super("error.msg.credit.bureau.loan.product.mapping.id.invalid", "Credit bureau loan product mapping id " + id + " does not exist",
                id);
    }

    public CreditBureauLoanProductMappingNotFoundException(final Long id, final String status) {
        super("error.msg.credit.bureau.loan.product.mapping.id.is.already." + status, "Credit bureau loan product mapping id " + id
                + " is already " + status, id, status);
    }
}