package com.finflux.portfolio.external.data;

import java.util.Collection;
import java.util.List;


public interface ExternalServicesReadService {

    ExternalServicesData findOneWithNotFoundException(Long id);

    Collection<ExternalServicesData> findExternalServicesByType(Integer type);

    List<ExternalServicePropertyData> findPropertiesForExternalServices(Long id);

}
