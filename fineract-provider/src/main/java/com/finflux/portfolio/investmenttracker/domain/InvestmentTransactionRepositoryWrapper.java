package com.finflux.portfolio.investmenttracker.domain;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentTransactionNotFoundException;

@Service
public class InvestmentTransactionRepositoryWrapper {

    private final InvestmentTransactionRepository investmentTransactionRepository;

    @Autowired
    public InvestmentTransactionRepositoryWrapper(final InvestmentTransactionRepository investmentTransactionRepository) {
        this.investmentTransactionRepository = investmentTransactionRepository;
    }

    public void save(final InvestmentTransaction investmentTransaction) {
        this.investmentTransactionRepository.save(investmentTransaction);
    }

    public void save(final Collection<InvestmentTransaction> investmentTransactions) {
        this.investmentTransactionRepository.save(investmentTransactions);
    }

    public InvestmentTransaction findOneWithNotFoundDetection(final Long id) {
        final InvestmentTransaction investmentTransaction = this.investmentTransactionRepository.findOne(id);
        if (investmentTransaction == null) { throw new InvestmentTransactionNotFoundException(id); }
        return investmentTransaction;
    }

}
