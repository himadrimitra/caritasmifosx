package com.finflux.portfolio.external.domain;

import com.finflux.portfolio.external.exception.ExternalServicesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.domain.BankAccountDetails;
import com.finflux.portfolio.bank.exception.BankAccountDetailNotFoundException;

@Service
public class OtherExternalServiceRepositoryWrapper {

    private final OtherExternalServiceRepository repository;

    @Autowired
    public OtherExternalServiceRepositoryWrapper(final OtherExternalServiceRepository repository) {
        this.repository = repository;
    }

    public OtherExternalService findOneWithNotFoundDetection(final Long id) {
        final OtherExternalService externalService = this.repository.findOne(id);
        if (externalService == null) { throw new ExternalServicesNotFoundException(id); }
        return externalService;
    }

}
