package com.finflux.reconcilation.bank.data;

@SuppressWarnings("unused")
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
