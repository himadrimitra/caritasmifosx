package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RequestIdNotFoundException extends AbstractPlatformResourceNotFoundException {

    public RequestIdNotFoundException(final String requestId) {
        super("error.msg.aadhaar.request.id.with.identifier.does.not.exist",
                "Aadhaar requestId with identifier " + requestId + " does not exist", requestId);

    }

}
