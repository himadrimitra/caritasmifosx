/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.infrastructure.gis.district.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.gis.district.api.DistrictApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class DistrictCommandFromApiJsonDeserializer {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public DistrictCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, DistrictApiConstants.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(DistrictApiConstants.DISTRICT_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(DistrictApiConstants.districtNameParamName, element);
        baseDataValidator.reset().parameter(DistrictApiConstants.districtNameParamName).value(name).notBlank().notExceedingLengthOf(100);

        final String code = this.fromApiJsonHelper.extractStringNamed(DistrictApiConstants.districtCodeParamName, element);
        baseDataValidator.reset().parameter(DistrictApiConstants.districtCodeParamName).value(code).notBlank().notExceedingLengthOf(5);

        final Long stateId = this.fromApiJsonHelper.extractLongNamed(DistrictApiConstants.stateIdParamName, element);
        baseDataValidator.reset().parameter(DistrictApiConstants.stateIdParamName).value(stateId).notNull().longGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, DistrictApiConstants.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(DistrictApiConstants.DISTRICT_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(DistrictApiConstants.districtNameParamName, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(DistrictApiConstants.districtNameParamName, element);
            baseDataValidator.reset().parameter(DistrictApiConstants.districtNameParamName).value(name).notBlank()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(DistrictApiConstants.districtCodeParamName, element)) {
            final String code = this.fromApiJsonHelper.extractStringNamed(DistrictApiConstants.districtCodeParamName, element);
            baseDataValidator.reset().parameter(DistrictApiConstants.districtCodeParamName).value(code).notBlank().notExceedingLengthOf(5);
        }

        if (this.fromApiJsonHelper.parameterExists(DistrictApiConstants.stateIdParamName, element)) {
            final Long stateId = this.fromApiJsonHelper.extractLongNamed(DistrictApiConstants.stateIdParamName, element);
            baseDataValidator.reset().parameter(DistrictApiConstants.stateIdParamName).value(stateId).notNull().longGreaterThanZero();
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}