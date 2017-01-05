package com.finflux.risk.profilerating.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ComputeProfileRatingWritePlatformService {

    CommandProcessingResult computeProfileRating(final JsonCommand command);
}