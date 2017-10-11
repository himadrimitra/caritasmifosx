package org.apache.fineract.portfolio.client.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientAccountLimitsApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class ClientLimitsDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientLimitsDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateAccountLimits(JsonCommand command) {
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientAccountLimitsApiConstants.CLIENT_ACCOUNT_LIMITST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientAccountLimitsApiConstants.RESOURCE_NAME);

        final BigDecimal limitOnTotalDisbursementAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                ClientAccountLimitsApiConstants.limitOnTotalDisbursementAmountParamName, element);
        baseDataValidator.reset().parameter(ClientAccountLimitsApiConstants.limitOnTotalDisbursementAmountParamName)
                .value(limitOnTotalDisbursementAmount).positiveAmount();

        final BigDecimal limitOnTotalLoanOutstandingAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                ClientAccountLimitsApiConstants.limitOnTotalLoanOutstandingAmountParamName, element);
        baseDataValidator.reset().parameter(ClientAccountLimitsApiConstants.limitOnTotalLoanOutstandingAmountParamName)
                .value(limitOnTotalLoanOutstandingAmount).positiveAmount();

        final BigDecimal dailyWithdrawalLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                ClientAccountLimitsApiConstants.dailyWithdrawalLimitParamName, element);
        baseDataValidator.reset().parameter(ClientAccountLimitsApiConstants.dailyWithdrawalLimitParamName).value(dailyWithdrawalLimit)
                .positiveAmount();

        final BigDecimal dailyTransferLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                ClientAccountLimitsApiConstants.dailyTransferLimitParamName, element);
        baseDataValidator.reset().parameter(ClientAccountLimitsApiConstants.dailyTransferLimitParamName).value(dailyTransferLimit)
                .positiveAmount();

        final BigDecimal limitOnTotalOverdraftAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                ClientAccountLimitsApiConstants.limitOnTotalOverdraftAmountParamName, element);
        baseDataValidator.reset().parameter(ClientAccountLimitsApiConstants.limitOnTotalOverdraftAmountParamName)
                .value(limitOnTotalOverdraftAmount).positiveAmount();
    }
}