package com.finflux.mandates.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class MandateAlreadyUnderProcessingException extends AbstractPlatformDomainRuleException {

        public MandateAlreadyUnderProcessingException() {
                super("error.msg.mandates.action.with.given.office.already.under.process",
                        "Processing request with given office already under process");
        }

}
