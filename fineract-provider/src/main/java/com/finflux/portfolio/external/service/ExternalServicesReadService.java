package com.finflux.portfolio.external.service;

import com.finflux.portfolio.external.data.ExternalServicePropertyData;
import com.finflux.portfolio.external.data.ExternalServicesData;

import java.util.Collection;
import java.util.List;


public interface ExternalServicesReadService {

    ExternalServicesData findOneWithNotFoundException(Long id);

    Collection<ExternalServicesData> findExternalServicesByType(Integer type);

    Collection<ExternalServicesData> findAllExternalServices();

    List<ExternalServicePropertyData> findClearPropertiesForExternalServices(Long id);

    List<ExternalServicePropertyData> findMaskedPropertiesForExternalServices(Long id);

}
