package com.finflux.risk.creditbureau.provider.cibil.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

public class CibilServerConnectionException extends AbstractPlatformServiceUnavailableException {

    public CibilServerConnectionException() {
        super("error.msg.cibil.connection.failed", "Unable to connect CIBIL servers");
    }

}
