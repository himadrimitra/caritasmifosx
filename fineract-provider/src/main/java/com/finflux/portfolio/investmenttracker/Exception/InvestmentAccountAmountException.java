package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentAccountAmountException  extends AbstractPlatformDomainRuleException {

	public InvestmentAccountAmountException() {
		super("error.msg.investment.amount.not.equal.to.sum.of.individual.amount" , "Investment Amount is not equal to sum of individual amount.");
	}

}
