package com.finflux.mandates.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidCommandQueryParamException extends AbstractPlatformDomainRuleException {

        public InvalidCommandQueryParamException(final String commandParam) {
                super("error.msg.mandates.command.invalid",
                        "command query param is invalid", commandParam);
        }
}
