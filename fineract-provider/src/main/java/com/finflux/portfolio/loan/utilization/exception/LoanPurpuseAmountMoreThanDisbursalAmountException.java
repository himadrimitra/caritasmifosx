package com.finflux.portfolio.loan.utilization.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LoanPurpuseAmountMoreThanDisbursalAmountException extends AbstractPlatformDomainRuleException {

    public LoanPurpuseAmountMoreThanDisbursalAmountException(final Long amount) {
        super("error.msg.amount.more.than.disbursal.amount", "amount:" + amount + "shold not more than disbursal amount", amount);

    }

}
