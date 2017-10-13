package org.apache.fineract.portfolio.loanaccount.exception;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SubsidyAmountExceedsPrincipalOutstandingException extends AbstractPlatformDomainRuleException {

    public SubsidyAmountExceedsPrincipalOutstandingException(final Long id, final String currencyCode,final BigDecimal exceedingAmount) {
	        super("error.msg.subsidy.amount.for.loan.exceeds.principal.outstanding", "Subsidy Amount for Loan with identifier " + id 
	        		+ " exceeds with the amount of " + exceedingAmount, currencyCode, exceedingAmount);
	    }
}
