package org.apache.fineract.portfolio.loanaccount.guarantor.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidGuarantorSavingAccountException extends AbstractPlatformDomainRuleException {

    public InvalidGuarantorSavingAccountException(final String globalisationMessageCode, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super(globalisationMessageCode, defaultUserMessage, defaultUserMessageArgs);
    }

}
