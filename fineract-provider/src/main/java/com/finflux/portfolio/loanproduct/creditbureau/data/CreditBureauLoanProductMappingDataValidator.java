package com.finflux.portfolio.loanproduct.creditbureau.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class CreditBureauLoanProductMappingDataValidator {

    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("creditBureauProductId", "loanProductId",
            "isCreditcheckMandatory", "skipCreditcheckInFailure", "stalePeriod", "isActive", "locale", "dateFormat"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CreditBureauLoanProductMappingDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("CREDIT_BUREAU_LOANPRODUCT_MAPPING");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed("creditBureauProductId", element);
        baseDataValidator.reset().parameter("creditBureauId").value(creditBureauProductId).notBlank().longGreaterThanZero();

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed("loanProductId", element);
        baseDataValidator.reset().parameter("loanProductId").value(loanProductId).notBlank().longGreaterThanZero();

        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper.extractBooleanNamed("isCreditcheckMandatory", element);
        baseDataValidator.reset().parameter("isCreditcheckMandatory").value(isCreditcheckMandatory).ignoreIfNull();

        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper.extractBooleanNamed("skipCreditcheckInFailure", element);
        baseDataValidator.reset().parameter("skipCreditcheckInFailure").value(skipCreditcheckInFailure).ignoreIfNull();

        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed("stalePeriod", element, locale);
        baseDataValidator.reset().parameter("stalePeriod").value(stalePeriod).notBlank().integerGreaterThanZero();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed("isActive", element);
        baseDataValidator.reset().parameter("isActive").value(isActive).ignoreIfNull();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("ACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed("creditBureauProductId", element);
        baseDataValidator.reset().parameter("creditBureauProductId").value(creditBureauProductId).notBlank().longGreaterThanZero();

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed("loanProductId", element);
        baseDataValidator.reset().parameter("loanProductId").value(loanProductId).notBlank().longGreaterThanZero();

        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper.extractBooleanNamed("isCreditcheckMandatory", element);
        baseDataValidator.reset().parameter("isCreditcheckMandatory").value(isCreditcheckMandatory).ignoreIfNull();

        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper.extractBooleanNamed("skipCreditcheckInFailure", element);
        baseDataValidator.reset().parameter("skipCreditcheckInFailure").value(skipCreditcheckInFailure).ignoreIfNull();

        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed("stalePeriod", element, locale);
        baseDataValidator.reset().parameter("stalePeriod").value(stalePeriod).notBlank().integerGreaterThanZero();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed("isActive", element);
        baseDataValidator.reset().parameter("isActive").value(isActive).ignoreIfNull();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}