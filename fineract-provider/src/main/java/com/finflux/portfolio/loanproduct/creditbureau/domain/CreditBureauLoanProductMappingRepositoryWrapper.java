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
        final CreditBureauLoanProductMapping creditBureauLoanProductMapping = this.repository.findOne(id);
        if (creditBureauLoanProductMapping == null) { throw new CreditBureauLoanProductMappingNotFoundException(id); }
        return creditBureauLoanProductMapping;
    }

    public void save(final CreditBureauLoanProductMapping creditBureauLoanProductMapping) {
        this.repository.save(creditBureauLoanProductMapping);
    }

    public void save(final List<CreditBureauLoanProductMapping> creditBureauLoanProductMapping) {
        this.repository.save(creditBureauLoanProductMapping);
    }

    public void saveAndFlush(final CreditBureauLoanProductMapping creditBureauLoanProductMapping) {
        this.repository.saveAndFlush(creditBureauLoanProductMapping);
    }

    public void delete(final CreditBureauLoanProductMapping creditBureauLoanProductMapping) {
        this.repository.delete(creditBureauLoanProductMapping);
    }
}