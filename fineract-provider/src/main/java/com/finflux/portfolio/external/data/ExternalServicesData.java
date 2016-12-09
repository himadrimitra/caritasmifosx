package com.finflux.portfolio.external.data;

public class ExternalServicesData {

    private final Long id;
    private final String name;
    private final String displayCode;
    private final Integer type;

    public ExternalServicesData(final Long id, final String name, final String displayCode, final Integer type) {
        this.id = id;
        this.name = name;
        this.displayCode = displayCode;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public Integer getType() {
        return type;
    }
}
