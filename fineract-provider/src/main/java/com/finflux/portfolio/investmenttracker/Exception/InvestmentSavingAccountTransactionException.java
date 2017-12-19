package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentSavingAccountTransactionException  extends AbstractPlatformDomainRuleException {

	public InvestmentSavingAccountTransactionException() {
		super("err.msg.saving.transaction.has.active.investment.account","Saving transaction has active investment account.");
	}

}
