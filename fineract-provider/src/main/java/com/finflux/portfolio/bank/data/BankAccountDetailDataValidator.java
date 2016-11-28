package com.finflux.portfolio.bank.data;

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

import com.finflux.portfolio.bank.api.BankAccountDetailConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class BankAccountDetailDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public BankAccountDetailDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, BankAccountDetailConstants.CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(BankAccountDetailConstants.resourceName);

        final String name = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.nameParameterName, element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.nameParameterName).value(name).notBlank();

        final String accountNumber = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.accountNumberParameterName,
                element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.accountNumberParameterName).value(accountNumber).notBlank();

        final String ifscCode = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.ifscCodeParameterName, element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.ifscCodeParameterName).value(ifscCode).notBlank();

        final String mobileNo = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.mobileNumberParameterName, element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.mobileNumberParameterName).value(mobileNo).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String email = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.emailParameterName, element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.emailParameterName).value(email).ignoreIfNull()
                .notExceedingLengthOf(50);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, BankAccountDetailConstants.UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(BankAccountDetailConstants.resourceName);

        if (this.fromApiJsonHelper.parameterExists(BankAccountDetailConstants.nameParameterName, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.nameParameterName, element);
            baseDataValidator.reset().parameter(BankAccountDetailConstants.nameParameterName).value(name).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(BankAccountDetailConstants.accountNumberParameterName, element)) {
            final String accountNumber = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.accountNumberParameterName,
                    element);
            baseDataValidator.reset().parameter(BankAccountDetailConstants.accountNumberParameterName).value(accountNumber).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(BankAccountDetailConstants.ifscCodeParameterName, element)) {
            final String ifscCode = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.ifscCodeParameterName, element);
            baseDataValidator.reset().parameter(BankAccountDetailConstants.ifscCodeParameterName).value(ifscCode).notBlank();
        }

        final String mobileNo = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.mobileNumberParameterName, element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.mobileNumberParameterName).value(mobileNo).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String email = this.fromApiJsonHelper.extractStringNamed(BankAccountDetailConstants.emailParameterName, element);
        baseDataValidator.reset().parameter(BankAccountDetailConstants.emailParameterName).value(email).ignoreIfNull()
                .notExceedingLengthOf(50);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}
