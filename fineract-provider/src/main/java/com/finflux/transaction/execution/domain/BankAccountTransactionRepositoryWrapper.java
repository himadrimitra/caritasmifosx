package com.finflux.transaction.execution.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.domain.BankAccountDetailsRepository;
import com.finflux.transaction.execution.exception.BankAccountTransactionNotFoundException;

@Service
public class BankAccountTransactionRepositoryWrapper {

    private final BankAccountTransactionRepository repository;

    @Autowired
    public BankAccountTransactionRepositoryWrapper(final BankAccountTransactionRepository repository) {
        this.repository = repository;
    }

    public BankAccountTransaction findOneWithNotFoundDetection(final Long id) {
        final BankAccountTransaction bankAccountTransaction = this.repository.findOne(id);
        if (bankAccountTransaction == null) { throw new BankAccountTransactionNotFoundException(id); }
        return bankAccountTransaction;
    }

}
