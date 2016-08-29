package com.finflux.infrastructure.gis.taluka.domain;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.infrastructure.gis.district.domain.District;

@Entity
@Table(name = "f_taluka")
public class Taluka extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = true)
    private District district;

    @Column(name = "iso_taluka_code", length = 3, nullable = false)
    private String isoTalukaCode;

    @Column(name = "taluka_name", length = 100, nullable = false)
    private String talukaName;

    protected Taluka() {}

    private Taluka(final District district, final String isoTalukaCode, final String talukaName) {
        this.district = district;
        this.isoTalukaCode = isoTalukaCode;
        this.talukaName = talukaName;
    }

    public static Taluka create(final JsonCommand command, final District district) {
        final String isoTalukaCode = command.stringValueOfParameterNamed("isoTalukaCode");
        final String talukaName = command.stringValueOfParameterNamed("talukaName");
        return new Taluka(district, isoTalukaCode, talukaName);
    }

    public District getDistrict() {
        return this.district;
    }

    public Long getDistrictId() {
        Long districtId = null;
        if (this.district != null) {
            districtId = this.district.getId();
        }
        return districtId;
    }

    public String getTalukaName() {
        return this.talukaName;
    }
}
