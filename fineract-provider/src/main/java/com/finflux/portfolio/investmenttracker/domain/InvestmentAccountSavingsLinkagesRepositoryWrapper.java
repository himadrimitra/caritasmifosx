package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountNotFoundException;
import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountSavingsLinkagesNotFoundException;

@Service
public class InvestmentAccountSavingsLinkagesRepositoryWrapper{
    
    private final InvestmentAccountSavingsLinkagesRepository repository;
    
    @Autowired
    public InvestmentAccountSavingsLinkagesRepositoryWrapper(InvestmentAccountSavingsLinkagesRepository repository) {
        this.repository = repository;
    }
    
    public InvestmentAccountSavingsLinkages findOneWithNotFoundDetection(final Long id) {
        final InvestmentAccountSavingsLinkages investmentSavingAccount = this.repository.findOne(id);
        if (investmentSavingAccount == null) { throw new InvestmentAccountSavingsLinkagesNotFoundException(id); }
        return investmentSavingAccount;
    }

}
