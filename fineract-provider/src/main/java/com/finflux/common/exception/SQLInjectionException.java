package com.finflux.common.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SQLInjectionException extends AbstractPlatformDomainRuleException {

    public SQLInjectionException() {
        super("error.msg.sql.injection.exception", "SQL Injection Exception");
    }
}