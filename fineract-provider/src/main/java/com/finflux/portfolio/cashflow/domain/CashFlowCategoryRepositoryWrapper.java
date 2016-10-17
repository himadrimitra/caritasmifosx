package com.finflux.portfolio.cashflow.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.exception.CashFlowCategoryNotFoundException;

@Service
public class CashFlowCategoryRepositoryWrapper {

    private final CashFlowCategoryRepository repository;

    @Autowired
    public CashFlowCategoryRepositoryWrapper(final CashFlowCategoryRepository repository) {
        this.repository = repository;
    }

    public CashFlowCategory findOneWithNotFoundDetection(final Long id) {
        final CashFlowCategory cashFlowCategory = this.repository.findOne(id);
        if (cashFlowCategory == null) { throw new CashFlowCategoryNotFoundException(id); }
        return cashFlowCategory;
    }

    public void save(final CashFlowCategory cashFlowCategory) {
        this.repository.save(cashFlowCategory);
    }

    public void save(final List<CashFlowCategory> cashFlowCategorys) {
        this.repository.save(cashFlowCategorys);
    }

    public void saveAndFlush(final CashFlowCategory cashFlowCategory) {
        this.repository.saveAndFlush(cashFlowCategory);
    }

    public void delete(final CashFlowCategory cashFlowCategory) {
        this.repository.delete(cashFlowCategory);
    }
}