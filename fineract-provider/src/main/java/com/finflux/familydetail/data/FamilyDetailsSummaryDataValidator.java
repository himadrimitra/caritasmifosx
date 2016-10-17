package com.finflux.familydetail.data;

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

import com.finflux.familydetail.FamilyDetailsSummaryApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class FamilyDetailsSummaryDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public FamilyDetailsSummaryDataValidator(FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                FamilyDetailsSummaryApiConstants.CREATE_FAMILY_DETAILS_SUMMARY_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FamilyDetailsSummaryApiConstants.FAMILY_DETAILS_SUMMARY_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer noOfFamilyMembers = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName).value(noOfFamilyMembers).notNull()
                .integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentMinors = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName).value(noOfDependentMinors)
                .ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentAdults = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName).value(noOfDependentAdults)
                .ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentSeniors = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName).value(noOfDependentSeniors)
                .ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentsWithSeriousIllness = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName)
                .value(noOfDependentsWithSeriousIllness).ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                FamilyDetailsSummaryApiConstants.UPDATE_FAMILY_DETAILS_SUMMARY_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FamilyDetailsSummaryApiConstants.FAMILY_DETAILS_SUMMARY_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Integer noOfFamilyMembers = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfFamilyMembersParamName).value(noOfFamilyMembers).notNull()
                .integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentMinors = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentMinorsParamName).value(noOfDependentMinors)
                .ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentAdults = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentAdultsParamName).value(noOfDependentAdults)
                .ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentSeniors = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentSeniorsParamName).value(noOfDependentSeniors)
                .ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        final Integer noOfDependentsWithSeriousIllness = this.fromApiJsonHelper.extractIntegerNamed(
                FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsSummaryApiConstants.noOfDependentsWithSeriousIllnessParamName)
                .value(noOfDependentsWithSeriousIllness).ignoreIfNull().integerGreaterThanZero().notGreaterThanMax(99);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
