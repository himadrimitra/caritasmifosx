package com.finflux.portfolio.loan.mandate.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoActiveMandateFoundException extends AbstractPlatformDomainRuleException {

        public NoActiveMandateFoundException() {
                super("error.msg.loan.mandate.no.active.mandate.found",
                        "No Mandate with Active Status Found, requested action cannot be performed");
        }
}
