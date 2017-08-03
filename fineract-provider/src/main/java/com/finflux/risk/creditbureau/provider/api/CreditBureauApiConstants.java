package com.finflux.risk.creditbureau.provider.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CreditBureauApiConstants {

    public static final String HIGHMARKRESOUCENAME = "HIGHMARK";
    public static final String ACTIVELOANCOUNTPARAMNAME = "activeLoanCount";
    public static final String CLOSEDlOANCOUNTPARAMNAME = "closedLoanCount";
    public static final String TOTALOUTSTANDINGPARAMNAME = "totalOutstanding";
    public static final String TOTALOVERDUEPARAMNAME = "totalOverdues";
    public static final String TOTALINSTALMENTPARAMNAME = "totalInstallments";
    public static final String RESPONSEDATE = "responseDate";

    public static final String CREDIT_BUREAU_PRODUCTID = "creditBureauProductId";
    public static final String LOAN_PRODUCTID = "loanProductId";
    public static final String IS_CREDIT_CHECK_MANDATORY = "isCreditcheckMandatory";
    public static final String SKIP_CREDIT_CHECK_IN_FAILURE = "skipCreditcheckInFailure";
    public static final String STALE_PERIOD = "stalePeriod";
    public static final String IS_ACTIVE = "isActive";
    public static final String LOCALE = "locale";
    public static final String DATE_FORMAT = "dateFormat";
    public static final String OFFICES = "offices";
    public static final String DUPLICATE_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION = "duplicate.loan.product.and.credit.bureau.product.combination";
    public static final String DEFAULT_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION = "default.loan.product.and.credit.bureau.product.combination.exists";
    public static final String DEFAULT_LOAN_PRODUCT_AND_CREDIT_BUREAU_COMBINATION_NOT_FOUND = "default.loan.product.and.credit.bureau.product.combination.does.not.exists";
    public static final String LOAN_PRODUCT_AND_OFFICE_COMBINATION = "loan.product.and.office.combination.exists";

    public static final Set<String> HIGHMARK_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(ACTIVELOANCOUNTPARAMNAME,
            CLOSEDlOANCOUNTPARAMNAME, TOTALOUTSTANDINGPARAMNAME, TOTALOVERDUEPARAMNAME, TOTALINSTALMENTPARAMNAME, RESPONSEDATE));

    public static final Set<String> CREDIT_BUREAU_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(CREDIT_BUREAU_PRODUCTID, LOAN_PRODUCTID, IS_CREDIT_CHECK_MANDATORY, SKIP_CREDIT_CHECK_IN_FAILURE, STALE_PERIOD,
                    IS_ACTIVE, LOCALE, DATE_FORMAT, OFFICES));

}
