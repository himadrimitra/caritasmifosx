package com.finflux.infrastructure.gis.country.data;

import java.util.Collection;

import com.finflux.infrastructure.gis.state.data.StateData;
import com.finflux.infrastructure.gis.taluka.data.TalukaData;

public class CountryData {

    private final Long countryId;
    private final String isoCountryCode;
    private final String countryName;
    private final Collection<StateData> statesDatas;

    private CountryData(final Long countryId, final String isoCountryCode, final String countryName,
            final Collection<StateData> statesDatas) {
        this.countryId = countryId;
        this.isoCountryCode = isoCountryCode;
        this.countryName = countryName;
        this.statesDatas = statesDatas;
    }

    public static CountryData instance(final Long countryId, final String isoCountryCode, final String countryName,
            final Collection<StateData> statesDatas) {
        return new CountryData(countryId, isoCountryCode, countryName, statesDatas);
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public String getIsoCountryCode() {
        return this.isoCountryCode;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public Collection<StateData> getStatesDatas() {
        return this.statesDatas;
    }
}
