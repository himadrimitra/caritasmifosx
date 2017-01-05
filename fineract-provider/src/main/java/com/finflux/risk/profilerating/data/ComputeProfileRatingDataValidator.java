package com.finflux.risk.profilerating.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.risk.profilerating.api.ComputeProfileRatingApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class ComputeProfileRatingDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ComputeProfileRatingDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForComputeProfileRating(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ComputeProfileRatingApiConstants.CREATE_COMPUT_PROFILE_RATING_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ComputeProfileRatingApiConstants.COMPUTE_PROFILE_RATING_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer scopeEntityType = this.fromApiJsonHelper.extractIntegerNamed(
                ComputeProfileRatingApiConstants.scopeEntityTypeParamName, element, locale);
        baseDataValidator.reset().parameter(ComputeProfileRatingApiConstants.scopeEntityTypeParamName).value(scopeEntityType).notNull();

        final Long scopeEntityId = this.fromApiJsonHelper
                .extractLongNamed(ComputeProfileRatingApiConstants.scopeEntityIdParamName, element);
        baseDataValidator.reset().parameter(ComputeProfileRatingApiConstants.scopeEntityIdParamName).value(scopeEntityId).notNull();

        final Integer entityType = this.fromApiJsonHelper.extractIntegerNamed(ComputeProfileRatingApiConstants.entityTypeParamName,
                element, locale);
        baseDataValidator.reset().parameter(ComputeProfileRatingApiConstants.entityTypeParamName).value(entityType).notNull();

        final Integer overriddenScore = this.fromApiJsonHelper.extractIntegerNamed(
                ComputeProfileRatingApiConstants.overriddenScoreParamName, element, locale);
        baseDataValidator.reset().parameter(ComputeProfileRatingApiConstants.overriddenScoreParamName).value(overriddenScore)
                .ignoreIfNull();

        final Long entityId = this.fromApiJsonHelper.extractLongNamed(ComputeProfileRatingApiConstants.entityIdParamName, element);
        baseDataValidator.reset().parameter(ComputeProfileRatingApiConstants.entityIdParamName).value(entityId).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}