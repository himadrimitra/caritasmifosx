package com.finflux.infrastructure.gis.state.service;

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.state.data.StateData;

public interface StateReadPlatformService {

    StateData retrieveOne(final Long stateId,final boolean isTemplateRequired);

    Collection<StateData> retrieveAllStateDataByStateIds(final List<Long> stateIds,final boolean isTemplateRequired);

    Collection<StateData> retrieveAllStateDataByCountryId(final Long countryId);

    Collection<StateData> retrieveAllStateDataByCountryIds(final List<Long> countryIds);
}