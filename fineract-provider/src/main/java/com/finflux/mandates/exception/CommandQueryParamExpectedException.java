package com.finflux.mandates.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CommandQueryParamExpectedException extends AbstractPlatformDomainRuleException {

        public CommandQueryParamExpectedException() {
                super("error.msg.mandates.command.required",
                        "command query param is required");
        }
}
