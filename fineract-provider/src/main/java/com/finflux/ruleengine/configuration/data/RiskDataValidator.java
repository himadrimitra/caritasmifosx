package com.finflux.ruleengine.configuration.data;

import com.amazonaws.util.json.JSONArray;
import com.finflux.ruleengine.configuration.api.RiskConfigurationApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RiskDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public RiskDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreateRulePurpose(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                RiskConfigurationApiConstants.CREATE_RULE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(RiskConfigurationApiConstants.FACTOR_CONFIGURATION_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(RiskConfigurationApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.nameParamName).value(name).notNull();

        final String uname = this.fromApiJsonHelper.extractStringNamed(RiskConfigurationApiConstants.unameParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.unameParamName).value(uname).notNull()
                .notExceedingLengthOf(30);

        final String description = this.fromApiJsonHelper.extractStringNamed(RiskConfigurationApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.descriptionParamName).value(description).notNull();
        
        final JsonArray buckets=this.fromApiJsonHelper.extractJsonArrayNamed(RiskConfigurationApiConstants.bucketsParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.bucketsParamName).value(buckets).jsonArrayNotEmpty();
//        if (this.fromApiJsonHelper.parameterExists(RiskConfigurationApiConstants.loanPurposeGroupTypeIdParamName, element)) {
//            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
//                    RiskConfigurationApiConstants.loanPurposeGroupTypeIdParamName, element);
//            baseDataValidator.reset().parameter(RiskConfigurationApiConstants.loanPurposeGroupTypeIdParamName).value(loanPurposeGroupIds)
//                    .notBlank().arrayNotEmpty();
//        }

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(RiskConfigurationApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForUpdateRulePurpose(String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                RiskConfigurationApiConstants.CREATE_RULE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(RiskConfigurationApiConstants.FACTOR_CONFIGURATION_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(RiskConfigurationApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.nameParamName).value(name).notNull();

        final String uname = this.fromApiJsonHelper.extractStringNamed(RiskConfigurationApiConstants.unameParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.unameParamName).value(uname).notNull()
                .notExceedingLengthOf(30);

        final String description = this.fromApiJsonHelper.extractStringNamed(RiskConfigurationApiConstants.descriptionParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.descriptionParamName).value(description).notNull();
        
        final JsonArray buckets=this.fromApiJsonHelper.extractJsonArrayNamed(RiskConfigurationApiConstants.bucketsParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.bucketsParamName).value(buckets).jsonArrayNotEmpty();
//      

//        if (this.fromApiJsonHelper.parameterExists(RiskConfigurationApiConstants.loanPurposeGroupTypeIdParamName, element)) {
//            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
//                    RiskConfigurationApiConstants.loanPurposeGroupTypeIdParamName, element);
//            baseDataValidator.reset().parameter(RiskConfigurationApiConstants.loanPurposeGroupTypeIdParamName).value(loanPurposeGroupIds)
//                    .notBlank().arrayNotEmpty();
//        }

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(RiskConfigurationApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(RiskConfigurationApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}