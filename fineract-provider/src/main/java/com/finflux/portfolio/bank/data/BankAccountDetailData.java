package com.finflux.portfolio.bank.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class BankAccountDetailData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final String accountNumber;
    @SuppressWarnings("unused")
    private final String ifscCode;
    @SuppressWarnings("unused")
    private final String mobileNumber;
    @SuppressWarnings("unused")
    private final String email;
    @SuppressWarnings("unused")
    private final EnumOptionData status;

    public BankAccountDetailData(final Long id, final String name, final String accountNumber, final String ifscCode,
            final String mobileNumber, final String email, final EnumOptionData status) {
        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.status = status;
    }

}
