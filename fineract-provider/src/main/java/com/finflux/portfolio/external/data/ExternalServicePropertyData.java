package com.finflux.portfolio.external.data;

public class ExternalServicePropertyData {

    private final String name;
    private final String value;
    private final Boolean isEncrypted;

    public ExternalServicePropertyData(final String name, final String value, final Boolean isEncrypted) {
        this.name = name;
        this.value = value;
        this.isEncrypted = isEncrypted;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Boolean getEncrypted() {
        return isEncrypted;
    }
}
