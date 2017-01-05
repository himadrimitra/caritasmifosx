package com.finflux.risk.profilerating.service;

import java.util.Date;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.api.ComputeProfileRatingApiConstants;
import com.finflux.risk.profilerating.data.ProfileRatingRunStatus;
import com.finflux.risk.profilerating.domain.ProfileRatingConfig;
import com.finflux.risk.profilerating.domain.ProfileRatingConfigRepositoryWrapper;
import com.finflux.risk.profilerating.domain.ProfileRatingRun;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ComputeProfileRatingDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final ProfileRatingConfigRepositoryWrapper profileRatingConfigRepository;

    @Autowired
    public ComputeProfileRatingDataAssembler(final FromJsonHelper fromApiJsonHelper,
            final ProfileRatingConfigRepositoryWrapper profileRatingConfigRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.profileRatingConfigRepository = profileRatingConfigRepository;
    }

    public ProfileRatingRun assembleComputeProfileRating(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Integer scopeEntityType = this.fromApiJsonHelper.extractIntegerNamed(
                ComputeProfileRatingApiConstants.scopeEntityTypeParamName, element, locale);
        final Long scopeEntityId = this.fromApiJsonHelper
                .extractLongNamed(ComputeProfileRatingApiConstants.scopeEntityIdParamName, element);
        final Integer entityType = this.fromApiJsonHelper.extractIntegerNamed(ComputeProfileRatingApiConstants.entityTypeParamName,
                element, locale);
        final ProfileRatingConfig profileRatingConfig = this.profileRatingConfigRepository.findByType(entityType);
        final Date startTime = new Date();
        final Date endTime = null;
        final Integer status = ProfileRatingRunStatus.INITIATED.getValue();
        return ProfileRatingRun.create(scopeEntityType, scopeEntityId, entityType, profileRatingConfig.getCriteria(), startTime, endTime,
                status);
    }
}