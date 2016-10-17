package com.finflux.portfolio.cashflow.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.exception.IncomeExpenseNotFoundException;

@Service
public class IncomeExpenseRepositoryWrapper {

    private final IncomeExpenseRepository repository;

    @Autowired
    public IncomeExpenseRepositoryWrapper(final IncomeExpenseRepository repository) {
        this.repository = repository;
    }

    public IncomeExpense findOneWithNotFoundDetection(final Long id) {
        final IncomeExpense incomeExpense = this.repository.findOne(id);
        if (incomeExpense == null) { throw new IncomeExpenseNotFoundException(id); }
        return incomeExpense;
    }

    public void save(final IncomeExpense incomeExpense) {
        this.repository.save(incomeExpense);
    }

    public void save(final List<IncomeExpense> cashFlowCategorys) {
        this.repository.save(cashFlowCategorys);
    }

    public void saveAndFlush(final IncomeExpense incomeExpense) {
        this.repository.saveAndFlush(incomeExpense);
    }

    public void delete(final IncomeExpense incomeExpense) {
        this.repository.delete(incomeExpense);
    }
}