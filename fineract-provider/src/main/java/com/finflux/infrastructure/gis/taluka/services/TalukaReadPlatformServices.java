package com.finflux.infrastructure.gis.taluka.services;

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.taluka.data.TalukaData;

public interface TalukaReadPlatformServices {
    
    TalukaData retrieveOne(final Long talukaId);

    Collection<TalukaData> retrieveAllTalukaDataByTalukaIds(final List<Long> talukaIds);
    
    Collection<TalukaData> retrieveAllTalukaDataByDistrictId(final Long districtId);

    Collection<TalukaData> retrieveAllTalukaDataByDistrictIds(final List<Long> districtIds);

}
