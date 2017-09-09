package com.finflux.infrastructure.gis.district.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.village.data.VillageData;

import com.finflux.infrastructure.gis.district.data.DistrictData;

public interface DistrictReadPlatformService {

    DistrictData retrieveOne(final Long districtId, final boolean isTemplateRequired);

    Collection<DistrictData> retrieveAllDistrictDataByDistrictIds(final List<Long> districtIds, final boolean isTemplateRequired);

    Collection<DistrictData> retrieveAllDistrictDataByStateId(final Long stateId);

    Collection<DistrictData> retrieveAllDistrictDataByStateIds(final List<Long> stateIds);

    Collection<VillageData> retrieveVillages(final Long distictId, final Integer status);
}