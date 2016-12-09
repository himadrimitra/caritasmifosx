package com.finflux.portfolio.bank.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.exception.BankAccountDetailNotFoundException;

@Service
public class BankAccountAssociationDetailsRepositoryWrapper {

    private final BankAccountDetailAssociationsRepository repository;

    @Autowired
    public BankAccountAssociationDetailsRepositoryWrapper(final BankAccountDetailAssociationsRepository repository) {
        this.repository = repository;
    }

    public BankAccountDetailAssociations findOneWithNotFoundDetection(final Long entityId, final Integer entityTypeId) {
        final BankAccountDetailAssociations bankAccountDetails = this.repository.findByEntityIdAndEntityTypeId(entityId, entityTypeId);
        if (bankAccountDetails == null) { throw new BankAccountDetailNotFoundException(entityId, entityTypeId); }
        return bankAccountDetails;
    }

}
