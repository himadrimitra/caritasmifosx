package com.finflux.portfolio.bank.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.portfolio.bank.api.BankAccountDetailConstants;

@Entity
@Table(name = "f_bank_account_details")
public class BankAccountDetails extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    
    @Column(name = "account_type_enum", nullable = false)
    private Integer accountType;

    @Column(name = "ifsc_code", nullable = false)
    private String ifscCode;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "bank_name", length = 20)
    private String bankName;

    @Column(name = "bank_city", length = 20)
    private String bankCity;

    @Column(name = "status_id", nullable = false)
    private Integer status;

    protected BankAccountDetails() {}

    private BankAccountDetails(final String name, final String accountNumber, final String ifscCode, final String mobileNumber,
            final String email, final String bankName, final String bankCity, final Integer accountType) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.status = BankAccountDetailStatus.INITIATED.getValue();
        this.bankName = bankName;
        this.bankCity = bankCity;
        this.accountType = accountType;
    }

    
    public static BankAccountDetails create(final String name, final String accountNumber, final String ifscCode,
            final String mobileNumber, final String email, final String bankName, final String bankCity, final Integer accountType) {
        return new BankAccountDetails(name, accountNumber, ifscCode, mobileNumber, email,bankName,bankCity, accountType);
    }

    public static BankAccountDetails copy(BankAccountDetails bankAccountDetails) {
        return new BankAccountDetails(bankAccountDetails.name, bankAccountDetails.accountNumber, bankAccountDetails.ifscCode,
                bankAccountDetails.mobileNumber, bankAccountDetails.email, bankAccountDetails.bankName, bankAccountDetails.bankCity, bankAccountDetails.accountType);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(3);

        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.nameParameterName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.nameParameterName);
            actualChanges.put(BankAccountDetailConstants.nameParameterName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.accountNumberParameterName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.accountNumberParameterName);
            actualChanges.put(BankAccountDetailConstants.accountNumberParameterName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.ifscCodeParameterName, this.ifscCode)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.ifscCodeParameterName);
            actualChanges.put(BankAccountDetailConstants.ifscCodeParameterName, newValue);
            this.ifscCode = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.mobileNumberParameterName, this.mobileNumber)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.mobileNumberParameterName);
            actualChanges.put(BankAccountDetailConstants.mobileNumberParameterName, newValue);
            this.mobileNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.emailParameterName, this.email)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.emailParameterName);
            actualChanges.put(BankAccountDetailConstants.emailParameterName, newValue);
            this.email = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.bankNameParameterName, this.bankName)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.bankNameParameterName);
            actualChanges.put(BankAccountDetailConstants.bankNameParameterName, newValue);
            this.bankName = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(BankAccountDetailConstants.bankCityParameterName, this.bankCity)) {
            final String newValue = command.stringValueOfParameterNamed(BankAccountDetailConstants.bankCityParameterName);
            actualChanges.put(BankAccountDetailConstants.bankCityParameterName, newValue);
            this.bankCity = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInIntegerParameterNamed(BankAccountDetailConstants.accountTypeIdParamName, this.accountType)) {
            final Integer newValue = command.integerValueOfParameterNamed(BankAccountDetailConstants.accountTypeIdParamName);
            actualChanges.put(BankAccountDetailConstants.accountTypeIdParamName, newValue);
            this.accountType =BankAccountType.fromInt(newValue).getValue();
        }
        
        return actualChanges;
    }

    public void updateStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getIfscCode() {
        return this.ifscCode;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankCity() {
        return bankCity;
    }
    public Integer getAccountType() {
        return this.accountType;
    }
}
