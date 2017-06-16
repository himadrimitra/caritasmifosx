package com.finflux.integrationtests.accountlimit.builder;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.Gson;

public class AccountLimitTestBuilder {

    // Loan related limit
    private String limitOnTotalDisbursementAmount = "100000";
    private String limitOnTotalLoanOutstandingAmount = "100000";

    // Savings related limit
    private String dailyWithdrawalLimit = "100000";
    private String dailyTransferLimit = "100000";
    private String limitOnTotalOverdraftAmount = "100000";

    private String locale = "en";

    public String build() {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("limitOnTotalDisbursementAmount", this.limitOnTotalDisbursementAmount);
        map.put("limitOnTotalLoanOutstandingAmount", this.limitOnTotalLoanOutstandingAmount);
        map.put("dailyWithdrawalLimit", this.dailyWithdrawalLimit);
        map.put("dailyTransferLimit", this.dailyTransferLimit);
        map.put("limitOnTotalOverdraftAmount", this.limitOnTotalOverdraftAmount);
        map.put("locale", this.locale);
        System.out.println("Account limit request : " + map);
        return new Gson().toJson(map);
    }

    public AccountLimitTestBuilder withLoanAccountLimit() {
        this.limitOnTotalDisbursementAmount = "150000";
        this.limitOnTotalLoanOutstandingAmount = "150000";
        return this;
    }

    public AccountLimitTestBuilder withLoanAccountLimitInvalidData() {
        this.limitOnTotalDisbursementAmount = "nulls";
        this.limitOnTotalLoanOutstandingAmount = "nulls";
        return this;
    }
}