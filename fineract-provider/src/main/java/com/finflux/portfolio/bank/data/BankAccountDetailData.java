package com.finflux.portfolio.bank.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class BankAccountDetailData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final String accountNumber;
    private final EnumOptionData accountType;
    @SuppressWarnings("unused")
    private final String ifscCode;
    @SuppressWarnings("unused")
    private final String mobileNumber;
    @SuppressWarnings("unused")
    private final String email;
    @SuppressWarnings("unused")
    private final String bankName;
    @SuppressWarnings("unused")
    private final String bankCity;
    @SuppressWarnings("unused")
    private final EnumOptionData status;
    private  Collection<EnumOptionData> bankAccountTypeOptions;
    
    
    public BankAccountDetailData(Collection<EnumOptionData> bankAccountTypeOptions){
        this.id = null;
        this.name = null;
        this.accountNumber = null;
        this.ifscCode = null;
        this.mobileNumber = null;
        this.email = null;
        this.bankName = null;
        this.bankCity = null;
        this.status = null;
        this.accountType = null;
        this.bankAccountTypeOptions = bankAccountTypeOptions;
        
    }

    public BankAccountDetailData(final Long id, final String name, final String accountNumber, final String ifscCode,
            final String mobileNumber, final String email,final String bankName, final String bankCity, final EnumOptionData status, final EnumOptionData accountType) {
        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.bankName = bankName;
        this.bankCity = bankCity;
        this.status = status;
        this.accountType = accountType;
        this.bankAccountTypeOptions = null;
    }

    public Collection<EnumOptionData> getBankAccountTypeOptions() {
        return this.bankAccountTypeOptions;
    }

    public EnumOptionData getAccountType() {
        return this.accountType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public EnumOptionData getStatus() {
        return status;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankCity() {
        return bankCity;
    }

    
    public void setBankAccountTypeOptions(Collection<EnumOptionData> bankAccountTypeOptions) {
        this.bankAccountTypeOptions = bankAccountTypeOptions;
    }
}
