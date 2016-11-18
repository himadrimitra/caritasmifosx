/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
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
    

    public BankStatementDetails(final BankStatement bankStatement, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final String groupExternalId, final Boolean isReconciled,
            final LoanTransaction loanTransaction, final String branchExternalId, final String accountingType, final String glCode,
            final String transactionType, final Integer bankStatementDetailType, final String receiptNumber, Boolean isManualReconciled) {
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
    }

    public BankStatementDetails() {
        super();
    }

    public static BankStatementDetails instance(final BankStatement bankStatement, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final String groupExternalId, final Boolean isReconciled,
            final LoanTransaction loanTransaction, final String branchExternalId, final String accountingType, final String glCode,
            final String transactionType, final Integer bankStatementDetailType, final String receiptNumber, final Boolean isManualReconciled) {

        return new BankStatementDetails(bankStatement, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, groupExternalId, isReconciled, loanTransaction, branchExternalId, accountingType, glCode,
                transactionType, bankStatementDetailType, receiptNumber, isManualReconciled);
    }    
    
    public BankStatementDetails(final BankStatement bankStatement, final Date transactionDate,
            final BigDecimal amount, final String loanAccountNumber, final String receiptNumber,
            final Integer bankStatementDetailType, Boolean isManualReconciled) {
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
    }

    public static BankStatementDetails simplifiedBankDetails(final BankStatement bankStatement, final Date transactionDate,
            final BigDecimal amount, final String loanAccountNumber, final String receiptNumber,
            final Integer bankStatementDetailType, Boolean isManualReconciled) {

        return new BankStatementDetails(bankStatement, transactionDate, amount, loanAccountNumber, receiptNumber,  bankStatementDetailType, isManualReconciled);
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
	
}
