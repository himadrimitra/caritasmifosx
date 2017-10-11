package com.finflux.portfolio.loanemipacks.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoanEMIPacksApiConstants {

        public static final String RESOURCE_NAME = "loanemipacks";

        public static final String id = "id";
        public static final String loanProductId = "loanProductId";
        public static final String repaymentEvery = "repaymentEvery";
        public static final String repaymentFrequencyType = "repaymentFrequencyType";
        public static final String numberOfRepayments = "numberOfRepayments";
        public static final String sanctionAmount = "sanctionAmount";
        public static final String fixedEmi = "fixedEmi";
        public static final String disbursalAmount1 = "disbursalAmount1";
        public static final String disbursalAmount2 = "disbursalAmount2";
        public static final String disbursalAmount3 = "disbursalAmount3";
        public static final String disbursalAmount4 = "disbursalAmount4";
        public static final String disbursalEmi2 = "disbursalEmi2";
        public static final String disbursalEmi3 = "disbursalEmi3";
        public static final String disbursalEmi4 = "disbursalEmi4";
        public static final String loanProductName = "loanProductName";
        public static final String multiDisburseLoan = "multiDisburseLoan";
        public static final String maxTrancheCount = "maxTrancheCount";
        public static final String repaymentFrequencyTypeOptions = "repaymentFrequencyTypeOptions";
        public static final String localeParamName = "locale";

        /**
         * Request Data Parameters
         */
        public static final Set<String> EMI_PACK_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(repaymentEvery,
                repaymentFrequencyType,
                numberOfRepayments,
                sanctionAmount,
                fixedEmi,
                disbursalAmount1,
                disbursalAmount2,
                disbursalAmount3,
                disbursalAmount4,
                disbursalEmi2,
                disbursalEmi3,
                disbursalEmi4,
                localeParamName));

        public static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(id,
                loanProductId,
                repaymentEvery,
                repaymentFrequencyType,
                numberOfRepayments,
                sanctionAmount,
                fixedEmi,
                disbursalAmount1,
                disbursalAmount2,
                disbursalAmount3,
                disbursalAmount4,
                disbursalEmi2,
                disbursalEmi3,
                disbursalEmi4,
                loanProductName,
                multiDisburseLoan,
                maxTrancheCount,
                repaymentFrequencyTypeOptions));

}