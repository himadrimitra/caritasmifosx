package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentAccountSavingsLinkagesDuplicationException  extends AbstractPlatformDomainRuleException {

	public InvestmentAccountSavingsLinkagesDuplicationException() {
		super("error.msg.investmentaccount.saving.account.id.already.exist","Saving linkage already exist. ");
	}

}
