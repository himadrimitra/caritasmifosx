package com.finflux.infrastructure.gis.district.domain;

import static com.finflux.infrastructure.gis.district.api.DistrictApiConstants.districtCodeParamName;
import static com.finflux.infrastructure.gis.district.api.DistrictApiConstants.districtNameParamName;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.infrastructure.gis.district.api.DistrictApiConstants;
import com.finflux.infrastructure.gis.state.domain.State;

@Entity
@Table(name = "f_district")
public class District extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = true)
    private State state;

    @Column(name = "iso_district_code", length = 3, nullable = false)
    private String districtCode;

    @Column(name = "district_name", length = 100, nullable = false)
    private String districtName;

    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name = "activation_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @Column(name = "actvivatedby_userid", nullable = true)
    private Long activatedBy;

    @Column(name = "rejectedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date rejectedonDate;

    @Column(name = "rejectedby_userid", nullable = true)
    private Long rejectedBy;

    protected District() {}

    private District(final State state, final String districtName, final String districtCode) {
        this.state = state;
        this.districtName = districtName;
        this.districtCode = districtCode;
        this.status = DistrictStatus.PENDING.getValue();
    }

    public static District create(final JsonCommand command, final State state) {
        final String districtName = command.stringValueOfParameterNamed(districtNameParamName);
        final String districtCode = command.stringValueOfParameterNamed(districtCodeParamName);
        return new District(state, districtName, districtCode);
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

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(3);

        if (command.isChangeInLongParameterNamed(DistrictApiConstants.stateIdParamName, getStateId())) {
            final Long newValue = command.longValueOfParameterNamed(DistrictApiConstants.stateIdParamName);
            actualChanges.put(DistrictApiConstants.stateIdParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(DistrictApiConstants.districtNameParamName, this.districtName)) {
            final String newValue = command.stringValueOfParameterNamed(DistrictApiConstants.districtNameParamName);
            this.districtName = StringUtils.defaultIfEmpty(newValue, null);
            actualChanges.put(DistrictApiConstants.districtNameParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(DistrictApiConstants.districtCodeParamName, this.districtCode)) {
            final String newValue = command.stringValueOfParameterNamed(DistrictApiConstants.districtCodeParamName);
            this.districtCode = StringUtils.defaultIfEmpty(newValue, null);
            actualChanges.put(DistrictApiConstants.districtCodeParamName, newValue);
        }
        return actualChanges;
    }

    public Map<String, Object> activate(final AppUser currentUser) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);
        this.activationDate = DateUtils.getDateOfTenant();
        this.activatedBy = currentUser.getId();
        this.status = DistrictStatus.ACTIVE.getValue();
        actualChanges.put("status", DistrictStatus.ACTIVE.getEnumOptionData());
        return actualChanges;
    }

    public Map<String, Object> reject(final AppUser currentUser) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);
        this.rejectedonDate = DateUtils.getDateOfTenant();
        this.rejectedBy = currentUser.getId();
        this.status = DistrictStatus.REJECTED.getValue();
        actualChanges.put("status", DistrictStatus.REJECTED.getEnumOptionData());
        return actualChanges;
    }

    public boolean isPending() {
        return DistrictStatus.fromInt(this.status).isPending();
    }

    public void updateState(final State state) {
        this.state = state;
    }
}