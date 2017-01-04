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

import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class ProfileRatingConfigDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ProfileRatingConfigDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ProfileRatingConfigApiConstants.CREATE_PROFILE_RATING_CONFIG_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ProfileRatingConfigApiConstants.PROFILE_RATING_CONFIG_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer type = this.fromApiJsonHelper.extractIntegerNamed(ProfileRatingConfigApiConstants.typeParamName, element, locale);
        baseDataValidator.reset().parameter(ProfileRatingConfigApiConstants.typeParamName).value(type).notNull();

        final Long criteriaId = this.fromApiJsonHelper.extractLongNamed(ProfileRatingConfigApiConstants.criteriaIdParamName, element);
        baseDataValidator.reset().parameter(ProfileRatingConfigApiConstants.criteriaIdParamName).value(criteriaId).notNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ProfileRatingConfigApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(ProfileRatingConfigApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ProfileRatingConfigApiConstants.UPDATE_PROFILE_RATING_CONFIG_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ProfileRatingConfigApiConstants.PROFILE_RATING_CONFIG_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer type = this.fromApiJsonHelper.extractIntegerNamed(ProfileRatingConfigApiConstants.typeParamName, element, locale);
        baseDataValidator.reset().parameter(ProfileRatingConfigApiConstants.typeParamName).value(type).notNull();

        final Long criteriaId = this.fromApiJsonHelper.extractLongNamed(ProfileRatingConfigApiConstants.criteriaIdParamName, element);
        baseDataValidator.reset().parameter(ProfileRatingConfigApiConstants.criteriaIdParamName).value(criteriaId).notNull();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ProfileRatingConfigApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(ProfileRatingConfigApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}