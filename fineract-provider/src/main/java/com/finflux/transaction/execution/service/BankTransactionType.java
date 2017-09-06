/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.transaction.execution.service;

public enum BankTransactionType {

    CREATE(0, "bankTransactionType.create", "create"), //
    SUBMIT(1, "bankTransactionType.submit", "submit"), //
    INITIATE(2, "bankTransactionType.initiate", "initiate"), //
    UNDO_DISBURSEMENT(3, "bankTransactionType.undoDisbursement", "undoDisbursement");

    private final Integer value;
    private final String code;
    private final String displayName;

    private BankTransactionType(final Integer value, final String code, final String displayName) {
        this.value = value;
        this.code = code;
        this.displayName = displayName;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isCreateTransaction() {
        return this.value.equals(BankTransactionType.CREATE.getValue());
    }

    public boolean isSubmitTransaction() {
        return this.value.equals(BankTransactionType.SUBMIT.getValue());
    }

    public boolean isInitiateTransaction() {
        return this.value.equals(BankTransactionType.INITIATE.getValue());
    }

    public boolean isUndoDisbursement() {
        return this.value.equals(BankTransactionType.UNDO_DISBURSEMENT.getValue());
    }
}
