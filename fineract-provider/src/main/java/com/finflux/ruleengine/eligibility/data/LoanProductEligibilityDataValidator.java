package com.finflux.ruleengine.eligibility.data;

import com.finflux.ruleengine.eligibility.api.LoanProductEligibilityApiConstants;
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
public class LoanProductEligibilityDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanProductEligibilityDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreateLoanProductEligibility(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanProductEligibilityApiConstants.CREATE_LOAN_PRODUCT_ELIGIBILITY_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanProductEligibilityApiConstants.FACTOR_CONFIGURATION_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(LoanProductEligibilityApiConstants.loanProductIdParamName, element);
        baseDataValidator.reset().parameter(LoanProductEligibilityApiConstants.loanProductIdParamName).value(loanProductId).notNull();


//        if (this.fromApiJsonHelper.parameterExists(LoanProductEligibilityApiConstants.loanPurposeGroupTypeIdParamName, element)) {
//            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
//                    LoanProductEligibilityApiConstants.loanPurposeGroupTypeIdParamName, element);
//            baseDataValidator.reset().parameter(LoanProductEligibilityApiConstants.loanPurposeGroupTypeIdParamName).value(loanPurposeGroupIds)
//                    .notBlank().arrayNotEmpty();
//        }

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanProductEligibilityApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(LoanProductEligibilityApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForUpdateRulePurpose(String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                LoanProductEligibilityApiConstants.CREATE_LOAN_PRODUCT_ELIGIBILITY_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LoanProductEligibilityApiConstants.FACTOR_CONFIGURATION_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanProductEligibilityApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(LoanProductEligibilityApiConstants.isActiveParamName).value(isActive).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}