package com.finflux.infrastructure.gis.country.service;

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.country.data.CountryData;

public interface CountryReadPlatformService {

    CountryData retrieveOne(final Long countryId);

    Collection<CountryData> retrieveAllCountryDataByCountryIds(final List<Long> countryIds);

    Collection<CountryData> retrieveAll();
}
