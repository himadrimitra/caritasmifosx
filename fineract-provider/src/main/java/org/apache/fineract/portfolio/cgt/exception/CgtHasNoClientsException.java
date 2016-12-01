package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class CgtHasNoClientsException extends AbstractPlatformResourceNotFoundException {

    public CgtHasNoClientsException(String defaultUserMessage, String cgtName) {
        super("error.msg.cgt.has.no.clients", defaultUserMessage, cgtName);
    }

}
