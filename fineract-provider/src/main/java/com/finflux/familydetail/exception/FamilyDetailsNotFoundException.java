package com.finflux.familydetail.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class FamilyDetailsNotFoundException extends AbstractPlatformResourceNotFoundException {

    public FamilyDetailsNotFoundException(final Long id) {
        super("error.msg.family.details.id.invalid", "Family details with identifier " + id + " does not exist", id);
    }
}