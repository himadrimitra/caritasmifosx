package com.finflux.portfolio.external.data;

import java.util.Collection;


public interface ExternalServicesReadService {

    ExternalServicesData findOneWithNotFoundException(Long id);

    Collection<ExternalServicesData> findExternalServicesByType(Integer type);

}
