package com.finflux.kyc.address.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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

import com.finflux.kyc.address.api.AddressApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class AddressDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    /**
     * Validate Create Address Request Parameters
     * 
     * @param json
     */
    @Autowired
    public AddressDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /**
     * Validating all required parameters exists or not
     * 
     * @param entityTypeEnum
     * @param entityId
     * @param json
     */
    public void validateForCreate(final Integer entityTypeEnum, final Long entityId, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AddressApiConstants.CREATE_ADDRESS_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(AddressApiConstants.ADDRESSES_RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);
        final JsonObject parentElementObj = parentElement.getAsJsonObject();

        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(AddressApiConstants.addressesParamName, parentElement)) {
            validateEachJsonObjectForCreate(entityTypeEnum, entityId, parentElement.getAsJsonObject(), baseDataValidator);
        } else if (this.fromApiJsonHelper.parameterExists(AddressApiConstants.addressesParamName, parentElement)) {

            final JsonArray array = parentElementObj.get(AddressApiConstants.addressesParamName).getAsJsonArray();

            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateEachJsonObjectForCreate(entityTypeEnum, entityId, element, baseDataValidator);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachJsonObjectForCreate(final Integer entityTypeEnum, final Long entityId, final JsonObject element,
            DataValidatorBuilder baseDataValidator) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        baseDataValidator.reset().parameter(AddressApiConstants.entityTypeEnumParamName).value(entityTypeEnum).notNull()
                .integerGreaterThanZero().isOneOfTheseValues(AddressEntityTypeEnums.integerValues());

        baseDataValidator.reset().parameter(AddressApiConstants.entityIdParamName).value(entityId).notBlank();

        final String[] addressTypes = this.fromApiJsonHelper.extractArrayNamed(AddressApiConstants.addressTypesParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressTypesParamName).value(addressTypes).ignoreIfNull().arrayNotEmpty();

        final String houseNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.houseNoParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.houseNoParamName).value(houseNo).ignoreIfNull().notExceedingLengthOf(200);

        final String streetNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.streetNoParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.streetNoParamName).value(streetNo).ignoreIfNull().notExceedingLengthOf(200);

        final String addressLineOne = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineOneParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressLineOneParamName).value(addressLineOne).ignoreIfNull()
                .notExceedingLengthOf(200);

        final String addressLineTwo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineTwoParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressLineTwoParamName).value(addressLineTwo).ignoreIfNull()
                .notExceedingLengthOf(200);

        final String landmark = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.landmarkParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.landmarkParamName).value(landmark).ignoreIfNull().notExceedingLengthOf(100);

        final String villageTown = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.villageTownParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.villageTownParamName).value(villageTown).ignoreIfNull()
                .notExceedingLengthOf(100);

        final Long talukaId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.talukaIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.talukaIdParamName).value(talukaId).ignoreIfNull();

        final Long districtId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.districtIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.districtIdParamName).value(districtId).ignoreIfNull();

        final Long stateId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.stateIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.stateIdParamName).value(stateId).ignoreIfNull();

        final Long countryId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.countryIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.countryIdParamName).value(countryId).ignoreIfNull();

        final String postalCode = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.postalCodeParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.postalCodeParamName).value(postalCode).ignoreIfNull();

        final BigDecimal latitude = this.fromApiJsonHelper.extractBigDecimalNamed(AddressApiConstants.latitudeParamName, element, locale);
        baseDataValidator.reset().parameter(AddressApiConstants.latitudeParamName).value(latitude).ignoreIfNull();

        final BigDecimal longitude = this.fromApiJsonHelper.extractBigDecimalNamed(AddressApiConstants.longitudeParamName, element, locale);

        baseDataValidator.reset().parameter(AddressApiConstants.longitudeParamName).value(longitude).ignoreIfNull();
    }

    /**
     * Validating all required parameters exists or not
     * 
     * @param entityTypeEnum
     * @param entityId
     * @param json
     */
    public void validateForUpdate(final Integer entityTypeEnum, final Long entityId, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AddressApiConstants.UPDATE_ADDRESS_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(AddressApiConstants.ADDRESSES_RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        
        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(AddressApiConstants.addressesParamName, parentElement)) {
            validateEachObjectForUpdate(entityTypeEnum, entityId, parentElement.getAsJsonObject(), baseDataValidator);
        } else if (this.fromApiJsonHelper.parameterExists(AddressApiConstants.addressesParamName, parentElement)) {
            final JsonArray array = parentElementObj.get(AddressApiConstants.addressesParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateEachObjectForUpdate(entityTypeEnum, entityId, element, baseDataValidator);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachObjectForUpdate(final Integer entityTypeEnum, final Long entityId, final JsonObject element,
            final DataValidatorBuilder baseDataValidator) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        baseDataValidator.reset().parameter(AddressApiConstants.entityTypeEnumParamName).value(entityTypeEnum).notNull()
                .integerGreaterThanZero().isOneOfTheseValues(AddressEntityTypeEnums.integerValues());

        baseDataValidator.reset().parameter(AddressApiConstants.entityIdParamName).value(entityId).notBlank();

        final String[] addressTypes = this.fromApiJsonHelper.extractArrayNamed(AddressApiConstants.addressTypesParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressTypesParamName).value(addressTypes).ignoreIfNull().arrayNotEmpty();

        final Long addressId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.addressIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressIdParamName).value(addressId).notBlank();

        final String houseNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.houseNoParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.houseNoParamName).value(houseNo).ignoreIfNull().notExceedingLengthOf(200);

        final String streetNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.streetNoParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.streetNoParamName).value(streetNo).ignoreIfNull().notExceedingLengthOf(200);

        final String addressLineOne = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineOneParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressLineOneParamName).value(addressLineOne).ignoreIfNull()
                .notExceedingLengthOf(200);

        final String addressLineTwo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineTwoParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.addressLineTwoParamName).value(addressLineTwo).ignoreIfNull()
                .notExceedingLengthOf(200);

        final String landmark = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.landmarkParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.landmarkParamName).value(landmark).ignoreIfNull().notExceedingLengthOf(100);

        final String villageTown = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.villageTownParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.villageTownParamName).value(villageTown).ignoreIfNull()
                .notExceedingLengthOf(100);

        final Long talukaId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.talukaIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.talukaIdParamName).value(talukaId).ignoreIfNull();

        final Long districtId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.districtIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.districtIdParamName).value(districtId).ignoreIfNull();

        final Long stateId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.stateIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.stateIdParamName).value(stateId).ignoreIfNull();

        final Long countryId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.countryIdParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.countryIdParamName).value(countryId).ignoreIfNull();

        final String postalCode = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.postalCodeParamName, element);
        baseDataValidator.reset().parameter(AddressApiConstants.postalCodeParamName).value(postalCode).ignoreIfNull();

        final BigDecimal latitude = this.fromApiJsonHelper.extractBigDecimalNamed(AddressApiConstants.latitudeParamName, element, locale);
        baseDataValidator.reset().parameter(AddressApiConstants.latitudeParamName).value(latitude).ignoreIfNull();

        final BigDecimal longitude = this.fromApiJsonHelper.extractBigDecimalNamed(AddressApiConstants.longitudeParamName, element, locale);
        baseDataValidator.reset().parameter(AddressApiConstants.longitudeParamName).value(longitude).ignoreIfNull();
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}