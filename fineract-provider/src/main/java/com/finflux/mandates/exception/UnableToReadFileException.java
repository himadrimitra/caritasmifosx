package com.finflux.mandates.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class UnableToReadFileException extends AbstractPlatformDomainRuleException {

        public UnableToReadFileException() {
                super("error.msg.mandates.unable.to.read.file",
                        "Unable to read file data");
        }

}
