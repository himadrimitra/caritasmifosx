package com.finflux.portfolio.external.data;

public class ExternalServicePropertyData {

    private String name;
    private String value;
    private Boolean isEncrypted;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEncrypted(Boolean encrypted) {
        isEncrypted = encrypted;
    }
}
