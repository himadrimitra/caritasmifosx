/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.reconcilation.bank.domain.Bank;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_bank_statement")
public class BankStatement extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "cif_key_document_id", nullable = true)
    private Document cpifDocument;

    @ManyToOne
    @JoinColumn(name = "org_statement_key_document_id", nullable = true)
    private Document orgStatementDocument;

    @Column(name = "is_reconciled")
    private Boolean isReconciled;

    @ManyToOne
    @JoinColumn(name = "bank", nullable = true)
    private Bank bank;
    
    @Column(name = "payment_type")
    private String paymentType;

    public BankStatement(final String name, final String description, final Document cpifDocument, final Document orgStatementDocument,
            final Boolean isReconciled, final Bank bank) {
        this.name = name;
        this.description = description;
        this.cpifDocument = cpifDocument;
        this.orgStatementDocument = orgStatementDocument;
        this.isReconciled = isReconciled;
        this.bank = bank;
    };

    public BankStatement() {
        super();
    }

    public static BankStatement instance(final String name, final String description, final Document cpifDocument,
            final Document orgStatementDocument, final Boolean isReconciled,
            final Bank bank) {
        return new BankStatement(name, description, cpifDocument, orgStatementDocument, isReconciled, bank);
    }

    public Document getCpifDocument() {
        return cpifDocument;
    }

    public void setCpifDocument(Document cpifDocument) {
        this.cpifDocument = cpifDocument;
    }

    public Document getOrgStatementDocument() {
        return orgStatementDocument;
    }

    public void setOrgStatementDocument(Document orgStatementDocument) {
        this.orgStatementDocument = orgStatementDocument;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsReconciled() {
        return isReconciled;
    }

    public void setIsReconciled(Boolean isReconciled) {
        this.isReconciled = isReconciled;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Bank getBank() {
        return bank;
    }

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
    
    
}
