package com.finflux.risk.profilerating.service;

import java.util.Collection;

import com.finflux.risk.profilerating.data.ProfileRatingConfigData;
import com.finflux.risk.profilerating.data.ProfileRatingConfigTemplateData;

public interface ProfileRatingConfigReadPlatformService {

    ProfileRatingConfigTemplateData retrieveTemplate();

    Collection<ProfileRatingConfigData> retrieveAll();

    ProfileRatingConfigData retrieveOne(final Long profileRatingConfigId);
}