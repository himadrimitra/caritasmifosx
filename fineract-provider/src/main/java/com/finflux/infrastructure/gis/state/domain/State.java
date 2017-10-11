package com.finflux.infrastructure.gis.state.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.infrastructure.gis.country.domain.Country;

@Entity
@Table(name = "f_state")
public class State extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = true)
    private Country country;

    @Column(name = "iso_state_code", length = 3, nullable = false)
    private String isoStateCode;

    @Column(name = "state_name", length = 100, nullable = false)
    private String stateName;

    protected State() {}

    private State(final Country country, final String isoStateCode, final String stateName) {
        this.country = country;
        this.isoStateCode = isoStateCode;
        this.stateName = stateName;
    }

    public static State createNew(final JsonCommand command, final Country country) {
        final String isoStateCode = command.stringValueOfParameterNamed("isoStateCode");
        final String stateName = command.stringValueOfParameterNamed("stateName");
        return new State(country, isoStateCode, stateName);
    }

    public String getStateName() {
        return this.stateName;
    }

    public Country getCountry() {
        return this.country;
    }

    public Long getCountryId() {
        Long CountryId = null;
        if (this.country != null) {
            CountryId = this.country.getId();
        }
        return CountryId;
    }
}
