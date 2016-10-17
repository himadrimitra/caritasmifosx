package com.finflux.familydetail.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class FamilyDetailsSummaryNotFoundException extends AbstractPlatformResourceNotFoundException {

    public FamilyDetailsSummaryNotFoundException(final Long id) {
        super("error.msg.family.details.summary.id.invalid", "Family details summary with identifier " + id + " does not exist", id);
    }
}