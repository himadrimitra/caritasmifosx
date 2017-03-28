package com.finflux.mandates.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoMandatesToBeProcessedException extends AbstractPlatformDomainRuleException {

        public NoMandatesToBeProcessedException() {
                super("error.msg.mandates.no.records.to.process",
                        "No records to process with given office");
        }

}
