package com.finflux.infrastructure.gis.district.data;

public class DistrictData {

    private final Long districtId;
    private final Long stateId;
    private final String isoDistrictCode;
    private final String districtName;

    private DistrictData(final Long districtId, final Long stateId, final String isoDistrictCode, final String districtName) {
        this.districtId = districtId;
        this.stateId = stateId;
        this.isoDistrictCode = isoDistrictCode;
        this.districtName = districtName;
    }

    public static DistrictData instance(final Long districtId, final Long stateId, final String isoDistrictCode,
            final String districtName) {
        return new DistrictData(districtId, stateId, isoDistrictCode, districtName);
    }
    
    public Long getDistrictId() {
        return this.districtId;
    }

    
    public Long getStateId() {
        return this.stateId;
    }

    
    public String getIsoDistrictCode() {
        return this.isoDistrictCode;
    }

    
    public String getDistrictName() {
        return this.districtName;
    }
}
