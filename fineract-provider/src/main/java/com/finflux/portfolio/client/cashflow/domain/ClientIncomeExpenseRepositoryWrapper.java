package com.finflux.portfolio.client.cashflow.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.client.cashflow.exception.ClientOrFamilyMemberIncomeExpenseNotFoundException;

@Service
public class ClientIncomeExpenseRepositoryWrapper {

    private final ClientIncomeExpenseRepository repository;

    @Autowired
    public ClientIncomeExpenseRepositoryWrapper(final ClientIncomeExpenseRepository repository) {
        this.repository = repository;
    }

    public ClientIncomeExpense findOneWithNotFoundDetection(final Long id) {
        final ClientIncomeExpense clientIncomeExpense = this.repository.findOne(id);
        if (clientIncomeExpense == null) { throw new ClientOrFamilyMemberIncomeExpenseNotFoundException(id); }
        return clientIncomeExpense;
    }

    public void save(final ClientIncomeExpense clientIncomeExpense) {
        this.repository.save(clientIncomeExpense);
    }

    public void save(final List<ClientIncomeExpense> clientIncomeExpense) {
        this.repository.save(clientIncomeExpense);
    }

    public void saveAndFlush(final ClientIncomeExpense clientIncomeExpense) {
        this.repository.saveAndFlush(clientIncomeExpense);
    }

    public void delete(final ClientIncomeExpense clientIncomeExpense) {
        this.repository.delete(clientIncomeExpense);
    }
}