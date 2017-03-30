package com.finflux.portfolio.loan.mandate.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidCommandQueryParamException extends AbstractPlatformDomainRuleException {

        public InvalidCommandQueryParamException(final String commandParam) {
                super("error.msg.loan.mandate.command.invalid",
                        "command query param is invalid", commandParam);
        }
}
