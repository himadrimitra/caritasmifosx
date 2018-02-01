package com.finflux.portfolio.investmenttracker.Exception;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidAmountException  extends AbstractPlatformDomainRuleException  {
	
	public InvalidAmountException(final String amountType, BigDecimal amount, final String baseAmountType, BigDecimal baseAmount) {
        super("error.msg."+amountType+".amount.cannot.be.less.than." + baseAmountType+".amount" , amountType+" amount can not be less than "+baseAmountType+" amount.", amount,baseAmount);
    }

}