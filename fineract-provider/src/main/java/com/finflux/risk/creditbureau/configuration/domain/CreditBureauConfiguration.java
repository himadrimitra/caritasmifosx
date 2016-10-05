package com.finflux.risk.creditbureau.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_creditbureau_configuration")
public class CreditBureauConfiguration extends AbstractPersistable<Long> {

    @Column(name = "creditbureau_implementation_key")
    private String creditBureauImplementationKey;

    @Column(name = "config_key")
    private String configKey;

    private String value;

    private String description;

    @Column(name = "is_configurable")
    private boolean isConfigurable;

    protected CreditBureauConfiguration() {

    }

    public CreditBureauConfiguration(String creditBureauImplementationKey, String configKey, String value, String description,
            boolean isConfigurable) {
        this.creditBureauImplementationKey = creditBureauImplementationKey;
        this.configKey = configKey;
        this.value = value;
        this.description = description;
        this.isConfigurable = isConfigurable;
    }
}
