package com.finflux.reconcilation.bank.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class BankNotAssociatedExcecption extends AbstractPlatformDomainRuleException {

    public BankNotAssociatedExcecption() {
        super("error.msg.bank.not.associated.to.bankstatement.", "Bank is not associated to bankstatement.");
    }
}
