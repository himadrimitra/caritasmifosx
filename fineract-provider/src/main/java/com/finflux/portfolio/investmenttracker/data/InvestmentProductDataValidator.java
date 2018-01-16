package com.finflux.portfolio.investmenttracker.data;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.currencyCodeParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.digitsAfterDecimalParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.inMultiplesOfParamName;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.common.AccountingConstants.INVESTMENT_PRODUCT_ACCOUNTING_PARAMS;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.api.InvestmentProductApiconstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class InvestmentProductDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public InvestmentProductDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentProductApiconstants.INVESTMENT_PRODUCT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(InvestmentProductApiconstants.INVESTMENT_PRODUCT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.nameParamName, element);
        baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);

        final String shortName = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.shortNameParamName, element);
        baseDataValidator.reset().parameter(LoanProductConstants.shortName).value(shortName).notBlank().notExceedingLengthOf(4);
        
        final Integer categoryId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(InvestmentProductApiconstants.categoryParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.categoryParamName).value(categoryId).integerGreaterThanZero();
        
        final String description = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.descriptionParamName, element);
        baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);

        final String currencyCode = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.currencyCodeParamName, element);
        baseDataValidator.reset().parameter(currencyCodeParamName).value(currencyCode).notBlank();

        final Integer digitsAfterDecimal = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                InvestmentProductApiconstants.digitsAfterDecimalParamName, element);
        baseDataValidator.reset().parameter(digitsAfterDecimalParamName).value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);

        if (this.fromApiJsonHelper.parameterExists(inMultiplesOfParamName, element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed(InvestmentProductApiconstants.inMultiplesOfParamName,
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter(inMultiplesOfParamName).value(inMultiplesOf).ignoreIfNull().integerZeroOrGreater();
        }

        final BigDecimal defaultNominalInterestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                InvestmentProductApiconstants.defaultNominalInterestRateParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.defaultNominalInterestRateParamName)
                .value(defaultNominalInterestRate).positiveAmount();

        BigDecimal minNominalInterestRate = null;
        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.minNominalInterestRateParamName, element)) {
            minNominalInterestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    InvestmentProductApiconstants.minNominalInterestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.minNominalInterestRateParamName)
                    .value(minNominalInterestRate).ignoreIfNull().positiveAmount();
        }

        BigDecimal maxNominalInterestRate = null;
        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.maxNominalInterestRateParamName, element)) {
            maxNominalInterestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    InvestmentProductApiconstants.maxNominalInterestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.maxNominalInterestRateParamName)
                    .value(maxNominalInterestRate).ignoreIfNull().positiveAmount();
        }

        Integer nominalInterestRateEnum = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentProductApiconstants.nominalInterestRateEnumParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.nominalInterestRateEnumParamName).value(nominalInterestRateEnum)
                .notNull().integerZeroOrGreater();
        if(this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName, element)){
        	Integer interestCompoundingPeriodEnum = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName)
                    .value(interestCompoundingPeriodEnum).notNull().integerZeroOrGreater();
        }

        final Integer defaultInvestmentTermPeriod = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName)
                .value(defaultInvestmentTermPeriod).notNull().integerGreaterThanZero();

        Integer minInvestmentTermPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.minInvestmentTermPeriodParamName, element)) {
            minInvestmentTermPeriod = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.minInvestmentTermPeriodParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.minInvestmentTermPeriodParamName)
                    .value(minInvestmentTermPeriod).ignoreIfNull().integerGreaterThanZero();
        }

        Integer maxInvestmentTermPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName, element)) {
            maxInvestmentTermPeriod = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName)
                    .value(maxInvestmentTermPeriod).ignoreIfNull().integerGreaterThanZero();
        }

        Integer investmentTermEnum = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentProductApiconstants.investmentTermEnumParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.investmentTermEnumParamName).value(investmentTermEnum).notNull()
                .integerZeroOrGreater();

        boolean overrideTermsInInvestmentAccounts = this.fromApiJsonHelper.extractBooleanNamed(
                InvestmentProductApiconstants.overrideTermsParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.overrideTermsParamName).value(overrideTermsInInvestmentAccounts)
                .notNull();

        boolean nominalInterestRate = this.fromApiJsonHelper.extractBooleanNamed(
                InvestmentProductApiconstants.nominalInterestRateParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.nominalInterestRateParamName).value(nominalInterestRate)
                .notNull();

        boolean investmentTerm = this.fromApiJsonHelper.extractBooleanNamed(InvestmentProductApiconstants.investmentTermParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.investmentTermParamName).value(investmentTerm).notNull();

        Integer accountingType = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentProductApiconstants.accountingTypeParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.accountingTypeParamName).value(accountingType).notNull()
                .inMinMaxRange(1, 2);

        if (AccountingRuleType.CASH_BASED.getValue().equals(accountingType)) {
            final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
                    element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue()).value(fundAccountId).notNull()
                    .integerGreaterThanZero();

            final Long investmentAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue())
                    .value(investmentAccountId).notNull().integerGreaterThanZero();

            final Long incomeFromInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue())
                    .value(incomeFromInterestAccountId).notNull().integerGreaterThanZero();

            final Long feeExpenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue()).value(feeExpenseAccountId)
                    .notNull().integerGreaterThanZero();
            
            final Long interestOnSavingsAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_SAVINGS_ACCOUNT.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_SAVINGS_ACCOUNT.getValue())
                    .value(interestOnSavingsAccountId).notNull().integerGreaterThanZero();
            
            final Long savingsControlAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.SAVINGS_CONTROL_ACCOUNT.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.SAVINGS_CONTROL_ACCOUNT.getValue())
                    .value(savingsControlAccountId).notNull().integerGreaterThanZero();
            
            final Long incomeFromFeesAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue())
                    .value(incomeFromFeesAccountId).notNull().integerGreaterThanZero();
            
            final Long partnerInterestReceviableAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PARTNER_INTEREST_RECEIVABLE.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PARTNER_INTEREST_RECEIVABLE.getValue())
                    .value(partnerInterestReceviableAccountId).notNull().integerGreaterThanZero();


            validatePaymentChannelFundSourceMappings(baseDataValidator, element);
            validateFeesToExpenseAccountMappings(baseDataValidator, element);
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validation for advanced accounting options
     */
    private void validatePaymentChannelFundSourceMappings(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        if (this.fromApiJsonHelper.parameterExists(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(),
                element)) {
            final JsonArray paymentChannelMappingArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(), element);
            if (paymentChannelMappingArray != null && paymentChannelMappingArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = paymentChannelMappingArray.get(i).getAsJsonObject();
                    final Long paymentTypeId = this.fromApiJsonHelper.extractLongNamed(
                            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_TYPE.getValue(), jsonObject);

                    baseDataValidator
                            .reset()
                            .parameter(
                                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue() + "."
                                            + INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_TYPE.toString()).value(paymentTypeId).notNull()
                            .integerGreaterThanZero();

                    final Long paymentSpecificFundAccountId = this.fromApiJsonHelper.extractLongNamed(
                            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), jsonObject);

                    baseDataValidator
                            .reset()
                            .parameter(
                                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue() + "."
                                            + INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue())
                            .value(paymentSpecificFundAccountId).notNull().integerGreaterThanZero();
                    i++;
                } while (i < paymentChannelMappingArray.size());
            }
        }
    }

    private void validateFeesToExpenseAccountMappings(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        if (this.fromApiJsonHelper.parameterExists(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE_ACCOUNT_MAPPING.getValue(), element)) {
            final JsonArray feeToExpenseAccountMappingArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE_ACCOUNT_MAPPING.getValue(), element);
            if (feeToExpenseAccountMappingArray != null && feeToExpenseAccountMappingArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = feeToExpenseAccountMappingArray.get(i).getAsJsonObject();
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed(
                            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.CHARGE_ID.getValue(), jsonObject);

                    baseDataValidator
                            .reset()
                            .parameter(
                                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE_ACCOUNT_MAPPING.getValue() + "."
                                            + INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.CHARGE_ID.toString()).value(chargeId).notNull()
                            .integerGreaterThanZero();

                    final Long expenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(), jsonObject);

                    baseDataValidator
                            .reset()
                            .parameter(
                                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE_ACCOUNT_MAPPING.getValue() + "."
                                            + INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue()).value(expenseAccountId)
                            .notNull().integerGreaterThanZero();
                    i++;
                } while (i < feeToExpenseAccountMappingArray.size());
            }
        }
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                InvestmentProductApiconstants.INVESTMENT_PRODUCT_UPDATE_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(InvestmentProductApiconstants.INVESTMENT_PRODUCT_RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.nameParamName, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.nameParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.nameParamName).value(name).notBlank()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.shortNameParamName, element)) {
            final String shortName = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.shortNameParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.shortNameParamName).value(shortName).notBlank()
                    .notExceedingLengthOf(4);
        }
        
        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.categoryParamName, element)) {
            final Integer category = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(InvestmentProductApiconstants.categoryParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.categoryParamName).value(category).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.descriptionParamName, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.descriptionParamName,
                    element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.descriptionParamName).value(description).notBlank()
                    .notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.currencyCodeParamName, element)) {
            final String currencyCode = this.fromApiJsonHelper.extractStringNamed(InvestmentProductApiconstants.currencyCodeParamName,
                    element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.currencyCodeParamName).value(currencyCode).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.digitsAfterDecimalParamName, element)) {
            final Integer digitsAfterDecimal = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    InvestmentProductApiconstants.digitsAfterDecimalParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.digitsAfterDecimalParamName).value(digitsAfterDecimal)
                    .notNull().inMinMaxRange(0, 6);
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.inMultiplesOfParamName, element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed(InvestmentProductApiconstants.inMultiplesOfParamName,
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.inMultiplesOfParamName).value(inMultiplesOf).ignoreIfNull()
                    .integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.defaultNominalInterestRateParamName, element)) {
            final BigDecimal defaultNominalInterestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    InvestmentProductApiconstants.defaultNominalInterestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.defaultNominalInterestRateParamName)
                    .value(defaultNominalInterestRate).positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.minNominalInterestRateParamName, element)) {
            BigDecimal minNominalInterestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    InvestmentProductApiconstants.minNominalInterestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.minNominalInterestRateParamName)
                    .value(minNominalInterestRate).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.maxNominalInterestRateParamName, element)) {
            BigDecimal maxNominalInterestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    InvestmentProductApiconstants.maxNominalInterestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.maxNominalInterestRateParamName)
                    .value(maxNominalInterestRate).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.nominalInterestRateEnumParamName, element)) {
            Integer nominalInterestRateEnum = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.nominalInterestRateEnumParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.nominalInterestRateEnumParamName)
                    .value(nominalInterestRateEnum).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName, element)) {
            Integer interestCompoundingPeriodEnum = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName)
                    .value(interestCompoundingPeriodEnum).notNull().integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName, element)) {
            final Integer defaultInvestmentTermPeriod = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName)
                    .value(defaultInvestmentTermPeriod).notNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.minInvestmentTermPeriodParamName, element)) {
            Integer minInvestmentTermPeriod = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.minInvestmentTermPeriodParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.minInvestmentTermPeriodParamName)
                    .value(minInvestmentTermPeriod).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName, element)) {
            Integer maxInvestmentTermPeriod = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName)
                    .value(maxInvestmentTermPeriod).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.investmentTermEnumParamName, element)) {
            Integer investmentTermEnum = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.investmentTermEnumParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.investmentTermEnumParamName).value(investmentTermEnum)
                    .notNull().integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.overrideTermsParamName, element)) {
            boolean overrideTermsInInvestmentAccounts = this.fromApiJsonHelper.extractBooleanNamed(
                    InvestmentProductApiconstants.overrideTermsParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.overrideTermsParamName)
                    .value(overrideTermsInInvestmentAccounts).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.nominalInterestRateParamName, element)) {
            boolean nominalInterestRate = this.fromApiJsonHelper.extractBooleanNamed(
                    InvestmentProductApiconstants.nominalInterestRateParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.nominalInterestRateParamName).value(nominalInterestRate)
                    .notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.investmentTermParamName, element)) {
            boolean investmentTerm = this.fromApiJsonHelper.extractBooleanNamed(InvestmentProductApiconstants.investmentTermParamName,
                    element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.investmentTermParamName).value(investmentTerm).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(InvestmentProductApiconstants.accountingTypeParamName, element)) {
            Integer accountingType = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    InvestmentProductApiconstants.accountingTypeParamName, element);
            baseDataValidator.reset().parameter(InvestmentProductApiconstants.accountingTypeParamName).value(accountingType).notNull()
                    .inMinMaxRange(1, 2);
        }

        Integer accountingType = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                InvestmentProductApiconstants.accountingTypeParamName, element);
        baseDataValidator.reset().parameter(InvestmentProductApiconstants.accountingTypeParamName).value(accountingType).notNull()
                .inMinMaxRange(1, 2);

        if (AccountingRuleType.CASH_BASED.getValue().equals(accountingType)) {
            final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
                    element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue()).value(fundAccountId).notNull()
                    .integerGreaterThanZero();

            final Long investmentAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue())
                    .value(investmentAccountId).notNull().integerGreaterThanZero();

            final Long incomeFromInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue())
                    .value(incomeFromInterestAccountId).notNull().integerGreaterThanZero();

            final Long feeExpenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue()).value(feeExpenseAccountId)
                    .notNull().integerGreaterThanZero();
            
            final Long interestOnSavingsAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_SAVINGS_ACCOUNT.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_SAVINGS_ACCOUNT.getValue())
                    .value(interestOnSavingsAccountId).notNull().integerGreaterThanZero();
            
            final Long savingsControlAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.SAVINGS_CONTROL_ACCOUNT.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.SAVINGS_CONTROL_ACCOUNT.getValue())
                    .value(savingsControlAccountId).notNull().integerGreaterThanZero();
            
            final Long incomeFromFeesAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue())
                    .value(incomeFromFeesAccountId).notNull().integerGreaterThanZero();
            
            final Long partnerInterestReceviableAccountId = this.fromApiJsonHelper.extractLongNamed(
                    INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PARTNER_INTEREST_RECEIVABLE.getValue(), element);
            baseDataValidator.reset().parameter(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PARTNER_INTEREST_RECEIVABLE.getValue())
                    .value(partnerInterestReceviableAccountId).notNull().integerGreaterThanZero();

            validatePaymentChannelFundSourceMappings(baseDataValidator, element);
            validateFeesToExpenseAccountMappings(baseDataValidator, element);
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
