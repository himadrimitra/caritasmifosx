package org.apache.fineract.portfolio.account.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AccountTransferReverseException extends AbstractPlatformDomainRuleException {

    public AccountTransferReverseException() {
        super("error.msg.account.transfer.reverse.not.allowed.for.loan.disbursement",
                "Account transfer reversal for loan disbursal transaction is not allowed");
    }

}
