package com.finflux.portfolio.external.domain;

import com.finflux.portfolio.external.exception.ExternalServicePropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtherExternalServicePropertyRepositoryWrapper {

    private final OtherExternalServicePropertyRepository repository;

    @Autowired
    public OtherExternalServicePropertyRepositoryWrapper(final OtherExternalServicePropertyRepository repository) {
        this.repository = repository;
    }

    public OtherExternalServiceProperty findOneWithNotFoundDetection(final Long serviceId, final String name) {
        final OtherExternalServiceProperty serviceProperty = this.repository.findByExternalServiceIdAndName(serviceId,name);
        if (serviceProperty == null) { throw new ExternalServicePropertyNotFoundException(serviceId,name); }
        return serviceProperty;
    }

}
