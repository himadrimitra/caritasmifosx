package com.finflux.portfolio.external.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.List;

public class ExternalServicesData {

    private final Long id;
    private final String name;
    private final String displayCode;
    private final EnumOptionData type;
    private List<ExternalServicePropertyData> properties;

    public ExternalServicesData(final Long id, final String name, final String displayCode, final EnumOptionData type) {
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

    public EnumOptionData getType() {
        return type;
    }

    public List<ExternalServicePropertyData> getProperties() {
        return properties;
    }

    public void setProperties(List<ExternalServicePropertyData> properties) {
        this.properties = properties;
    }
}
