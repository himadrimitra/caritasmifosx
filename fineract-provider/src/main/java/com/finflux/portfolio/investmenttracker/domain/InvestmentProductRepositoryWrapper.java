package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentProductNotFoundException;

@Service
public class InvestmentProductRepositoryWrapper {
    
    private final InvestmentProductRepository investmentProductRepository;
    
    @Autowired
    public InvestmentProductRepositoryWrapper(final InvestmentProductRepository investmentProductRepository){
        this.investmentProductRepository = investmentProductRepository;
    }
    
    public InvestmentProduct findOneWithNotFoundDetection(final Long id) {
        final InvestmentProduct investmentProduct = this.investmentProductRepository.findOne(id);
        if (investmentProduct == null) { throw new InvestmentProductNotFoundException(id); }
        return investmentProduct;
    }

}
