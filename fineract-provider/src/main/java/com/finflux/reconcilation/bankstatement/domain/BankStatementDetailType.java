/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.domain;

public enum BankStatementDetailType {
	
	INVALID(0, "bankStatementDetailsType.invalid"),
	PORTFOLIO(1, "bankStatementDetailsType.portfolio"), //
    NONPORTFOLIO(2, "bankStatementDetailsType.nonportfolio"), //
    MISCELLANEOUS(3, "bankStatementDetailsType.miscellaneous"), //
    SIMPLIFIED_PORTFOLIO(4, "bankStatementDetailsType.simplifiedPortfolio"), //
    LOANS(5, "bankStatementDetailsType.loan"), DEPOSITS(6, "bankStatementDetailsType.deposits");

    private final Integer value;
    private final String code;

    private BankStatementDetailType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static BankStatementDetailType fromInt(final Integer value) {

    	BankStatementDetailType enumeration = BankStatementDetailType.INVALID;
        switch (value) {
            case 1:
                enumeration = BankStatementDetailType.PORTFOLIO;
            break;
            case 2:
                enumeration = BankStatementDetailType.NONPORTFOLIO;
            break;
            case 3:
                enumeration = BankStatementDetailType.MISCELLANEOUS;
            break;
            case 4:
                enumeration = BankStatementDetailType.LOANS;
            break;
            case 5:
                enumeration = BankStatementDetailType.DEPOSITS;
            break;
        }
        return enumeration;
    }
    
    public boolean isLoanType() {
        return this.value.equals(BankStatementDetailType.LOANS.getValue());
    }

    public boolean isDepositType() {
        return this.value.equals(BankStatementDetailType.DEPOSITS.getValue());
    }
}