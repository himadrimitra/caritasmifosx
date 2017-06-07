package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class AadhaarNumberNotFoundException extends AbstractPlatformResourceNotFoundException {

    public AadhaarNumberNotFoundException(final String aadhaarNumber) {
        super("error.msg.aadhaar.number.with.identifier.does.not.exist",
                "Aadhaar Number with identifier " + aadhaarNumber + " does not exist", aadhaarNumber);

    }
}
