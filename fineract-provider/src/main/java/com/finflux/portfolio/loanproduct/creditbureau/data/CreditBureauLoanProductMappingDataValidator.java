package com.finflux.portfolio.loanproduct.creditbureau.data;

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

import com.finflux.portfolio.loanproduct.creditbureau.service.CreditBureauLoanProductOfficeMappingReadPlatformService;
import com.finflux.risk.creditbureau.provider.api.CreditBureauApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class CreditBureauLoanProductMappingDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final CreditBureauLoanProductOfficeMappingReadPlatformService creditBureauLoanProductOfficeMappingReadPlatformService;

    @Autowired
    public CreditBureauLoanProductMappingDataValidator(final FromJsonHelper fromApiJsonHelper,
            final CreditBureauLoanProductOfficeMappingReadPlatformService creditBureauLoanProductOfficeMappingReadPlatformService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.creditBureauLoanProductOfficeMappingReadPlatformService = creditBureauLoanProductOfficeMappingReadPlatformService;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CreditBureauApiConstants.CREDIT_BUREAU_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("CREDIT_BUREAU_LOANPRODUCT_MAPPING");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID,
                element);
        baseDataValidator.reset().parameter("creditBureauId").value(creditBureauProductId).notBlank().longGreaterThanZero();

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.LOAN_PRODUCTID, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.LOAN_PRODUCTID).value(loanProductId).notBlank().longGreaterThanZero();

        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper
                .extractBooleanNamed(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY).value(isCreditcheckMandatory)
                .ignoreIfNull();

        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper
                .extractBooleanNamed(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE).value(skipCreditcheckInFailure)
                .ignoreIfNull();

        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed(CreditBureauApiConstants.STALE_PERIOD, element, locale);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.STALE_PERIOD).value(stalePeriod).notBlank().integerGreaterThanZero();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(CreditBureauApiConstants.IS_ACTIVE, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.IS_ACTIVE).value(isActive).ignoreIfNull();

        final JsonArray offices = this.fromApiJsonHelper.extractJsonArrayNamed(CreditBureauApiConstants.OFFICES, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.OFFICES).value(offices).ignoreIfNull();

        final Object defaultUserMessageArgs = null;
        Integer count = this.creditBureauLoanProductOfficeMappingReadPlatformService.retrieveLoanProductDefaultMappingCount(loanProductId);
        if (offices == null || offices.size() == 0) {
            if (count > 0) {
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                        CreditBureauApiConstants.DEFAULT_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION, defaultUserMessageArgs);
            }
        } else {
            if (count < 1) {
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                        CreditBureauApiConstants.DEFAULT_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION_NOT_FOUND, defaultUserMessageArgs);
            }
        }

        Integer productAndCreditBureauCount = this.creditBureauLoanProductOfficeMappingReadPlatformService
                .retrieveCreditBureauAndLoanProductMappingCount(creditBureauProductId, loanProductId);
        if (productAndCreditBureauCount != null && productAndCreditBureauCount > 0) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                    CreditBureauApiConstants.DUPLICATE_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION, defaultUserMessageArgs);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CreditBureauApiConstants.CREDIT_BUREAU_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("ACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID,
                element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID).value(creditBureauProductId).notBlank()
                .longGreaterThanZero();

        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.LOAN_PRODUCTID, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.LOAN_PRODUCTID).value(loanProductId).notBlank().longGreaterThanZero();

        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper
                .extractBooleanNamed(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY).value(isCreditcheckMandatory)
                .ignoreIfNull();

        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper
                .extractBooleanNamed(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE).value(skipCreditcheckInFailure)
                .ignoreIfNull();

        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed(CreditBureauApiConstants.STALE_PERIOD, element, locale);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.STALE_PERIOD).value(stalePeriod).notBlank().integerGreaterThanZero();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(CreditBureauApiConstants.IS_ACTIVE, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.IS_ACTIVE).value(isActive).ignoreIfNull();

        final JsonArray offices = this.fromApiJsonHelper.extractJsonArrayNamed(CreditBureauApiConstants.OFFICES, element);
        baseDataValidator.reset().parameter(CreditBureauApiConstants.OFFICES).value(offices).ignoreIfNull();

        if (offices == null || offices.size() == 0) {
            Integer count = this.creditBureauLoanProductOfficeMappingReadPlatformService
                    .retrieveDefaultCreditBureauAndLoanProductMappingCount(creditBureauProductId, loanProductId);
            if (count > 0) {
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                        CreditBureauApiConstants.DUPLICATE_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION, "This combination for default!!");
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}