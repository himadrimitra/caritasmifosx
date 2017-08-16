package com.finflux.risk.creditbureau.provider.cibil.request;

public class CustomerIdenfier {

    private final String identityType;
    private final String identity;

    public CustomerIdenfier(String identityType, String identity) {
        super();
        this.identityType = identityType;
        this.identity = identity;
    }

    public String getIdentityType() {
        return this.identityType;
    }

    public String getIdentity() {
        return this.identity;
    }
}