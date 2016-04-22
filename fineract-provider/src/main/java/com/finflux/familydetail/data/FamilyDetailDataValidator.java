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
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class FamilyDetailDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public FamilyDetailDataValidator(FromJsonHelper fromApiJsonHelper) {
        super();
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                FamilyDetailsApiConstants.FAMILYDETAILS_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FamilyDetailsApiConstants.FAMILY_DETAIL_RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);
        final JsonObject parentElementObj = parentElement.getAsJsonObject();

        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.familyMembersParamName, parentElement)) {
            validateEachJsonObjectForCreate(parentElement.getAsJsonObject(), baseDataValidator);
        } else if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.familyMembersParamName, parentElement)) {
            final JsonArray array = parentElementObj.get(FamilyDetailsApiConstants.familyMembersParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateEachJsonObjectForCreate(element, baseDataValidator);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachJsonObjectForCreate(final JsonObject element, DataValidatorBuilder baseDataValidator) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long clientId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.clientParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.clientParamName).value(clientId).notBlank();

        final String firstname = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.firstnameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.firstnameParamName).value(firstname).notBlank()
                .notExceedingLengthOf(50);

        final String middlename = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.middlenameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.middlenameParamName).value(middlename).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(lastname).ignoreIfNull().notExceedingLengthOf(50);

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.dobParamName, element)) {
            final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(FamilyDetailsApiConstants.dobParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.dobParamName).value(dateOfBirth).ignoreIfNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.ageParamName, element)) {
            final Integer age = this.fromApiJsonHelper.extractIntegerNamed(FamilyDetailsApiConstants.ageParamName, element, locale);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.ageParamName).value(age).ignoreIfNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.genderIdParamName, element)) {
            final Long genderId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.genderIdParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.genderIdParamName).value(genderId).longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.relationshipParamName, element)) {
            final Long relationshipId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.relationshipParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.relationshipParamName).value(relationshipId)
                    .longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.salutationParamName, element)) {
            final Long salutationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.salutationParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.salutationParamName).value(salutationId).longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.occupationalDetailsParamName, element)) {
            final Long occupationalDetailsId = this.fromApiJsonHelper.extractLongNamed(
                    FamilyDetailsApiConstants.occupationalDetailsParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.occupationalDetailsParamName).value(occupationalDetailsId)
                    .longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.educationIdParamName, element)) {
            final Long educationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.educationIdParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.educationIdParamName).value(educationId).longGreaterThanZero();
        }
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                FamilyDetailsApiConstants.FAMILYDETAILS_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FamilyDetailsApiConstants.FAMILY_DETAIL_RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);
        final JsonObject parentElementObj = parentElement.getAsJsonObject();

        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.familyMembersParamName, parentElement)) {
            validateEachObjectForUpdate(parentElement.getAsJsonObject(), baseDataValidator);
        } else if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.familyMembersParamName, parentElement)) {
            final JsonArray array = parentElementObj.get(FamilyDetailsApiConstants.familyMembersParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateEachObjectForUpdate(element, baseDataValidator);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachObjectForUpdate(final JsonObject element, final DataValidatorBuilder baseDataValidator) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long id = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.idParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.idParamName).value(id).notBlank().longGreaterThanZero();

        final String firstname = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.firstnameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.firstnameParamName).value(firstname).notBlank()
                .notExceedingLengthOf(50);

        final String middlename = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.middlenameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.middlenameParamName).value(middlename).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(lastname).ignoreIfNull().notExceedingLengthOf(50);

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.dobParamName, element)) {
            final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(FamilyDetailsApiConstants.dobParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.dobParamName).value(dateOfBirth).ignoreIfNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.ageParamName, element)) {
            final Integer age = this.fromApiJsonHelper.extractIntegerNamed(FamilyDetailsApiConstants.ageParamName, element, locale);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.ageParamName).value(age).ignoreIfNull().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.genderIdParamName, element)) {
            final Long genderId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.genderIdParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.genderIdParamName).value(genderId).longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.relationshipParamName, element)) {
            final Long relationship = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.relationshipParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.relationshipParamName).value(relationship).longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.salutationParamName, element)) {
            final Long salutation = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.salutationParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.salutationParamName).value(salutation).longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.occupationalDetailsParamName, element)) {
            final Long occupationalDetails = this.fromApiJsonHelper.extractLongNamed(
                    FamilyDetailsApiConstants.occupationalDetailsParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.occupationalDetailsParamName).value(occupationalDetails)
                    .longZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(FamilyDetailsApiConstants.educationIdParamName, element)) {
            final Long educationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.educationIdParamName, element);
            baseDataValidator.reset().parameter(FamilyDetailsApiConstants.educationIdParamName).value(educationId).longGreaterThanZero();
        }
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
