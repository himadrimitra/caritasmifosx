/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.data;

public class BankData {

    private final Long id;
    private final String name;
    private final Long glAccount;
    private final String glCode;

    public BankData(final Long id, final String name, final Long glAccount, final String glCode) {
        this.id = id;
        this.name = name;
        this.glAccount = glAccount;
        this.glCode = glCode;
    }

    public static BankData instance(final Long id, final String name, final Long glAccount, final String glCode) {
        return new BankData(id, name, glAccount, glCode);
    }
}
