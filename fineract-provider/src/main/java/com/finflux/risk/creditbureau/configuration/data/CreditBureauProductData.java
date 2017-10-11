package com.finflux.risk.creditbureau.configuration.data;

public class CreditBureauProductData {

    private final Long id;
    private final String name;
    private final String implementationKey;
    private final Boolean isActive;

    private CreditBureauProductData(final Long id, final String name, final String implementationKey, final Boolean isActive) {
        this.id = id;
        this.name = name;
        this.implementationKey = implementationKey;
        this.isActive = isActive;
    }

    public static CreditBureauProductData instance(final Long id, final String name, final String implementationKey, final Boolean isActive) {
        return new CreditBureauProductData(id, name, implementationKey, isActive);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getImplementationKey() {
        return this.implementationKey;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }
}