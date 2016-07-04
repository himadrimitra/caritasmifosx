package com.finflux.reconcilation.bank.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class BankAssociatedToBankStatementCanNotBeDeletedOrUpdated extends AbstractPlatformDomainRuleException {

    public BankAssociatedToBankStatementCanNotBeDeletedOrUpdated() {
        super("error.msg.bank.associated.to.bankstatement.cannot.be.deleted.or.updated",
                "Bank is associated to bankstatement so can not be updated or deleted.");
    }
}
