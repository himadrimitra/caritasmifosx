package com.finflux.portfolio.bank.data;

import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class BankAccountDetailData {

    private final Long id;
    private final String name;
    private final String accountNumber;
    private final EnumOptionData accountType;
    private final String ifscCode;
    private final String mobileNumber;
    private final String email;
    private final String bankName;
    private final String bankCity;
    private final Long documentId;
    private final EnumOptionData status;
    private Collection<EnumOptionData> bankAccountTypeOptions;

    private final Date lastTransactionDate;

    private final String micrCode ;
    private final String branchName ;
    private final String checkerInfo;

    public BankAccountDetailData(Collection<EnumOptionData> bankAccountTypeOptions) {
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
        this.lastTransactionDate = null;
        this.micrCode = null ;
        this.branchName = null ;
        this.documentId = null;
        this.checkerInfo = null;
    }

    public BankAccountDetailData(final Long id, final String name, final String accountNumber, final String ifscCode,
            final String mobileNumber, final String email, final String bankName, final String bankCity,
            final EnumOptionData status, final EnumOptionData accountType, final Date lastTransactionDate,
            final String micrCode, final String branchName, final Long documentId, String checkerInfo) {
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
        this.lastTransactionDate = lastTransactionDate;
        this.micrCode = micrCode ;
        this.branchName = branchName ;
        this.documentId = documentId;
        this.checkerInfo = checkerInfo;
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

    public String getMicrCode() {
        return this.micrCode ;
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

    public String getBranchName() {
        return this.branchName ;
    }
    
    public String getBankCity() {
        return bankCity;
    }

    public Date getLastTransactionDate() {
        return this.lastTransactionDate ;
    }
    
    public void setBankAccountTypeOptions(Collection<EnumOptionData> bankAccountTypeOptions) {
        this.bankAccountTypeOptions = bankAccountTypeOptions;
    }

    public Long getDocumentId() {
        return documentId;
    }
}
