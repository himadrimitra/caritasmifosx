/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.reconcilation.ReconciliationApiConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_bank")
public class Bank extends AbstractPersistable<Long> {

    @Column(name = "name")
    private String name;

    @OneToOne(optional = true)
    @JoinColumn(name = "gl_account", nullable = true)
    private GLAccount glAccount;

    private Bank(final String name, final GLAccount glAccount) {
        this.name = name;
        this.glAccount = glAccount;
    }

    public static Bank instance(final String name, final GLAccount glAccount) {
        return new Bank(name, glAccount);
    }

    public Bank() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GLAccount getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(GLAccount glAccount) {
        this.glAccount = glAccount;
    }

    public Map<String, Object> getBankActualChanges(JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInStringParameterNamed(ReconciliationApiConstants.nameParamName, this.name)) {
            final String newName = command.stringValueOfParameterNamed(ReconciliationApiConstants.nameParamName);
            actualChanges.put(ReconciliationApiConstants.nameParamName, newName);
        }

        if (command.isChangeInLongParameterNamed(ReconciliationApiConstants.glAccountParamName, this.glAccount.getId())) {
            final Long newGlCode = command.longValueOfParameterNamed(ReconciliationApiConstants.glAccountParamName);
            actualChanges.put(ReconciliationApiConstants.glAccountParamName, newGlCode);
        }

        return actualChanges;
    }

}
