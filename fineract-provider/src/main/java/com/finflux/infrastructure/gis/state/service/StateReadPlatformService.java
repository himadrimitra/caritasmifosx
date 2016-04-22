package com.finflux.infrastructure.gis.state.service;

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.state.data.StateData;

public interface StateReadPlatformService {

    StateData retrieveOne(final Long stateId);

    Collection<StateData> retrieveAllStateDataByStateIds(final List<Long> stateIds);

    Collection<StateData> retrieveAllStateDataByCountryId(final Long countryId);

    Collection<StateData> retrieveAllStateDataByCountryIds(final List<Long> countryIds);
}