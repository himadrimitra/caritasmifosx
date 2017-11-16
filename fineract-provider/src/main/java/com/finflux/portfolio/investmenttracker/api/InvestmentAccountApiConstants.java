package com.finflux.portfolio.investmenttracker.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InvestmentAccountApiConstants {

    public static final String INVESTMENT_ACCOUNT_RESOURCE_NAME = "investment_account";
    
    public static final String idParamName = "id";
    public static final String accountNoParamName = "accountNo";
    public static final String externalIdParamName = "externalId";
    public static final String partnerIdParamName = "partnerId";
    public static final String officeIdParamName = "officeId";
    public static final String investmetProductIdParamName = "investmentProductId";
    public static final String statusParamName = "status";
    public static final String currencyCodeParamName = "currencyCode";
    public static final String digitsAfterDecimalParamName = "digitsAfterDecimal";
    public static final String inMultiplesOfParamName = "inMultiplesOf";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String approvedOnDateParamName = "approvedOnDate";
    public static final String activatedOnDateParamName = "activatedOnDate";
    public static final String investmentOnDateParamName = "investmentOnDate";
    public static final String investmentAmountParamName = "investmentAmount";
    public static final String interestRateParamName = "interestRate";
    public static final String interestRateTypeParamName = "interestRateType";
    public static final String investmentTermParamName = "investmentTerm";
    public static final String investmentTermTypeParamName = "investmentTermType";
    public static final String maturityOnDateParamName = "maturityOnDate";
    public static final String maturityAmountParamName = "maturityAmount";
    public static final String reinvestAfterMaturityParamName = "reinvestAfterMaturity";
    //savings account
    public static final String savingsAccountsParamName = "savingsAccounts";
    public static final String savingsAccountIdParamName = "savingsAccountId";
    public static final String individualInvestmentAmountParamName = "individualInvestmentAmount";
    //charges
    public static final String chargesParamName = "charges";
    public static final String chargeIdParamName = "chargeId";
    public static final String isPentalityParamName = "isPenality";
    public static final String isActiveParamName = "isActive";
    public static final String inactivationDateParamName = "inactivationDate";
    
    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    
    public static Set<String> INVESTMENT_ACCOUNT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(externalIdParamName,
            partnerIdParamName, officeIdParamName, investmetProductIdParamName, statusParamName, currencyCodeParamName,
            digitsAfterDecimalParamName,inMultiplesOfParamName,submittedOnDateParamName,approvedOnDateParamName,
            investmentOnDateParamName,investmentAmountParamName,interestRateParamName,interestRateTypeParamName,
            investmentTermParamName,investmentTermTypeParamName,maturityOnDateParamName,maturityAmountParamName,
            reinvestAfterMaturityParamName,savingsAccountsParamName,chargesParamName,localeParamName,dateFormatParamName));
}
