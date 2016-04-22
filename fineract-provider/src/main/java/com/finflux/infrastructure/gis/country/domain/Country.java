package com.finflux.infrastructure.gis.country.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_country")
public class Country extends AbstractPersistable<Long> {

    @Column(name = "iso_country_code", length = 2, nullable = false)
    private String isoCountryCode;

    @Column(name = "country_name", length = 100, nullable = false)
    private String countryName;

    protected Country() {}

    private Country(final String isoCountryCode, final String countryName) {
        this.isoCountryCode = isoCountryCode;
        this.countryName = countryName;
    }

    public static Country createNew(final JsonCommand command) {
        final String isoCountryCode = command.stringValueOfParameterNamed("isoCountryCode");
        final String countryName = command.stringValueOfParameterNamed("countryName");
        return new Country(isoCountryCode, countryName);
    }

    public String getIsoCountryCode() {
        return this.isoCountryCode;
    }

    public String getCountryName() {
        return this.countryName;
    }
}
