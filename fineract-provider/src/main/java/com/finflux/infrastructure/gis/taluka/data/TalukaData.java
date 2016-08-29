package com.finflux.infrastructure.gis.taluka.data;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

public class TalukaData {

    private final Long talukaId;
    private final Long districtId;
    private final String isoTalukaCode;
    private final String talukaName;
    
    public TalukaData(final Long talukaId, final Long districtId, final String isoTalukaCode, final String talukaName) {
        this.talukaId = talukaId;
        this.districtId = districtId;
        this.isoTalukaCode = isoTalukaCode;
        this.talukaName = talukaName;
    }
    
    public static TalukaData instance(final Long talukaId, final Long districtId, final String isoTalukaCode,
            final String talukaName) {
        return new TalukaData(talukaId, districtId, isoTalukaCode, talukaName);
    }
    
    public Long getTalukaId() {
        return this.talukaId;
    }
    
    public Long getDistrictId() {
        return this.districtId;
    }

    public String getIsoTalukaCode() {
        return this.isoTalukaCode;
    }
    
    public String getTalukaName() {
        return this.talukaName;
    }
    
}
