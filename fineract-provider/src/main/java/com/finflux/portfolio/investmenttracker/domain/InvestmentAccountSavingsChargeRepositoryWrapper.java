package com.finflux.portfolio.investmenttracker.domain;

import java.util.List;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentAccountSavingsChargeRepositoryWrapper {
    
    private final InvestmentAccountSavingsChargeRepository repository;
    
    
    @Autowired
    public InvestmentAccountSavingsChargeRepositoryWrapper(InvestmentAccountSavingsChargeRepository repository) {
        this.repository = repository;
    }

    public void save(final InvestmentAccountSavingsCharge investmentAccountSavingsCharge) {
        this.repository.save(investmentAccountSavingsCharge);
    }
    
    public void save(final List<InvestmentAccountSavingsCharge> investmentAccountSavingsCharges) {
        this.repository.save(investmentAccountSavingsCharges);
    }
    
    public InvestmentAccountSavingsCharge findByInvestmentChargeAndSavingLinkedAccount(final Long investmentAccountCharge, final Long investmentAccountSavingsLinkageId) {
        final InvestmentAccountSavingsCharge investmentAccountSavingsCharge = this.repository.findByInvestmentChargeAndSavingLinkedAccount(investmentAccountCharge, investmentAccountSavingsLinkageId);
        return investmentAccountSavingsCharge;
    }
    
    public List<InvestmentAccountSavingsCharge> findBySavingLinkedAccount(final Long investmentAccountSavingsLinkageId) {
        final List<InvestmentAccountSavingsCharge> investmentAccountSavingsCharges = this.repository.findBySavingLinkedAccount(investmentAccountSavingsLinkageId);
        return investmentAccountSavingsCharges;
    }
}
