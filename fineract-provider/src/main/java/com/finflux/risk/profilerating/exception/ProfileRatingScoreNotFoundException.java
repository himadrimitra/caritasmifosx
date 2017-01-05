package com.finflux.risk.profilerating.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ProfileRatingScoreNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ProfileRatingScoreNotFoundException(final Long id) {
        super("error.msg.profile.rating.score.invalid", "Profile rating score with identifier " + id + " does not exist", id);
    }
}