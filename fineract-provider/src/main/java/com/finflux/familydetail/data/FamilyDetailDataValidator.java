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
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

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

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, element.toString(),
                FamilyDetailsApiConstants.CREATE_FAMILYDETAILS_REQUEST_DATA_PARAMETERS);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long salutationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.salutationIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.salutationIdParamName).value(salutationId).ignoreIfNull()
                .longGreaterThanZero();

        final String firstName = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.firstNameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.firstNameParamName).value(firstName).notBlank()
                .notExceedingLengthOf(50);

        final String middleName = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.middleNameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.middleNameParamName).value(middleName).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String lastName = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.lastNameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.lastNameParamName).value(lastName).notNull().notExceedingLengthOf(50);

        final Long relationshipId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.relationshipIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.relationshipIdParamName).value(relationshipId).notNull()
                .longGreaterThanZero();

        final Long genderId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.genderIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.genderIdParamName).value(genderId).ignoreIfNull()
                .longGreaterThanZero();

        final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(FamilyDetailsApiConstants.dateOfBirthParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.dateOfBirthParamName).value(dateOfBirth).ignoreIfNull()
                .validateDateBefore(DateUtils.getLocalDateOfTenant());

        final Integer age = this.fromApiJsonHelper.extractIntegerNamed(FamilyDetailsApiConstants.ageParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.ageParamName).value(age).notNull().integerGreaterThanZero();

        final Long occupationDetailsId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.occupationDetailsIdParamName,
                element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.occupationDetailsIdParamName).value(occupationDetailsId)
                .ignoreIfNull().longGreaterThanZero();

        final Long educationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.educationIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.educationIdParamName).value(educationId).ignoreIfNull()
                .longGreaterThanZero();

        final Boolean isDependent = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isDependentParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.isDependentParamName).value(isDependent).ignoreIfNull();

        final Boolean isSeriousIllness = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isSeriousIllnessParamName,
                element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.isSeriousIllnessParamName).value(isSeriousIllness).ignoreIfNull();

        final Boolean isDeceased = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isDeceasedParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.isDeceasedParamName).value(isDeceased).ignoreIfNull();
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

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

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, element.toString(),
                FamilyDetailsApiConstants.UPDATE_FAMILYDETAILS_REQUEST_DATA_PARAMETERS);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long salutationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.salutationIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.salutationIdParamName).value(salutationId).ignoreIfNull()
                .longGreaterThanZero();

        final String firstname = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.firstNameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.firstNameParamName).value(firstname).notBlank()
                .notExceedingLengthOf(50);

        final String middlename = this.fromApiJsonHelper.extractStringNamed(FamilyDetailsApiConstants.middleNameParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.middleNameParamName).value(middlename).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middleNameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.middleNameParamName).value(lastname).ignoreIfNull().notExceedingLengthOf(50);

        final Long relationshipId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.relationshipIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.relationshipIdParamName).value(relationshipId).ignoreIfNull()
                .longGreaterThanZero();

        final Long genderId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.genderIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.genderIdParamName).value(genderId).ignoreIfNull()
                .longGreaterThanZero();

        final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(FamilyDetailsApiConstants.dateOfBirthParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.dateOfBirthParamName).value(dateOfBirth).ignoreIfNull()
                .validateDateBefore(DateUtils.getLocalDateOfTenant());

        final Integer age = this.fromApiJsonHelper.extractIntegerNamed(FamilyDetailsApiConstants.ageParamName, element, locale);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.ageParamName).value(age).ignoreIfNull().integerGreaterThanZero();

        final Long occupationDetailsId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.occupationDetailsIdParamName,
                element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.occupationDetailsIdParamName).value(occupationDetailsId)
                .ignoreIfNull().longGreaterThanZero();

        final Long educationId = this.fromApiJsonHelper.extractLongNamed(FamilyDetailsApiConstants.educationIdParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.educationIdParamName).value(educationId).ignoreIfNull()
                .longGreaterThanZero();

        final Boolean isDependent = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isDependentParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.isDependentParamName).value(isDependent).ignoreIfNull();

        final Boolean isSeriousIllness = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isSeriousIllnessParamName,
                element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.isSeriousIllnessParamName).value(isSeriousIllness).ignoreIfNull();

        final Boolean isDeceased = this.fromApiJsonHelper.extractBooleanNamed(FamilyDetailsApiConstants.isDeceasedParamName, element);
        baseDataValidator.reset().parameter(FamilyDetailsApiConstants.isDeceasedParamName).value(isDeceased).ignoreIfNull();
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
