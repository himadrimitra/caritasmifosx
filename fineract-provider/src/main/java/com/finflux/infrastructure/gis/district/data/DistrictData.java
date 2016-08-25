package com.finflux.infrastructure.gis.district.data;

import java.util.Collection;

import com.finflux.infrastructure.gis.taluka.data.TalukaData;

public class DistrictData {

    private final Long districtId;
    private final Long stateId;
    private final String isoDistrictCode;
    private final String districtName;
    private final Collection<TalukaData> talukaDatas;

    private DistrictData(final Long districtId, final Long stateId, final String isoDistrictCode, final String districtName,final Collection<TalukaData> talukaDatas) {
        this.districtId = districtId;
        this.stateId = stateId;
        this.isoDistrictCode = isoDistrictCode;
        this.districtName = districtName;
        this.talukaDatas = talukaDatas;
    }

    public static DistrictData instance(final Long districtId, final Long stateId, final String isoDistrictCode,
            final String districtName,final Collection<TalukaData> talukaDatas) {
        return new DistrictData(districtId, stateId, isoDistrictCode, districtName,talukaDatas);
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
    public Collection<TalukaData> getTalukaDatas() {
        return this.talukaDatas;
    }
}
