package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidVoucherRequestParametersException extends AbstractPlatformDomainRuleException{

    public InvalidVoucherRequestParametersException(String globalisationMessageCode, String defaultUserMessage) {
        super(globalisationMessageCode, defaultUserMessage, defaultUserMessage);
    }
}
