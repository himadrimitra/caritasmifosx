package com.finflux.risk.creditbureau.configuration.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.exception.CreditBureauProductNotFoundException;

@Service
public class CreditBureauProductRepositoryWrapper {

    private final CreditBureauProductRepository repository;

    @Autowired
    public CreditBureauProductRepositoryWrapper(final CreditBureauProductRepository repository) {
        this.repository = repository;
    }

    public CreditBureauProduct findOneWithNotFoundDetection(final Long id) {
        final CreditBureauProduct creditBureauProduct = this.repository.findOne(id);
        if (creditBureauProduct == null) { throw new CreditBureauProductNotFoundException(id); }
        return creditBureauProduct;
    }

    public void save(final CreditBureauProduct creditBureauProduct) {
        this.repository.save(creditBureauProduct);
    }

    public void save(final List<CreditBureauProduct> creditBureauProduct) {
        this.repository.save(creditBureauProduct);
    }

    public void saveAndFlush(final CreditBureauProduct creditBureauProduct) {
        this.repository.saveAndFlush(creditBureauProduct);
    }

    public void delete(final CreditBureauProduct creditBureauProduct) {
        this.repository.delete(creditBureauProduct);
    }
}