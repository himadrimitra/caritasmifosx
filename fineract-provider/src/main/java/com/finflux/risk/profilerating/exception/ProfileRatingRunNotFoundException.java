package com.finflux.risk.profilerating.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ProfileRatingRunNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ProfileRatingRunNotFoundException(final Long id) {
        super("error.msg.profile.rating.run.invalid", "Profile rating run with identifier " + id + " does not exist", id);
    }
}