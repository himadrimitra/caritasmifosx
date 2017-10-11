package com.finflux.portfolio.bank.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.exception.BankAccountDetailNotFoundException;

@Service
public class BankAccountDetailsRepositoryWrapper {

    private final BankAccountDetailsRepository repository;

    @Autowired
    public BankAccountDetailsRepositoryWrapper(final BankAccountDetailsRepository repository) {
        this.repository = repository;
    }

    public BankAccountDetails findOneWithNotFoundDetection(final Long id) {
        final BankAccountDetails bankAccountDetails = this.repository.findOne(id);
        if (bankAccountDetails == null) { throw new BankAccountDetailNotFoundException(id); }
        return bankAccountDetails;
    }

}
