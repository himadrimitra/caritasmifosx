package org.apache.fineract.portfolio.charge.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class UpfrontChargeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public UpfrontChargeNotFoundException(final String entity, final String errorMessage, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super("error.msg." + entity + "." +  errorMessage , defaultUserMessage, defaultUserMessageArgs);
    }
    
}
