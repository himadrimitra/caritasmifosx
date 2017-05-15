package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidVoucherCommandQueryParamException extends AbstractPlatformDomainRuleException {

        public InvalidVoucherCommandQueryParamException(final String commandParam) {
                super("error.msg.vouchers.command.invalid",
                        "command query param is invalid", commandParam);
        }
}
