package org.apache.fineract.infrastructure.sms.exception;

import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;

public class InvalidSmsStatusException extends UnrecognizedQueryParamException {

    public InvalidSmsStatusException(final String status) {
        super("status", status , new Object[] { 100, 200,300,400 });
    }
}
