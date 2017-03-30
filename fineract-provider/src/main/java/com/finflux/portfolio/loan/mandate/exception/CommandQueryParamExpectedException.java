package com.finflux.portfolio.loan.mandate.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CommandQueryParamExpectedException extends AbstractPlatformDomainRuleException {

        public CommandQueryParamExpectedException() {
                super("error.msg.loan.mandate.command.required",
                        "command query param is required");
        }
}
