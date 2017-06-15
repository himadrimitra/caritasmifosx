package org.apache.fineract.portfolio.client.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClientAccountLimitsApiConstants {

    public static final String RESOURCE_NAME = "customeraccountlimits";

    // client Account Limits
    public static final String limitOnTotalDisbursementAmountParamName = "limitOnTotalDisbursementAmount";
    public static final String limitOnTotalLoanOutstandingAmountParamName = "limitOnTotalLoanOutstandingAmount";
    public static final String dailyWithdrawalLimitParamName = "dailyWithdrawalLimit";
    public static final String dailyTransferLimitParamName = "dailyTransferLimit";
    public static final String limitOnTotalOverdraftAmountParamName = "limitOnTotalOverdraftAmount";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    public static final Set<String> CLIENT_ACCOUNT_LIMITST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            limitOnTotalDisbursementAmountParamName, limitOnTotalLoanOutstandingAmountParamName, dailyWithdrawalLimitParamName,
            dailyTransferLimitParamName, limitOnTotalOverdraftAmountParamName, localeParamName, dateFormatParamName));
}