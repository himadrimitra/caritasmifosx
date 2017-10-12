package org.apache.fineract.portfolio.fund.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidLoanPurposeAmountException extends AbstractPlatformDomainRuleException {

    public InvalidLoanPurposeAmountException() {
        super("error.msg.mange.fund.invalid.loan.purpose.amoumt", "Invalid loan purpose amount for manage fund ");
    }

}
