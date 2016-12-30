package com.finflux.risk.profilerating.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ProfileRatingConfigNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ProfileRatingConfigNotFoundException(final Long id) {
        super("error.msg.profile.rating.config.invalid", "Profile Rating Config with identifier " + id + " does not exist", id);
    }

    public ProfileRatingConfigNotFoundException(final Long id, final String status) {
        super("error.msg.profile.rating.config.id.is.already." + status, "Profile rating config id " + id + " is already " + status, id,
                status);
    }
}