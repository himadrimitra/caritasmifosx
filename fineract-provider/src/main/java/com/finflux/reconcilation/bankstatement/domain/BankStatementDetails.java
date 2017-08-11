/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.reconcilation.ReconciliationApiConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_bank_statement_details")
public class BankStatementDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "bank_statement_id", nullable = false)
    private BankStatement bankStatement;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "client_account_number")
    private String clientAccountNumber;

    @Column(name = "loan_account_number")
    private String loanAccountNumber;

    @Column(name = "group_external_id")
    private String groupExternalId;

    @Column(name = "is_reconciled")
    private Boolean isReconciled;

    @OneToOne(optional = true)
    @JoinColumn(name = "loan_transaction", nullable = true)
    private LoanTransaction loanTransaction;

    @Column(name = "accounting_type")
    private String accountingType;

    @Column(name = "branch_external_id")
    private String branchExternalId;

    @Column(name = "gl_code")
    private String glCode;

    @Column(name = "transaction_type")
    private String transactionType;
    
    @Column(name = "bank_statement_detail_type", nullable = false)
    private Integer bankStatementDetailType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", nullable = true)
    private Date updatedDate = null;

    @Column(name = "receipt_number", nullable = true)
    private String receiptNumber;

    @Column(name = "is_error")
    private Boolean isError;
    
    @Column(name = "is_manual_reconciled")
    private Boolean isManualReconciled;
    
    @Column(name = "office_id")
    private Long officeId;
    
    @Column(name = "gl_account")
    private String glAccount;

    @Column(name = "savings_account_number")
    private String savingsAccountNumber;

    @Column(name = "payment_type")
    private String paymentTypeName;

    @Column(name = "payment_detail_account_number")
    private String paymentDetailAccountNumber;

    @Column(name = "payment_detail_cheque_number")
    private String paymentDetailChequeNumber;

    @Column(name = "routing_code")
    private String routingCode;

    @Column(name = "payment_detail_bank_number")
    private String paymentDetailBankNumber;

    @Column(name = "note")
    private String note;
    
    @Column(name = "error_msg", nullable = true)
    private String errmsg;
    
    @Column(name = "transaction_id_for_update")
    private Long transactionIdForUpdate;

    public BankStatementDetails(final BankStatement bankStatement, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final String groupExternalId, final Boolean isReconciled,
            final LoanTransaction loanTransaction, final String branchExternalId, final String accountingType, final String glCode,
            final String transactionType, final Integer bankStatementDetailType, final String receiptNumber, Boolean isManualReconciled, 
            final Long transactionIdForUpdate) {
        this.bankStatement = bankStatement;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.clientAccountNumber = clientAccountNumber;
        this.loanAccountNumber = loanAccountNumber;
        this.groupExternalId = groupExternalId;
        this.isReconciled = isReconciled;
        this.loanTransaction = loanTransaction;
        this.branchExternalId = branchExternalId;
        this.accountingType = accountingType;
        this.glCode = glCode;
        this.transactionType = transactionType;
        this.bankStatementDetailType = bankStatementDetailType;
        this.updatedDate = null;
        this.receiptNumber = receiptNumber;
        this.isManualReconciled = isManualReconciled;
        this.transactionIdForUpdate = transactionIdForUpdate;
    }

    public BankStatementDetails() {
        super();
    }

    public static BankStatementDetails instance(final BankStatement bankStatement, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final String groupExternalId, final Boolean isReconciled,
            final LoanTransaction loanTransaction, final String branchExternalId, final String accountingType, final String glCode,
            final String transactionType, final Integer bankStatementDetailType, final String receiptNumber, final Boolean isManualReconciled, 
            final Long transactionIdForUpdate) {

        return new BankStatementDetails(bankStatement, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, groupExternalId, isReconciled, loanTransaction, branchExternalId, accountingType, glCode,
                transactionType, bankStatementDetailType, receiptNumber, isManualReconciled, transactionIdForUpdate);
    }    
    
    public BankStatementDetails(final BankStatement bankStatement, final Date transactionDate,
            final BigDecimal amount, final String loanAccountNumber, final String receiptNumber,
            final Integer bankStatementDetailType, Boolean isManualReconciled, final String paymentTypeName, boolean isError) {
        this.bankStatement = bankStatement;
        this.loanAccountNumber = loanAccountNumber;
        this.transactionDate = transactionDate;
        this.bankStatementDetailType = bankStatementDetailType;
        this.receiptNumber = receiptNumber;
        this.transactionId = null;
        this.description = null;
        this.amount = amount;
        this.mobileNumber = null;
        this.clientAccountNumber = null;
        this.groupExternalId = null;
        this.isReconciled = false;
        this.loanTransaction = null;
        this.branchExternalId = null;
        this.accountingType = ReconciliationApiConstants.CLIENT_PAYMENT;
        this.glCode = null;
        this.transactionType = null;
        this.updatedDate = null;
        this.isManualReconciled = isManualReconciled;
        this.isError = isError;
        this.paymentTypeName = paymentTypeName;
    }

    public static BankStatementDetails simplifiedBankDetails(final BankStatement bankStatement, final Date transactionDate,
            final BigDecimal amount, final String loanAccountNumber, final String receiptNumber, final Integer bankStatementDetailType,
            Boolean isManualReconciled, final String paymentTypeName, boolean isError) {

        return new BankStatementDetails(bankStatement, transactionDate, amount, loanAccountNumber, receiptNumber, bankStatementDetailType,
                isManualReconciled, paymentTypeName, isError);
    }

    public void setBankStatement(BankStatement bankStatement) {
        this.bankStatement = bankStatement;
    }

    public void setIsReconciled(Boolean isReconciled) {
        this.isReconciled = isReconciled;
    }

    public void setLoanTransaction(LoanTransaction loanTransaction) {
        this.loanTransaction = loanTransaction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getGroupExternalId() {
        return groupExternalId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }
    
    public boolean isReconciled(){
        return this.isReconciled;
    }
    
    public String getGlCode(){
        return this.glCode;
    }
    
    public String getAccountingType(){
        return this.accountingType;
    }
    
    public void setGlAccount(String glAccount){
        this.glAccount = glAccount;
    }

	public Integer getBankStatementDetailType() {
		return this.bankStatementDetailType;
	}

	public void setBankStatementDetailType(Integer bankStatementDetailType) {
		this.bankStatementDetailType = bankStatementDetailType;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public LoanTransaction getLoanTransaction() {
		return this.loanTransaction;
	}

    
    public Date getTransactionDate() {
        return this.transactionDate;
    }
    
    public LocalDate getTransactionLocalDate() {
        LocalDate transactionDate = null;
        if (this.transactionDate != null) {
            transactionDate = LocalDate.fromDateFields(this.transactionDate);
        }
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    
    public String getLoanAccountNumber() {
        return this.loanAccountNumber;
    }

    
    public void setLoanAccountNumber(String loanAccountNumber) {
        this.loanAccountNumber = loanAccountNumber;
    }

	public BankStatement getBankStatement() {
		return bankStatement;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public Boolean getIsError() {
		return isError;
	}

	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	public Boolean isManualReconciled() {
		return this.isManualReconciled;
	}

	public void setManualReconciled(Boolean isManualReconciled) {
		this.isManualReconciled = isManualReconciled;
	}
	
	public String getBranchExternalId(){
		return this.branchExternalId;
	}
	
	public void setBranchExternalId(String branchExternalId){
		this.branchExternalId = branchExternalId;
	}
	
	public void setBranchId(Long branchId){
		this.officeId = branchId;
	};
	
	public Long getBrtanchid(){
		return this.officeId;
	}
	
    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        if (command.isChangeInLocalDateParameterNamed(ReconciliationApiConstants.transactionDateParamName, getTransactionLocalDate())) {
            final Date newValue = command.DateValueOfParameterNamed(ReconciliationApiConstants.transactionDateParamName);
            actualChanges.put(ReconciliationApiConstants.transactionDateParamName, newValue);
            this.transactionDate = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ReconciliationApiConstants.amountParamName, this.amount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ReconciliationApiConstants.amountParamName);
            actualChanges.put(ReconciliationApiConstants.amountParamName, newValue);
            this.amount = newValue;
        }

        if (command.isChangeInStringParameterNamed(ReconciliationApiConstants.accountingTypeParamName, this.accountingType)) {
            final String newValue = command.stringValueOfParameterNamedAllowingNull(ReconciliationApiConstants.accountingTypeParamName);
            actualChanges.put(ReconciliationApiConstants.accountingTypeParamName, newValue);
            this.accountingType = newValue;
        }

        if (command.isChangeInStringParameterNamed(ReconciliationApiConstants.glCodeParamName, this.glCode)) {
            final String newValue = command.stringValueOfParameterNamedAllowingNull(ReconciliationApiConstants.glCodeParamName);
            actualChanges.put(ReconciliationApiConstants.glCodeParamName, newValue);
            this.glCode = newValue;
        }

        if (command.isChangeInLongParameterNamed(ReconciliationApiConstants.officeIdParamName, this.officeId)) {
            final Long newValue = command.longValueOfParameterNamed(ReconciliationApiConstants.officeIdParamName);
            actualChanges.put(ReconciliationApiConstants.officeIdParamName, newValue);
            this.officeId = newValue;
        }

        return actualChanges;
    }

    public static BankStatementDetails instance(final BankStatement bankStatement, final String accountingType,
            final String loanAccountNumber, final Date transactionDate, final BigDecimal amount, final String paymentTypeName,
            final String paymentDetailAccountNumber, final String paymentDetailChequeNumber, final String routingCode,
            final String receiptNumber, final String paymentDetailBankNumber, final String note, final Integer bankStatementDetailType,
            final String savingsAccountNumber, final Long transactionIdForUpdate) {

        return new BankStatementDetails(bankStatement, accountingType, loanAccountNumber, transactionDate, amount, paymentTypeName,
                paymentDetailAccountNumber, paymentDetailChequeNumber, routingCode, receiptNumber, paymentDetailBankNumber, note,
                bankStatementDetailType, savingsAccountNumber, transactionIdForUpdate);
    }

    public BankStatementDetails(final BankStatement bankStatement, final String accountingType, final String loanAccountNumber,
            final Date transactionDate, final BigDecimal amount, final String paymentTypeName, final String paymentDetailAccountNumber,
            final String paymentDetailChequeNumber, final String routingCode, final String receiptNumber,
            final String paymentDetailBankNumber, final String note, final Integer bankStatementDetailType,
            final String savingsAccountNumber, final Long transactionIdForUpdate) {
        this.bankStatement = bankStatement;
        this.accountingType = accountingType;
        this.loanAccountNumber = loanAccountNumber;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.paymentTypeName = paymentTypeName;
        this.paymentDetailAccountNumber = paymentDetailAccountNumber;
        this.paymentDetailChequeNumber = paymentDetailChequeNumber;
        this.routingCode = routingCode;
        this.receiptNumber = receiptNumber;
        this.paymentDetailBankNumber = paymentDetailBankNumber;
        this.note = note;
        this.bankStatementDetailType = bankStatementDetailType;
        this.savingsAccountNumber = savingsAccountNumber;
        this.isReconciled = false;
        this.transactionIdForUpdate = transactionIdForUpdate;
    }

    public String getSavingsAccountNumber() {
        return this.savingsAccountNumber;
    }

    public void setSavingsAccountNumber(String savingsAccountNumber) {
        this.savingsAccountNumber = savingsAccountNumber;
    }

    public String getPaymentDetailAccountNumber() {
        return this.paymentDetailAccountNumber;
    }

    public String getPaymentDetailChequeNumber() {
        return this.paymentDetailChequeNumber;
    }

    public String getRoutingCode() {
        return this.routingCode;
    }

    public String getPaymentDetailBankNumber() {
        return this.paymentDetailBankNumber;
    }

    public String getNote() {
        return this.note;
    }

    public String getPaymentTypeName() {
        return this.paymentTypeName;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.isError = true;
        this.errmsg = errmsg;
    }
    
    public Long getTransactionIdForUpdate() {
        return this.transactionIdForUpdate;
    }

}
