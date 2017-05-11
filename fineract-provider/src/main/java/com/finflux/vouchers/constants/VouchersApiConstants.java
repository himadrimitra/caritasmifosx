package com.finflux.vouchers.constants;

public interface VouchersApiConstants {

    // Request
    String officeId_ParamName = "officeId";
    String currencyCode_ParamName = "currencyCode";
    String transactionDate_ParamName = "transactionDate";
    String narration_ParamName = "narration";
    String debitAccounts_ParamName = "debitAccounts";
    String creditAccounts_ParamName = "creditAccounts";
    String glAccountId_ParamName = "glAccountId";
    String amount_ParamName = "amount";
    String locale_ParamName = "locale";
    String dateFormat_ParamName = "dateFormat";

    //Inter-branch transfers
    String fromOfficeId_ParamName = "fromOfficeId";
    String toOfficeId_ParamName = "toOfficeId";
    
    // PaymentDetails
    String paymentDetails_paramName = "paymentDetails";
    String paymentType_ParamName = "paymentType";
    String instrumentionNumber_ParamName = "instrumentionNumber";
    String paymentDate_ParamName = "instrumentationDate";
    String bankName_ParamName = "bankName";
    String branchName_paramName = "branchName";

    // Supported Voucher types
    String CASH_PAYMENT = "Cash Payment";
    String CASH_RECEIPT = "Cash Receipt";
    String BANK_PAYMMENT = "Bank Payment";
    String BANK_RECEIPT = "Bank Receipt";
    String CONTRA_ENTRY = "Contra Entry";
    String JV_ENTRY = "Journal Voucher";
    String INTER_BRANCH_CASH_TRANSFER = "Inter Branch Cash Transfer";
    String INTER_BRANCH_BANK_TRANSFER = "Inter Branch Bank Transfer";
}
