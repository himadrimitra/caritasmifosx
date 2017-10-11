package com.finflux.infrastructure.gis.state.data;

import java.util.Collection;

import com.finflux.infrastructure.gis.district.data.DistrictData;

public class StateData {

    private final Long stateId;
    private final Long countryId;
    private final String isoStateCode;
    private final String stateName;
    private final Collection<DistrictData> districtDatas;

    private StateData(final Long stateId, final Long countryId, final String isoStateCode, final String stateName,
            final Collection<DistrictData> districtDatas) {
        this.stateId = stateId;
        this.countryId = countryId;
        this.isoStateCode = isoStateCode;
        this.stateName = stateName;
        this.districtDatas = districtDatas;
    }

    public static StateData instance(final Long stateId, final Long countryId, final String isoStateCode, final String stateName,
            final Collection<DistrictData> districtDatas) {
        return new StateData(stateId, countryId, isoStateCode, stateName, districtDatas);
    }

    public Long getStateId() {
        return this.stateId;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public String getIsoStateCode() {
        return this.isoStateCode;
    }

    public String getStateName() {
        return this.stateName;
    }

    public Collection<DistrictData> getDistrictDatas() {
        return this.districtDatas;
    }

}
