package com.finflux.infrastructure.gis.district.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.infrastructure.gis.state.domain.State;

@Entity
@Table(name = "f_district")
public class District extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = true)
    private State state;

    @Column(name = "district_name", length = 100, nullable = false)
    private String districtName;

    protected District() {}

    private District(final State state, final String districtName) {
        this.state = state;
        this.districtName = districtName;
    }

    public static District create(final JsonCommand command, final State state) {
        final String districtName = command.stringValueOfParameterNamed("districtName");

        return new District(state, districtName);
    }

    public State getState() {
        return this.state;
    }

    public Long getStateId() {
        Long stateId = null;
        if (this.state != null) {
            stateId = this.state.getId();
        }
        return stateId;
    }

    public String getDistrictName() {
        return this.districtName;
    }
}