package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountNotFoundException;

@Service
public class InvestmentAccountRepositoryWrapper {

    
    private final InvestmentAccountRepository investmentAccountRepository;
    
    @Autowired
    public InvestmentAccountRepositoryWrapper(final InvestmentAccountRepository investmentAccountRepository){
        this.investmentAccountRepository = investmentAccountRepository;
    }
    
    public InvestmentAccount findOneWithNotFoundDetection(final Long id) {
        final InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(id);
        if (investmentAccount == null) { throw new InvestmentAccountNotFoundException(id); }
        return investmentAccount;
    }
}
