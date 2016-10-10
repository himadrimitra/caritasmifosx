package com.finflux.risk.creditbureau.provider.highmark.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HighmarkApiConstants {

    public static final String HIGHMARKRESOUCENAME = "HIGHMARK";
    public static final String ACTIVELOANCOUNTPARAMNAME = "activeLoanCount";
    public static final String CLOSEDlOANCOUNTPARAMNAME = "closedLoanCount";
    public static final String TOTALOUTSTANDINGPARAMNAME = "totalOutstanding";
    public static final String TOTALOVERDUEPARAMNAME = "totalOverdues";
    public static final String TOTALINSTALMENTPARAMNAME = "totalInstallments";
    public static final String RESPONSEDATE = "responseDate";

    public static final Set<String> HIGHMARK_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(ACTIVELOANCOUNTPARAMNAME,
            CLOSEDlOANCOUNTPARAMNAME, TOTALOUTSTANDINGPARAMNAME, TOTALOVERDUEPARAMNAME, TOTALINSTALMENTPARAMNAME, RESPONSEDATE));

}
