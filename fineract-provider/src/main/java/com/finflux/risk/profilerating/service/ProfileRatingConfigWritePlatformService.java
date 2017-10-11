package com.finflux.risk.profilerating.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ProfileRatingConfigWritePlatformService {

    CommandProcessingResult create(final JsonCommand command);

    CommandProcessingResult update(final Long profileRatingConfigId, final JsonCommand command);

    CommandProcessingResult activate(final Long profileRatingConfigId, JsonCommand command);

    CommandProcessingResult inActivate(final Long profileRatingConfigId, JsonCommand command);
}