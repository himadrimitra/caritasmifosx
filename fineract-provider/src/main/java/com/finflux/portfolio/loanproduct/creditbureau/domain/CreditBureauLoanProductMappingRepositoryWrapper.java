package com.finflux.portfolio.loanproduct.creditbureau.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loanproduct.creditbureau.exception.CreditBureauLoanProductMappingNotFoundException;

@Service
public class CreditBureauLoanProductMappingRepositoryWrapper {

    private final CreditBureauLoanProductMappingRepository repository;

    @Autowired
    public CreditBureauLoanProductMappingRepositoryWrapper(final CreditBureauLoanProductMappingRepository repository) {
        this.repository = repository;
    }

    public CreditBureauLoanProductMapping findOneWithNotFoundDetection(final Long id) {
        final CreditBureauLoanProductMapping loanProductCreditBureauMapping = this.repository.findOne(id);
        if (loanProductCreditBureauMapping == null) { throw new CreditBureauLoanProductMappingNotFoundException(id); }
        return loanProductCreditBureauMapping;
    }

    public void save(final CreditBureauLoanProductMapping loanProductCreditBureauMapping) {
        this.repository.save(loanProductCreditBureauMapping);
    }

    public void save(final List<CreditBureauLoanProductMapping> loanProductCreditBureauMapping) {
        this.repository.save(loanProductCreditBureauMapping);
    }

    public void saveAndFlush(final CreditBureauLoanProductMapping loanProductCreditBureauMapping) {
        this.repository.saveAndFlush(loanProductCreditBureauMapping);
    }

    public void delete(final CreditBureauLoanProductMapping loanProductCreditBureauMapping) {
        this.repository.delete(loanProductCreditBureauMapping);
    }
}