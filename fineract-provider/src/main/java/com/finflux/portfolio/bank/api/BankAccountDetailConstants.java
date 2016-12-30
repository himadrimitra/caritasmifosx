package com.finflux.portfolio.bank.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BankAccountDetailConstants {

    
    public static final String resourceName = "bank.account.detail";
    public static final String resourceNameForPermission = "BANKACCOUNTDETAIL";
    public static final String nameParameterName = "name";
    public static final String accountNumberParameterName = "accountNumber";
    public static final String ifscCodeParameterName = "ifscCode";
    public static final String mobileNumberParameterName = "mobileNumber";
    public static final String emailParameterName = "email";
    public static final String bankNameParameterName = "bankName";
    public static final String bankCityParameterName = "bankCity";
    public static final String newBankAccountDetailId = "newBankAccountDetailId";

    public static final Set<String> CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParameterName,
            accountNumberParameterName, ifscCodeParameterName, mobileNumberParameterName, emailParameterName,
            bankNameParameterName, bankCityParameterName));

    public static final Set<String> UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParameterName,
            accountNumberParameterName, ifscCodeParameterName, mobileNumberParameterName, emailParameterName,
            bankNameParameterName, bankCityParameterName));
}
