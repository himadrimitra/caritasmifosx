package com.finflux.infrastructure.gis.taluka.data;

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
