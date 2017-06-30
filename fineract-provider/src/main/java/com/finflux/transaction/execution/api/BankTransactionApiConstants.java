package com.finflux.transaction.execution.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BankTransactionApiConstants {

    public static final String consumption = "Consumption";

    public static final String transferType = "transferType";
    public static final String rejectCommandParam="reject";

    public static final Set<String> SUBMIT_TRANSACTION_PARAMETERS = new HashSet<>(Arrays.asList(
            transferType));


    public static final String SUBMIT_BANK_TRANSACTION_RESOURCE = "submitBankTransaction";
    
    public static final String INITIATE_BANK_TRANSACTION_RESOURCE="initiateBankTransaction";
}
