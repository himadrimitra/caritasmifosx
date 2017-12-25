package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ActiveInvestmentAccountException  extends AbstractPlatformDomainRuleException {

	public ActiveInvestmentAccountException() {
		super("error.msg.charge.has.active.investment.account","Charge can not be updated due to active investment account.");
	}

}
