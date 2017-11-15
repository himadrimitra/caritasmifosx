package com.finflux.portfolio.investmenttracker.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.fineract.accounting.common.AccountingConstants.INVESTMENT_PRODUCT_ACCOUNTING_PARAMS;

public class InvestmentProductApiconstants {

    public static final String INVESTMENT_PRODUCT_RESOURCE_NAME = "investment_product";

    public static final String nameParamName = "name";
    public static final String shortNameParamName = "shortName";
    public static final String descriptionParamName = "description";
    public static final String currencyCodeParamName = "currencyCode";
    public static final String digitsAfterDecimalParamName = "digitsAfterDecimal";
    public static final String inMultiplesOfParamName = "inMultiplesOf";
    public static final String minNominalInterestRateParamName = "minNominalInterestRate";
    public static final String defaultNominalInterestRateParamName = "defaultNominalInterestRate";
    public static final String maxNominalInterestRateParamName = "maxNominalInterestRate";
    public static final String nominalInterestRateEnumParamName = "nominalInterestRateType";
    public static final String interestCompoundingPeriodEnumParamName = "interestCompoundingPeriodType";
    public static final String minInvestmentTermPeriodParamName = "minInvestmentTermPeriod";
    public static final String defaultInvestmentTermPeriodParamName = "defaultInvestmentTermPeriod";
    public static final String maxInvestmentTermPeriodEnumParamName = "maxInvestmentTermPeriod";
    public static final String investmentTermEnumParamName = "investmentTermType";
    public static final String overrideTermsParamName = "overrideTermsInInvestmentAccounts";
    public static final String nominalInterestRateParamName = "nominalInterestRate";
    public static final String interestCompoundingPeriodParamName = "interestCompoundingPeriod";
    public static final String investmentTermParamName = "investmentTerm";
    public static final String accountingTypeParamName = "accountingRuleType";
    public static final String chargesParamName = "charges";
    public static final String idParamName = "id";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    public static final Set<String> INVESTMENT_PRODUCT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            shortNameParamName, descriptionParamName, currencyCodeParamName, digitsAfterDecimalParamName, inMultiplesOfParamName,
            minNominalInterestRateParamName, defaultNominalInterestRateParamName, maxNominalInterestRateParamName,
            nominalInterestRateEnumParamName, interestCompoundingPeriodEnumParamName, minInvestmentTermPeriodParamName,
            defaultInvestmentTermPeriodParamName, maxInvestmentTermPeriodEnumParamName, investmentTermEnumParamName,
            overrideTermsParamName, nominalInterestRateParamName, interestCompoundingPeriodParamName, investmentTermParamName,
            accountingTypeParamName, INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE_ACCOUNT_MAPPING.getValue(), localeParamName, dateFormatParamName,
            chargesParamName));

    public static final Set<String> INVESTMENT_PRODUCT_UPDATE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            shortNameParamName, descriptionParamName, currencyCodeParamName, digitsAfterDecimalParamName, inMultiplesOfParamName,
            minNominalInterestRateParamName, defaultNominalInterestRateParamName, maxNominalInterestRateParamName,
            nominalInterestRateEnumParamName, interestCompoundingPeriodEnumParamName, minInvestmentTermPeriodParamName,
            defaultInvestmentTermPeriodParamName, maxInvestmentTermPeriodEnumParamName, investmentTermEnumParamName,
            overrideTermsParamName, nominalInterestRateParamName, interestCompoundingPeriodParamName, investmentTermParamName,
            accountingTypeParamName, INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(),
            INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE_ACCOUNT_MAPPING.getValue(), localeParamName, dateFormatParamName,
            chargesParamName, idParamName));
}
