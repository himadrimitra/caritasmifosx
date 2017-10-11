package com.finflux.risk.creditbureau.configuration.serialization;

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
public class CBLPCommandFromApiJsonDeserializer {

    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("loan_product_id", "is_creditcheck_mandatory",
            "skip_creditcheck_in_failure", "stale_period", "is_active", "locale"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CBLPCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("ACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long creditBureauId = this.fromApiJsonHelper.extractLongNamed("creditBureauId", element);
        baseDataValidator.reset().parameter("creditBureauId").value(creditBureauId).notBlank().longGreaterThanZero();

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed("loanProductId", element);
        baseDataValidator.reset().parameter("loanProductId").value(loanProductId).notBlank().longGreaterThanZero();

        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper.extractBooleanNamed("isCreditcheckMandatory", element);
        baseDataValidator.reset().parameter("isCreditcheckMandatory").value(isCreditcheckMandatory).notBlank()
                .trueOrFalseRequired(isCreditcheckMandatory);

        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper.extractBooleanNamed("skipCreditcheckInFailure", element);
        baseDataValidator.reset().parameter("skipCreditcheckInFailure").value(skipCreditcheckInFailure).notBlank()
                .trueOrFalseRequired(skipCreditcheckInFailure);

        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed("stalePeriod", element, locale);
        baseDataValidator.reset().parameter("stalePeriod").value(stalePeriod).notBlank().integerGreaterThanZero();

        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed("isActive", element);
        if (isActive == null) {
            isActive = false;
        } else {
            baseDataValidator.reset().parameter("isActive").value(isActive).notBlank().trueOrFalseRequired(isActive);
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}