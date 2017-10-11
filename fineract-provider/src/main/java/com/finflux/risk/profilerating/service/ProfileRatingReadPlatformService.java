package com.finflux.risk.profilerating.service;

import com.finflux.risk.profilerating.data.ProfileRatingScoreData;

public interface ProfileRatingReadPlatformService {

    ProfileRatingScoreData retrieveProfileRatingScoreByEntityTypeAndEntityId(final Integer entityType, final Long entityId);
}