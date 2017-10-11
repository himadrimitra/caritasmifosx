package com.finflux.infrastructure.gis.country.service;

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.country.data.CountryData;

public interface CountryReadPlatformService {

    CountryData retrieveOne(final Long countryId, final boolean isTemplateRequired);

    Collection<CountryData> retrieveAllCountryDataByCountryIds(final List<Long> countryIds,final boolean isTemplateRequired);

    Collection<CountryData> retrieveAll();
}
